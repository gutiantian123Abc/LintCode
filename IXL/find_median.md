# IXL VO — LC 295 Find Median from Data Stream 终极版(Java · 双堆 + 无分支三步舞)

> **情报卡**:面经 #19 **实锤出现在 VO 第一轮,且有人挂在这道题上**;PriorityQueue 是 Glassdoor 跨年反复点名的 IXL 方向(池里 295 / Kth Largest / Task Scheduler 三题共用)。综合押题:**第一轮概率榜第一**。
> **代码状态**:`MedianFinder295.java` JDK 21 编译通过;LC 原例 / 逐步中位数 / 全重复 / 负数 / **MAX 溢出** / **2000 轮随机 vs 排序 oracle 逐步比对** / 计数版 1000 轮交叉验证 / 空结构防御,全绿。
> **本题特殊性**:它是"看起来最简单的挂人题"——挂人机制见第 2 节,解药是**无分支的三步舞**。

---

## 1. 原题还原(英文,LC 原文形态)

> *The median is the middle value in an ordered integer list. If the size of the list is even, the median is the average of the two middle values.*
>
> *Implement the MedianFinder class:*
> - *`MedianFinder()` initializes the object.*
> - *`void addNum(int num)` adds the integer `num` from the data stream to the data structure.*
> - *`double findMedian()` returns the median of all elements so far.*
>
> *Follow up:*
> - *If all integer numbers from the stream are in the range `[0, 100]`, how would you optimize your solution?*
> - *If `99%` of all integer numbers from the stream are in the range `[0, 100]`, how would you optimize your solution?*

```text
示例:addNum(1) → addNum(2) → findMedian() = 1.5 → addNum(3) → findMedian() = 2.0
```

## 2. 为什么这道"模板题"挂人(先读这节)

这题人人见过,危险恰恰在这:**多数人记得"两个堆",但没把 addNum 的搬运顺序写成肌肉记忆**,现场靠直觉重新发明——最常见的发明是"分支版":

```java
// 现场容易写出的分支版(能写对,但雷区密布)
if (lower.isEmpty() || num <= lower.peek()) {   // 雷1:第一次 add 时 lower 空,漏了 isEmpty 就 NPE
    lower.offer(num);
} else {
    upper.offer(num);
}
if (lower.size() > upper.size() + 1) {          // 雷2:两个方向的再平衡,阈值 +1 写错一处就偏
    upper.offer(lower.poll());
} else if (upper.size() > lower.size()) {       // 雷3:> 还是 >=,边界选错中位数就漂
    lower.offer(upper.poll());
}
```

四条执行路径、三处边界选择,压力之下错任何一处,**某些特定序列的中位数就是错的**,而且现场很难定位。在"简单题"上翻车,印象分损失是双倍的——这就是 #19 的挂人机制。解药:第 5 节的**无分支三步舞**,零个 if 作用在数值上,只有一次大小检查,没有雷区。

## 3. 开场 3 分钟:澄清问题(附建议默认值)

| 要问的问题 | 建议默认 | 为什么要问 |
|---|---|---|
| findMedian 调用频率高吗? | 高——所以要 O(1) 查询 | 决定"维护在手"还是"现算" |
| 偶数个时返回什么类型? | 两个中间值的平均,double | 铺垫 `/2.0` 细节 |
| 数值范围?会溢出吗? | int 全域——**两个 int 相加会溢出** | 铺垫 long 强转 |
| 有重复值吗? | 有,正常处理 | 堆天然支持,说一句即可 |
| 空结构上调 findMedian? | 抛异常(LC 保证不会,防御性写上) | 边界意识 |
| 值域集中在 [0,100]? | 不假设——但这是官方 follow-up,主动提 | 铺垫 FU1 |

## 4. 解法推导(讲给面试官听的顺序)

**第一步:排除笨办法。** 每次 findMedian 现排序:O(n log n) 每查询;维护有序数组 + 二分插入:查询 O(1) 但插入 O(n)(搬移)。流式场景两者都不合格。

**第二步:关键观察——中位数只关心"中间",不关心两侧内部的顺序。** 把已见过的数劈成两半:下半场(较小的一半)和上半场(较大的一半)。只要能随时拿到**下半场的最大值**和**上半场的最小值**,中位数就在这两个数手里——两侧内部乱着放完全无所谓。

