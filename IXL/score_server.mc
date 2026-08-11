# IXL VO — Score Server 终极版(Java · 乱序消息 + 前缀推进 + 并发)

> **情报卡**:面经 #3,VO 第二轮方向。**IXL 最有区分度的一题**——考并发建模 + 一个漂亮的洞察(报告入列、clamp 不可交换)。无 LC 原题,但骨架 = TCP 重排,与 ABC FU4 同构。
> **代码状态**:`ScoreServer.java` JDK 21 编译通过;乱序单元 / clamp 反例 / 边界 / **16 线程 × 20 轮 × 200 条乱序压力测试 vs 顺序参照实现逐一比对**全绿。
> **一句话**:题面看似"写个小服务器",实际考的是——**发现"到达顺序里没有你要的信息",然后把信息自己带过来**。

---

## 1. 原题还原(英文,面试官视角)

> *A server stores a single student's score — an integer clamped to the range 0–100. A single client sends the server two kinds of messages:*
>
> - *score **changes** ("add 10 points", "subtract 15 points") — the server does **not** reply;*
> - ***report** requests — the server must reply with the current score.*
>
> *System constraints:*
> 1. *Messages arrive in parallel: each arrival invokes `handleMessage` on a **new thread**.*
> 2. *The network delays messages arbitrarily — they can arrive **out of order** — but nothing is lost or corrupted; everything is eventually delivered.*
> 3. *A report must include **exactly** the changes sent **before** it (even those that haven't arrived yet), and **none** sent **after** it (even those that already arrived).*
> 4. *Assume near-unlimited memory first; once it works, discuss **space optimization**.*

## 2. 题意图解(30 秒把题读对)

约束 3 是全题的灵魂,读三遍:"之前 / 之后"指的是**发送顺序**,而网络把到达顺序打乱了。举个具体的:客户端依次发了 `+10`、`报告`、`−5`,网络送达顺序却是 `−5`、`+10`、`报告`——报告必须回 **10**(只含它之前发的 +10),**不能**回 5(−5 是它之后发的,哪怕先到了)。

**到达顺序里根本没有"发送顺序"这个信息**——这是本题第一个要说出口的发现。信息不在,就让消息自己把信息带来。

## 3. 开场 3 分钟:澄清问题(附建议默认值)

| 要问的问题 | 建议默认 | 为什么要问 |
|---|---|---|
| **消息格式能改吗?** | 能——加 seq 正是设计的一部分 | **先问再加**,这个动作本身是加分项 |
| 报告的回复通道长什么样? | 本实现用 `CompletableFuture`;现场可能是回调/网络写 | 决定 API 形态 |
| 初始分数? | 0 | 一句话的事,但要定 |
| 0–100 是 clamp(卡住)还是 reject(拒绝)? | clamp | 决定核心一行怎么写 |
| 单客户端确定吗? | 题面明说单客户;多客户是 FU1 | 铺垫 follow-up |
| seq 会溢出吗? | 单客户 int 够;long 更稳 | 展示边界意识 |

## 4. 解法推导(六步,讲给面试官听的顺序)

**第一步:天真做法为什么死。** 按"到达顺序"直接应用?约束 3 当场判死刑——上面的例子里报告会错回 5。到达顺序是网络的事故现场,不是客户端的意图。

**第二步:把发送顺序随消息带过来。** 客户端给**每条消息**(加减分和报告都算)编连续递增的序号 seq = 1, 2, 3…。约束 3 立刻变得精确:*seq 为 s 的报告,必须反映且仅反映 seq < s 的全部加减分。*

**第三步:乱序到达,按序应用(拼图)。** 服务器维护"**已应用前缀**"指针 `nextSeq`:到达的消息先扔进缓冲 `Map<seq, msg>`;每次到达后尽力推进——只要缓冲里有 `nextSeq`,取出应用、指针 +1、循环。像拼拼图:碎片乱序到手,只按编号顺序拼上。

**第四步:报告也是序列的一员(本设计的精髓)。** 报告**不需要任何特殊处理**——它排在序列里,轮到它被"应用"时,score 恰好等于它前面所有加减分依序作用后的值,当场回复即可。不用快照表、不用条件变量,一个缓冲 + 一个指针,整个服务器 20 行。**把报告塞进同一条序列,一切特判消失**——这是本题最优雅的一手。

**第五步:clamp 让"按序"从讲究变成必须。** 分数卡在 [0,100] 使加减**不可交换**:

```text
95 分时:先 +10(卡到 100)再 −5  → 95
         先 −5(到 90)再 +10     → 100   ← 顺序不同,结果不同!
```

所以不能偷懒"把 delta 求和一次算完"——必须严格按 seq 逐条应用。**主动讲出这个反例,是全题最亮的一句话。**

**第六步:空间优化(题面明示的 follow-up)自动达成。** 天真解法存全部历史、报告时排序重算;我们的设计里**消息一旦应用立刻丢弃**,缓冲里只剩"前缀缺口之后"的消息——**内存 = 乱序窗口大小,不是历史总量**。第四问在设计里免费解决,把这句说出来。

## 5. 手走一遍(考场要做的事)

发送:`1:+10  2:报告  3:−5`;到达顺序:`3 → 1 → 2`。

| 到达 | 缓冲(应用前) | 推进过程 | score | nextSeq | 报告 |
|---|---|---|---|---|---|
| 3(−5) | {3} | 没有 seq 1,不动 | 0 | 1 | — |
| 1(+10) | {1, 3} | 应用 1 → score 10;seq 2 不在,停 | 10 | 2 | — |
| 2(报告) | {2, 3} | 应用 2 → **回复 10**;应用 3 → score 5 | 5 | 4 | **10** ✓ |

报告正确地只包含了它之前发出的 +10,排除了先到但后发的 −5——约束 3 被结构性满足,没有任何 if 判断"这条是不是特殊情况"。

## 6. 参考实现(已验证)

<details>
<summary><b>展开完整代码(ScoreServer.java;测试:乱序单元 / clamp 反例 95→+10→−5 必须回 95 / 首条即报告回 0 / 下界 clamp / 16 线程 × 20 轮 × 200 条乱序压力 vs 顺序参照逐一比对)</b></summary>

```java
import java.util.*;
import java.util.concurrent.*;

public class ScoreServer {

    public static class Message {
        final int seq;
        final Integer delta;                              // null = 报告请求
        final CompletableFuture<Integer> response;        // 仅报告使用

        private Message(int seq, Integer delta, CompletableFuture<Integer> response) {
            this.seq = seq;
            this.delta = delta;
            this.response = response;
        }

        public static Message delta(int seq, int delta) {
            return new Message(seq, delta, null);
        }

        public static Message report(int seq) {
            return new Message(seq, null, new CompletableFuture<>());
        }
    }

    private final Map<Integer, Message> pending = new HashMap<>();  // 乱序缓冲:seq -> 消息
    private int nextSeq = 1;                                        // 前缀边界:下一个待应用的 seq
    private int score = 0;

    /** 题面约定:每条消息到达时在新线程调用本方法 */
    public synchronized void handleMessage(Message m) {
        pending.put(m.seq, m);
        while (pending.containsKey(nextSeq)) {            // 尽力推进前缀
            Message cur = pending.remove(nextSeq);        // 应用即丢弃 -> 空间 = 乱序窗口
            if (cur.delta != null) {
                score = Math.max(0, Math.min(100, score + cur.delta));  // clamp -> 必须按序
            } else {
                cur.response.complete(score);             // 按序轮到报告时,score 恰为其前缀分数
            }
            nextSeq++;
        }
    }
}
```

</details>

逐段解说:`Message` 是带工厂方法的值对象,`delta == null` 表示报告,报告自带 `CompletableFuture` 当回复通道(调用方 `report.response.get()` 等答案);`pending` 是乱序缓冲,**应用即 remove**——第六步的空间优化就藏在这一行;`while (pending.containsKey(nextSeq))` 是推进循环——每条消息只会被应用一次,均摊 O(1);clamp 那行 `Math.max(0, Math.min(100, ...))` 注意嵌套方向(先 min 上界再 max 下界,写反一个就错)。

## 7. 并发正确性(说出口的版本)

`handleMessage` 整个标 `synchronized`(单把锁):多线程到达被串行化,`pending`/`nextSeq`/`score` 三个共享可变状态全部在锁内。锁内工作量小(每条消息只被应用一次),单客户场景完全够——**correctness first**。对比口述一个**阻塞式设计**:报告线程 `wait` 到前缀推进过自己再回复——语义相同,代码更长还要处理虚假唤醒;我们的版本里"等待"被物化成了"消息躺在缓冲里",不占线程。

> *"One lock serializes arrivals; each message is applied exactly once inside the lock, so the critical section is amortized O(1). Waiting is materialized as 'sitting in the buffer' instead of a blocked thread."*

## 8. 复杂度(说出口的版本)

> *"Each message is buffered once and applied once — amortized **O(1)** per message. Memory is the **out-of-order window** (messages ahead of the prefix), not the full history — that's the space optimization the problem asks for, and it falls out of the design."*

## 9. Follow-up 全集(带详细答案)

### FU1 — "多个客户端怎么办?"

每个客户一套 `(pending, nextSeq, score)`:`Map<clientId, State>`。锁粒度顺势细化:全局一把锁 → **每客户一把锁**(不同客户互不阻塞;同客户仍串行,这正是语义要求)。seq 也变成 per-client 编号——"发送顺序"本来就是每个客户自己的概念。

### FU2 — "不用锁能做吗?"(Actor 模式)

把消息全部丢进一个 `BlockingQueue`,单个 worker 线程消费——**天然串行,无锁 by construction**,代价是一次线程切换。与 synchronized 版是**同一语义的两种实现**。这就是 Task Scheduler FU6 的层 3——两道题在这里会师,面试里点破:*"same single-writer idea as an event loop / actor."*

### FU3 — "消息会丢失呢?"

前缀会**永远卡死**在缺口上(后面的全部堆在缓冲里)。诚实的第一句:**这打破了题面的"不丢失"假设,是另一个问题**。然后给方向:ack + 重传 + 超时(把 TCP 的活自己干一遍);或超时跳过缺口并标记"结果可能不一致"——一致性和可用性在此交易(CAP 的味道,点到即止)。

### FU4 — "服务器重启怎么恢复?"

持久化 `score + nextSeq` 两个数即可恢复到前缀边界;缓冲里未应用的消息丢了没关系——客户端重发(配合 FU3 的重传机制)。追加写日志(journal)也行:每应用一条记一条,重放即恢复——与 Task Scheduler FU9 同款思想。

### FU5 — "用时间戳代替 seq 行不行?"

不行,这是个值得主动讲的深坑:时间戳能排序,但**判断不了"完整性"**——收到 t=100 和 t=300 的消息,你**无法知道**中间有没有一条 t=200 还在路上;而 seq 的本质是**连续无洞**,seq 4 没到就是没到,缺口看得见。报告需要的恰恰是"我之前的**全部**都到齐了"这个完整性承诺——**seq 给得了,时间戳给不了**。(另加时钟偏移一句:多机时间戳还有 clock skew 问题。)

## 10. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **按到达顺序应用** | 约束 3 直接违反 | 发送顺序要靠 seq 自己带来 |
| **对 delta 求和一次算完** | clamp 不可交换,结果错 | 95→+10→−5 反例当场默写;必须按序逐条 |
| **报告走特殊通道**(快照/条件变量) | 复杂且易错 | 报告入列,当成序列一员"应用" |
| **忘加锁** | `nextSeq`/`score`/缓冲竞态 | `synchronized` 或 actor;三个共享状态点名给面试官 |
| clamp 嵌套写反 | 上下界失效 | `Math.max(0, Math.min(100, x))`——先卡上界再卡下界 |
| 应用后不删缓冲 | 内存 = 全部历史,第四问白给 | `pending.remove(nextSeq)`,应用即丢弃 |
| 假设 seq 可以有洞 | 推进循环永不前进 | 澄清:seq 连续递增是客户端的义务 |
| 初始分/回复通道不确认 | 细节被动 | 澄清清单过一遍 |

## 11. 30 分钟考场节奏 + 互动台词

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–3 min | 读题 + 点破灵魂 | "The key tension: 'before/after' refers to **send** order, but the network scrambles arrival order — arrival order simply doesn't contain the information I need." |
| 3–6 min | 提出 seq + **要 buy-in** | "Can I add a sequence number to every message, including reports? Then the spec becomes precise: report s reflects exactly deltas with seq < s. I'll buffer out-of-order arrivals and apply them in seq order — like TCP reassembly. Sound good?" |
| 6–16 min | 写代码,持续 narrate | "The report is just another item in the sequence — when its turn comes, the score is exactly its prefix sum. No special casing." |
| 16–20 min | 手走第 5 节例子 + **clamp 反例** | "Why strict ordering? Clamping makes deltas non-commutative: at 95, +10 then −5 gives 95, but −5 then +10 gives 100." |
| 20–24 min | 并发 + 空间 | "One lock serializes arrivals… and memory is the out-of-order window, not history — messages are dropped once applied." |
| 24–30 min | Follow-up 对话(第 9 节弹药) | 多客户 / actor / 丢失 / 恢复 / 时间戳为什么不行 |

## 12. 30 秒总结陈词(背诵版)

> *"Arrival order doesn't contain send order, so I make every message carry a sequence number — reports included. The server buffers out-of-order arrivals and advances a prefix pointer, applying messages strictly in sequence; when a report's turn comes, the score is exactly the sum of everything sent before it — no special casing. Ordering is mandatory, not cosmetic: clamping to 0–100 makes deltas non-commutative. Each message is applied once — amortized O(1) — and dropped after applying, so memory is the out-of-order window, not the history. It's TCP reassembly with a tiny state machine on top."*

**记忆钩子**:到达序没有发送序 → **seq 自带** → 缓冲 + 前缀指针(拼图)→ **报告入列免特判** → **clamp 反例证明必须按序** → 应用即丢弃(内存 = 乱序窗口)→ 一把锁收工。三处呼应:TCP 重排 = ABC FU4;actor = Task Scheduler FU6 层3;journal 恢复 = Task Scheduler FU9。
