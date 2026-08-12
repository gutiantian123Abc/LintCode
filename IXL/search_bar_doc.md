# IXL VO — 读 Doc 题(Search Bar 渲染)终极版(Java · 流程题的打法)

> **情报卡**:面经 #12,VO 后段轮次方向。**全题池里证据等级最特殊的一题**:真实考题的规则写在**现场发给你的 doc** 里,本文题面是按面经线索("有一些 rules,尽可能显示最多的搜索结果")做的**合理重构**——现场规则几乎必然和这里不同。
> **因此复习目标不是记住规则,是练熟"读需求的六步流程"**——规则换成什么样都不慌。本文题面只是练习用的例题,现场一切以发给你的 doc 为准。
> **代码状态**:`SearchBarRenderer.java` JDK 21 编译通过,逐规则用例全绿。

---

## 1. 这题真正考什么(和其他题都不同)

别的题考你"会不会解",这题考你**"能不能把一页英文需求变成正确的代码"**——工程师日常工作的直接采样。评分面:

| 评分点 | 对应动作 |
|---|---|
| 需求提取 | 把散落在段落里的规则编号成 R1、R2… |
| 需求确认 | 复述给面试官,当场纠偏 |
| 留白嗅觉 | 主动挑出 doc 没定义的行为(留白是**故意**的) |
| 规则可验证化 | 每条规则翻译成一个测试用例 |
| 可审查的实现 | 代码注释标规则号,面试官对照 doc 零成本检查 |
| 验收意识 | 用 doc 自带的例子走查 + 自报边界 |

一句话:**这是四题里"先约定再动手"哲学最纯粹的形态**——约定本身就是题目。

## 2. 六步流程(全文唯一要背的东西)

1. **通读一遍,给规则编号**——doc 的规则常藏在叙述段落里,先提取成 R1、R2…,写在纸上/注释区。
2. **复述确认**:*"Let me make sure I've got the rules right: R1…, R2…, did I miss anything?"*——理解错误在这一步的成本是 30 秒,写完代码后是 20 分钟。
3. **主动挑留白**:规则冲突时听谁的?空输入?并列怎么排?上限不够放一个组怎么办?——doc 一定有没定义的行为,挑出来问,这是本题最大的加分动作。
4. **每条规则先翻译成一个测试用例**,然后才动手写实现——测试即需求验收单。
5. **实现里用注释标规则号**(`// R4 保底`)——让面试官对照 doc 检查你的代码零成本。
6. **用 doc 里自带的例子当场走查**,再自报边界 case("what would break this")。

六步的英文开场白(读完 doc 的第一句话):*"I'll go through the doc once and number the rules, then read them back to you to make sure I've got them right before writing anything."*

## 3. 重构题面(练习实例;现场以 doc 为准)

**场景**:搜索框的下拉建议列表。用户输入关键词,系统返回一批**已按 relevance(相关性)从高到低排好序**的结果;每条结果有 `title` 和 `category`(如 Math、ELA)。屏幕空间有限——下拉列表**最多显示 `maxRows` 行**。任务:实现 `render(results, maxRows)`,决定**显示什么、怎么排**,输出最终的行列表。

**输入 / 输出长这样**(先看效果,再读规则):

```text
输入 results(已按 relevance 从高到低): M1(Math), E1(ELA), M2(Math), E2(ELA)
输入 maxRows = 6

输出(恰好 6 行;每行要么是 header,要么是 result):
  [Math]        ← Math 的 header,自己占 1 行
    M1
    M2
  [ELA]         ← ELA 的 header
    E1
    E2
```

**关键理解**:当 `maxRows` 放不下全部内容时,必须**做取舍**——可以整个 group 不显示,也可以一个 group 只显示前几条。下面六条规则就是取舍的**约束**(R1–R5)和**目标**(R6)。每条:英文原文 + 中文白话:

- **R1** *Displayed results are grouped by category; groups appear in the order each category **first appears** in `results`.*
  显示的 results 按 category 分组;group 的先后 = 各 category 在 `results` 里**第一次出现**的先后。上例:M1(Math)排在 E1(ELA)前面,所以 Math group 在 ELA group 前面。
