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

### 心脏九行:handleMessage 逐行拆解

先钉两个状态变量的身份。**`nextSeq` = 追剧书签:"我该看第几集了"**——各集(消息)乱序下载完成,但必须按集数看,`nextSeq` 就是"看完的最后一集 + 1"。它是一条分界线:编号 < nextSeq 的**全部处理完毕并丢弃**;≥ nextSeq 的要么在架上等、要么还没到。三个易混点:它**可以不动**(来的不是等的那个,书签不翻页);**只 +1 前进,永不跳跃**(不能跳集——这正是约束 3);**和消息的 seq 是两回事**(seq 是"这是第几集",属于消息;nextSeq 是"看到哪了",属于服务器)。**`pending` = 提前到货的架子**:已到但没轮到的消息。

```java
public synchronized void handleMessage(Message m) {
    pending.put(m.seq, m);                     // ① 不管是谁,先上架(统一路径,无分叉)
    while (pending.containsKey(nextSeq)) {     // ② 等的编号在架上吗?不在 → 本次到此为止
        Message cur = pending.remove(nextSeq); // ③ 取下并删掉(应用即丢弃 = 空间优化)
        if (cur.delta != null) {               // ④ 类型标记:delta 有值=加减分,null=报告
            score = Math.max(0, Math.min(100, score + cur.delta));   // ⑤ 更新 + 夹紧
        } else {
            cur.response.complete(score);      // ⑥ 轮到报告:score 恰为前缀分,按铃
        }
        nextSeq++;                             // ⑦ 书签翻页,回到 ② 看下一集在不在
    }
}
```

② 用 `while` 不用 `if` 是**多米诺**的关键:处理完这集,下一集可能**早在架上**,要连着看;写成 `if`,架上的存货永远没人触发,系统卡死。追踪输出(发送 `1:+10, 2:报告, 3:−5`,到达 `3→1→2`,实测):

```text
到达3:上架,等 1 → 本次 0 处理,架上 [3]
到达1:等的到了!应用1(score=10)→ 2 不在 → 停,架上 [3]
到达2:应用2(报告,complete(10))→ 3 早在架上!→ 顺势应用3(score=5)→ 架空
```

第三次到达触发**两连处理**(多米诺);报告回 10 不是 5(约束 3 由处理顺序结构性满足);全程架上最多 2 件(空间 = 乱序窗口)。

**三个常见误解(修正后的执行模型)**:三条消息乱序到达 = **三次独立的 handleMessage 调用**——不是攒一批一次处理,前两次是"纯上架零处理"的空跑,while 三连完整发生在第三次调用内部;拿锁的是**到达线程**(题面:每条消息到达时在新线程调用),客户端线程从不进 handleMessage,它只在 `join()` 上睡觉等按铃;锁是**每次调用进门拿、出门放**(三次独立拿放),不是一次锁到底。

**不变量(自检用),方法每次退出时恒成立**:编号 < nextSeq 全部应用并丢弃;≥ nextSeq 在架上或未到;score = 前缀 [1, nextSeq) 依序应用后的值。九行代码唯一的工作就是维护这句话。

**clamp 小注**:夹钳——把值夹进 [0,100],超出按边界算。口诀:**min 配上界(压下来),max 配下界(托上去)**;JDK 21 有现成的 `Math.clamp(值, 0, 100)`(已验证),手写嵌套更通用且显原理。

## 7. 并发正确性(说出口的版本)

`handleMessage` 整个标 `synchronized`(单把锁):多线程到达被串行化,`pending`/`nextSeq`/`score` 三个共享可变状态全部在锁内。锁内工作量小(每条消息只被应用一次),单客户场景完全够——**correctness first**。对比口述一个**阻塞式设计**:报告线程 `wait` 到前缀推进过自己再回复——语义相同,代码更长还要处理虚假唤醒;我们的版本里"等待"被物化成了"消息躺在缓冲里",不占线程。

> *"One lock serializes arrivals; each message is applied exactly once inside the lock, so the critical section is amortized O(1). Waiting is materialized as 'sitting in the buffer' instead of a blocked thread."*

**精确语义(被追问时的版本)**:`synchronized` 保证**互斥 + 完整性**——一个线程跑完整个方法体,三个共享变量绝不交叉(实测:两线程无锁各百万次 `++` 丢 26 万次更新,加锁后分毫不差),但**不承诺顺序**——几乎同时到门口的线程谁先进是任意的(内置锁不公平,甚至可能插队)。而这恰好无所谓:**处理逻辑只认 seq,不认进门顺序**,任何入场序结局相同。分工一句话:

