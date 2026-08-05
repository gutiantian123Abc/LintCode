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

静默跑一遍 Kahn(普通队列即可),剩下的 `stuck` 集合 = 环上或被环卡住的任务。**父方向信息只有这里需要**:把 childrenMap 反转一次(O(V+E) 临时表)。提取环的引理:卡住的任务必有至少一个"未执行的父任务"(否则它早就 indegree 归零了),而那个父任务也必然卡着——所以**沿"卡着的父亲"指针走,永远走得下去,集合有限必绕回**;`visitedAt` 记首访位置,绕回处截取即得环。注意走的时候跳过已执行的父亲(反转表里有它们)。O(V + E)。

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

最简正确:两个方法 `synchronized`(粗粒度,正确性优先)。进阶:execute 开头锁内拷贝 baseIndegree 快照(本实现天然如此),之后无锁计算——读写分离;再进阶:单线程 actor 收编所有变更(与 Score Server 同一思想)。先说 "correctness first, then measure"。

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

## 9. 30 秒总结陈词(背诵版)

> "It's Kahn's topological sort — the standard Course Schedule II template — with the ready queue replaced by a max-heap. Indegree counting is the whole trick: a task enters the heap only when its count hits zero, so admission guarantees dependency-safety and the heap only orders safe tasks. That's also why there's no layered loop — a newly-unlocked high-priority task competes immediately. O(V log V + E) time, O(V + E) space. Cycles surface when the executed count falls short, and I can extract the actual cycle by inverting the graph and walking stuck tasks' unfinished parents."
