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
| **★ 随机本身是需求吗?**(每次调用要产出不同布局,还是任意一个合法布局即可?) | 是——题面写了 random,本题是**生成器题**不是求解题 | **一个问题决定写哪半边世界**:若 any valid 即可,整题塌缩成确定性装箱(FU8),Random/shuffle/重启全部拆除 |
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

**实测数字(10×5 放两个 5×5,万次试验)**:第一块的 6 个可能位置里 4 个是死胡同 → `place` 单跑成功率理论 1/3,实测 **33.3%**;套上 20 次重启,失败率 (2/3)²⁰ ≈ 0.03%,实测 **100.0%**。单跑失败时重试当前矩形五万次也没用(结构性无解),重启一步炸掉元凶——**内层重试治运气,外层重启治决策**。

`placeWithRestarts` 里还有两个可讲的设计点:**① 机制与策略分层**——`place` 是机制(诚实试一局,有界重试,失败明确抛出),wrapper 是策略(要不要再来、来几局),分开后调用方按场景选(UI 要快速失败用裸 `place`,批任务要尽力成功用 restarts);**② 异常类型在做分诊**——wrapper 只 catch `IllegalStateException`(运气/死胡同病:换一局可能好),`IllegalArgumentException`(参数病:矩形比板大,重来一万次也没救)直接穿透上抛。**异常类型区分了"可重试的失败"和"不可重试的失败"**——HTTP 客户端对 503 重试、对 400 不重试的同款纪律。

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

## 7. 深水区:采样零基础 +"随机"到底什么意思(这题深度的天花板)

### 7.1 采样与拒绝采样(零基础版)

**采样 = 让随机输出服从你想要的分布。** 手里的工具只有均匀骰子(`nextInt`),想要的却是"某个复杂集合上均匀的一个"——采样方法就是把骰子加工成目标分布的技术。**拒绝采样**只有三步:① 找一个包住合格区域的简单大区域(它能直接均匀抽);② 从大区域均匀抽一个;③ 合格收下,不合格**扔掉重抽**。收下的样本在合格区域内自动均匀——因为大区域里每点机会本来相同,"扔掉不合格"只是过滤,**不改变合格点之间的相对比例**。经典例:圆内均匀取点(LC 478)= 外接正方形里抽、落圆外扔。效率 = 合格面积 ÷ 大区域面积(圆/正方形 ≈ 78.5%)——**合格区越小扔得越多,这就是"板越满重试越多"的数学本质**。本题:大区域 = `[0,W−w]×[0,H−h]`,检查 = AABB,拒绝 = 重摇。

### 7.2 每步均匀 ≠ 布局均匀(骨牌实验,数字实测)

玩具盘面:1×5 的板依次放两个 1×2 骨牌(起点 0–3,起点差 <2 算重叠)。合法**最终布局**只有三种:`{0,2}`、`{0,3}`、`{1,3}`。每步都用"当时合法集合上均匀抽",逐路径算:

| 第一步(各 1/4) | 第二步合法集 | 产出布局 |
|---|---|---|
| p1=0 | {2,3} 各 1/2 | {0,2} 得 1/8;{0,3} 得 1/8 |
| p1=1 | 只有 {3} | {1,3} 得 **1/4** |
| p1=2 | 只有 {0} | {0,2} 得 **1/4** |
| p1=3 | {0,1} 各 1/2 | {0,3} 得 1/8;{1,3} 得 1/8 |

汇总:`{0,2}` = **3/8**,`{0,3}` = **2/8**,`{1,3}` = **3/8**(百万次模拟实测 0.374 / 0.250 / 0.376,吻合)。**每一步都均匀,最终却不均匀**——因为**第二步的分母不一样**(有的第一步之后剩 2 个选择,有的剩 1 个),不同路径携带不同权重。这就是 "earlier rectangles constrain later ones" 的全部含义:**局部均匀叠不出全局均匀**。

### 7.3 打法

要"所有合法布局等概率"难得多:得枚举全部布局(装箱计数,组合爆炸)或上 **MCMC**(在布局间随机游走至收敛)——说出名词即到顶。考场动作:**主动把区别说出来,然后确认档次**——*"Rejection sampling is uniform per placement, but not uniform over all layouts — earlier rectangles constrain later ones. Is per-placement uniformity acceptable, or do we need uniformity over layouts? The latter is a much harder sampling problem."* 面试官通常答"per placement 就够"。