- **R2** *Each displayed group has a **header row** (costs 1 row).*
  每个被显示的 group 上方有一行 header(如 `[Math]`),**header 本身占掉 1 行**。
- **R3** *Within a group, results keep relevance order.*
  group 内部的 results 保持 relevance 顺序(M1 在 M2 前面)。
- **R4** *A displayed group must contain **at least one** result (no orphan headers).*
  不允许"只有 header、下面一条 result 都没有"的空 group。
- **R5** *Total rows (headers + results) ≤ `maxRows`.*
  输出总行数 = header 行数 + result 行数,不能超过 `maxRows`。
- **R6** *Goal: show as **many results** as possible; ties → **fewer groups**; still tied → groups whose first appearance is **earlier**.*
  取舍的目标,按优先级:① 显示的 **result 条数**尽量多(header 不算在内);② 条数并列时,group 数**更少**的方案赢;③ 仍并列,选 first appearance **更早**的那些 group。

## 4. 解法推导(讲给面试官听的顺序)

**核心观察:每个 group header 自己占 1 行(R2)。** 总共只有 `maxRows` 行;选了 k 个 group,就有 k 行被 header 用掉,**留给 results 的只剩 `maxRows − k` 行**。所以多显示一个 group 不一定划算——多一个 header 就少一行 result。R6 的关键例子(逐规则测试第 2 条)就是它:

```text
Math 有 3 条 results,ELA 有 2 条,maxRows = 4:
  选两个 group:2 行 header + 2 行 results = 4 行 → 只显示 2 条 results
  只选 Math:  1 行 header + 3 行 results = 4 行 → 显示 3 条 results ✓
丢掉整个 ELA,反而多显示了 results —— 因为省下了 1 行 header。
```

**算法:枚举 group 数 k。** 选 k 个 group → header 占 k 行,results 可用行数 = `maxRows − k`,且必须 ≥ k(R4:每个 group 至少 1 条 result)。对每个合法的 k:取 **results 最多的 k 个 group**(并列取 first appearance 早的),能显示的 results 数 = `min(maxRows − k, 这 k 个 group 的 results 总数)`。k 从小到大枚举,**严格更优才更新**——并列时自动保留更小的 k,R6 的 "ties → fewer groups" 免费实现。

**配额分配:保底 + 补位。** 定了 group 集合后,每个 group 先拿 1 条保底名额(该组最相关的那条,满足 R4);剩余名额按**全局 relevance 顺序**补位——保底保证小 group 不被大 group 挤掉,补位保证剩余行给最相关的 results。

## 5. 手走精髓例子

`maxRows = 4`,results 依相关性:`M1(Math) M2(Math) E1(ELA) M3(Math) E2(ELA)`:

| k | header 占行 | results 可用行 | 取 results 最多的 k 个 group | 能显示 | 判定 |
|---|---|---|---|---|---|
| 1 | 1 | 3 | {Math}(3 条) | min(3, 3) = **3** | 最优 ✓ |
| 2 | 2 | 2 | {Math, ELA}(共 5 条) | min(2, 5) = 2 | 不如 k=1 |

输出:`[Math]` `M1` `M2` `M3` —— ELA 整个 group 不显示,换来多显示 1 条 result。

## 6. 参考实现(已验证;注意注释里的规则号——第 5 步流程的落地)

<details>
<summary><b>展开完整代码(SearchBarRenderer.java;逐规则测试全绿)</b></summary>