**第三步:"随时拿到最大/最小" = 堆的定义。**

```text
lower = max-heap(下半场):堆顶 = 下半场最大 = 中间偏左的那个数
upper = min-heap(上半场):堆顶 = 上半场最小 = 中间偏右的那个数
```

**第四步:先声明两个不变量,再写代码**(这句 narration 就是这题的互动分):

```text
不变量①(顺序):lower 的每个元素 ≤ upper 的每个元素   —— 保证"劈半"是真的按大小劈
不变量②(大小):lower.size() − upper.size() ∈ {0, 1}  —— 保证劈在正中间;约定多余的归 lower
```

两个不变量成立 ⟹ 奇数个时中位数 = `lower.peek()`;偶数个时 = 两个堆顶的平均。findMedian 就是两行。

## 5. addNum:无分支三步舞(全题核心,背到肌肉记忆)

```java
public void addNum(int num) {
    lower.offer(num);                       // ① 无条件先进下半场
    upper.offer(lower.poll());              // ② 把下半场当前最大挤到上半场
    if (upper.size() > lower.size()) {      // ③ 上半场人多了 → 还它的最小回来
        lower.offer(upper.poll());
    }
}
```

**为什么①②之后不变量①必然成立**:走进 upper 的那个元素是 `lower` 加入 num 之后的**最大值**——它 ≥ 留在 lower 的所有元素;而它进入 upper 后,upper 原有元素本来就 ≥ 原 lower 的所有元素(旧不变量)。分两种情况验证:num 被挤走(说明 num ≥ 原 lower 全体,进 upper 合法);num 留下(说明被挤走的是原 lower 最大值 m,num ≤ m ≤ upper 全体,留下合法)。**无论 num 是几,不变量①都被这两步机械地保住——这就是"无分支"的含义:正确性不依赖对 num 的判断。**

**为什么③保住不变量②**:①②净效果是 upper +1。若之前两边持平,现在 upper 多 1,违反"多余归 lower"→ 把 upper 的最小还给 lower(它 ≥ lower 全体,回去当新最大,不变量①不破)。若之前 lower 多 1,现在恰好持平,③不触发。

**成本**:每次 addNum 固定 2–3 次堆操作,O(log n),无分支无边界——和第 2 节分支版对比:**零条数值判断 vs 三处雷区**。

## 6. 手走 add 5, 2, 8, 1(考场要做的事)

| add | ①进 lower 后 | ②挤到 upper 后 | ③再平衡后 | median |
|---|---|---|---|---|
| 5 | lower{5} | lower{}, upper{5} | **lower{5}**, upper{}(③触发) | **5** |
| 2 | lower{5,2}顶5 | lower{2}, upper{5} | 不触发(1:1) | (2+5)/2 = **3.5** |
| 8 | lower{8,2}顶8 | lower{2}, upper{5,8}顶5 | **lower{5,2}顶5**, upper{8}(③触发) | **5** |
| 1 | lower{5,2,1}顶5 | lower{2,1}顶2, upper{5,8} | 不触发(2:2) | (2+5)/2 = **3.5** |

对照排序验证:{5}→5;{2,5}→3.5;{2,5,8}→5;{1,2,5,8}→3.5 ✓。注意 add 8 那一行:8 先进 lower 又立刻被挤去 upper——**看似绕路,换来的是零分支**;每次多付一次 O(log n),买断全部边界雷区,值。

## 7. 参考实现(已验证)

<details>
<summary><b>展开完整代码(MedianFinder295.java;测试:LC 原例 / 手走序列 / 全重复 / 负数 / MAX 溢出 / 2000 轮 vs 排序 oracle / 计数版交叉验证 / 空结构)</b></summary>