## 8. 复杂度(说出口的版本)

> *"Each attempt does an O(k) scan over the k placed rectangles, so placing n rectangles is O(n² · attempts) worst case. Attempts scale inversely with free-space density — the fuller the board, the more retries. Space is O(n). For many rectangles I'd switch the collision check to a spatial grid, making each attempt amortized O(1)."*

## 9. Follow-up 全集(带详细答案)

### FU1 — "怎么保证一定终止?"(抽签不放回)

骰子的深层缺陷是**有放回**:可能反复试同一位置(浪费),且 `maxAttempts` 用尽时**开不出"不存在"的证明**——"有位置但运气差"和"真没位置"无法区分,可能误报失败。药方:**确定性枚举 + 随机顺序**——候选位置在整数格上有限(`(W−w+1)×(H−h+1)` 张签),全部列出、`shuffle`、按序试第一个合法的;**整表走完都不合法 = 证明了不存在**。每张签恰好试一次,死循环结构上不可能。代价:单矩形最坏 O(候选数 × 已放数) + 候选表内存——"贵但有底"换"快但没底":**随机换速度,枚举换保证**。

与原版的差别只在"给一个矩形找位置"这一段循环,其余(参数校验、AABB、返回、restart 用法)一字不变:

```java
// ── 原版(骰子):随机猜,可能重复,需要人为上限 ──
for (int attempt = 0; attempt < maxAttemptsPerRect; attempt++) {
    int x = rnd.nextInt(W - w + 1);
    int y = rnd.nextInt(H - h + 1);
    if (!overlapsAny(placed, x, y, w, h)) { placed.add(new int[]{x, y, w, h}); ok = true; break; }
}
// 失败时只知道"猜了 50 次没中"——有没有位置?不知道。

// ── FU1 版(名单 + 洗牌):注意 maxAttemptsPerRect 参数直接消失 ──
List<int[]> candidates = new ArrayList<>();
for (int x = 0; x <= W - w; x++) {              // ① 列名单:所有可能位置,一个不漏
    for (int y = 0; y <= H - h; y++) {
        candidates.add(new int[]{x, y});
    }
}
Collections.shuffle(candidates, rnd);           // ② 打乱顺序(随机性只来自这里)

boolean ok = false;
for (int[] c : candidates) {                    // ③ 挨个试,每个位置恰好试一次
    if (!overlapsAny(placed, c[0], c[1], w, h)) {
        placed.add(new int[]{c[0], c[1], w, h});
        ok = true;
        break;
    }
}
if (!ok) throw new IllegalStateException("no legal position EXISTS");
// ↑ 失败时知道的是"全部位置都亲自试过了,确定没有"。
```

三处肉眼可见的差别:`maxAttemptsPerRect` **参数消失**(名单有限,循环天然会停);两行 `nextInt` 变成**列名单 + 洗一次牌**;失败信息从"50 次没猜中"升级为"**确定不存在**"。完整可跑的对比实现与实测(骰子 7.6% vs 名单 18.0% 打满理论上限)在 `EnumDemo.java`。

三个精度点:**① 分布毫无损失**——整数坐标 + 真 shuffle 下,"随机排列取第一个合法"在合法集上**严格均匀**(对称性:合法候选谁排最前机会相同),与拒绝采样每步分布一模一样;偏差只在"连续坐标被离散化"或"随机起点线性扫描"这类偷工时出现。**② 布局层面的不均匀原样保留**(每步分布相同 ⟹ 布局分布相同)——FU1 改的是终止保证,不是分布。**③ 死胡同不消失,只是实锤化**:枚举证明的是"在已放矩形前提下没位置",前提本身可能错,外层 restart 照旧;区别是重启触发从"疑似"变"证明",不再有冤案。

实战答案常是**混合**:先扔几十次飞镖(稀疏时快),不中再退回枚举(出证明兜底)。口播:*"Rejection sampling samples with replacement — it can never prove a spot doesn't exist. Enumerate the finite grid, shuffle, take the first legal: same per-placement distribution, but exhausting the list is a proof. In practice I'd try random darts first and fall back to enumeration."*

