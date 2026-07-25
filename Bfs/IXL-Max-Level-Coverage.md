# IXL Interview — Max Level Coverage(出现在最多层的字符)

![Topic](https://img.shields.io/badge/topic-BFS%20Level%20Order%20%2B%20Per--Level%20Dedup-00695c) ![Frequency](https://img.shields.io/badge/VO%20%E5%87%BA%E7%8E%B0-2%20%E6%AC%A1-orange) ![Tests](https://img.shields.io/badge/tests-7%20groups%20passed-brightgreen)

> **一句话**:[LC 102 层序遍历](https://leetcode.com/problems/binary-tree-level-order-traversal/)的变体 —— 每层**先去重**再给字符计数,返回覆盖层数最多的所有字符。
> 还原自面经 #5(N 叉树版)/ #14(二叉树版);代码 JDK 21 编译,7 组测试通过。无 LC 原题。

---

## 题面还原

给一棵字符树,找出出现在**最多个不同层**上的字符;并列时全部返回。

**面经 #5 原例(N 叉树):**

```text
      e          level 1
    / | \
   c  a  c       level 2
  /  / \ | \
 d  a  c c  d    level 3

每层字符集:L1={e}  L2={c,a}  L3={d,a,c}
层数统计:  a:2  c:2  e:1  d:1
输出:[a, c]     (a 和 c 各出现在 2 个不同的层)
```

**面经 #14 原例(二叉树):**

```text
      A
     / \
    A   B        A 出现在第 1、2 层 → 返回 [A]
   / \
  C   C          C 只在第 3 层(同层两次只算一层)
```

## 这题考什么

| 考点 | 说明 |
|---|---|
| **同层去重**(第一坑) | "不同层的个数" ≠ 出现次数。每层先收进 `Set` 去重,再计数 —— #5 里 c 在 L3 出现两次只记一层,d 出现两次也只有 1 层 |
| 层序遍历基本功 | BFS + `size` 层级循环,或 DFS 带深度参数 |
| 并列语义 | max 相同的**全部**返回;输出顺序题面没说 → 主动定(排序)并说出来 |
| 边界 | 空树、单节点、children 里的 null |

### ★ 和 Task Scheduler 的对照(重要)

同样是 `int size = queue.size()` 的层级循环:在 **Task Scheduler 里是错的**(那题是串行调度,"层"不存在,新解锁的高优先级任务必须立刻竞争);在**这题里是必须的**("层"就是题目语义本身,必须知道每个字符属于哪一层)。判断标准一句话:**"层"是不是题面里的概念?是 → 分层;不是 → 别分层。** 两道题都在 IXL 的 VO 池里,拿这对比着记,面试时不会用错。

## 解法

**BFS(推荐)**:逐层弹出,当层字符收进 `Set<Character>` 去重,层结束后统一给 `Map<Character, Integer>` 计数;最后扫一遍取 max 并列的全部收集。O(n) 时间,空间 = 队列宽度 + 计数 Map。

**DFS(替代)**:递归带 `depth` 参数,维护 `Map<Character, Set<Integer>>`(char → 出现过的层号集合),最后比 `set.size()`。同为 O(n),但要额外存层号集合,且**深树有栈溢出风险**——说得出这条 trade-off 就够了,写 BFS。

## 参考实现

先自己限时 20 分钟写一遍(含造树测试),再展开对照 👇

<details>
<summary><b>展开完整代码(含 7 组测试)</b></summary>

```java
import java.util.*;

public class MaxLevelCoverage {

    static class Node {
        char val;
        List<Node> children;

        Node(char val) {
            this.val = val;
            this.children = new ArrayList<>();
        }
    }

    public static List<Character> maxLevelCoverage(Node root) {
        if (root == null) {
            return new ArrayList<>();   // 澄清点:空树返回空列表(不是 null)
        }

        Map<Character, Integer> levelCount = new HashMap<>();  // char -> 出现过的不同层数
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            // ★ 这题的 size 层级循环是必须的 —— "层"是题目语义本身(对比:Task Scheduler 里层级是错的)
            int size = queue.size();
            Set<Character> charsInLevel = new HashSet<>();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                charsInLevel.add(node.val);   // Set 自动去重:同层出现多次只记一次
                for (Node child : node.children) {
                    if (child != null) {
                        queue.add(child);
                    }
                }
            }
            // 每层去重之后才计数
            for (char c : charsInLevel) {
                if (!levelCount.containsKey(c)) {
                    levelCount.put(c, 0);
                }
                levelCount.put(c, levelCount.get(c) + 1);
            }
        }

        int max = 0;
        for (int count : levelCount.values()) {
            max = Math.max(max, count);
        }
        List<Character> result = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : levelCount.entrySet()) {
            if (entry.getValue() == max) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);   // 澄清点:输出顺序题面没说,排序保证确定性(也方便测试)
        return result;
    }

    // ---------------- 测试 ----------------
    private static Node n(char val, Node... children) {
        Node node = new Node(val);
        for (Node c : children) {
            node.children.add(c);
        }
        return node;
    }

    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // 用例1:面经 #5 原例(N 叉树)
        Node t1 = n('e',
                n('c', n('d')),
                n('a', n('a'), n('c')),
                n('c', n('c'), n('d')));
        check(maxLevelCoverage(t1).equals(List.of('a', 'c')), "case1 failed");

        // 用例2:面经 #14 原例(二叉树)
        Node t2 = n('A', n('A', n('C'), n('C')), n('B'));
        check(maxLevelCoverage(t2).equals(List.of('A')), "case2 failed");

        // 用例3:同层重复只算一次 —— 两个 q 同层,和 p 并列 -> [p, q]
        Node t3 = n('p', n('q'), n('q'));
        check(maxLevelCoverage(t3).equals(List.of('p', 'q')), "case3 failed");

        // 用例4:空树 / 单节点
        check(maxLevelCoverage(null).isEmpty(), "case4a failed");
        check(maxLevelCoverage(n('z')).equals(List.of('z')), "case4b failed");

        // 用例5:同一字符纵贯所有层
        Node t5 = n('x', n('x', n('x')), n('y'));
        check(maxLevelCoverage(t5).equals(List.of('x')), "case5 failed"); // x:3 层

        // 用例6:多个并列时输出排序
        Node t6 = n('b', n('a'), n('c'));
        check(maxLevelCoverage(t6).equals(List.of('a', 'b', 'c')), "case6 failed");

        // 用例7:children 里混入 null 不崩
        Node t7 = n('m');
        t7.children.add(null);
        t7.children.add(n('m'));
        check(maxLevelCoverage(t7).equals(List.of('m')), "case7 failed"); // m:2 层

        System.out.println("all tests passed");
    }
}
```

</details>

## 测试用例一览

| # | 场景 | 期望 |
|---|---|---|
| 1 | 面经 #5 原例:c 在 L3 出现两次只算一层 | `[a, c]` |
| 2 | 面经 #14 原例:二叉树 | `[A]` |
| 3 | **同层两个 q** 只记一层,与 p 并列 | `[p, q]` |
| 4 | 空树 / 单节点 | `[]` / `[z]` |
| 5 | 同一字符纵贯 3 层 | `[x]` |
| 6 | 全员并列 | `[a, b, c]` 排序输出 |
| 7 | children 含 `null` | 不崩,`[m]` |

## 面试当场要确认的澄清点

- [ ] 树是二叉还是 N 叉?(两条面经各出现过一种,写 N 叉版天然兼容二叉)
- [ ] 同一字符同层出现多次,算一层还是多次?(题意:**不同层的个数**,同层去重 —— 主动复述这句)
- [ ] 并列时全返回?输出顺序有要求吗?(本实现:全返回 + 排序)
- [ ] 空树返回什么?(本实现:空列表)
- [ ] 字符区分大小写吗?节点值会不会是 null?

## Java 细节(说出来是加分项)

- `Set<Character>` 每层新建,层结束立刻计数、不保留 —— 空间只与树宽有关,不用存所有层的集合
- DFS 替代方案要说出**深树栈溢出**的风险(几万层的链状树会打爆默认栈)
- 计数 Map 的 value 用 `Integer` 装箱,极端规模下可提 `int[26]`(若确认只有小写字母)
- 复杂度一句话:*"Each node enters the queue once — O(n) time; extra space is the queue width plus the count map."*
