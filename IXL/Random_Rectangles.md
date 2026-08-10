# IXL VO — Random Rectangles 终极版(Java · 拒绝采样 + 开放讨论题的打法)

> **情报卡**:面经 #10 / #16,**VO 出现 2 次**,第二轮方向,押题榜第四位(与 Italic Parser 并列)。无 LC 原题。
> **代码状态**:`RandomRectangles.java` JDK 21 编译通过;200 种子属性测试、同种子复现性、紧密布局重启、非法参数防御全绿。
> **本题特殊性**:四题里唯一的**开放题**——没有唯一正解,**写代码占一半分,讨论 trade-off 占另一半**。评分的主轴是对话质量:先问清需求、从最简单方案写起、**主动说出它的缺陷和改进方向**。这是 IXL"互动为纲"最极致的一题。

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

### AABB 重叠判定(一行,背下来)

两矩形重叠 ⟺ 在 x、y 两个轴上的投影**都**相交:

```java
ax < bx + bw && bx < ax + aw && ay < by + bh && by < ay + ah   // 共边不算重叠
```

推导方式记"反面":**不重叠 = 我完全在你左边 / 右边 / 上边 / 下边**,四个条件取反再德摩根,就是上面这行。共边要算重叠的话,把 `<` 全换成 `<=`——这正是澄清清单里"共边算不算"的落点。

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

### FU6 — "怎么测试随机代码?"(几乎必问)

三板斧:**注入 Random**(同种子可复现,测试确定性);**属性测试**(随机多轮,断言不变量:界内 + 两两不重叠,而非具体坐标);**边界用例**(恰好塞满的板、放不下的板、1×1 矩形)。本实现的测试就是这么写的:200 种子属性测试 + 同种子两次调用逐字节一致 + 紧密布局走重启版。

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