**零件分工(一句话钉死)**:**名单管"不重复",洗牌管"随机"**——去重是"列名单"天然自带的(不洗牌按序走照样不重复、照样能证明不存在),shuffle 只负责打乱顺序。而 shuffle 有**双重身份**:对外满足"随机摆放"的产品需求(不洗则永远选最小 (x,y),矩形全堆左上角、布局千篇一律);对内给 restart 供应多样性(不洗则 place 变确定性函数,每轮重启重放**同一个**死胡同,重启形同虚设)。作用范围也要说准:"不重复"是**单矩形单轮**的承诺;跨矩形、跨重启的重新尝试两版都有且必要——盘面变了,同一位置的占/空答案也变了。

### FU2 — "有没有更快且必成功的放法?"

**Guillotine(空闲区域切割)**:维护"空闲矩形列表",随机挑一块能容纳的,把矩形放进去(位置在块内随机),剩余空间切成两块放回列表。快、必成功(只要有块容得下)、无重试;代价是随机性有偏(切割顺序塑造了可能的布局形态)。游戏里的贴图打包(texture packing)就是这个思路。

### FU3 — "一万个矩形怎么办?"(空间网格 = HashMap 思想搬进二维)

`overlapsAny` 是全员点名:每次尝试 O(已放数),n 个矩形 O(n²)。浪费显而易见——碰撞是**邻居间的事**,左上角的新矩形不可能撞到右下角的旧矩形,但平铺 List 不知道谁是邻居。

**空间网格(spatial grid)**:板划成边长 cellSize 的格子,维护 `格子 → 覆盖它的矩形列表`;新矩形用整数除法算出自己覆盖哪些格子(`x/cellSize` 到 `(x+w)/cellSize`,y 同理),**只跟这几格里登记的矩形做 AABB**,放置成功后把自己登记进去。具体感受:1000×1000 板、cellSize=50,一个 40×30 的矩形只占 2 个格子——就算全板已放一万个,也只查这两格里的几个。

```java
Map<Long, List<int[]>> grid = new HashMap<>();
long key(int cx, int cy) { return ((long) cx << 32) | cy; }   // 格坐标拼成桶号
// 查询与登记:遍历矩形覆盖的 (cx,cy) 范围,访问对应桶
```

**为什么均摊 O(1)**:cellSize 选得和典型矩形尺寸相当时,每个矩形只覆盖 O(1) 个格、每格只装得下 O(1) 个互不重叠的矩形 → 候选集平均常数个。说"均摊"是诚实:格子太大退化回线性,太小则登记成本上升——**经验法则 cellSize ≈ 典型矩形边长**。小细节:横跨多格的候选可能被查两次,用 Set 去重或干脆接受(AABB 一行很便宜)。

**点破本质**:*"It's exactly the HashMap bucket idea — the cell coordinate is the key."* HashMap 把"n 里线性找"变"算桶号看桶内几个",网格把"跟 n 个比"变"算格号比格内几个"。谱系一句防深挖:均匀网格是最简空间索引;尺寸悬殊用 quadtree(按需细分),数据库/地图用 R-tree、geohash;游戏引擎碰撞粗筛(broad-phase)就是这套。

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

### FU8 — "如果不需要随机(any valid placement)呢?"(整题塌缩)

本题是**生成器题**(合法布局千千万,每次随机抽一个);把 random 拿掉就变**求解题**(给一个合法布局即可),整套随机机器全部拆除,答案是**确定性 first-fit(贴瓷砖)**:

```java
for (int[] s : sizes) {                          // 逐个矩形
    boolean ok = false;
    for (int y = 0; y <= H - s[1] && !ok; y++)   // 行优先扫描
        for (int x = 0; x <= W - s[0] && !ok; x++)
            if (!overlapsAny(placed, x, y, s[0], s[1])) {
                placed.add(new int[]{x, y, s[0], s[1]});
                ok = true;                        // 第一个能放的就放,绝不挑
            }
    if (!ok) throw new IllegalStateException("no spot");
}
```

没有 Random、shuffle、maxAttempts、restarts;同输入永远同布局,矩形从左上角码起。两个补充:first-fit 快但**不完备**(装箱 NP-hard,贪心可能在有解时失败)——要完备就上**回溯**(每矩形记住名单试到第几号,无处可放退回上一个换下一候选,N-Queens 结构原样平移;FU7 那句"回溯是可枚举世界的工具"在此应验);先把矩形**按面积降序排序**再放(First-Fit Decreasing)成功率大增,一行 sort 的事。

