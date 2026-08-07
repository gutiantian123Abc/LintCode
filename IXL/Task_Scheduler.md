# IXL VO — Task Scheduler 终极版(Java · indegree 计数版)

![Frequency](https://img.shields.io/badge/VO%20%E7%8B%AC%E7%AB%8B%E6%8A%A5%E5%91%8A-2%20%E6%AC%A1%EF%BC%8C%E6%8A%BC%E9%A2%98%E6%A6%9C%E7%AC%AC%E4%B8%80-red) ![Topic](https://img.shields.io/badge/topic-Topological%20Sort%20%2B%20Max--Heap-00695c) ![Tests](https://img.shields.io/badge/tests-9%20groups%20passed%20(%E5%90%AB%20500%20%E8%BD%AE%E4%BA%A4%E5%8F%89%E9%AA%8C%E8%AF%81)-brightgreen)

> 出处:面经 #15、#21(独立两次 VO 报告);Glassdoor 2025-12 有人被考同族基础题 Course Schedule II。
> **本版就是你的 LC 210 官方 indegree 模板直迁**——换四个零件即达最优复杂度,起手即终态。
> 代码 JDK 21 编译,9 组测试通过,含全部 follow-up 实现(executeNext / executeFifo / findCycle)。

---

## 1. 原题还原(英文,面试官视角)

> Implement a task scheduling system that supports the following two operations:
>
> **`addTask(int taskId, int priority, List<Integer> dependencies)`**
> Registers a task. `taskId` is a unique identifier. A higher `priority` value means higher priority. `dependencies` is a list of task IDs that must be completed before this task can be executed.
>
> **`execute()` → `List<Integer>`**
> Executes all registered tasks in a valid order and returns the sequence of executed task IDs.
>
> Rules: a task may only run after **all** of its dependencies have finished. Among all tasks that are **currently runnable** (all dependencies satisfied), the one with the **highest priority** runs first.
>
> Example:
> ```text
> addTask(1, 5,  [])       addTask(2, 10, [])
> addTask(3, 1,  [2])      addTask(4, 20, [1, 3])
> execute() → [2, 1, 3, 4]
> ```

## 2. 开场 3 分钟:澄清问题(附建议默认值)

| 问题 | 建议默认(说出来) |
|---|---|
| Duplicate `taskId`? | Throw `IllegalArgumentException` |
| Dependency on a task not yet added(前向引用)? | Allow at `addTask`, validate at `execute` |
| `dependencies` 有重复或包含自己? | 重复在计数架构下自洽(见 FU4),但声明会去重更干净;自依赖 = 最小的环,环检测自然抓住 |
| Same priority — tie-break? | `taskId` ascending for determinism(FIFO 版见 FU2) |
| Cycle — throw or return empty? | Throw with a clear message |
| `execute()` 可重复调用?之后还能 `addTask`? | Both allowed — 执行状态是 baseIndegree 的拷贝,不碰原图 |
| `dependencies == null`? | Throw(要求传空列表) |

收尾台词:*"Let me confirm: single worker, one task at a time, and a newly-unlocked high-priority task competes immediately — correct?"*

## 3. 解法推导(讲给面试官听的顺序)

**第一步,识别**:依赖 = 有向边,"依赖先行" = 拓扑排序 —— *"This is topological sort — Course Schedule II's structure — with one twist."*

**第二步,唯一的算法改动**:LC 210 任意合法序都行,ready 用 FIFO queue;这题 ready 集合内按 priority 贪心 → **queue 换 max-heap**。

**第三步,核心不变量**(满分句):

> *"The invariant: a task enters the ready heap only when its remaining-dependency count drops to zero — so admission guarantees dependency-safety, and the heap only decides order among safe tasks. Two separate concerns."*

**入堆资格管依赖(indegree 归零),出堆顺序管 priority,各管各的。**

**第四步,主动排掉两个错误方案**:

1. **不能先全局按 priority 排序再跑**——priority 是"当前可执行集合内"的局部贪心:示例里 4 的 priority 全场最高(20)却必须最后跑。
2. **不能套 BFS 层级循环**(`int size = queue.size()`)——层级改变语义,反例(测试用例 2):

```text
1(p5, 无依赖)   2(p1, 无依赖)   3(p100, 依赖1)

层级版:第一层 {1,2} 排序后一口气执行 → 1, 2, 3  ✗
正确:  执行完 1 的瞬间 3(p100) 解锁,立刻压过 2 → 1, 3, 2  ✓
```

(反向加分句:*"If tasks in a batch ran in parallel per round, the layered version would be the right model — that's why I asked about single-worker semantics."*)

**手走示例**(写完代码用它验证):

| 步骤 | 动作 | ready 堆(按 priority) | 说明 |
|---|---|---|---|
| 初始 | 1、2 的 indegree 为 0,入堆 | {2(p10), 1(p5)} | 4(p20) 最高但 indegree=2,进不来 |
| 1 | poll **2** | {1(p5), 3(p1)} | 3 的 indegree 1→0,解锁 |
| 2 | poll **1** | {3(p1)} | 4 的 indegree 2→1 |
| 3 | poll **3** | {4(p20)} | 4 的 indegree 1→0 |
| 4 | poll **4** | — | 结果 [2, 1, 3, 4] ✓ |

## 4. 参考实现:LC 210 模板直迁(这就是现场要写的版本)

与你的 Course Schedule II 官方解逐行对应:

| 你的 LC 210 代码 | 本版对应物 | 为什么变 |
|---|---|---|
| `edges`(`List<List<Integer>>`) | `childrenMap`(`Map<Integer, List<Integer>>`) | taskId 稀疏、总数未知 |
| `indeg[]` | `baseIndegree`(构建期)→ `indeg` 拷贝(执行期) | 稀疏 + 支持重复 execute |
| `++indeg[info[0]]`(建边时) | `baseIndegree.merge(taskId, 1)`(addTask 时) | 同一动作 |
| `queue`(LinkedList) | `PriorityQueue` | **唯一算法改动** |
| `index != numCourses → return []` | `order.size() != n → throw` | OOD 显式报错 |

动态结构只有三张表:`priorityMap`、`childrenMap`、`baseIndegree`。没有 parentsMap——初始入度由 `baseIndegree` 承担,unknown-dep 校验由 `childrenMap.keySet()`(恰好 = 被引用过的全部 parent)承担;父方向信息只有 `findCycle` 这个 follow-up 需要,到时从 childrenMap 反转一次即可。

<details>
<summary><b>展开完整代码(含 executeFifo / executeNext / findCycle,测试见 TaskSchedulerLC.java)</b></summary>

```java
import java.util.*;

public class TaskSchedulerLC {

    private final Map<Integer, Integer> priorityMap = new HashMap<>();
    private final Map<Integer, List<Integer>> childrenMap = new HashMap<>(); // = LC 的 edges
    private final Map<Integer, Integer> baseIndegree = new HashMap<>();      // = LC 的 indeg[]
    private final Map<Integer, Integer> insertionOrder = new HashMap<>();    // FU2:稳定 tie-break
    private int nextInsertion = 0;
    private boolean streamStarted = false;

    public void addTask(int taskId, int priority, List<Integer> dependencies) {
        if (streamStarted) {
            throw new IllegalStateException("cannot addTask after incremental execution started");
        }
        if (priorityMap.containsKey(taskId)) {
            throw new IllegalArgumentException("duplicate taskId: " + taskId);
        }
        if (dependencies == null) {
            throw new IllegalArgumentException("dependencies must not be null (pass empty list)");
        }
        priorityMap.put(taskId, priority);
        insertionOrder.put(taskId, nextInsertion++);
        baseIndegree.putIfAbsent(taskId, 0);          // ★ 无依赖任务也要有条目,否则播种时漏掉
        for (int parent : dependencies) {
            childrenMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(taskId);
            baseIndegree.merge(taskId, 1, Integer::sum);
        }
    }

    /** 默认版:同优先级按 taskId 升序(确定性) */
    public List<Integer> execute() {
        return executeInternal(byPriorityThenId());
    }

    /** FU2:同优先级按 addTask 顺序(稳定/FIFO)—— 只换比较器,骨架不动 */
    public List<Integer> executeFifo() {
        return executeInternal((a, b) -> {
            int cmp = Integer.compare(priorityMap.get(b), priorityMap.get(a));
            return cmp != 0 ? cmp : Integer.compare(insertionOrder.get(a), insertionOrder.get(b));
        });
    }

    private Comparator<Integer> byPriorityThenId() {
        return (a, b) -> {
            int cmp = Integer.compare(priorityMap.get(b), priorityMap.get(a));  // 大 priority 在前
            return cmp != 0 ? cmp : Integer.compare(a, b);
        };
    }

    private List<Integer> executeInternal(Comparator<Integer> cmp) {
        validateDependenciesRegistered();
        Map<Integer, Integer> indeg = new HashMap<>(baseIndegree);      // 拷贝 -> 可重复调用

        PriorityQueue<Integer> ready = new PriorityQueue<>(cmp);
        for (Map.Entry<Integer, Integer> e : indeg.entrySet()) {
            if (e.getValue() == 0) {
                ready.add(e.getKey());
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!ready.isEmpty()) {                                      // 单层 while,无层级循环
            int u = ready.poll();
            order.add(u);
            for (int v : childrenMap.getOrDefault(u, Collections.emptyList())) {
                if (indeg.merge(v, -1, Integer::sum) == 0) {            // --indeg[v] == 0,O(1)
                    ready.add(v);
                }
            }
        }
        if (order.size() != priorityMap.size()) {                       // index != numCourses
            throw new IllegalStateException("cycle detected: no valid execution order");
        }
        return order;
    }

    // ---------------- FU1:增量执行 executeNext() ----------------
    private PriorityQueue<Integer> streamReady = null;
    private Map<Integer, Integer> streamIndeg = null;
    private int streamExecuted = 0;

    /** 每次只执行一个任务并返回其 id;全部执行完返回 null;发现环抛异常 */
    public Integer executeNext() {
        if (!streamStarted) {
            validateDependenciesRegistered();
            streamIndeg = new HashMap<>(baseIndegree);
            streamReady = new PriorityQueue<>(byPriorityThenId());
            for (Map.Entry<Integer, Integer> e : streamIndeg.entrySet()) {
                if (e.getValue() == 0) {
                    streamReady.add(e.getKey());
                }
            }
            streamStarted = true;
        }
        if (streamReady.isEmpty()) {
            if (streamExecuted < priorityMap.size()) {
                throw new IllegalStateException("cycle detected: no runnable task remains");
            }
            return null;                                                // 正常完结
        }
        int task = streamReady.poll();
        streamExecuted++;
        for (int v : childrenMap.getOrDefault(task, Collections.emptyList())) {
            if (streamIndeg.merge(v, -1, Integer::sum) == 0) {
                streamReady.add(v);
            }
        }
        return task;
    }

    // ---------------- FU3:环路报告 findCycle() ----------------
    /** 无环返回空列表;有环返回一条具体环路(相邻元素为依赖关系,首尾相接) */
    public List<Integer> findCycle() {
        validateDependenciesRegistered();
        Map<Integer, Integer> indeg = new HashMap<>(baseIndegree);
        Deque<Integer> ready = new ArrayDeque<>();                      // 静默 Kahn,顺序无所谓
        for (Map.Entry<Integer, Integer> e : indeg.entrySet()) {
            if (e.getValue() == 0) {
                ready.add(e.getKey());
            }
        }
        Set<Integer> stuck = new HashSet<>(priorityMap.keySet());
        while (!ready.isEmpty()) {
            int u = ready.poll();
            stuck.remove(u);
            for (int v : childrenMap.getOrDefault(u, Collections.emptyList())) {
                if (indeg.merge(v, -1, Integer::sum) == 0) {
                    ready.add(v);
                }
            }
        }
        if (stuck.isEmpty()) {
            return new ArrayList<>();                                   // 无环
        }
        // 只有这里需要父方向信息:反转 childrenMap,一次 O(V+E) 临时构建
        Map<Integer, List<Integer>> parentsOf = new HashMap<>();
        for (int p : childrenMap.keySet()) {
            for (int c : childrenMap.get(p)) {
                parentsOf.computeIfAbsent(c, k -> new ArrayList<>()).add(p);
            }
        }
        // 引理:卡住的任务必有"未执行的父任务",且那个父任务也必然卡着 —— 沿父指针走必绕回
        Map<Integer, Integer> visitedAt = new HashMap<>();
        List<Integer> path = new ArrayList<>();
        int cur = stuck.iterator().next();
        while (!visitedAt.containsKey(cur)) {
            visitedAt.put(cur, path.size());
            path.add(cur);
            for (int p : parentsOf.get(cur)) {                          // 跳过已执行的父亲
                if (stuck.contains(p)) {
                    cur = p;
                    break;
                }
            }
        }
        return new ArrayList<>(path.subList(visitedAt.get(cur), path.size()));
    }

    // ---------------- 私有工具 ----------------
    private void validateDependenciesRegistered() {
        // childrenMap 的 key 恰好是"被引用过的全部 parent"
        for (int parent : childrenMap.keySet()) {
            if (!priorityMap.containsKey(parent)) {
                throw new IllegalStateException("dependency on unknown task " + parent);
            }
        }
    }
}
```

</details>

**面试写作策略**:现场只写 `addTask` + `execute`(合并 executeInternal,约 35 行)——就是你 LC 模板换四个零件。三个 FU 方法是被追问时的弹药,你已写过验证过。

## 5. 复杂度(说出口的版本)

| 环节 | 成本 | 说明 |
|---|---|---|
| `addTask` | O(1 + deps) 均摊 | 建边 + 计数 |
| `execute` | **O(V log V + E)** | 每任务入/出堆各一次(log V);每条边一次 O(1) 递减 |
| 空间 | O(V + E) | 图 + 计数表 + 堆 |

英文一句话:*"Each task enters and leaves the heap once — that's V log V; each edge triggers one O(1) decrement — that's E. Total O(V log V + E), space O(V + E)."* 这已是该问题的最优——比较型 ready 选择本身有 log 下界(除非 priority 值域小,见 FU5 第三层)。

## 6. Follow-up 全集(带详细答案)

### FU1 — "能不能一次只执行一个?"(executeNext,已实现)

把 indegree 拷贝、ready 堆、已执行计数从局部变量升格为实例状态,首次调用惰性初始化;每次 poll 一个、递减 children、返回 id;堆空时——已执行数 < 总数 → **这一刻才发现环**,抛异常;否则返回 null 完结。两个要主动声明的语义:流开始后禁止 addTask(本实现抛异常;动态加任务需维护"已执行集合",新任务依赖若全部已执行则直接入堆——口述即可);环的发现被推迟到"无任务可跑"的时刻。

### FU2 — "同优先级想按提交顺序跑"(executeFifo,已实现)

addTask 时记 `insertionOrder` 递增序号,比较器第三键换成它。**只换比较器,骨架不动**——比较器抽成 `executeInternal(Comparator)` 参数的回报,顺手展示开闭原则。注意 `PriorityQueue` 本身不稳定,"稳定"必须靠显式序号键。

### FU3 — "检测到环,能告诉我环在哪吗?"(findCycle,已实现)

**深度校准**:检测环是必会的一行(`order.size() != n`);**报出环成员**是加分题——讲清思路即满分,现场要求手写的概率很低。思路 = 三块已经会的东西:Kahn、一条两句话的引理、链表找环(LC 141/142)。

**第一步:静默 Kahn,捞出淤积区。** 用普通 `ArrayDeque` 再跑一遍——只关心谁能跑,不关心顺序,这里用 PriorityQueue 是浪费。执行不了的任务留在 `stuck` 集合里。注意**淤积区 ≠ 环**:它 = 环成员 + 被环堵住的下游受害者。

**第二步:引理(算法地基)。** *卡住的任务必有至少一个"也卡住"的父任务。* 证明两句:卡住 = indegree 没归零 = 有父任务从没执行;而"从没执行"恰好就是"卡住"的定义。

**第三步:沿"卡住的父亲"走,走成链表找环。** 每个卡住的节点挑**任意一个**卡住的父亲当 next 指针(挑到就 `break`,每节点只留一个出口,图就退化成了链表)。引理保证这条路永远走得下去;集合有限,必撞上走过的节点——路径呈 ρ 形:

```text
deps: 1←{3}  2←{1}  3←{2}  4←{3}  5←{}     Kahn 只跑掉 5,stuck = {1,2,3,4}

沿"卡住的父亲"走:  4 ──▶ 3 ──▶ 2 ──▶ 1
                         ▲───────────┘    第 5 步撞回 3
path = [4,3,2,1],visitedAt[3] = 1  →  subList(1, 4) = [3,2,1] = 环
(受害者 4 是 ρ 的尾巴,被 subList 自然切掉)
```

`visitedAt` = HashSet 判重 + 顺手多记一个"首访下标"(为了最后切尾巴;嫌名字绕可改叫 `firstSeenIndex`,或用普通 Set 判重、绕回时 `path.indexOf` 现场找位置,多一次 O(n) 而已)。

**四个被追问的精确点**:

- 撞上的是"走过的某个节点",**不保证是出发点**——出发点若是受害者,永远不会被回访。所以循环条件必须是 `!visitedAt.containsKey(cur)` 而不是 `cur != start`(后者从受害者出发会死循环)。
- **最多绕一圈**:任何节点第一次被重访的瞬间就停,整条路 ≤ |stuck| 步,这一段 O(V)。"在环里一圈圈绕"的画面属于 Floyd 快慢指针(O(1) 空间那版)——这里花 O(n) 记忆买"一圈封顶"。
- **一次只报一条环**:掉进哪条报哪条(由父列表顺序决定;同图重复调用结果稳定)。这是工程上正确的合同——错误报告要的是一个具体反例,像编译器报第一个错;修掉再跑,自然看到下一条。
- 追问"我要全部的环":涉环任务全集 = **SCC**(Tarjan/Kosaraju,O(V+E));字面枚举所有环路是**指数级**输出(Johnson 算法领域),真实系统里几乎永远是错需求。说到 SCC 即可收住。

实现细节两笔:父方向信息只有这里需要——现场反转 childrenMap(O(V+E) 临时表,只在错误路径构建,不常驻);卡住节点 indegree > 0 必有父亲,`parentsOf.get(cur)` 不会 NPE。总复杂度 O(V + E)。

**口播(20 秒)**:*"Run Kahn's once — whatever never executes is 'stuck': the cycle plus everything downstream of it. Every stuck task must have at least one stuck dependency, otherwise its indegree would have reached zero. So walking from any stuck task to a stuck parent can never stop — but the set is finite, so I must revisit some node, and that closes a concrete cycle. Same idea as finding a cycle in a linked list."*

**记忆钩子**:跑一遍 Kahn 捞出淤积区;在淤积区里沿父指针走;走成链表找环。

### FU4 — "dependencies 有重复/自依赖怎么办?"

重复在计数架构下**自洽**:`[2,2]` 使 baseIndegree +2,childrenMap[2] 也对称存两个该 child,执行时递减两次恰好归零,输出正确(验证过)。所以去重(`new LinkedHashSet<>(deps)`)的理由是**语义干净**("依赖两次"无意义),不是正确性。**自依赖 = 最小的环**:它让自己的 indegree 至少为 1 且永不归零,环检测自然抓住,`findCycle` 返回 `[x]`(测试 3c)。把"我验证过重复是自洽的,但仍会去重并说明理由"讲出来,展示的是验证习惯。

### FU5 — "一百万个任务怎么再优化?"

**第一层你已经站在上面了**:本版天生就是 indegree 计数,没有任何 O(deg) 删除。作为"为什么不用剩余父列表删除"的证据,星形图实测(JDK 21):

| n(单任务依赖数) | List 按值删除 | indegree 计数 |
|---|---|---|
| 10,000 | 49 ms | 12 ms |
| 30,000 | 358 ms | 10 ms |
| 60,000 | **1,377 ms** | **23 ms**(≈60 倍) |

再往上两层(口述):② **数组化**——把 taskId 重映射到 0..V-1,`Map` 全换 `int[] indeg`、`int[][] children`(或 fastutil 原生集合),消灭装箱与哈希开销,这就回到了你 LC 官方解的形态;③ priority 值域小(如 1-10)时,max-heap 换**桶数组 + 当前最高非空桶指针**,出队 O(1),总复杂度 O(V + E)。

**什么时候才轮到 Set**:只有真需要动态"剩余父任务名单"(进度 UI 展示"还在等谁")时,`Map<Integer, Set<Integer>>` 才是对的工具;纯执行场景计数完胜。收束句:*"We never need to know **which** parents remain — only **how many**. Counting replaces membership."*

### FU6 — "多线程同时 addTask / execute?"

**深度校准**:24 条面经中零条要求写线程安全代码。此题若出现,几乎必是收尾的**口头题**,考并发意识而非 j.u.c. 熟练度。层1 说满 + 被追问时补层2 一句 = 满分;层3 是谈资,不会被要求写。

**必背两句(90% 场景到此为止)**:

> *"I'd make both methods synchronized — simplest correct answer, correctness first. If profiling later shows execute blocking writers too long, I'd shrink the critical section: copy the maps under the lock and run the sort outside on the copy — but I'd measure before optimizing."*

**层1 —— `synchronized` 两个方法**。为什么对:一把锁 → 同一时刻只有一个线程碰三张表 → 不存在交错。代价(实测,12 万任务星形图 + execute 热循环):execute 握锁跑完整个 O(V log V + E),且 `synchronized` 是**不公平锁**,刚放锁的热线程会连续插队 → 写者饥饿,单次 addTask 最大卡顿实测 **20.7 秒**。

**层2 —— 快照锁**:锁内只拷三张表,出锁后对副本跑 Kahn。动作早就会——就是 `executeInternal` 里的 `new HashMap<>(baseIndegree)`,只是圈进锁里。唯一的新知识点:**三张表都要拷,且 childrenMap 要深拷到内层 List**(addTask 会往内层 append)。⚠️ 修正旧版"本实现天然如此"的说法:单线程下只拷 indegree 确实够;线程安全必须全快照——只拷两张表的反面教材单线程完全等价,并发压测第 34 次 execute 即抛 `ConcurrentModificationException`。效果:最大卡顿 20719 ms → **4 ms**。语义主动声明:plan 是取快照那一瞬的一致视图,之后加入的任务不在其中。

**层3 —— actor(一句话谈资)**:单线程独占三张表,外界经 `BlockingQueue` 投递消息——线程封闭(thread confinement),无锁 by construction;与 Score Server 的单写线程同思想,也是 6.5 消耗式 Service 的天然宿主。

三层可运行代码、并发压测(4 生产者 × 5000 任务,每份 plan 校验无重复/依赖闭合/依赖在前)、反面教材与延迟对比都在 `SchedulerConcurrency.java`。支撑重构一句话:**把 Kahn 写成"吃三张表"的纯函数,三层共用——锁策略与算法正交**。

<details>
<summary><b>展开三层核心代码(完整实现与压测见 SchedulerConcurrency.java)</b></summary>

```java
// 层1:临界区 = 整个方法
public synchronized void addTask(int id, int prio, List<Integer> deps) { doAdd(...); }
public synchronized List<Integer> execute() {
    return runKahn(priorityMap, childrenMap, baseIndegree);   // 整个 Kahn 都握着锁
}

// 层2:临界区 = 三表拷贝;Kahn 在锁外对私有快照跑
public List<Integer> execute() {
    Map<Integer, Integer> prioSnap, indegSnap;
    Map<Integer, List<Integer>> childSnap = new HashMap<>();
    synchronized (lock) {
        prioSnap  = new HashMap<>(priorityMap);
        indegSnap = new HashMap<>(baseIndegree);
        for (Map.Entry<Integer, List<Integer>> e : childrenMap.entrySet())
            childSnap.put(e.getKey(), List.copyOf(e.getValue()));   // ★ 深到内层 List
    }
    return runKahn(prioSnap, childSnap, indegSnap);            // 锁外计算,不挡 addTask
}

// 层3:actor —— 三张表只被 worker 线程触碰,外界投递消息,异常经 future 传回
while (true) {
    Object msg = mailbox.take();
    if (msg == POISON) return;
    if (msg instanceof AddMsg m) {
        try { doAdd(m.id(), m.prio(), m.deps()); m.ack().complete(null); }
        catch (RuntimeException ex) { m.ack().completeExceptionally(ex); }
    } else if (msg instanceof PlanMsg m) {
        try { m.reply().complete(runKahn(priorityMap, childrenMap, baseIndegree)); }
        catch (RuntimeException ex) { m.reply().completeExceptionally(ex); }
    }
}
```

</details>

### FU7 — "任务有时长,k 个 worker 并行?"

事件驱动模拟(口述 + 伪代码):

```text
readyHeap: 按 priority 的 max-heap     eventHeap: 按完成时间的 min-heap
now = 0, freeWorkers = k

循环直到全部完成:
  while freeWorkers > 0 且 readyHeap 非空:
      task = readyHeap.poll(); eventHeap.push(now + duration[task]); freeWorkers--
  (finishTime, task) = eventHeap.poll(); now = finishTime; freeWorkers++
  完成 task,按 indegree 递减解锁 children 入 readyHeap
```

O(V log V + E)。**警告:不是 [LC 621](https://leetcode.com/problems/task-scheduler/)**——同名不同题(冷却间隔贪心),别被名字带偏。

### FU8 — "支持取消任务 removeTask(id)?"

先问语义:已执行的不可撤销(只能补偿);未执行的,它的 dependents 怎么办——**级联取消**(反向图 BFS 全部移除)或**拒绝删除**(有 dependents 就抛异常,像外键约束)。数据结构上要同时维护 childrenMap 和 baseIndegree 的一致性(删一个任务 = 删它的出边 + 给每个 child 的计数 -1 + 处理指向它的入边)。摆出两个约定让面试官选。

### FU9 — "进程崩了怎么恢复?"

执行日志(journal):每执行完一个任务追加落盘;重启重放——已执行集合恢复后按它重算 indegree,继续跑。前提任务幂等或有事务边界。一句带过,顺手连到 Score Server 的账本思想。

## 6.5 Long-Running(消耗式)版本 —— FU1 的进阶形态(已实现:TaskSchedulerService.java)

FU1 的拷贝式 executeNext 有个别扭之处:流一旦开始就禁止 addTask(快照定格了)。追问 *"What if it's a long-running service that keeps accepting tasks while running?"* 时,正确形态是**消耗式**——对象语义从"一次性规划器"变成"一台长期运转、边跑边收任务的调度机":

| 维度 | 拷贝式(TaskSchedulerLC.executeNext) | **消耗式(TaskSchedulerService)** |
|---|---|---|
| 执行的含义 | 在快照上推进游标,定义表不动 | **真消耗**:任务进 `executedSet` 墓碑,从活动表出清,indegree 永久递减 |
| 执行中 addTask | 禁止(快照不认后来者) | **天然合法**——这就是选它的理由 |
| 依赖已执行的任务 | n/a(所有任务开局注册完) | **视为已满足,不计入 indegree**(靠墓碑区分"执行过"和"从不存在") |
| 前向引用(依赖尚未注册的任务) | 允许,execute 时校验 | **不允许,addTask 当场报错**(服务语义下引用未来是错误) |
| 环检测 | 执行尽头清点人数 | **环不可构造**(见下) |
| 历史 | 一次性返回完整 order | `getExecutedOrder()` 单独查询(CQS) |

**四个设计要点**(面试口述的顺序):

1. **墓碑,不是物理删除**:执行完的任务必须留在 `executedSet` 里——否则后来的任务依赖它时,你无法区分"它执行过了(依赖满足)"和"它从不存在(该报错)"。删干净会把两种截然不同的情况混成一种。
2. **环不可构造(写测试时撞见的涌现性质)**:因为依赖只能指向"已存在"的任务,每条边都指向注册时间的**过去**;而环需要至少一条指向**未来**的边——那条边在 addTask 时就被 "unknown task" 拒掉了。**禁前向引用 ⟹ 图恒为 DAG ⟹ 运行时环检测成为防御性死代码**(保留它,万一将来放开前向引用即激活)。把这条因果链讲出来,是这版最亮的一句话。
3. **返回值遵循 CQS**:`executeNext()` 命令返回增量(刚执行的 taskId);历史用 `getExecutedOrder()` 查询单独拿。若面试官坚持一个调用两样都要:`record StepResult(int taskId, List<Integer> orderSoFar)`,orderSoFar 用 `Collections.unmodifiableList`(O(1) 活视图)并**说明**它是活的;每步防御性拷贝是 O(n)/步、全程 O(n²),别默默选它。还要问清"最新的 orderList"指**已执行历史**还是**剩余任务的预计顺序**——后者要跑规划器模拟,且会被未来的 addTask 作废。
4. **为什么没有批量 `execute()` / `getAllOrderedList()`**:活动表随执行不断出清,批量排序只能看到"剩下的"——那个名字在撒谎,所以干脆不提供。诚实的拆法是把完整顺序**按时间切成两半**:过去 = `getExecutedOrder()`(只增历史,天然无副作用);未来 = `getPlannedOrder()`(对剩余活动表**拷贝后**跑 Kahn——"看一眼计划"不能顺手把任务消耗掉,消耗式想回答未来照样得 copy。可见 copy 不是拷贝版的怪癖,而是"查询与变更共存"的通用代价)。两者拼接 ≈ 拷贝版 `execute()` 的完整输出(无中途 addTask 时严格相等)。口播:*"History is recorded; the future is recomputed."*

<details>
<summary><b>展开消耗式核心代码(完整实现与 6 组测试见 TaskSchedulerService.java)</b></summary>

```java
public class TaskSchedulerService {

    private final Map<Integer, Integer> priorityMap = new HashMap<>();       // 仅活动任务
    private final Map<Integer, List<Integer>> childrenMap = new HashMap<>(); // 仅活动边
    private final Map<Integer, Integer> indegree = new HashMap<>();          // 实时入度
    private final PriorityQueue<Integer> ready = new PriorityQueue<>((a, b) -> {
        int cmp = Integer.compare(priorityMap.get(b), priorityMap.get(a));
        return cmp != 0 ? cmp : Integer.compare(a, b);
    });
    private final Set<Integer> executedSet = new HashSet<>();                // 墓碑
    private final List<Integer> executedOrder = new ArrayList<>();           // 历史

    public void addTask(int taskId, int priority, List<Integer> dependencies) {
        if (priorityMap.containsKey(taskId) || executedSet.contains(taskId)) {
            throw new IllegalArgumentException("duplicate taskId: " + taskId);  // 生命周期唯一
        }
        int indeg = 0;
        for (int p : dependencies) {
            if (executedSet.contains(p)) {
                continue;                                  // ★ 依赖已执行 -> 视为满足
            }
            if (!priorityMap.containsKey(p)) {
                throw new IllegalArgumentException("dependency on unknown task " + p);
            }
            childrenMap.computeIfAbsent(p, k -> new ArrayList<>()).add(taskId);
            indeg++;
        }
        priorityMap.put(taskId, priority);
        indegree.put(taskId, indeg);
        if (indeg == 0) {
            ready.add(taskId);                             // 来了就能跑的直接入堆
        }
    }

    /** 命令:执行一个任务返回其 id;当前无任务返回 null(之后仍可继续喂) */
    public Integer executeNext() {
        if (ready.isEmpty()) {
            if (!priorityMap.isEmpty()) {                  // 防御性死代码:现规则下环不可构造
                throw new IllegalStateException("cycle detected among remaining tasks");
            }
            return null;
        }
        int task = ready.poll();
        executedSet.add(task);                             // 消耗:进墓碑
        executedOrder.add(task);
        List<Integer> children = childrenMap.remove(task); // 出清活动边
        priorityMap.remove(task);                          // poll 之后再删,比较器已用不到它
        indegree.remove(task);
        if (children != null) {
            for (int v : children) {
                if (indegree.merge(v, -1, Integer::sum) == 0) {
                    ready.add(v);
                }
            }
        }
        return task;
    }

    /** 查询:执行历史(不可变快照;高频调用可换 unmodifiableList 活视图) */
    public List<Integer> getExecutedOrder() {
        return List.copyOf(executedOrder);
    }
}
```

</details>

关键测试(用例 2):执行掉 2 之后动态 `addTask(5, 99, [2])`——依赖已执行的任务、立刻 ready、p99 直接插队下一个执行;全部跑完后还能继续喂任务再跑。这就是消耗式存在的意义。

**命名备注(CQS)**:批量版 `execute()` 其实是纯查询,生产代码里更好的名字是 `getExecutionOrder()`,把 "execute" 留给真正改状态的流式方法——面试照题面的 API 写,但把这个观察说出来值十秒钟的 Design 分。

## 7. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| `PriorityQueue` 默认 min-heap | 低优先级先跑 | 比较器反转 `Integer.compare(b优先级, a优先级)` |
| 比较器写 `b - a` | 极端值溢出 | 永远 `Integer.compare` |
| **无依赖任务忘了给 indegree 条目** | 播种循环遍历 indeg 表时漏掉它,永不执行 | `baseIndegree.putIfAbsent(taskId, 0)` —— 本版第一大坑 |
| **套 BFS 层级循环** | 新解锁高优先级任务被压一层 | 单层 while;反例 [1,3,2] 当场默写 |
| 直接改 baseIndegree 执行 | 第二次 execute 结果错 | 先 `new HashMap<>(baseIndegree)` 拷贝(测试:连续调两次) |
| `childrenMap.get(u)` 为 null | 叶子任务 NPE | `getOrDefault(u, emptyList())` |
| 依赖未注册任务 | 永不 ready,表现像环 | execute 前用 `childrenMap.keySet()` 校验,报 "unknown task" |
| priority 可变 | 入堆后改 = 堆序损坏 | 本设计不可变;要支持 setPriority → remove + re-add |
| 忘记环检测 | 有环时静默输出部分结果 | `order.size() != n` 必查 |

## 8. 30 分钟考场节奏 + 互动台词

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–3 min | 澄清(第 2 节清单) | "Let me confirm a few assumptions before I code." |
| 3–5 min | 方案 + 复杂度 + **要 buy-in** | "Topological sort — my Course Schedule II template — with the ready queue swapped for a max-heap. O(V log V + E). Sound good?" |
| 5–18 min | 写 addTask + execute,持续 narrate | "Indegree counts how many dependencies remain — zero means runnable." |
| 18–23 min | 手走示例 [2,1,3,4] + 边界 | "Let me walk through the example before edge cases." |
| 23–26 min | **主动**报边界 | "What would break this: null deps, duplicate id, unknown dependency, a cycle, a no-dependency task missing its indegree entry, calling execute twice." |
| 26–30 min | Follow-up 对话(第 6 节弹药) | "For millions of tasks I'd remap ids and go primitive arrays — same shape as the canonical LC solution." |

**接提示**:面试官任何插话 → *"Oh, that's a good point — let me incorporate that."* **冷面孔轮**:对方不接话就自己稳住节奏,清晰的独白也是协作展示。

## 9. 全景闭环 —— 收尾叙事(⚠️ 只在最后三分钟或被问 production 时用,不是开场白)

三个方法不是三道孤立的 API,而是调度系统完整生命周期的三个角色:

```text
        ┌──────────────────────────────────────────────┐
        │                                              ▼
   addTask(建模/写)  →  getExecutionOrder(规划/读)  →  executeNext(执行/命令)
        ▲                    随时可问、反复可问              让现实前进一格
        │                                              │
        └────────── onTaskCompleted / onTaskFailed ◄───┘
                    (反馈弧 —— 补上它,环才真正闭合)
```

- **addTask = 建模**:把世界的事实喂进模型(任务、优先级、依赖)
- **execute = 规划**:对当前模型的**纯查询**(所以可重复调用、所以按 CQS 更该叫 `getExecutionOrder`)——规划是可再生的视图,不是一次性产物,每走一步都能基于最新状态重问
- **executeNext = 执行**:真正改状态的命令;拷贝式(FU1)是快照游标,消耗式(6.5)是活模型
- **反馈弧 = 缺的最后一段**:现有实现假设任务必然成功;补 `onTaskCompleted/onTaskFailed` 把结果写回模型——失败时重试或级联取消 dependents(接 FU8),循环才完整

这四个部件与 Airflow/CI 系统一一对应:DAG 定义、scheduler、executor、task-state 回写。**使用时机**:① 代码写完的收尾陈词;② *"How would this run in production?"* / *"Anything you'd improve?"*;③ VP 轮聊系统理解。开场前 18 分钟属于代码,别在那时谈架构。

英文版(收尾时说):

> *"Stepping back — these three methods are the full lifecycle of a scheduler: addTask builds the model, the order query is a recomputable read model, executeNext advances reality one step. The piece I'd add for production is the feedback arc — completion and failure callbacks writing results back into the model, with retries or cascading cancellation. That's the same shape as Airflow or any CI system: DAG definition, scheduler, executor, state write-back."*

## 9.1 30 秒总结陈词(背诵版)

> "It's Kahn's topological sort — the standard Course Schedule II template — with the ready queue replaced by a max-heap. Indegree counting is the whole trick: a task enters the heap only when its count hits zero, so admission guarantees dependency-safety and the heap only orders safe tasks. That's also why there's no layered loop — a newly-unlocked high-priority task competes immediately. O(V log V + E) time, O(V + E) space. Cycles surface when the executed count falls short, and I can extract the actual cycle by inverting the graph and walking stuck tasks' unfinished parents."