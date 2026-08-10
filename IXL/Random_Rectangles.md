# IXL VO — Random Rectangles 终极版(Java · 拒绝采样 + 开放讨论题的打法)

> **情报卡**:面经 #10 / #16,**VO 出现 2 次**,第二轮方向,押题榜第四位(与 Italic Parser 并列)。无 LC 原题。
> **代码状态**:`RandomRectangles.java` JDK 21 编译通过;200 种子属性测试、同种子复现性、紧密布局重启、非法参数防御全绿。
> **本题特殊性**:四题里唯一的**开放题**——没有唯一正解,**写代码占一半分,讨论 trade-off 占另一半**。评分的主轴是对话质量:先问清需求、从最简单方案写起、**主动说出它的缺陷和改进方向**。这是 IXL"互动为纲"最极致的一题。
> **证据等级(读这份文档前必看)**:题面与出现频率是**面经实锤**;三工程点、测试打法、全部 follow-up 是按题目性质**推演的备弹**(面经未记载考官的具体要求)。实锤部分押重注;推演部分被换个方式问到时,现场重推,别背稿。

---

## 1. 原题还原(英文,面试官视角)

> *Write a function that places a number of small rectangles at **random** positions inside a large W×H board, such that no two rectangles overlap. Return the position of each rectangle.*

题面就这么短——**留白比 Italic Parser 还大**,这是故意的:尺寸怎么给、"随机"多严格、放不下怎么办,全都要你来问。

## 2. 这题真正考什么

| 评分点 | 说明 |
|---|---|
| **需求澄清** | 题面至少 7 个未定义行为(第 3 节),开放题的澄清不是礼貌,是解题的一部分 |
| **从简单起步的判断力** | 正确开局:最笨但正确的拒绝采样,**边写边声明缺陷**;错误开局:上来聊高级算法却写不完 |
| **工程习惯** | 三个必讲点:终止性、Random 注入、死胡同——每个都是"生产代码素养"的信号 |
| **trade-off 对话** | 面试官会不断问"如果……呢?"——这题的 follow-up 不是加分项,是主体 |

### 这题为什么没有 LC 式"标准答案"(结构上就没有)

LC 式标准答案需要三个前提:输入输出合同精确定义、存在唯一正确(或可判定)的输出、存在公认最优解。这题一个都不给:题面故意留白(澄清本身是考题)、输出是随机布局(合法答案无穷多,只能验证性质)、三条路线各赢一个维度(拒绝采样赢简单、枚举赢终止保证、guillotine 赢速度,没有全能冠军)。所以它的"标准"长在**过程**上:澄清 → 最简正确起步 → 主动报缺陷 → 按追问换方案。

