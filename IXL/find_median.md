# IXL VO — LC 295 Find Median from Data Stream 终极版(Java · 双堆 + 无分支三步舞)

> **情报卡**:面经 #19 **实锤出现在 VO 第一轮,且有人挂在这道题上**;PriorityQueue 是 Glassdoor 跨年反复点名的 IXL 方向(池里 295 / Kth Largest / Task Scheduler 三题共用)。综合押题:**第一轮概率榜第一**。
> **代码状态**:`MedianFinder295.java` JDK 21 编译通过;LC 原例 / 逐步中位数 / 全重复 / 负数 / **MAX 溢出** / **2000 轮随机 vs 排序 oracle 逐步比对** / 计数版 1000 轮交叉验证 / 空结构防御,全绿。FU2 混合结构另测 2000 条流(含 MIN/MAX 离群值)、204,058 个逐步中位数全绿。
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
    lower.add(num);
} else {
    upper.add(num);
}
if (lower.size() > upper.size() + 1) {          // 雷2:两个方向的再平衡,阈值 +1 写错一处就偏
    upper.add(lower.poll());
} else if (upper.size() > lower.size()) {       // 雷3:> 还是 >=,边界选错中位数就漂
    lower.add(upper.poll());
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
    lower.add(num);                         // ① 无条件先进下半场
    upper.add(lower.poll());                // ② 把下半场当前最大挤到上半场
    if (upper.size() > lower.size()) {      // ③ 上半场人多了 → 还它的最小回来
        lower.add(upper.poll());
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

## 6.5 findMedian 逐行:三个"偷懒"全是不变量②的红利

```java
public double findMedian() {
    if (lower.isEmpty()) {                  // 只查 lower = 查"整个结构是否为空"
        throw new IllegalStateException("no numbers yet");
    }
    if (lower.size() > upper.size()) {      // 奇数个:中位数 = lower 堆顶
        return lower.peek();
    }
    return ((long) lower.peek() + (long) upper.peek()) / 2.0;   // 偶数个:两堆顶平均
}
```

这段代码"省"了三处判断,每一处都靠 addNum 第③步维护的不变量②(`upper.size() <= lower.size()`,多余归 lower)撑腰:

- **为什么不判断 `upper.isEmpty()`**:`upper.peek()` 只在最后一行执行;能走到最后一行,说明前两个 if 都没拦住——lower 非空,且 `lower.size() <= upper.size()`。叠加不变量的 `upper.size() <= lower.size()`,两边只能**相等**;lower 非空 ⟹ 相等的 upper 也非空。关键边界:**只有 1 个数时**(lower 1 个、upper 0 个)走的是第二个 if 直接返回 `lower.peek()`,空的 upper 从未被读。
- **为什么 `lower.isEmpty()` 一个判断 = 整个结构空**:upper 永远不比 lower 多 ⟹ lower 空则 upper 也空 ⟹ 总数 0;反过来只要进过一个数,lower 至少有一个。查一个堆就够。
- **为什么没有 `upper.size() > lower.size()` 分支**:这个状态在每次 addNum 返回后**不存在**。可能状态一共三种:

| 总数 | lower | upper | findMedian 走哪条 |
|---|---|---|---|
| 0 | 空 | 空 | 抛异常 |
| 奇数 2k+1 | k+1 个 | k 个 | 第二个 if → `lower.peek()` |
| 偶数 2k | k 个 | k 个 | 最后一行 → 两堆顶平均 |

**瞬时 vs 稳定**(回答"为什么③和 findMedian 的比较方向相反"):在 addNum 内部,②之后 upper 确实可能暂时比 lower 多 1——③干的就是修这个。不变量保证的是每次 addNum **返回之后**的状态,也就是 findMedian 一切可能被调用的时刻。③写 `upper.size() > lower.size()` 修内部瞬时超标;findMedian 写 `lower.size() > upper.size()` 读方法之间的稳定状态——两个方向各管各的时刻,并不矛盾。

**实测**(151,779 次 addNum 逐步断言):addNum 结束时 `lower.size() - upper.size()` 范围 [0, 1],零违例;②之后③之前范围 [−1, 0];③触发 77,147 次 = add 之前总数为偶数的次数,分毫不差;"lower 空而总数 ≥ 1"与"两边相等而 upper 空"从未发生。

面试官若指着最后一行问 *"what if upper is empty here?"*,标准答案:

> *"The size invariant makes that unreachable — lower always holds the extra element, so by the time we reach this line the sizes are equal and both non-empty. The only truly empty case is lower being empty, which the first check handles."*

这也是坑清单里"多余归谁必须前后一致"的正面版本:addNum 约定多余归 lower,findMedian 的奇数分支就读 `lower.peek()`——两个方法被不变量焊在一起,改一头必须同时改另一头。

## 7. 参考实现(已验证)

<details>
<summary><b>展开完整代码(MedianFinder295.java;测试:LC 原例 / 手走序列 / 全重复 / 负数 / MAX 溢出 / 2000 轮 vs 排序 oracle / 计数版交叉验证 / 空结构)</b></summary>

```java
import java.util.*;

class MedianFinder {
    private final PriorityQueue<Integer> lower =
            new PriorityQueue<>((a, b) -> Integer.compare(b, a));  // max-heap:下半场,堆顶 = 下半场最大
            // 铁律:Integer.compare(b, a),不写 b - a(极端值减法溢出)
    private final PriorityQueue<Integer> upper =
            new PriorityQueue<>();                             // min-heap:上半场,堆顶 = 上半场最小

    // 不变量①(顺序):lower 的所有元素 <= upper 的所有元素
    // 不变量②(大小):lower.size() - upper.size() ∈ {0, 1}(多余的归 lower)

    public void addNum(int num) {
        lower.add(num);                         // ① 无条件先进下半场
        upper.add(lower.poll());                // ② 下半场当前最大挤到上半场 -> 保不变量①
        if (upper.size() > lower.size()) {      // ③ 上半场人多 -> 还最小回来 -> 保不变量②
            lower.add(upper.poll());
        }
    }

    public double findMedian() {
        if (lower.isEmpty()) {
            throw new IllegalStateException("no numbers yet");
        }
        if (lower.size() > upper.size()) {      // 奇数个:中位数 = lower 堆顶
            return lower.peek();
        }
        return ((long) lower.peek() + (long) upper.peek()) / 2.0;   // 偶数个:两堆顶平均
        // 双边 (long) 在相加之前升位,和在 long 里算;/2.0 防整数除法 —— 两个送命点一行避掉
        // cast 位置是生死线:(long)(a + b) 是错的 —— int 相加先溢出,加完再 cast 已晚
    }
}
```

</details>

## 8. Java 细节(边写边说,每条都是加分口)

- **max-heap 的构造(你的写法)**:`new PriorityQueue<>((a, b) -> Integer.compare(b, a))`。**铁律:lambda 里必须 `Integer.compare(b, a)`,永远不写 `b - a`**——两个极端值相减会 int 溢出,比较方向悄悄翻转,堆就坏了,而且普通测试测不出来。等价替代:`Collections.reverseOrder()`(效果一样,按手感选;你顺手 lambda 就写 lambda)。**PriorityQueue 默认是 min-heap**——你的老坑清单第一条,在这题写错方向 = 两个堆全反。
- **`add` 与 `offer` 完全等价**:PriorityQueue 无界、永不"满",`add` 内部就是调 `offer`,不会抛"队列满"异常也不会返回 false。用你习惯的 `add` / `poll` / `peek` 一套走到底即可。
- **平均那一行(你的写法为准)**:`((long) lower.peek() + (long) upper.peek()) / 2.0` 一行躲两坑——相加**之前**把操作数升成 long,和在 long 里算不溢出;除以 `2.0` 不是 `2`(整数除法 3/2 = 1)。单边 cast `(lower.peek() + (long) upper.peek())` **完全等价**:Java 的二元数值提升(binary numeric promotion)规定 `+` 两边一个 int 一个 long 时,int 自动升 long;双边 cast 只是把这个自动行为亲手写明,读代码的人不用记规则——考场推荐双边。**生死线是 cast 的位置,不是数量**:必须作用在加法之前的操作数上,`(long) (a + b)` 是错的——括号里 int + int 先溢出,再 cast 已晚。实测(MAX 与 MAX−1,正确答案 2147483646.5):

```text
((long) a + (long) b) / 2.0 = 2.1474836465E9   ✓ 双边 cast(采用)
(a + (long) b) / 2.0        = 2.1474836465E9   ✓ 单边 cast,等价
(a + b) / 2.0               = -1.5             ✗ 无 cast,int 相加溢出(和 = -3)
(long) (a + b) / 2.0        = -1.5             ✗ cast 加在和上,加完才 cast,已晚
```

- 命名:`lower/upper` 比 `small/large` 或 `left/right` 自解释——lower half / upper half。

## 9. 复杂度(说出口的版本)

> *"addNum is two to three heap operations — O(log n); findMedian reads at most two tops — O(1); space O(n). That's the right trade for a stream: pay a little on every insert so every query is free."*

## 10. Follow-up 全集(带详细答案)

### FU1 — "所有数都在 [0, 100]?"(LC 官方追问一,已实现,与主解 1000 轮交叉验证)

**面试官钓什么**:值域变有限了,算法的付费方式可以整个换掉——从"按元素个数付钱"换成"按值域大小付钱"。

**核心一句:值域有限时,"排序"退化成"数数"**(和计数排序同一原理)。[0, 100] 只有 101 种可能的值,不存数本身,只记每种值出现几次:

```java
private final int[] count = new int[101];   // count[v] = 值 v 到现在为止出现的次数
private int total = 0;                      // 一共进来了多少个数
```

count 数组本质是**整个数据流的压缩版排序结果**——从 count[0] 读到 count[100],等于把所有数从小到大读一遍,重复的值只占一格。

**addNum:两行,O(1)**(对比堆版 O(log n)):

```java
public void addNum(int num) { count[num]++; total++; }
```

**findMedian:在桶里"数"出第 k 小**:

```java
int mid1 = (total + 1) / 2;                             // 一个公式罩住奇偶(向下取整的效果)
int a = kth(mid1);
int b = (total % 2 == 1) ? a : kth(total / 2 + 1);      // 奇数:b = a;偶数:再找右中位
return (a + b) / 2.0;

private int kth(int k) {                // 第 k 小(1-based)
    int seen = 0;
    for (int v = 0; v <= 100; v++) {
        seen += count[v];               // seen = 到 v 为止,值 <= v 的数一共有几个
        if (seen >= k) return v;        // 第一次凑够 k 个,第 k 小就是 v
    }
}
```

`seen` 的精确含义:**处理完桶 v 之后,流里 ≤ v 的数的总个数**。第一次 `seen >= k` 发生在 v,说明走到 v 之前还不足 k 个、加上 v 的份额才够——排序后第 k 个位置恰好落在 v 的那串重复里,第 k 小 = v。

奇偶验证:total = 7(奇)→ mid1 = 4(正中间),b = a;total = 6(偶)→ mid1 = 3(左中位),b = kth(4)(右中位),取平均。

**手走**(add 5, 2, 8, 2, 5, 5;实测):

```text
count[2]=2, count[5]=3, count[8]=1,其余 98 个桶全 0,total = 6
排序对照:2,2,5,5,5,8 → 中位数 = (第3小 + 第4小) / 2 = (5+5)/2 = 5.0
kth(3):v=2: seen=2 < 3,继续 → v=5: seen=5 >= 3,停,第3小 = 5
kth(4):v=2: seen=2 < 4,继续 → v=5: seen=5 >= 4,停,第4小 = 5
```

**两版对比(空间是戏剧性的一格)**:

| | 双堆版 | 计数版 |
|---|---|---|
| addNum | O(log n) | **O(1)** |
| findMedian | O(1) | O(101) = **O(1) 常数** |
| 空间 | **O(n)**(10 亿个数真的存 10 亿) | **O(101),流多长都不变** |

**两个细节(加分口)**:这版 `(a + b) / 2.0` **不需要 long**——a、b 都 ≤ 100,相加最多 200,溢出坑自动消失;但 `/2.0` 仍然必须(3/2 = 1 的整数除法坑还在)。流真的无限长时,total 和桶本身可能加爆 int,防御性做法是声明成 long——一句带过即可。

**口述**:

> *"With a bounded range, sorting degenerates into counting. I replace the heaps with a frequency array indexed by value — insert is an O(1) increment, and the median comes from walking at most 101 buckets, which is constant time. Space drops from O(n) to 101 counters no matter how long the stream runs."*

### FU2 — "99% 的数在 [0, 100]?"(LC 官方追问二,已实现,2000 条流 vs 排序 oracle 全绿)

**面试官钓什么**:不是新算法,是系统思维——**为主流路径优化,给例外留一个正确的出口**(fast path / slow path)。

**结构:FU1 的桶继续当主体,两个溢出容器接住出界的 1%**:

```text
below(< 0 的数,有序)  |  count[101] 桶(0..100,99% 的数)  |  above(> 100 的数,有序)
```

概念上整个流 = 三段拼接的有序序列:below 全体 < 桶内全体 < above 全体。溢出区只有 ~1% 的数,用**有序 ArrayList + 二分插入**(结构选择理由见下)。

**addNum:三分支**:

```java
if (num >= 0 && num <= 100) { count[num]++; inRange++; }   // 99%:O(1) 快路径
else if (num < 0) insertSorted(below, num);                 // 1%:慢路径
else              insertSorted(above, num);

private static void insertSorted(List<Integer> list, int num) {
    int pos = Collections.binarySearch(list, num);
    if (pos < 0) pos = -pos - 1;        // 没找到时返回 -(插入点)-1,还原成插入点
    list.add(pos, num);                 // 尾巴只有 ~1%,O(t) 搬移无所谓
}
```

**findMedian:先定段,再段内找**——FU1 的 kth 外面套一层"扣除":

```java
private int kth(int k) {
    if (k <= below.size()) return below.get(k - 1);   // 落在 below:按下标直取
    k -= below.size();                                 // 扣掉 below 的人数
    if (k <= inRange) { /* FU1 的桶行走,原封不动 */ }   // 99% 的情况走这里,O(101)
    return above.get(k - inRange - 1);                 // 落在 above
}
```

**必说的精确点**:99% 是**整体分布**的承诺,不是**每个前缀**的保证——流的开头可能全是离群值,中位数真的会落在溢出区。实测:add −5, −3, −1 → median = −3(below 段);再 add 50, 60 → median 仍 = **−1,还在 below 段**(5 个数里 below 占 3 个)。偶数个时两个中间值还可能**跨段**(再 add 70 → (−1 + 50)/2 = 24.5,一个在 below 一个在桶里)——kth 每次独立定段,跨段平均自动处理。所以慢路径不是装饰,必须正确。

**账目**:addNum 主流 O(1);findMedian 主流 O(101) = O(1);空间 O(101 + 离群个数) ≈ O(0.01n)——不再是 FU1 的严格常数,但比存全量小两个数量级。实测:2000 条随机流(约 99% 在 [0, 100],离群值掺 MIN/MAX)、**204,058 个逐步中位数与排序 oracle 全部一致**。

**below/above 用什么结构?**(被追问就按这个答)先列需求:只要 ① 插入(可能有重复)② 按名次取第 k 小。拿需求挑:

| 候选 | 插入 | 取第 k 小 | 判决 |
|---|---|---|---|
| **有序 ArrayList + 二分插入** | O(log t) 定位 + O(t) 搬移 | **O(1)** 按下标直取 | ✅ 首选:唯一直接支持名次访问,代码最短 |
| PriorityQueue 堆 | O(log t) | ✗ 堆只能看顶,取第 k 个要 poll k 次破坏结构 | 排除 |
| TreeMap<值, 次数> | O(log t) | O(t) 遍历累加(TreeMap **没有**"第 k 小"API) | 可用但不占优 |
| TreeSet | — | — | ✗ 存不了重复值,流里出现两个 −5 就丢数 |

t ≈ 1% 又是罕见路径,O(t) 搬移不值得为它上复杂结构——FU7 那张表的道理在小场景重演:Java 没有现成的 order-statistics tree,需要名次访问时,小数据量下有序数组最实惠。追问"离群值越来越多呢":**那说明 99% 前提已破,正确动作不是给尾巴升级结构,而是整个退回双堆主解**——特化方案的前提没了,就回到通用方案。

**口述**:

> *"I keep the counting array as the fast path for the 99%, and two small sorted overflow lists for outliers below 0 and above 100. To find the median I locate which of the three segments the k-th element falls in by comparing k against the segment sizes — 99% of the time it's the same constant-time bucket walk. The overflow paths are rare but fully correct, since early in the stream the median can genuinely sit there."*

**考场深度**:口述即封顶;真要求写,写桶 + 说明溢出区接口("a sorted list with binary-search insert")就是满分姿态。

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
| **PriorityQueue 默认 min-heap** | lower 方向反,全盘错 | `(a, b) -> Integer.compare(b, a)`,写完口头确认一次方向 |
| **现场发明分支版 addNum** | 空堆 NPE / 再平衡方向错 / 边界 >< 选错——#19 挂人机制 | **三步舞,零数值分支**,背到肌肉记忆 |
| `(a + b) / 2` | 整数除法,3/2 = 1 | `/ 2.0` |
| 两堆顶直接相加,或 `(long) (a + b)` 加完才 cast | int 相加先溢出,事后 cast 救不回 | cast 加在**操作数**上:`((long) a + (long) b)`(实测 MAX 用例) |
| 比较器写 `(a, b) -> b - a` | 极端值减法溢出 | **`Integer.compare(b, a)`**(或 `Collections.reverseOrder()`) |
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