```java
import java.util.*;

public class SearchBarRenderer {

    public static class Result {
        final String title;
        final String category;

        public Result(String title, String category) {
            this.title = title;
            this.category = category;
        }
    }

    public static List<String> render(List<Result> results, int maxRows) {
        if (results == null) {
            throw new IllegalArgumentException("results must not be null");
        }
        if (results.isEmpty() || maxRows < 2) {
            return new ArrayList<>();                     // R4+R5 推论:一组最少要 2 行(头+1 结果)
        }

        // 按首次出现顺序分组(R1),组内天然保持相关性顺序(R3)
        LinkedHashMap<String, List<Result>> groups = new LinkedHashMap<>();
        for (Result r : results) {
            groups.computeIfAbsent(r.category, k -> new ArrayList<>()).add(r);
        }
        List<String> cats = new ArrayList<>(groups.keySet());
        Map<String, Integer> appearance = new HashMap<>();
        for (int i = 0; i < cats.size(); i++) {
            appearance.put(cats.get(i), i);
        }

        // 枚举组数 k(R5/R6):k 个组头,结果预算 maxRows-k,需 >= k(R4)
        int bestShown = 0;
        List<String> bestChosen = null;
        List<String> bySize = new ArrayList<>(cats);
        bySize.sort((a, b) -> {
            int cmp = Integer.compare(groups.get(b).size(), groups.get(a).size());
            return cmp != 0 ? cmp : Integer.compare(appearance.get(a), appearance.get(b));
        });
        for (int k = 1; k <= cats.size() && maxRows - k >= k; k++) {
            List<String> chosen = bySize.subList(0, k);
            int total = 0;
            for (String c : chosen) {
                total += groups.get(c).size();
            }
            int shown = Math.min(maxRows - k, total);
            if (shown > bestShown) {                      // 严格大于:并列时保留更小的 k(R6)
                bestShown = shown;
                bestChosen = new ArrayList<>(chosen);
            }
        }
        if (bestChosen == null) {
            return new ArrayList<>();
        }

        // 配额:每组保底 1(R4),剩余按全局相关性补位
        Set<String> chosenSet = new HashSet<>(bestChosen);
        Map<String, Integer> take = new HashMap<>();
        int extras = bestShown - bestChosen.size();
        for (Result r : results) {
            if (!chosenSet.contains(r.category)) {
                continue;
            }
            int t = take.getOrDefault(r.category, 0);
            if (t == 0) {
                take.put(r.category, 1);                  // 保底名额,不消耗 extras
            } else if (extras > 0) {
                take.put(r.category, t + 1);
                extras--;
            }
        }

        // 渲染:组按首次出现顺序(R1),组头 + 组内前 take 条(R2/R3)
        List<String> rows = new ArrayList<>();
        for (String cat : cats) {
            if (!chosenSet.contains(cat)) {
                continue;
            }
            rows.add("[" + cat + "]");
            List<Result> rs = groups.get(cat);
            for (int i = 0; i < take.get(cat); i++) {
                rows.add("  " + rs.get(i).title);
            }
        }
        return rows;
    }
}
```

</details>

三个实现细节边写边说:`LinkedHashMap` 一石二鸟——**保持插入顺序**恰好实现 R1 的"首现顺序"(普通 HashMap 顺序就丢了);选大组的比较器带 `appearance` 做 tie-break,落实 R6 的"首现早优先";配额循环里**保底不消耗 extras**——写反的话小组会被大组的补位挤掉保底。

## 7. 复杂度(说出口的版本)

> *"Grouping and quota assignment are O(n). Enumerating k over G groups with re-summation is O(G²) — fine since G is small; a prefix sum over the sorted sizes would make it O(G log G) if it mattered."*

## 8. 逐规则测试(这题的测试就是需求验收单)

| 用例 | 验收的规则 |
|---|---|
| 预算充足全展示,组序/组内序正确 | R1 R2 R3 |
| `maxRows=4` 丢 ELA 反而多显示 | **R6 关键**(header 占行导致的取舍) |
| `maxRows=6` 选两组,保底 + 补位 | R4 + 配额 |
| 小组首条出现很晚仍拿到保底 | R4 |
| `maxRows<2` / 空 results → 空输出 | R5 边界 |
| 并列时组数少优先、首现早优先 | R6 次级优先 |

**这张表本身就是第 4 步流程的产物**——先有它,后有代码;考场上把它写在注释区,面试官看到的就是"规则 → 验收"的完整闭环。

## 9. Follow-up 全集(带答案方向)

### FU1 — "被截断的组加一行 'Show more…' 入口?"