但**零件是有 LC 标准的,开放的只是组装**:AABB 判定 = [LC 836 Rectangle Overlap](https://leetcode.com/problems/rectangle-overlap/) 原题公式,必须一字不差;拒绝采样 = [LC 470](https://leetcode.com/problems/implement-rand10-using-rand7/) / [LC 478](https://leetcode.com/problems/generate-random-point-in-a-circle/) 的核心模式("生成→检查→不合格重来")。**零件按标准背,组装按对话走。**

## 3. 开场 4 分钟:澄清问题(附建议默认值)

| 要问的问题 | 建议默认 | 为什么要问 |
|---|---|---|
| 小矩形的尺寸和数量怎么给? | 参数传入 `List<int[]> sizes`(每项 w×h) | 决定函数签名 |
| 允许旋转 90° 吗? | 不允许;允许则是 FU5 | 影响尝试逻辑 |
| **"随机"要多严格?** | 每次放置在当前合法位置上均匀即可 | 这是本题最深的水(第 7 节),先确认档次 |
| 放不下怎么办? | 抛异常;可改返回部分结果 / Optional | 失败语义必须先定 |
| 坐标整数格还是连续实数? | 整数格 | 决定 nextInt 还是 nextDouble |
| **共边(挨着)算不算重叠?** | 不算(共边合法) | AABB 判定用 `<` 还是 `<=` 的分水岭 |
| 板一定放得下所有矩形吗? | 不保证——所以才需要失败语义 | 铺垫终止性讨论 |

## 4. 解法推导:先写最笨但正确的(拒绝采样)

```text
对每个小矩形:
  重复至多 maxAttempts 次:
    随机生成 x ∈ [0, W-w],y ∈ [0, H-h]      ← 范围本身保证不出界
    与所有已放置矩形做 AABB 重叠检测
    不重叠 → 放下,处理下一个
  重试耗尽 → 失败(按约定抛异常)
```

开局台词把定位说清:*"I'll start with rejection sampling — the simplest correct approach — and then talk about its weaknesses."* 开放题的最优开局**不是最优算法,是最快到达"能跑 + 能讨论"状态的算法**。

### AABB 重叠判定(LC 836 原题公式,一字不差 + 完整推导)

```java
ax < bx + bw && bx < ax + aw && ay < by + bh && by < ay + ah   // 共边不算重叠
```

**第一步:降维到一根数轴。** 线段 A `[ax, ax+aw]`、线段 B `[bx, bx+bw]` 什么时候重叠?正面枚举相交姿势(左搭/右搭/包含……)容易漏,**反面一步到位:一根数轴上只有两种躲法**——

```text
躲法一:A 完全在 B 左边          躲法二:A 完全在 B 右边
A: ├────┤                                    A: ├────┤
B:         ├────┤                B: ├────┤
条件:ax+aw ≤ bx                 条件:bx+bw ≤ ax
```

没有第三种躲法。取反即公式:

```text
重叠 = 两种躲法都失败 = ax+aw > bx && bx+bw > ax = bx < ax+aw && ax < bx+bw
```

读出声:**"互相都开始于对方结束之前"**,中间必有交集。

**第二步:升维 = 影子判定。** 矩形往 x、y 轴各投一条"影子"。轴对齐矩形的定理:**两矩形重叠 ⟺ 横向影子重叠 且 纵向影子重叠**——因为平面上不斜放的矩形只有**四种躲法**(完全在左/右/上/下),前两种 = 横向影子分离,后两种 = 纵向影子分离;四种全失败必相交。公式就是**一维判定写两遍**(x 一遍、y 一遍)。

**第三步:实测对号**(A 固定 `(0,0,4,3)`,输出为真实运行结果):

| B | x 影子判定 | y 影子判定 | 结论 |
|---|---|---|---|
| (2,1,4,3) 普通相交 | 0<6 ✓ 2<4 ✓ | 0<4 ✓ 1<3 ✓ | **true** |
| (5,0,2,2) 完全在右 | `5 < 4` **✗** 躲法成立 | 不用看 | false |
| (4,0,2,2) 共享右边 | `4 < 4` **✗** | — | **false(共边不算)** |
| (4,3,2,2) 共享一角 | `4 < 4` ✗ | `3 < 3` 也 ✗ | false |
| 包含:A=(0,0,10,10) ⊃ B=(2,2,3,3) | 都 ✓ | 都 ✓ | **true(包含免特判)** |

最后一行多看一眼:**包含也是重叠,公式零特判自动裁对**——"包含"时四种躲法显然全失败。手写时给包含单开分支,是没从躲法角度想的后遗症。

**共边分水岭**:严格 `<` 意味着共边/共角 = 贴着 = 不算重叠(`4 < 4` 为假)——这正是澄清清单"touching counts?"的落点;面试官若说贴着也算,四个 `<` 全换 `<=`。放矩形场景默认共边合法(紧挨着摆恰是想要的)。

**记忆法与自检**:正面版口诀"**我的左边 < 你的右边,双向成立,两轴都成立**";反面版"**四种躲法全失败即相撞**"(忘了公式当场重推,推的过程本身是加分展示)。写完做**对称性自检**:a、b 互换后公式应不变,不对称的式子几乎必错。y 轴朝向(屏幕向下/数学向上)无关紧要——公式只关心区间相交,`ay` 取 y 方向小端即可。

## 5. 三个必须主动讲的工程点(本题的评分主轴)

**① 终止性:必须设 `maxAttempts` 上限。** 纯随机重试可能永远失败(板越满失败率越高),不设上限就是潜在死循环。台词:*"I won't write code that can loop forever — bounded retries, then a defined failure."* 这句话值一分。

**② 可测试性:`Random` 依赖注入。** `Random` 作为参数传入,而不是函数内部 `new Random()`。两个回报:同种子结果**可复现**(线上问题能重放调试);测试可用固定种子做**确定性断言**。这是"如何测试随机代码"的标准答案,面试官几乎必问。测试策略再补一句:**属性测试**——随机跑很多轮,断言**不变量**(全在界内 + 两两不重叠)恒成立,而不是断言具体位置。和 ABC 的校验器思想同源:**答案不唯一时,验证性质而不是比对结果**——IXL 连着两道题都在奖励这个思维。

**③ 死胡同:per-rect 重试不回溯,可能走进死路。** 教科书反例(当场画得出来):

```text
10×5 的板放两个 5×5:唯一解是左右各一
┌─────┬─────┐
│ 5×5 │ 5×5 │   ✓ 唯一可行布局
└─────┴─────┘
但第一个随机落在中间 → 第二个永远放不下,重试多少次都没用
┌──┬─────┬──┐
│  │ 5×5 │  │   ✗ 死胡同:不是运气差,是结构性无解
└──┴─────┴──┘
```

单个矩形的重试解决不了**上一步**的错误。解法:**外层整体重启**(推倒全部重来)——死胡同概率被重启次数指数压低。能主动指出这个缺陷的人,比写出完美代码的人少得多;这也是参考实现里 `placeWithRestarts` 存在的理由。

## 6. 参考实现(已验证)

<details>
<summary><b>展开完整代码(RandomRectangles.java;测试:200 种子属性测试 / 同种子复现 / 紧密布局重启 / 非法参数)</b></summary>

```java
import java.util.*;

public class RandomRectangles {

    /** 依次放置 sizes 里的每个 (w,h);返回每个矩形的 [x,y](左上角)。放不下抛异常。 */
    public static List<int[]> place(int W, int H, List<int[]> sizes, Random rnd, int maxAttemptsPerRect) {
        if (sizes == null || rnd == null) {
            throw new IllegalArgumentException("sizes/rnd must not be null");
        }
        if (W <= 0 || H <= 0) {
            throw new IllegalArgumentException("board must be positive");
        }
        List<int[]> placed = new ArrayList<>();   // 每项 [x, y, w, h]
        for (int[] s : sizes) {
            int w = s[0], h = s[1];
            if (w <= 0 || h <= 0 || w > W || h > H) {
                throw new IllegalArgumentException("rect " + w + "x" + h + " cannot fit board");
            }
            boolean ok = false;
            for (int attempt = 0; attempt < maxAttemptsPerRect; attempt++) {
                int x = rnd.nextInt(W - w + 1);           // 合法起点范围 [0, W-w],注意 +1
                int y = rnd.nextInt(H - h + 1);
                if (!overlapsAny(placed, x, y, w, h)) {
                    placed.add(new int[]{x, y, w, h});
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                throw new IllegalStateException("failed after " + maxAttemptsPerRect + " attempts");
            }
        }
        List<int[]> result = new ArrayList<>();
        for (int[] p : placed) {
            result.add(new int[]{p[0], p[1]});
        }
        return result;
    }

    /** 外层重启版:per-rect 拒绝采样不回溯,可能死胡同;整体重来能解 */
    public static List<int[]> placeWithRestarts(int W, int H, List<int[]> sizes, Random rnd,
                                                int maxAttemptsPerRect, int maxRestarts) {
        for (int r = 0; r < maxRestarts; r++) {
            try {
                return place(W, H, sizes, rnd, maxAttemptsPerRect);
            } catch (IllegalStateException e) {
                // 本轮布局死胡同,整体重启
            }
        }
        throw new IllegalStateException("failed after " + maxRestarts + " restarts");
    }

    /** AABB 重叠判定:两轴投影都相交才算重叠(共边不算) */
    static boolean overlaps(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
        return ax < bx + bw && bx < ax + aw && ay < by + bh && by < ay + ah;
    }

    private static boolean overlapsAny(List<int[]> placed, int x, int y, int w, int h) {
        for (int[] p : placed) {
            if (overlaps(x, y, w, h, p[0], p[1], p[2], p[3])) {
                return true;
            }
        }
        return false;
    }
}
```

</details>

边写边说的两个细节:`rnd.nextInt(W - w + 1)` 的 **+1**——合法起点是闭区间 `[0, W-w]`,`nextInt` 是右开的,漏 +1 矩形永远贴不到右/下边界(不崩溃、但分布错了,这种"静默偏差"比崩溃更阴);返回前重新拼 `[x,y]` 列表,内部的 `[x,y,w,h]` 工作表示不外泄。

## 7. 深水区:"随机"到底什么意思(这题深度的天花板)

拒绝采样对**单个矩形**是在"当前所有合法位置"上均匀的;但**整体布局**的分布不均匀——先放的矩形占据了选择权,后放的只能捡剩下的空间,某些布局形态会被系统性偏爱。如果面试官要的是"**所有合法布局等概率**",那是难得多的问题(要枚举全部布局,或 MCMC 采样),没人指望你现场解。

打法:**主动把这个区别说出来,然后确认档次**——*"Rejection sampling is uniform per placement, but not uniform over all layouts — earlier rectangles constrain later ones. Is per-placement uniformity acceptable, or do we need uniformity over layouts? The latter is a much harder sampling problem."* 说完这段,这题的深度你就见底了,面试官通常会说"per placement 就够"。

## 8. 复杂度(说出口的版本)

> *"Each attempt does an O(k) scan over the k placed rectangles, so placing n rectangles is O(n² · attempts) worst case. Attempts scale inversely with free-space density — the fuller the board, the more retries. Space is O(n). For many rectangles I'd switch the collision check to a spatial grid, making each attempt amortized O(1)."*

## 9. Follow-up 全集(带详细答案)

### FU1 — "怎么保证一定终止?"

把随机重试换成**确定性枚举 + 随机顺序**:把所有候选位置离散成网格,shuffle 后依次试——试完没有就是真没有,**无死循环、无死胡同误报**(对单个矩形而言),代价是 O(候选数 × 已放数) 和一点均匀性偏差。这是"拒绝采样 ↔ 穷举"的经典权衡:**随机换速度,枚举换保证**。

### FU2 — "有没有更快且必成功的放法?"

**Guillotine(空闲区域切割)**:维护"空闲矩形列表",随机挑一块能容纳的,把矩形放进去(位置在块内随机),剩余空间切成两块放回列表。快、必成功(只要有块容得下)、无重试;代价是随机性有偏(切割顺序塑造了可能的布局形态)。游戏里的贴图打包(texture packing)就是这个思路。

### FU3 — "一万个矩形怎么办?"

碰撞检测从 O(已放数) 线性扫描升级为**空间网格哈希**:板划成格子,每格记录覆盖它的矩形;新矩形只查自己覆盖的格子。均摊 O(1)/次。这是所有 2D 碰撞系统的标准第一课。

### FU4 — "支持删除矩形、空间复用?"

拒绝采样天然支持(删除后空间自动"可命中");guillotine 路线则要做**空闲块合并**(相邻空闲块拼回大块),这是它的经典难点——说出"free-list coalescing"这个词就够了,内存分配器同款问题。

### FU5 — "允许旋转 90°?"

每次尝试先随机(或依次)选方向 `(w,h)` / `(h,w)`,其余逻辑不动。注意 `w == h` 时别重复计数方向。一句话的改动,考的是你听到需求后改动最小。

### FU6 — "怎么测试随机代码?"(几乎必问;注意本节的证据等级)

先亮底:**属性测试不是面经记载的考纲,是推演的高分打法**。但"你怎么测它"这个问题本身无处可逃——随机代码写不出"硬编码期望输出"的测试,你必须回答"断言什么"。答案分**一个地基 + 两条正路**:

**地基(不可少):注入 Random。** 不注入的话连固定种子都做不到,**任何**测试都写不出来。只记一件事就记它。

**路一:固定种子的确定性测试(golden test)。** 注入后同种子输出完全确定,可以断言具体坐标。合法、直觉,但**脆**——实现里随机调用的顺序稍一变(先生成 y 再生成 x),所有断言全失效;且只覆盖那几个种子走过的路径。

**路二:属性测试(不变量断言)。** 不管输出具体是什么,断言性质恒成立:全在界内 + 两两不重叠。健壮、覆盖广,代价是要想清楚"不变量是什么"。与 ABC 的校验器同源:**答案不唯一时,验证性质而不是比对结果**。

实战 = 组合:固定种子保可复现 + 不变量保正确性 + 边界用例(恰好塞满、放不下、1×1)。本实现即如此:200 种子属性测试 + 同种子两次调用逐字节一致 + 紧密布局走重启版。台词:*"First I'd inject the Random so runs are reproducible. Then two kinds of tests: fixed-seed tests for reproducibility, and property tests asserting the invariants — everything in bounds, no two overlap — since exact positions aren't meaningful to assert."*

### FU7 — "死胡同为什么不用回溯(backtracking),而用整体重启?"

回溯是合法替代(N-Queens 同款直觉),没选它是三笔账:

**第一笔:回溯的前提在这里不成立。** N-Queens 每层候选**有限、可枚举、可穷尽**(第 k 行就 N 列,换"下一个没试过的",试完即终止)。这里每层候选是整个 `(W−w+1)×(H−h+1)` 位置空间——"撤回后换个随机位置再试"没有"下一个"的概念:会重复、无进度、无终止保证。**随机版回溯依然是启发式,却背上了回溯的全部记账成本**(撤销栈、每层状态),两头不占。要严谨必须先离散化枚举——那是指数搜索树,bin-packing(NP-hard)的地界,只对很小的盘面可行。

**第二笔:重启的经济学恰好压中失败模式。** 死胡同是**罕见事件**(正常填充率下多数轮一把成功)——回溯为治罕见病让每轮都付记账成本,重启平时零开销。且死胡同的元凶通常是**早期**摆放(占住正中的那个),回溯按 LIFO 撤销,把浅层组合试穿了才摸到真凶;**重启一步炸掉元凶**。数学干净:单轮成功率 p,失败概率被 (1−p)^R 指数压低,期望重启 1/p 次。代码 6 行 try/catch 对回溯的一整套机械,性价比一边倒。

**第三笔:什么时候才轮到回溯/系统搜索。** 高填充率紧密盘面:p 趋近 0,1/p 爆炸,随机撞不进去了——离散化 + 回溯剪枝或装箱启发式登场。谱系:

```text
稀疏盘面 → 拒绝采样 + 整体重启(随机便宜,失败罕见)
紧密盘面 → 离散枚举 + 回溯/装箱启发式(随机失效,必须系统搜索)
```

口播:*"Backtracking assumes each level has an enumerable, exhaustible candidate set — true for N-Queens' columns, not for thousands of random positions. Since dead-ends are rare and usually caused by early placements, a global restart hits the culprit directly with independent retries and exponentially-suppressed failure. If the board were tightly packed, I'd switch to discretized systematic search — that's bin-packing territory."*

## 10. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **AABB 写错** | 重叠判定失效,输出非法 | 从"不重叠的四种躲法"取反推导;共边用 `<`(不算重叠) |
| **`nextInt(W - w)` 漏 +1** | 矩形永远贴不到右/下边界——静默分布偏差 | 合法起点是闭区间 `[0, W-w]`,`nextInt(W - w + 1)` |
| **不设 maxAttempts** | 板满时死循环 | 有界重试 + 定义失败行为,并把这句说出来 |
| **函数内部 `new Random()`** | 不可复现、不可测试 | 依赖注入,Random 作参数 |
| **不知道死胡同存在** | 被 10×5 放两个 5×5 一问击穿 | per-rect 重试不回溯 → 外层整体重启 |
| 断言具体坐标的测试 | 随机代码测试写不下去 | 属性测试:验证不变量 |
| "随机"档次不确认 | 深水区被动挨打 | 主动区分 per-placement vs per-layout 均匀 |
| 修改传入的 sizes / 泄漏内部表示 | 工程细节丢分 | 只读输入;返回重新拼的 `[x,y]` 列表 |
| 矩形比板大不检查 | 死循环或负数 nextInt 崩溃 | 入口参数校验,`nextInt` 参数必须 ≥1 |

## 11. 30 分钟考场节奏 + 互动台词

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–4 min | 澄清(第 3 节 7 连问) | "The problem leaves a lot open — let me pin down sizes, rotation, what 'random' means, failure behavior, and whether touching edges count as overlap." |
| 4–7 min | 方案 + **要 buy-in** | "I'll start with rejection sampling — simplest correct thing — with bounded retries, then discuss its weaknesses. Sound good?" |
| 7–17 min | 写 place + overlaps,持续 narrate | "Generating x in [0, W−w] keeps the rectangle in bounds by construction — one less check." |
| 17–22 min | **主动**报三工程点 | 终止性 / Random 注入 / 死胡同 + 重启(第 5 节三段台词) |
| 22–26 min | 测试讨论 | "Property-based: fixed seeds, assert in-bounds and pairwise disjoint — never exact positions." |
| 26–30 min | 深水区 + follow-up | 第 7 节 uniformity 台词 + FU1–FU3 按需接招 |

**心态设定**:这题面试官插话越多越是好事——开放题的插话就是对话,对话就是这题的评分项。

## 12. 30 秒总结陈词(背诵版)

> *"Rejection sampling: for each rectangle, generate a position in the always-in-bounds range, AABB-check against placed ones, bounded retries, defined failure. Random is injected so runs are reproducible and tests assert invariants — in-bounds and pairwise disjoint — never exact positions. Two known weaknesses I'd call out: per-rectangle retries can't undo earlier placements, so a global restart layer handles dead-ends; and it's uniform per placement, not over all layouts — if layout-level uniformity is required, that's a much harder sampling problem. For scale, a spatial grid makes each collision check amortized O(1)."*

**记忆钩子**:笨办法开局 + 三工程点(有界重试 / 注入 Random / 死胡同重启)+ AABB 一行 + `nextInt` 的 +1 + 均匀性分档 + 属性测试。

---

## 附:Java Random 零基础速成(含 seed 深讲)

### 心智模型:一盘由 seed 决定的磁带

`Random` 是**伪随机**:内部一个状态数,每次调用按死公式算出下一个数并更新状态。它本质是**一条被 seed 完全决定的数列磁带**——每次 `nextInt(...)` 就是磁带前进一格读一个数。实测(输出原样):

```text
new Random(42) 第一台: 30 63 48 84 70 25     ← 同种子
new Random(42) 第二台: 30 63 48 84 70 25     ← 序列一模一样
new Random(7)  另一台: 36 64 85 44 80 54     ← 换种子换磁带
```

"看起来随机、实际可复现"不是缺陷,**是最有用的特性**。

### 两种构造

```java
Random r = new Random();      // 系统挑难预测的种子:每次运行不同 → 生产用
Random r = new Random(42);    // 固定种子:每次运行完全一样 → 测试/调试/复现用
```

### 常用 API + 三个食谱

| 调用 | 产出 | 备注 |
|---|---|---|
| `nextInt(bound)` | `[0, bound)` 均匀整数 | **主力**;右开;`bound ≤ 0` 抛异常 |
| `nextInt()` | 全体 int(含负数) | 几乎不用,别误拿 |
| `nextDouble()` | `[0.0, 1.0)` | 连续场景 |
| `nextBoolean()` / `nextGaussian()` | 均匀真假 / 标准正态 | 知道即可 |

```java
int x = a + rnd.nextInt(b - a + 1);            // 闭区间 [a, b](经典公式,-a 再 +1)
T pick = list.get(rnd.nextInt(list.size()));   // 随机挑元素
Collections.shuffle(list, rnd);                // 洗牌(传 rnd 保持可复现)
```

### 本题三处应用

1. **`rnd.nextInt(W - w + 1)` 的 +1**:合法 x 是闭区间 `[0, W−w]`。W=10、w=5 → 合法值 {0..5} 共 **6** 个 = `nextInt(6)`(实测 1000 次恰好覆盖 [0,5])。写成 `nextInt(W−w)` 则矩形永远贴不上右边界——不崩溃的静默分布偏差,全题最阴的坑。
2. **注入 = 可测试性的开关**:`Random` 当参数传,测试给 `new Random(42)`,生产给 `new Random()`。函数内部自己 `new` 的话,任何测试都写不出来。
3. **seed 重放调试**:磁带决定一切 → 日志记 seed,出问题拿 seed 整局重放,一步不差。随机代码调试的标准姿势。

### seed 到底是什么:环形轨道与上车站点

生成器内部只是一条递推:`新状态 = (a × 旧状态 + c) mod m`(线性同余法,Java 即此类)。玩具参数 `(5×旧+3) mod 16` 实测:

```text
seed=7 : 6 1 8 11 10 5 12 15 14 9      ← 从 7 出发的脚印,永远这一条
seed=7 : 6 1 8 11 10 5 12 15 14 9      ← 再来一遍,分毫不差
seed=9 : 0 3 2 13 4 7 6 1 8 11         ← 走到 7 之后,汇入上面同一条路!
```

第三行是精髓:状态空间有限、每个状态的"下一步"唯一 → 整个空间被织成**固定的环形轨道**;不同 seed 只是**不同的上车站点**,走的是同一条轨道。"随机"只是公式把位搅得够乱,看不出规律而已——全程确定,这就是"伪"。

真实的 `java.util.Random`:48 位状态,`新 = (0x5DEECE66D × 旧 + 0xB) mod 2⁴⁸`,`nextInt` 取高 32 位(高位更均匀)。两个细节:**seed 不是第一个输出**(seed=42 的第一个 `nextInt(100)` 是 30——seed 进门先被异或搅拌成初始状态,输出是状态的高位);**`new Random()` 的种子从哪来**——`System.nanoTime()` 混合一个原子递增计数器,连续两次 `new Random()` 也不同。"不给 seed" = "让库替你挑个难预测的上车点"。

### 三条进阶常识(防追问,各一句)

- **调用顺序敏感**:磁带顺序消费,交换"先 x 后 y"为"先 y 后 x",同 seed 输出全变——golden test 脆的根源,属性测试更稳的理由。
- **多线程**:`Random` 线程安全但高并发慢(内部 CAS 竞争),并发用 `ThreadLocalRandom.current().nextInt(...)`。
- **安全场景**:轨道是死的,少量输出可反推状态、预测未来——抽奖/token/密码必须 `SecureRandom`(熵来自操作系统,不可反推);反之模拟用 SecureRandom 是白慢。

**记忆钩子**:seed 是起点不是输出;起点一定,全程皆定;测试钉住种子,生产放开种子;闭区间长度 = b − a + 1;磁带从门口递进来,别在函数里自己造。
