# IXL Interview — Student Leaderboard(OOD)

![Topic](https://img.shields.io/badge/topic-OOD%20%2B%20Sorting%20%2B%20Integer%20Math-00695c) ![Frequency](https://img.shields.io/badge/%E7%94%B5%E9%9D%A2%E5%87%BA%E7%8E%B0-2%20%E6%AC%A1-orange) ![Round](https://img.shields.io/badge/45min%20phone%20screen-%E9%AB%98%E6%A6%82%E7%8E%87%E9%A2%98-red) ![Tests](https://img.shields.io/badge/tests-9%20groups%20%2B%20500%20fuzz%20passed-brightgreen)

> **一句话**:算法不难的纯 OOD 题,拼的是**类设计、边界语义、整数运算的干净度和对话质量**。
> 还原自面经 #6 / #16 —— #16 的 phone screen 就是它:"45min,题目不难是一道 OOD,很快做出来和 interviewer 聊了会儿天,30 分钟结束"。
> 相关(不同)题:[LC 1244. Design A Leaderboard](https://leetcode.com/problems/design-a-leaderboard/)(会员)。

---

## 题面还原

设计一个提交排行榜系统,实现两个方法:

| API | 说明 |
|---|---|
| `submit(int studentId, boolean correct, int time)` | 记录一次提交:`studentId` 学生唯一标识;`correct` 本次提交是否正确;`time` 本次解题用时 |
| `getTopStudents() → List<Integer>` | 返回**至多 3 个**学生 id |

`getTopStudents` 的入选与排序规则:

1. 该学生**提交过至少 4 道题**
2. 正确率**严格大于 75%**
3. 入选者按**平均每题用时升序**排列(越快排越前)

## 这题考什么(45 分钟电面的评分点)

| 评分点 | 对应做法 |
|---|---|
| 类设计 | 每个学生的统计封装成 `StudentStats` 小类,**不要开三个平行 Map**(`attemptsMap`/`correctMap`/`timeMap` 是扣分写法:不变量散落、更新易漏) |
| 数值正确性 | 正确率和平均用时**全用整数运算**,一个浮点数都不出现(见下) |
| 边界语义 | 恰好 75% 排除、恰好 4 次入选、答错的提交也计入用时、"至多 3 个" |
| 确定性 | 平均用时相同怎么排?主动定 tie-break(id 升序)并说出来 |
| 复杂度对话 | `submit` O(1);`getTopStudents` O(n log n),聊 trade-off(见 follow-up) |

### 两个整数运算技巧(这题最出彩的地方)

```text
正确率 > 75%:   correct / attempts > 3/4     →  correct * 4 > attempts * 3
平均用时比较:   totalA/attA < totalB/attB    →  totalA * attB < totalB * attA(交叉相乘)
```

一个 `double` 都不用:没有精度问题、没有除零问题(`attempts >= 4` 已保证非零)。写的时候把这两行注释直接写在代码里,面试官会注意到。

## 参考实现

先自己限时 30 分钟写一遍(含测试),再展开对照 👇

<details>
<summary><b>展开完整代码(含 8 组测试)</b></summary>

```java
import java.util.*;

public class StudentLeaderboard {

    /** 每个学生的统计封装在一个小类里(OOD 信号:不变量集中、避免三个平行 Map) */
    private static class StudentStats {
        int attempts;
        int correctCount;
        long totalTime;   // 用 long:大量提交时 int 会溢出
    }

    private final Map<Integer, StudentStats> statsMap = new HashMap<>();

    public void submit(int studentId, boolean correct, int time) {
        if (time < 0) {
            throw new IllegalArgumentException("time must be non-negative: " + time);
        }
        if (!statsMap.containsKey(studentId)) {
            statsMap.put(studentId, new StudentStats());
        }
        StudentStats stats = statsMap.get(studentId);
        stats.attempts++;
        if (correct) {
            stats.correctCount++;
        }
        stats.totalTime += time;   // 澄清点:答错的提交也计入 attempts 和总用时
    }

    public List<Integer> getTopStudents() {
        List<Integer> qualified = new ArrayList<>();
        for (Map.Entry<Integer, StudentStats> entry : statsMap.entrySet()) {
            StudentStats stats = entry.getValue();
            // 正确率严格 > 75%:整数乘法代替浮点 —— correct/attempts > 3/4 等价于 correct*4 > attempts*3
            if (stats.attempts >= 4 && (long) stats.correctCount * 4 > (long) stats.attempts * 3) {
                qualified.add(entry.getKey());
            }
        }

        // 平均用时升序:totalA/attA < totalB/attB 用交叉相乘比较,完全避开浮点误差
        qualified.sort((a, b) -> {
            StudentStats sa = statsMap.get(a);
            StudentStats sb = statsMap.get(b);
            long left = sa.totalTime * sb.attempts;
            long right = sb.totalTime * sa.attempts;
            if (left != right) {
                return Long.compare(left, right);
            }
            return Integer.compare(a, b);   // 澄清点:平均用时相同按 id 升序,保证输出确定性
        });

        return new ArrayList<>(qualified.subList(0, Math.min(3, qualified.size())));
    }

    /** Follow-up 版本:只要前 3 → 读时构建大小为 3 的 bounded heap,O(n log 3) ≈ O(n),免全排序。
     *  注意:堆是 getTop 调用时临时建的,不能像 LC 703 那样常驻 —— submit 会改变学生的
     *  平均用时甚至入选资格,而 PriorityQueue 不支持原地更新(remove(Object) 是 O(n))。
     *  真要常驻有序结构应选 TreeSet(remove → 更新 → add)。 */
    public List<Integer> getTopStudentsHeap() {
        // 堆顶 = 当前入选者里最慢的(同速则 id 大的),超过 3 个就把堆顶踢掉
        PriorityQueue<Integer> slowestOnTop = new PriorityQueue<>((a, b) -> {
            StudentStats sa = statsMap.get(a);
            StudentStats sb = statsMap.get(b);
            long left = sa.totalTime * sb.attempts;
            long right = sb.totalTime * sa.attempts;
            if (left != right) {
                return Long.compare(right, left);   // 慢的排堆顶(先被踢)
            }
            return Integer.compare(b, a);           // 同速:大 id 先被踢,保留小 id
        });
        for (Map.Entry<Integer, StudentStats> entry : statsMap.entrySet()) {
            StudentStats stats = entry.getValue();
            if (stats.attempts >= 4 && (long) stats.correctCount * 4 > (long) stats.attempts * 3) {
                slowestOnTop.offer(entry.getKey());
                if (slowestOnTop.size() > 3) {
                    slowestOnTop.poll();
                }
            }
        }
        // 堆里剩最快的 <= 3 个;弹出顺序是慢 -> 快,从头部插入还原成升序
        LinkedList<Integer> result = new LinkedList<>();
        while (!slowestOnTop.isEmpty()) {
            result.addFirst(slowestOnTop.poll());
        }
        return new ArrayList<>(result);
    }

    // ---------------- 测试 ----------------
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static void submitN(StudentLeaderboard lb, int id, int n, boolean correct, int time) {
        for (int i = 0; i < n; i++) {
            lb.submit(id, correct, time);
        }
    }

    public static void main(String[] args) {
        // 用例1:基础排名(平均用时升序)+ 不足 4 次被过滤 + 至多返回 3 个
        StudentLeaderboard lb = new StudentLeaderboard();
        submitN(lb, 1, 4, true, 10);   // avg 10
        submitN(lb, 2, 4, true, 5);    // avg 5
        submitN(lb, 3, 4, true, 20);   // avg 20
        check(lb.getTopStudents().equals(List.of(2, 1, 3)), "case1 failed");

        submitN(lb, 4, 3, true, 1);    // 只有 3 次提交,100% 正确也不够格
        check(lb.getTopStudents().equals(List.of(2, 1, 3)), "case2 failed");

        submitN(lb, 5, 4, true, 1);    // avg 1,第 4 个合格者加入
        check(lb.getTopStudents().equals(List.of(5, 2, 1)), "case3 failed: 只取前3");

        // 用例4:正确率边界 —— 恰好 75% 不算,必须严格大于
        StudentLeaderboard lb2 = new StudentLeaderboard();
        lb2.submit(1, true, 10); lb2.submit(1, true, 10);
        lb2.submit(1, true, 10); lb2.submit(1, false, 10);      // 3/4 = 75% -> 排除
        submitN(lb2, 2, 4, true, 10); lb2.submit(2, false, 10); // 4/5 = 80% -> 合格
        check(lb2.getTopStudents().equals(List.of(2)), "case4 failed: 75% 边界");

        // 用例5:平均用时相同 -> 按 id 升序
        StudentLeaderboard lb3 = new StudentLeaderboard();
        submitN(lb3, 8, 2, true, 5); submitN(lb3, 8, 2, true, 15);  // total 40 / 4 = avg 10
        submitN(lb3, 7, 4, true, 10);                                // avg 10
        check(lb3.getTopStudents().equals(List.of(7, 8)), "case5 failed: tie-break");

        // 用例6:空 leaderboard / 无人合格
        check(new StudentLeaderboard().getTopStudents().isEmpty(), "case6a failed");
        StudentLeaderboard lb4 = new StudentLeaderboard();
        submitN(lb4, 1, 4, false, 10);   // 0% 正确率
        check(lb4.getTopStudents().isEmpty(), "case6b failed");

        // 用例7:答错的提交也计入平均用时
        StudentLeaderboard lb5 = new StudentLeaderboard();
        lb5.submit(9, false, 100);          // 一次很慢的错误提交
        submitN(lb5, 9, 4, true, 1);        // 5 次,4 对(80%),total 104,avg 20.8
        submitN(lb5, 10, 4, true, 25);      // avg 25
        check(lb5.getTopStudents().equals(List.of(9, 10)), "case7 failed: 错误提交计入用时");

        // 用例8:非法输入
        try { lb.submit(1, true, -5); check(false, "case8 should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }

        // 用例9:heap 版与 sort 版在所有场景下输出一致
        check(lb.getTopStudentsHeap().equals(lb.getTopStudents()), "case9 lb failed");
        check(lb2.getTopStudentsHeap().equals(lb2.getTopStudents()), "case9 lb2 failed");
        check(lb3.getTopStudentsHeap().equals(lb3.getTopStudents()), "case9 lb3 failed");
        check(lb4.getTopStudentsHeap().equals(lb4.getTopStudents()), "case9 lb4 failed");
        check(lb5.getTopStudentsHeap().equals(lb5.getTopStudents()), "case9 lb5 failed");

        System.out.println("all tests passed");
    }
}
```

</details>

## 测试用例一览

| # | 场景 | 期望 |
|---|---|---|
| 1 | 三人合格,平均用时 5/10/20 | `[2, 1, 3]` 升序 |
| 2 | 100% 正确但只提交 3 次 | 被过滤,榜单不变 |
| 3 | 第 4 个合格者(avg 1)加入 | 只返回前 3,最慢的被挤出 |
| 4 | **恰好 75%**(3/4)vs 80%(4/5) | 75% 排除 —— "严格大于" |
| 5 | 平均用时相同(40/4 vs 10×4) | 按 id 升序,输出确定 |
| 6 | 空榜单 / 全答错 | 空列表(不是 null) |
| 7 | 一次很慢的**错误**提交 | 计入 attempts 和总用时,拉低排名 |
| 8 | `time` 为负 | 抛 `IllegalArgumentException` |
| 9 | heap 版 vs sort 版 | 所有场景输出一致(另做过 500 轮随机 fuzz) |

## 面试当场要确认的澄清点

- [ ] "attempted at least 4 problems" = 4 次**提交**还是 4 道**不同的题**?(API 里没有 problemId,只能按提交数算 —— 主动指出这一点很加分)
- [ ] 答错的提交计入平均用时吗?(本实现:计入)
- [ ] 恰好 75% 算不算?(题面 "greater than" → 不算,写测试卡它)
- [ ] 平均用时相同怎么排?(本实现:id 升序)
- [ ] 不足 3 人合格返回几个?(题面 "up to 3" → 有几个返回几个,空了返回空列表)
- [ ] `time` 为负 / 同一学生海量提交(溢出)怎么办?

## Follow-up:如果 getTopStudents 调用非常频繁?

当前设计是**写快读慢**:`submit` O(1),`getTop` O(n log n)。可聊的优化方向:

1. **只要前 3**:遍历时维护大小为 3 的 bounded heap(堆顶放最慢者,超 3 踢堆顶),O(n log 3) ≈ O(n),不用全排序 —— 完整代码见上方 `getTopStudentsHeap()`,与 sort 版的一致性经 9 组用例 + 500 轮随机 fuzz 验证。**注意堆是读时临时建的,不能常驻**:submit 会改变学生的平均用时和入选资格,`PriorityQueue` 不支持原地更新(`remove(Object)` 是 O(n)),数据会陈旧
2. **读远多于写**:维护有序结构(`TreeSet` + 自定义比较器),`submit` 时先删后插 O(log n),`getTop` 取前 3 O(1)——注意 `TreeSet` 里的元素排序键不能原地改,必须 remove→update→add
3. 说清楚**默认选简单版**的理由:45 分钟电面,正确、干净、可测试 > 过早优化——这句话本身就是他们 rubric 里的 "Design: thoughtful separation of concerns"

## Java 细节(说出来是加分项)

- `totalTime` 用 `long`,正确率判断加 `(long)` 强转 —— 溢出意识
- 比较器里 `Long.compare` / `Integer.compare`,不用减法
- 返回 `new ArrayList<>(subList(...))` 而不是 `subList` 视图(避免暴露内部结构)
- 空结果返回**空列表而非 null**(调用方不用判空)