```text
锁管"同时"(不许两人同时摸共享变量)——但连门口排队顺序都不管
seq 管"乱序"(按发送序应用)——不管消息以什么顺序到、以什么顺序进门
锁不解决乱序,seq 不解决同时——各司其职,拼起来严丝合缝
```

两个附注:锁挂在**对象**上,凡碰这三个变量的方法**都**得 synchronized,否则等于后门没锁;synchronized 还附送**内存可见性**(前人改的值,后人进门保证看得见,happens-before)——被深挖时说出这两个词即封顶。

> *"synchronized guarantees mutual exclusion — each call runs to completion atomically. It does **not** guarantee ordering — but that's fine: ordering is seq's job, not the lock's. The lock handles 'at the same time'; the sequence numbers handle 'out of order'."*

## 8. 复杂度(说出口的版本)

> *"Each message is buffered once and applied once — amortized **O(1)** per message. Memory is the **out-of-order window** (messages ahead of the prefix), not the full history — that's the space optimization the problem asks for, and it falls out of the design."*

## 9. Follow-up 全集(带详细答案)

### FU1 — "多个客户端怎么办?"(锁粒度经典题,含实测)

**题目变化**:N 个学生各自的分数、各自的消息流。关键观察:**"发送顺序"只在单个客户内部有定义**——A 的消息和 B 的消息之间没有任何顺序关系(各发各的,互不知晓)。所以 seq 天然变 **per-client 编号**:消息带 `(clientId, seq)`,各自从 1 数起。

**状态分家**:每客户一套 `(pending, nextSeq, score)` 装进 State 小盒子,`Map<clientId, State>` 当目录。处理逻辑**一字不变**,先找到自己那套再跑。

**锁粒度**:全局一把锁正确但蠢——A 处理时 B 明明碰的是完全不同的数据,也得排队,万级客户全挤一把锁。改**每户一把锁**:锁挂到 State 对象上(块语法 `synchronized (st) {...}` 可锁**任意对象**;方法版 = 锁 `this`)。不同客户 → 不同锁 → 并行;同一客户 → 同一锁 → 仍串行——**这不是妥协,是语义要求**(同户的前缀推进必须原子)。设计原则一句话:**锁的粒度匹配数据的粒度**。

**暗坑:目录本身也是共享的。** 两线程同时对同一个新客户"查无则建",普通 HashMap 会竞态出**两套 State**(数据分裂)。解法:`ConcurrentHashMap.computeIfAbsent(clientId, id -> new State())`——"查无则建"整体原子,一个客户永远只有一套。(λ 参数 `id` 接到的就是缺失的那个 key,本例用不上,但 API 总会递给你——`computeIfAbsent(p, k -> new ArrayList<>())` 里的 `k` 是同款角色;配方只在缺失时执行,已验证。)于是形成经典**两层结构:目录用并发容器,门锁挂每户门上**。

```java
public void handleMessage(String clientId, Message m) {
    State st = clients.computeIfAbsent(clientId, id -> new State());  // 原子"查无则建"
    synchronized (st) {                     // 锁"这一户",不锁整栋楼
        // …原九行逻辑,全部换成 st.pending / st.nextSeq / st.score…
    }
}
```

**实测**(4 客户 × 20 条消息,处理带 5ms 模拟耗时,4 线程并发投递):全局一把锁 **430 ms**,每户一把锁 **105 ms**——正好 4 倍,正确性检查全过。**延伸一句很值钱**:同一把"按 clientId 分区"的钥匙从锁一路开到集群——一致性哈希 `clientId → shard`,每户状态天然独立,水平分片零改造。

**口播(简版,3 句,背这个)**:*"Give each client its own state and its own lock — a map from client ID to state. Different clients run in parallel; the same client is still one-at-a-time, and that's what we want, because order only matters inside one client. Sequence numbers are also per-client."* 被追问 scaling 补一句:*"The same idea scales out — hash the client ID to pick a server."*

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

---

## 附:多线程零基础速成

**线程 = 一条独立往前推进的执行流**,三个动词全在这:

