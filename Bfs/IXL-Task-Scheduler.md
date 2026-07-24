# IXL Task Scheduler — Java 版

> 还原自面经 #11 / #12 / #15(VO 高频,出现 3 次)。代码已在 JDK 21 编译并通过全部测试。

## 题面还原

实现一个任务调度系统,支持两个操作:

- `addTask(int taskId, int priority, List<Integer> dependencies)` —— 注册任务:taskId 唯一;priority 越大优先级越高;dependencies 是必须先完成的任务列表。
- `execute() -> List<Integer>` —— 按合法顺序执行所有任务并返回执行序列。规则:任务必须在其所有依赖完成后执行;**在所有"当前可执行"(依赖已全部满足)的任务中,priority 最高的先执行**。

## 与 LC 210 (Course Schedule II) 的差异速记

| 维度 | LC 210 | IXL Task Scheduler |
|---|---|---|
| 结果要求 | 任意合法拓扑序 | ready 集合内按 priority 贪心 |
| ready 数据结构 | FIFO `Queue` | **max-heap**(`PriorityQueue` + 自定义比较器) |
| 复杂度 | O(V+E) | O(V log V + E) |
| 接口 | 一次性数组输入 | 增量式 OOD,自己设计类与存储 |
| 有环 | 返回空数组 | 自定义行为(本实现抛异常) |
| 脏输入 | 不存在 | 重复 id、依赖未注册任务、重复 execute 等都要处理 |

**最容易翻车的语义**:priority 是 *ready 集合内的局部贪心*,不是全局排序——全场最高优先级的任务如果依赖未满足照样最后跑(见测试用例 1)。写之前先跟面试官确认这个语义。

## Java 实现(已验证)

```java
import java.util.*;

public class TaskScheduler {

    private final Map<Integer, Integer> priority = new HashMap<>();        // taskId -> priority
    private final Map<Integer, Set<Integer>> deps = new HashMap<>();       // taskId -> 它依赖的前置任务
    private final Map<Integer, Set<Integer>> dependents = new HashMap<>(); // 前置任务 -> 等它解锁的任务

    public void addTask(int taskId, int prio, List<Integer> dependencies) {
        if (priority.containsKey(taskId)) {
            throw new IllegalArgumentException("duplicate taskId: " + taskId); // 澄清点①:重复 id
        }
        priority.put(taskId, prio);
        deps.putIfAbsent(taskId, new HashSet<>());
        for (int d : dependencies) {
            deps.get(taskId).add(d);                                  // 澄清点②:允许依赖尚未 add 的任务?
            dependents.computeIfAbsent(d, k -> new HashSet<>()).add(taskId); // 此实现允许前向引用,execute 时校验
        }
    }

    public List<Integer> execute() {
        // 澄清点②的兜底:依赖里出现从未注册的任务 -> 永远无法满足,直接报错
        for (Map.Entry<Integer, Set<Integer>> e : deps.entrySet()) {
            for (int d : e.getValue()) {
                if (!priority.containsKey(d)) {
                    throw new IllegalStateException("task " + e.getKey() + " depends on unknown task " + d);
                }
            }
        }

        Map<Integer, Integer> indegree = new HashMap<>();
        for (int t : priority.keySet()) {
            indegree.put(t, deps.get(t).size());
        }

        // ★ 与 LC210 的唯一算法差异:ready 集合不用 Queue,用 max-heap
        //   priority 大的先出堆;同 priority 按 taskId 升序(澄清点③,保证确定性)
        PriorityQueue<Integer> ready = new PriorityQueue<>((a, b) -> {
            int cmp = Integer.compare(priority.get(b), priority.get(a));
            return cmp != 0 ? cmp : Integer.compare(a, b);
        });
        for (Map.Entry<Integer, Integer> e : indegree.entrySet()) {
            if (e.getValue() == 0) ready.offer(e.getKey());
        }

        List<Integer> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            int t = ready.poll();
            order.add(t);
            for (int nxt : dependents.getOrDefault(t, Collections.emptySet())) {
                indegree.merge(nxt, -1, Integer::sum);
                if (indegree.get(nxt) == 0) ready.offer(nxt);
            }
        }

        if (order.size() != priority.size()) {
            throw new IllegalStateException("cycle detected: no valid execution order"); // 澄清点④:有环行为
        }
        return order;
    }
}
```

### LC 210 对照版(普通 Queue,任意合法序)

```java
public static int[] courseScheduleII(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] p : prerequisites) {   // p = [a, b] 表示先修 b 再上 a
        graph.get(p[1]).add(p[0]);
        indegree[p[0]]++;
    }
    Deque<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) queue.offer(i);
    int[] order = new int[numCourses];
    int idx = 0;
    while (!queue.isEmpty()) {
        int c = queue.poll();
        order[idx++] = c;
        for (int nxt : graph.get(c)) {
            if (--indegree[nxt] == 0) queue.offer(nxt);
        }
    }
    return idx == numCourses ? order : new int[0];
}
```

## 测试用例(全部通过)

1. **priority 是局部贪心**:`1(p5,无依赖)、2(p10,无依赖)、3(p1,依赖2)、4(p20,依赖1和3)` → 执行序 `[2, 1, 3, 4]`。4 优先级全场最高但只能最后;2 完成解锁 3 后,ready={1,3},1 更高所以先 1。
2. **环检测**:5↔6 互相依赖 → 抛 `IllegalStateException("cycle detected...")`。
3. **依赖不存在的任务**:task 7 依赖 99(从未注册)→ 报错,注意它的表现和环一样(永远不 ready),要能解释。
4. **空 scheduler** → 返回空列表;**全无依赖** → 纯按 priority 降序。
5. **重复 taskId** → `addTask` 抛 `IllegalArgumentException`。

## Java 面试细节提醒

- `PriorityQueue` 默认是 **min-heap**,必须传比较器反转;比较器里用 `Integer.compare` 避免减法溢出(`b - a` 在极端值会溢出,说出这点是加分项)。
- 比较器加 taskId 作 tie-break,保证输出确定性——面试官问"同优先级怎么办"时你已经有答案。
- `indegree.merge(nxt, -1, Integer::sum)` 比 get+put 干净;`getOrDefault` 防 NPE。
- 追问弹药库(主动说):重复 execute 的语义(幂等?清空?)、execute 后还能 addTask 吗、并发调用要不要加锁。

## 练法

白板/HackerRank 环境限时 25 分钟自己写一遍(含环检测),写完对照本文查漏——重点检查你有没有主动处理澄清点①–④,那是这题和裸拓扑排序拉开差距的地方。
