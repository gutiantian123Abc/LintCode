# IXL Interview — Task Scheduler(Java)

![Topic](https://img.shields.io/badge/topic-Topological%20Sort%20%2B%20Max--Heap-00695c) ![Frequency](https://img.shields.io/badge/VO%20%E5%87%BA%E7%8E%B0-3%20%E6%AC%A1-orange) ![Tests](https://img.shields.io/badge/tests-6%20passed-brightgreen)

> **一句话**:LC 210 拓扑排序的 OOD 变体 —— 把 Kahn 的 `Queue` 换成 `PriorityQueue`(max-heap),ready 集合内按 priority 贪心。
> 还原自面经 #11 / #12 / #15;代码 JDK 21 编译,6 组测试通过。

---

## 题面还原

实现一个任务调度系统,支持两个操作:

| API | 说明 |
|---|---|
| `addTask(int taskId, int priority, List<Integer> dependencies)` | 注册任务。`taskId` 唯一;`priority` 越大优先级越高;`dependencies` 为必须先完成的任务列表 |
| `execute() → List<Integer>` | 按合法顺序执行全部任务并返回执行序列 |

**规则**:任务必须在其所有依赖完成后执行;在所有*当前可执行*(依赖已全部满足)的任务中,**priority 最高的先执行**。

## 与 LC 210 · Course Schedule II 的差异

| 维度 | [LC 210](https://leetcode.com/problems/course-schedule-ii/) | IXL Task Scheduler |
|---|---|---|
| 结果要求 | 任意合法拓扑序 | ready 集合内按 priority 贪心 |
| ready 数据结构 | FIFO `Queue` | **max-heap**(`PriorityQueue` + 比较器) |
| 复杂度 | O(V+E) | O(V log V + E) |
| 接口 | 一次性数组输入 | 增量式 OOD,自己设计类与存储 |
| 有环 | 返回空数组 | 自定义行为(本实现抛异常) |
| 脏输入 | 不存在 | 重复 id / 依赖未注册任务 / 重复 execute |

## 从我的 CS2 模板迁移过来,只改了三处

1. **`LinkedList` → `PriorityQueue`**(算法必须)。比较器用 `Integer.compare` 而不是 `b - a`(减法极端值会溢出);同 priority 按 taskId 升序 tie-break。
2. **去掉 `int size = queue.size()` 层级循环**(⚠️ 照搬模板会做错)。CS2 里分层无所谓,这题分层会改变语义:

   ```text
   ready = {A(p5), C(p1)} → 弹出 A,解锁 B(p100)
   层级循环:先弹完本层的 C        → A, C, B ✗
   正确语义:B 立刻参与竞争,先于 C → A, B, C ✓
   ```

   测试用例 6 专门卡这个场景。
3. **execute 前拷贝一份 `remainingParents` 再删**(OOD 适配)。CS2 直接删 `parentsMap` 是一次性调用没问题;类的 `execute()` 不该破坏原图,否则第二次调用结果就错了(用例 1 有连续调用断言)。

## 参考实现

先自己限时 25 分钟写一遍(含环检测),再展开对照 👇

<details>
<summary><b>展开完整代码(含 6 组测试)</b></summary>

```java
import java.util.*;

/**
 * IXL Task Scheduler — parentsMap / childrenMap 命名、显式 containsKey 初始化、
 * "从 parents 列表里删元素、删空进队"代替 indegree 计数、显式 null 检查。
 */
public class TaskScheduler {

    private final Map<Integer, Integer> priorityMap = new HashMap<>();
    private final Map<Integer, List<Integer>> parentsMap = new HashMap<>();   // task -> 它依赖的父任务
    private final Map<Integer, List<Integer>> childrenMap = new HashMap<>();  // task -> 依赖它的子任务

    public void addTask(int taskId, int priority, List<Integer> dependencies) {
        if (priorityMap.containsKey(taskId)) {
            throw new IllegalArgumentException("duplicate taskId: " + taskId);
        }
        priorityMap.put(taskId, priority);

        // 假设 dependencies 内部无重复(若可能重复需先去重,否则后面 indexOf/remove 会出错——可当面试澄清点)
        for (int parent : dependencies) {
            if (!parentsMap.containsKey(taskId)) {
                parentsMap.put(taskId, new ArrayList<>());
            }
            parentsMap.get(taskId).add(parent);

            if (!childrenMap.containsKey(parent)) {
                childrenMap.put(parent, new ArrayList<>());
            }
            childrenMap.get(parent).add(taskId);
        }
    }

    public List<Integer> execute() {
        // 校验:依赖了从未注册的任务(它永远不会 ready,表现和环一样,提前报错更清晰)
        for (int task : parentsMap.keySet()) {
            for (int parent : parentsMap.get(task)) {
                if (!priorityMap.containsKey(parent)) {
                    throw new IllegalStateException("task " + task + " depends on unknown task " + parent);
                }
            }
        }

        // 拷贝一份再删:execute() 不该破坏原图(否则第二次调用结果就错了)
        Map<Integer, List<Integer>> remainingParents = new HashMap<>();
        for (int task : parentsMap.keySet()) {
            remainingParents.put(task, new ArrayList<>(parentsMap.get(task)));
        }

        // ★ 改动1:LinkedList 换成 PriorityQueue(max-heap)
        Queue<Integer> queue = new PriorityQueue<>((a, b) -> {
            int cmp = Integer.compare(priorityMap.get(b), priorityMap.get(a));
            return cmp != 0 ? cmp : Integer.compare(a, b);
        });

        // 和 CS2 一样:不在 parentsMap 里(或列表为空)= 没有前置,直接进队
        for (int task : priorityMap.keySet()) {
            if (!remainingParents.containsKey(task) || remainingParents.get(task).size() == 0) {
                queue.add(task);
            }
        }

        List<Integer> order = new ArrayList<>();
        // ★ 改动2:单层 while,不要 size 层级循环
        while (!queue.isEmpty()) {
            int task = queue.poll();
            order.add(task);

            List<Integer> children = childrenMap.get(task);
            if (children != null && children.size() != 0) {
                for (int child : children) {
                    int parentIndex = remainingParents.get(child).indexOf(task);
                    remainingParents.get(child).remove(parentIndex);
                    if (remainingParents.get(child).size() == 0) {
                        queue.add(child);
                    }
                }
            }
        }

        // CS2 的习惯是返回空数组;OOD 版建议抛异常把"有环"显式暴露(可与面试官确认)
        if (order.size() != priorityMap.size()) {
            throw new IllegalStateException("cycle detected: no valid execution order");
        }
        return order;
    }

    // ---------------- 测试 ----------------
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // 用例1:priority 只在"同时 ready"的任务之间起作用;execute 可重复调用
        TaskScheduler s = new TaskScheduler();
        s.addTask(1, 5, List.of());
        s.addTask(2, 10, List.of());
        s.addTask(3, 1, List.of(2));
        s.addTask(4, 20, List.of(1, 3));
        check(s.execute().equals(List.of(2, 1, 3, 4)), "case1 failed");
        check(s.execute().equals(List.of(2, 1, 3, 4)), "case1 repeat failed");

        // 用例2:环检测
        TaskScheduler s2 = new TaskScheduler();
        s2.addTask(5, 1, List.of(6));
        s2.addTask(6, 1, List.of(5));
        try { s2.execute(); check(false, "case2 should throw"); }
        catch (IllegalStateException e) { check(e.getMessage().contains("cycle"), "case2 wrong error"); }

        // 用例3:依赖了不存在的任务
        TaskScheduler s3 = new TaskScheduler();
        s3.addTask(7, 1, List.of(99));
        try { s3.execute(); check(false, "case3 should throw"); }
        catch (IllegalStateException e) { check(e.getMessage().contains("unknown"), "case3 wrong error"); }

        // 用例4:空 scheduler / 无依赖全按 priority
        check(new TaskScheduler().execute().isEmpty(), "case4a failed");
        TaskScheduler s4 = new TaskScheduler();
        s4.addTask(1, 1, List.of()); s4.addTask(2, 3, List.of()); s4.addTask(3, 2, List.of());
        check(s4.execute().equals(List.of(2, 3, 1)), "case4b failed");

        // 用例5:重复 taskId
        TaskScheduler s5 = new TaskScheduler();
        s5.addTask(1, 1, List.of());
        try { s5.addTask(1, 2, List.of()); check(false, "case5 should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }

        // 用例6:层级循环陷阱 —— 新解锁的高优先级任务必须立刻参与竞争
        TaskScheduler s6 = new TaskScheduler();
        s6.addTask(1, 5, List.of());
        s6.addTask(2, 1, List.of());
        s6.addTask(3, 100, List.of(1));
        check(s6.execute().equals(List.of(1, 3, 2)), "case6 failed");

        System.out.println("all tests passed");
    }
}
```

</details>

## 测试用例一览

| # | 场景 | 输入(id, priority, deps) | 期望 |
|---|---|---|---|
| 1 | priority 是 ready 集合内的**局部**贪心 | 1(5,[]) 2(10,[]) 3(1,[2]) 4(20,[1,3]) | `[2, 1, 3, 4]`,重复调用结果一致 |
| 2 | 环检测 | 5(1,[6]) 6(1,[5]) | 抛 `cycle detected` |
| 3 | 依赖未注册任务 | 7(1,[99]) | 抛 `unknown task` |
| 4 | 空 / 全无依赖 | —— / 1(1) 2(3) 3(2) | `[]` / `[2, 3, 1]` |
| 5 | 重复 taskId | addTask(1,…) ×2 | 抛 `IllegalArgumentException` |
| 6 | **层级循环陷阱** | 1(5,[]) 2(1,[]) 3(100,[1]) | `[1, 3, 2]`(层级循环会错出 `[1, 2, 3]`) |

## 面试当场要确认的澄清点

- [ ] 重复 `taskId` 怎么处理?(本实现:抛异常)
- [ ] 允许依赖"还没 add 的任务"吗?(本实现:允许前向引用,execute 时校验)
- [ ] `dependencies` 里会有重复元素吗?(有则需去重,否则 `indexOf/remove` 出错)
- [ ] 同 priority 怎么排?(本实现:taskId 升序,保证确定性)
- [ ] 有环的行为:抛异常还是返回空?(LC 习惯返回空,OOD 建议抛异常)
- [ ] `execute()` 重复调用的语义?执行后还能继续 `addTask` 吗?

## Java 细节(说出来是加分项)

- `PriorityQueue` 默认 **min-heap**,必须传反转比较器
- 比较器用 `Integer.compare`,不用 `b - a`(溢出风险)
- 复杂度:`execute` O(V log V + E);`indexOf/remove` 是 O(deg) 的小额外开销,面试官追问可提"依赖多时换 `indegree` 计数或 `Set`"