```java
Thread t = new Thread(() -> { ...要跑的活... });   // 造(还没跑)
t.start();   // 从这刻起并行各跑各的(★写成 t.run() 就成了普通方法调用,没有新线程——经典坑)
t.join();    // 我停下来等它跑完
```

执行交错**每次运行都可能不同**(有时穿插,有时一方连跑到底),调度不归你管——多线程代码必须对**任意交错**都正确,这就是它难的根源。

**竞态(race condition)**:`count++` 看着一步,实际三步(读→加→写)。两线程交错时互相覆盖:各读到 5、各写回 6——两次 +1 只涨 1。实测:两线程各 +1 一百万次,无锁结果 **1,732,263**(凭空丢 26 万次);加 `synchronized` 后精确 **2,000,000**。竞态最阴在**概率性**——测试可能碰巧全对,上线偶尔错;所以本题的测试用 16 线程乱序压 20 轮去"钓"它(与 Rectangles 的属性测试同一哲学:确定性用例抓不到的,用量去抓)。

**synchronized = 单人卫生间的门锁**:进门拿锁,拿不到在门口排队;里面的人跑完**整个方法体**才放锁。锁挂在**对象**上——`synchronized` 方法 ≡ `synchronized(this) {...}`,块形式可锁任意对象(FU1 的每户锁靠它)。保证:**互斥 + 完整性 + 可见性**;不保证:**入场顺序**(不公平锁,谁抢到算谁的)。

**本题的两坑两药(全题并发认知的浓缩)**:

```text
坑一:同时(多条到达线程同一瞬间摸共享数据)→ 药:synchronized(排队,一次一个)
坑二:乱序(先发的消息可能后到)            → 药:seq + 缓冲 + 前缀指针(排回发送序)
锁不解决乱序,seq 不解决同时——各司其职,缺一不可
```

---

## 附:CompletableFuture 零基础速成(本题唯一的新面孔)

### 它解决的困境

报告消息到达时(线程 A),**答案可能还算不出来**——前缀有缺口,要等某条迟到的消息(未来由线程 B 送达)把前缀推过去,答案才诞生。困境:**答案诞生在线程 B 里,最初提问的人怎么拿到?** `CompletableFuture` = 一个"现在没有结果、将来会有"的盒子:提问方拿着盒子等,**任何线程在任何时刻**把答案装进去,等的人立刻醒来。

### 心智模型:取餐呼叫器

点单时店员递你一个呼叫器(`new CompletableFuture<>()`);后厨(另一个线程)饭好了按铃(`complete(饭)`);你的呼叫器一震,取餐(`join()`)。五个 API 够用:

| 调用 | 谁用 | 含义 |
|---|---|---|
| `new CompletableFuture<T>()` | 提问方 | 造一个空呼叫器 |
| `f.complete(v)` | **任何线程** | 装结果并按铃;只有第一次有效 |
| `f.join()` / `f.get()` | 等答案的人 | **阻塞**到有结果再取出(join 抛非受检异常,面试用它省事) |
| `f.isDone()` | 任何人 | 看一眼响没响,不等 |
| `f.completeExceptionally(e)` | 出错方 | 装入异常,等的人 join 时收到 |

实测两场景(`CfDemo.java`):**先等后响**——主线程 `join()` 真实阻塞 ~492ms,被厨房线程的 `complete(42)` 唤醒;**先响后等**——结果已在盒中,`join()` 0ms 立即返回。**呼叫器不在乎按铃和取餐谁先谁后**,这是它比裸线程通信省心的地方。(它还有一整套 `thenApply/supplyAsync` 异步流水线 API,本题用不到,知道存在即可。)

### 和 synchronized、多线程的关系:互斥 vs 交接

多线程世界有两类不同的问题,别混:

| | 问题 | 工具 |
|---|---|---|
| **互斥** | 两个线程**同时改**共享数据会打架(`score`/`pending`/`nextSeq`) | `synchronized`:同一时刻只放一个线程进临界区 |
| **交接** | 一个线程**等**另一个线程**未来**才产出的结果 | `CompletableFuture`:一次性的"结果盒子 + 门铃" |

二者**互补而非竞争**——本题两个都用了:锁保三个共享变量不被并发写坏,future 把报告答案从"算出它的线程"送回"提问的线程"。面试里说出这个分工,并发一节就稳了。

**揭魅:它不是魔法,是打磨好的 wait/notify。** 十行裸版:

```java
class MyFuture {                              // CompletableFuture 的裸版
    private Integer value = null;
    public synchronized void complete(int v) {
        if (value == null) { value = v; notifyAll(); }   // 装结果 + 摇铃
    }
    public synchronized int join() throws InterruptedException {
        while (value == null) wait();                    // 没结果就睡,摇醒再查
        return value;
    }
}
```

`CompletableFuture` 就是这个"一次性阀门"的工业级版本(免虚假唤醒、带异常通道、带超时、带链式回调)——第 7 节说的"阻塞式设计语义相同但代码更长",长的就是这个样子。

### 呼叫器在本题的完整旅程(发送 1:+10, 2:报告, 3:−5;到达 3→1→2)

```text
① 提问方造 Message.report(2) —— 呼叫器藏在消息里(response 字段),随消息漂流;
   提问方留引用,之后 report.response.join() 等答案(加减分消息 response=null,不需回复)
② seq 2 到达(某线程):前缀没到它,进缓冲。★ 该线程直接返回——没有任何线程在傻等!
   "等待"被物化成缓冲里的一条消息,而不是一个阻塞的线程
③ seq 1 到达(另一线程):推进前缀 → 轮到 2 → 这个线程调 response.complete(10) 按铃
   ★ 按铃的线程 ≠ 造呼叫器的线程 ≠ 送达报告的线程——呼叫器不在乎谁按
④ 提问方 join() 醒来,拿到 10
```

两颗星是本题最值得说出口的两句:**服务器里没有线程为等待而阻塞**(答案没好时,报告只是缓冲里的数据);**谁推进前缀谁按铃**(future 解耦了"谁问"和"谁答")。

**闭环**:Task Scheduler FU6 的 actor 版是同款——`AddMsg` 带 `ack`、`PlanMsg` 带 `reply`,都是 CompletableFuture:**mailbox 把请求送进单线程,future 把答案送出来**,这就是"线程间请求-响应"的标准形态。

### 为什么是 complete(score),而不能是普通赋值?

表层:类型不对(`response` 是盒子不是整数,`= score` 编译不过)。深层:就算把字段改成 `Integer answer` 直接赋值,客户端也是死路三条——**不知何时去读**(报告先到时答案尚未诞生,handleMessage 返回时还是 null);**只能死循环轮询烧 CPU**(实测:等 300ms 空转 **6.19 亿次**);**可见性无保证**(无同步时,写线程写下的值可能一直待在它的 CPU 缓存里,轮询线程可能永远读到 null,循环挂死)。

`complete(v)` 是**三合一**:赋值 + 按铃(唤醒所有睡在 join 上的线程)+ 可见性保证(happens-before);`join()` 是对偶三合一:睡觉(零空转)+ 被按铃叫醒 + 保证看见最新值。一句话:**`=` 只会放东西,不会叫人;跨线程传"未来才有的值",缺了"叫人"和"看得见",放了也白放。**

### join 的三种现实用法(ans 永远不会是 null)

先破一个常见错觉:`int ans = f.join()` 里 **ans 从无 null 时刻**——`join()` 是"不放行"的调用,线程停在这一行直到答案诞生,`=` 的赋值发生在 join 返回**之后**;能执行到下一行,ans 必然有值。**join 不是"取当前值",是"等到有值才放行"——交到你手里的永远是熟饭。**三种用法(全部实测):

1. **同步阻塞(join)——专职线程,等待就是本职**:Web 请求处理线程发 report → join 等 → 把答案写进 HTTP 响应。本题测试代码即此用法。
2. **异步回调(thenAccept)——不等,留条**:`f.thenAccept(v -> 更新界面(v))`,登记"答案到了执行这段",线程继续干别的。UI 线程必用(join 会冻住界面)。细节:回调由**按铃的那条线程**顺手执行。
3. **带超时(get + timeout)——生产铁律**:`f.get(2, TimeUnit.SECONDS)`,超时抛 TimeoutException → 降级("服务繁忙")或重试。为什么必须:FU3 的消息丢失场景里前缀卡死,future **永远不会被 complete**,裸 join 的线程永久泄漏——**超时是分布式系统的救生索**(这句把 FU3 和 CompletableFuture 串起来了)。

**记忆钩子**:呼叫器随消息漂流,任何线程可按铃,先响后取也不怕;**synchronized 管"别同时改",future 管"把未来的答案递回来"**;`=` 不会叫人,join 只交熟饭,现实中永远配超时。