```java
import java.util.*;

class MedianFinder {
    private final PriorityQueue<Integer> lower =
            new PriorityQueue<>(Collections.reverseOrder());   // max-heap:下半场,堆顶 = 下半场最大
    private final PriorityQueue<Integer> upper =
            new PriorityQueue<>();                             // min-heap:上半场,堆顶 = 上半场最小

    // 不变量①(顺序):lower 的所有元素 <= upper 的所有元素
    // 不变量②(大小):lower.size() - upper.size() ∈ {0, 1}(多余的归 lower)

    public void addNum(int num) {
        lower.offer(num);                       // ① 无条件先进下半场
        upper.offer(lower.poll());              // ② 下半场当前最大挤到上半场 -> 保不变量①
        if (upper.size() > lower.size()) {      // ③ 上半场人多 -> 还最小回来 -> 保不变量②
            lower.offer(upper.poll());
        }
    }

    public double findMedian() {
        if (lower.isEmpty()) {
            throw new IllegalStateException("no numbers yet");
        }
        if (lower.size() > upper.size()) {      // 奇数个:中位数 = lower 堆顶
            return lower.peek();
        }
        return (lower.peek() + (long) upper.peek()) / 2.0;   // 偶数个:两堆顶平均
        // long 强转防 int 相加溢出;/2.0 防整数除法 —— 两个送命点一行避掉
    }
}
```

</details>

## 8. Java 细节(边写边说,每条都是加分口)

- **max-heap 的构造**:`new PriorityQueue<>(Collections.reverseOrder())`——比手写 `(a, b) -> b - a` 好两次:不用担心减法溢出,也少写一个 lambda。**PriorityQueue 默认是 min-heap**——你的老坑清单第一条,在这题写错方向 = 两个堆全反。
- **`(lower.peek() + (long) upper.peek()) / 2.0`** 一行躲两坑:先 long 再相加(两个 int 直接加会溢出——实测 MAX 和 MAX−1 的中位数,naive 写法得到负数);除以 `2.0` 不是 `2`(整数除法 3/2 = 1)。
- 命名:`lower/upper` 比 `small/large` 或 `left/right` 自解释——lower half / upper half。

## 9. 复杂度(说出口的版本)

> *"addNum is two to three heap operations — O(log n); findMedian reads at most two tops — O(1); space O(n). That's the right trade for a stream: pay a little on every insert so every query is free."*

## 10. Follow-up 全集(带详细答案)

### FU1 — "所有数都在 [0, 100]?"(LC 官方追问一,已实现并与主解 1000 轮交叉验证)

值域小到可数 → **计数桶**:`int[101] count` + 总数 total。addNum = `count[num]++`,**O(1)**;findMedian = 从 0 走到 100 累加计数找第 k 小(偶数找两个),**O(101) = O(1)**。

```java
public void addNum(int num) { count[num]++; total++; }        // O(1)
private int kth(int k) {                                       // 第 k 小(1-based)
    int seen = 0;
    for (int v = 0; v <= 100; v++) {
        seen += count[v];
        if (seen >= k) return v;
    }
    throw new IllegalStateException();
}
```

本质:**值域有限时,"排序"退化成"数数"**——和计数排序同一个原理。堆版按元素个数付钱,计数版按值域大小付钱,n 巨大而值域固定时后者完胜。

### FU2 — "99% 的数在 [0, 100]?"(LC 官方追问二)

混合结构:主体走计数桶,**两个溢出容器**接住出界的——`below`(< 0)和 `above`(> 100),各用一个有序结构(堆或有序列表)。findMedian 先用三段的数量(below.size / total / above.size)定位第 k 小落在哪一段:99% 的情况落在桶里,O(101);极少数落进溢出区才碰有序结构。**思想:为主流路径优化,给例外留出口**——口述到这层即可,现场实现只写桶 + 说明溢出区的接口。

### FU3 — "滑动窗口中位数?"(LC 480,口述)

窗口滑动 = 既加数也**删数**,而堆不支持删任意元素。标准解:**双堆 + 延迟删除(lazy deletion)**——删除时只记账(`Map<数, 待删次数>`),数还留在堆里;等它**浮到堆顶**时再真正弹掉(peek 时循环清顶)。平衡判断用"逻辑大小"(扣除待删)而不是 `size()`。复杂度 O(n log k)。另一条路:两个 `TreeMap` 当 multiset,代码更直白。说出 "lazy deletion" 这个词 + 记账思路即封顶。

### FU4 — "支持 remove(num)?"

FU3 的延迟删除机制通用化,一样的记账 + 浮顶清理。主动说一句代价:被"删除"的元素在浮顶前仍占内存——若删除极频繁,换平衡 BST 类结构(TreeMap multiset)更实在。

### FU5 — "要 P90 / 任意分位数呢?"(泛化,展示理解深度的机会)