预算模型多一个变量:被截断的组额外占 1 行。枚举 k 的框架不动,`shown = min(maxRows − k − 截断组数, total)` 里多一项——**规则驱动的代码就该改得这么局部**。先问清:Show more 行算不算 maxRows?

### FU2 — "每组最多展示 3 条?"

配额分配加一个 cap:`take` 封顶 3,补位跳过已满的组。一行改动。这类"参数化规则"追问的意图就是看你的实现是不是**把规则写成了可拧的旋钮**。

### FU3 — "结果带相关性分数,补位按分数来?"

补位从"全局顺序扫描"改成"按分数的 max-heap 逐个弹出"——又回到 bounded heap 的老朋友。顺手说复杂度:O(n log n) 起步,n 小无所谓。

### FU4 — "分页 / 滚动加载?"

`render(results, maxRows)` 变 `render(results, maxRows, page)` 或带游标的状态化接口——聊的是**接口设计**:游标存什么(offset?上一页最后一条的 key?)、规则在跨页时怎么保持(组头每页都重打吗?)。开放讨论,先问需求。

## 10. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **把它当算法题猛冲**,跳过读 doc 流程 | 规则理解错,全盘白写 | 六步流程;前 10 分钟不写代码 |
| 孤儿组头(选了组没给结果) | 违反 R4 | 预算约束 `maxRows − k ≥ k` + 保底配额 |
| 用 HashMap 分组 | 组序丢失,违反 R1 | **LinkedHashMap**(插入序 = 首现序) |
| 并列时更新最优解 | 违反 R6"组数少优先" | **严格大于**才更新,小 k 先枚举 |
| 保底消耗 extras | 小组被大组挤掉保底 | 保底与补位分开记账(`t == 0` 分支) |
| `maxRows = 1` 或 0 | 放不下"头+1 结果"仍硬渲染 | 开头短路返回空(R4+R5 推论,主动声明) |
| 选大组并列时随便选 | 违反 R6"首现早优先" | 比较器第二键 = appearance |
| 注释无规则号 | 面试官对照 doc 检查成本高,流程分丢失 | 每个代码块标 `// R几` |
| 现场规则和练习不同就慌 | 白练 | **练的是六步流程,不是这套具体规则**——现场规则不同是预期之内 |

## 11. 40 分钟考场节奏 + 互动台词(这题通常给更长时间)

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–8 min | **读 doc + 编号 + 复述确认**(第 1–2 步) | "I'll number the rules as I read… Let me read them back: R1 group by category in first-appearance order… Did I get them right?" |
| 8–12 min | **挑留白**(第 3 步) | "Three things the doc doesn't pin down: ties in R6? empty input? what if maxRows can't even fit one group?" |
| 12–16 min | 规则 → 测试用例(第 4 步)+ 方案 buy-in | "Headers cost rows, so more groups isn't always better — I'll enumerate the number of groups k. Sound good?" |
| 16–32 min | 写实现,注释标规则号(第 5 步) | "Tagging each block with the rule it implements, so you can check me against the doc." |
| 32–37 min | doc 例子走查 + 自报边界(第 6 步) | "Walking the doc's own example… and what would break this: maxRows=1, one giant group, all ties." |
| 37–40 min | Follow-up 对话(第 9 节弹药) | — |

**心态**:这题写代码的时间占比是全池最低的——**前 16 分钟的"不写代码"恰恰是评分最重的部分**,别慌着动手。

## 12. 30 秒总结陈词(背诵版)

> *"Headers cost rows, so showing more groups isn't always better — the doc's own example drops a whole category to show more results. I enumerate the number of groups k: k headers leave maxRows − k for results, needing at least k for the no-orphan rule; for each k I take the k largest groups and keep the best strictly-improving answer, which automatically prefers fewer groups on ties. Then quotas: one guaranteed slot per group, extras filled by global relevance. Every block is tagged with the rule it implements, and every rule has a test."*

**记忆钩子**:**六步流程是主角,具体规则只是练习材料**——编号 → 复述 → 挑留白 → 规则变测试 → 注释标号 → 走查。算法侧一句话:**每个 header 占 1 行,枚举 group 数 k,严格更优才更新,保底加补位。**