| | random 版(原题) | any valid 版 |
|---|---|---|
| 随机源 | Random + shuffle | **无** |
| 找位置 | 骰子猜 / 名单洗牌 | 固定顺序扫描,第一个能放就放 |
| 死胡同对策 | **整体重启**(价值来自多样性) | **回溯**(价值来自可穷尽) |
| 多次调用 | 每次不同布局 | 永远同一布局 |
| 失败含义 | 概率性放弃 / 枚举后证明 | 回溯搜完 = 确凿无解 |

口播:*"If any valid placement is acceptable, this stops being a sampling problem — deterministic first-fit, no randomness at all; sort by size descending to help it, add backtracking if completeness is required. The whole random machinery only exists because randomness was a requirement."* 这正是澄清清单第一问的价值:**一个问题决定写哪半边世界**。

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

### 磁头规则:seed 定磁带,对象是磁头,只进不退

"同 seed 重跑 place 结果一样"要看 Random 怎么给,实测(100×100 放两个 10×10):

```text
每次新建 new Random(42) 跑 place:  (78,33)(48,38) / (78,33)(48,38) / (78,33)(48,38)  ← 三次逐字相同
同一个 Random(42) 对象连跑三次:    (78,33)(48,38) / (26,4)(78,3) / (55,80)(52,39)    ← 只有第一次相同
```

规则:`new Random(42)` = 把磁头放回 42 号磁带开头;每次 `nextInt` 磁头前进一格,**永不倒带**。同 seed **重新新建** → 整条抽数序列逐格重放,执行路径(每次撞车、每次重试)分毫不差;同一**对象**连用 → 第二次从磁带中间继续,全不同。

漂亮的闭环:**`placeWithRestarts` 能起作用,恰恰依赖"不倒带"**——内部每轮重启用同一个 rnd,磁带继续走,每局抽到不同布局才逃得出死胡同;若每局都 `new Random(seed)` 倒带,每次重启会一模一样地掉进同一个死胡同,重启一万次也是死。而从外面看 `placeWithRestarts(..., new Random(seed), ...)` 整体又可复现(重放的是"整串重启尝试")。**局部靠前进制造多样性,整体靠回带保住复现性。**

### Collections.shuffle 速成

**原地把列表洗成均匀随机顺序**(元素不增不减,只乱序;返回 void)。要点四条:

1. **必须传可变列表**:对 `List.of(...)`(不可变)直接洗抛 UnsupportedOperationException,先 `new ArrayList<>(...)` 包一层。
2. **双参版 `shuffle(list, rnd)` 才保可复现**:单参版内部自己 new Random,种子失控;传 rnd 进去,洗牌消费的是你那盘磁带——"Random 从门口递进来"同一条纪律。
3. **内部是 Fisher–Yates**,O(n) 且对全部 n! 种排列严格等概率:`for (i = n−1; i ≥ 1; i--) swap(i, rnd.nextInt(i + 1))`——注意随机范围是 `[0, i]` 不是 `[0, n)`。
4. **两种经典的错误洗牌**(被问能答):每次 `nextInt(n)` 全范围交换 → 路径数 nⁿ 除不尽 n!,必然有偏;用随机比较器排序洗牌 → 违反比较器契约,可能抛异常且分布错。**Java 里自己写洗牌几乎总是错误的开始,用现成的 `Collections.shuffle`。**

### 三条进阶常识(防追问,各一句)

- **调用顺序敏感**:磁带顺序消费,交换"先 x 后 y"为"先 y 后 x",同 seed 输出全变——golden test 脆的根源,属性测试更稳的理由。
- **多线程**:`Random` 线程安全但高并发慢(内部 CAS 竞争),并发用 `ThreadLocalRandom.current().nextInt(...)`。
- **安全场景**:轨道是死的,少量输出可反推状态、预测未来——抽奖/token/密码必须 `SecureRandom`(熵来自操作系统,不可反推);反之模拟用 SecureRandom 是白慢。

**记忆钩子**:seed 是起点不是输出;起点一定,全程皆定;测试钉住种子,生产放开种子;闭区间长度 = b − a + 1;磁带从门口递进来,别在函数里自己造。