中位数 = 50 分位的特例。双堆照用,只改**大小不变量**:P90 → lower 保持约 90% 的元素,upper 保持 10%,再平衡阈值按比例走;查询仍是堆顶。**把"劈半"泛化成"按 p 劈"**——一句话说穿双堆的本质:它维护的是"分位点两侧的有序边界"。

### FU6 — "数据量大到单机放不下?"

诚实分层:**精确**中位数需要全局信息,分布式代价高(跨节点选择算法/多轮通信);**生产答案是近似**——t-digest、GK sketch 这类分位数概要结构,可合并、误差有界。说出 "exact needs coordination; production uses mergeable quantile sketches like t-digest" 即封顶,别展开实现。

### FU7 — "为什么不用别的结构?"(对比表,防"你还有别的做法吗")

| 方案 | addNum | findMedian | 判决 |
|---|---|---|---|
| 每次排序 | O(1) 存 | O(n log n) | 查询太贵 |
| 有序数组 + 二分插入 | **O(n)**(搬移) | O(1) | 插入太贵 |
| 平衡 BST + 子树计数(order-statistics tree) | O(log n) | O(log n) | 理论可行,**Java 没有现成实现**(TreeMap 不支持"第 k 小"),现场手写不现实 |
| **双堆** | O(log n) | **O(1)** | Java 的标准答案 |

## 11. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **PriorityQueue 默认 min-heap** | lower 方向反,全盘错 | `Collections.reverseOrder()`,写完口头确认一次方向 |
| **现场发明分支版 addNum** | 空堆 NPE / 再平衡方向错 / 边界 >< 选错——#19 挂人机制 | **三步舞,零数值分支**,背到肌肉记忆 |
| `(a + b) / 2` | 整数除法,3/2 = 1 | `/ 2.0` |
| `lower.peek() + upper.peek()` 直接相加 | 两个大 int 相加溢出为负 | 先 `(long)` 强转(实测 MAX 用例) |
| 比较器写 `(a, b) -> b - a` | 极端值减法溢出 | `reverseOrder()` 或 `Integer.compare` |
| 不变量②的"多余归谁"前后不一致 | 奇数个时取错堆顶 | 开场声明"多余归 lower",findMedian 与之对齐 |
| 空结构 findMedian | NPE 或未定义 | 显式抛 IllegalStateException,主动提 |
| 重复元素慌乱 | 无 | 堆天然支持,说一句即可 |

## 12. 20 分钟考场节奏 + 互动台词(第一轮题,节奏更快)

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–2 min | 澄清(第 3 节前四行) | "Median of everything so far; even count returns the average as a double; how often is findMedian called?" |
| 2–4 min | **先声明不变量,要 buy-in** | "Two heaps splitting the stream: a max-heap for the lower half, a min-heap for the upper. Two invariants: every element in lower ≤ every element in upper, and lower holds at most one extra. Then the median is just the tops. Sound good?" |
| 4–10 min | 写代码,持续 narrate | "I add through the lower heap unconditionally and let one element flow to the upper — that keeps the order invariant with **zero branching on the value**." |
| 10–13 min | 手走 5,2,8,1(第 6 节表) | "Let me verify against the sorted list at each step." |
| 13–16 min | **主动**报边界 | "What would break this: the very first add, duplicates, negative numbers, two INT_MAX values overflowing the average, calling findMedian on empty." |
| 16–20 min | Follow-up 对话(第 10 节弹药) | 值域 [0,100] → 计数桶;99% → 桶 + 溢出区;滑动窗口 → lazy deletion |

## 13. 30 秒总结陈词(背诵版)

> *"Two heaps split the stream in half: a max-heap holds the lower half, a min-heap the upper, with two invariants — everything in lower ≤ everything in upper, and lower keeps at most one extra. addNum pushes through the lower heap and lets one element flow across, then rebalances — no branching on the value, so there are no edge cases to get wrong. findMedian just reads the tops: O(log n) insert, O(1) query. The average uses a long cast and 2.0 to dodge overflow and integer division. If all values fall in [0,100], I'd switch to a counting array — O(1) insert, constant-time query."*

**记忆钩子**:劈两半,顶对顶;**先进 lower、挤一个去 upper、多了还回来**——三步舞零分支;偶数取平均时 **long + 2.0**;值域小就数数。
