# IXL VO — ABC Log IDs 终极版(Java · TreeSet 贪心 + ArrayList 保险版)

> **情报卡**:面经 #9 / #16 / #23,VO 出现 2 次,**第二轮(偏重)方向,押题榜第三位**;#23 确认考官期待 **O(n log n)** 解。
> **代码状态**:两版实现 JDK 21 全部编译通过;2000 轮随机模糊 + 合法性校验器全绿;两版**输出逐字一致**(互为 oracle)。
> **主答案定版:ArrayList 版(你的熟悉区,`ABCLogIdsNoTreeSet.java`)**;TreeSet 版(`ABCLogIds.java`)留作口头升级与追问弹药(FU2)。

---

## 1. 原题还原(英文,面试官视角)

> *You're given a log stream as a list of tokens. There are three event types: **A**, **B**, and **C**. Each event belongs to a customer identified by an integer id. A and C events carry their id — `"A1"`, `"C2"` — but the B events lost theirs: they appear as just `"B"`.*
>
> *For every customer `i` there are exactly three events, and in the true chronology they occurred in the order `A_i` → `B_i` → `C_i`. Different customers' events can interleave arbitrarily.*
>
> ***Restore the ids**: assign each B a customer id such that every customer's A appears before their B, and their C appears after it. The input is guaranteed to have at least one valid assignment.*
>
> *Example: `[A1, B, A2, C1, B, C2]` → `[A1, B1, A2, C1, B2, C2]`*

## 2. 题意图解(30 秒把题读对)

现实故事:**A = 会话开始**(带客户号),**B = 会话中的一次操作**(日志 bug 把客户号弄丢了),**C = 会话结束**(带客户号)。任务是把每个 B 归还给正确的客户,让每个客户的时间线自洽。

```text
位置:   0     1     2     3     4     5
输入:   A1    B     A2    C1    B     C2

客户1 窗口:[0 ————————————— 3]   它的 B 必须落在 (0,3) 内 → 只有位置 1
客户2 窗口:            [2 ————————————— 5]   它的 B 必须落在 (2,5) 内 → 只有位置 4
```

一句话转译:**每个客户是一段窗口 `(Aᵢ 位置, Cᵢ 位置)`,匿名的 B 们要和窗口们配对,每个 B 必须落进自己窗口的内部。**(此例恰好每个窗口只剩一个可行 B,所以答案唯一;一般情况下答案**不唯一**——这件事后面有大用。)

## 3. 开场 3 分钟:澄清问题(附建议默认值)

| 要问的问题 | 建议默认 | 为什么要问 |
|---|---|---|
| token 格式固定吗?id 会是多位数吗? | `"A"/"C" + 十进制 id`,B 是裸 `"B"` | 决定解析方式(`substring(1)`,不是 `charAt(1)`) |
| 输入保证有解吗?无解时期望什么行为? | 保证有解;实现仍防御性报错 | 决定错误路径,也铺垫 FU1 |
| 每个客户恰好一条 B 吗? | 是;k 条是 follow-up | 影响结算逻辑 |
| id 连续吗?从 1 开始吗? | 不假设——任意正整数 | 防止拿数组下标存 aPos |
| 输出形态?新列表还是原地改? | 返回填好 id 的新列表 | API 形态 |
| 多个合法答案时任意一个可接受? | 是 | 铺垫"用校验器测试"的话术 |

## 4. 解法推导(讲给面试官听的顺序)

### 4.1 结算时机:C 到来的那一刻

扫描到 `Cᵢ` 时,客户 i 的窗口**关闭**了:它的 B 只能从"已经出现、还没被分配"的 B 里选(C 之后的 B 对它永远非法),而且必须在 `Aᵢ` 之后。**这一刻不结算,以后再也没机会**——所以算法在每个 C 处做决定,天然单遍在线。

### 4.2 贪心规则:拿"Aᵢ 之后最早的未分配 B"

选哪一个?答案必须是**最早的合格 B**。"随便拿一个"或"拿最晚的"都会翻车,反例要背到能当场默写:

```text
A1   B    A2   B    C1   C2
0    1    2    3    4    5

C1(位置4)若贪最晚的 B(位置3):C2 只剩位置1,但 1 < aPos(2)=2 → 无解 ✗
C1 拿 A1 之后最早的(位置1),把位置3 留给 C2 → 成功 ✓
```

直觉:**越早的 B 越"廉价"**——能用它的客户集合只会更少(它更容易掉到别人的 A 前面去),先用掉早的,把晚的、更通用的留给后面。严格证明是两句话的交换论证(FU5)。

### 4.3 数据结构:这是一个 successor 查询

"Aᵢ 之后最早的未分配 B" = **在未分配 B 的位置集合里,找严格大于 aPos 的最小元素**。这是只有"有序结构"才做得了的 successor 查询,先把不行的排除掉:

- `HashMap`/`HashSet` 出局——无序结构找 successor 只能 O(n) 全扫;
- `PriorityQueue` 出局——堆只露**全局**最小,回答不了"大于某阈值的最小"。反例 `A1 B A2 B C2 C1`:C2 时未分配 B 是 {1, 3},堆 poll 出 1,但 1 在 A2(位置 2)之前,不合格;"poll 出来不合格再塞回去"会被反复 poll,退化不可控。**堆是最值机器,不是有序地图**;
- ★ **主答案(你写这版):天然有序的 ArrayList + 二分 + 墓碑**——关键观察是 B 的位置按扫描顺序追加,**列表生来升序,一次排序都不用做**。successor = lower-bound 二分找第一个 `> aPos` 的下标,已分配的打墓碑、查询时向右跳过;
- 标准库的现成货是 `TreeSet.higher(aPos)`(稳定 O(log n))——**留作口头升级**,见 FU2。

到此方案完整:一遍扫描,A 记位置、B 追加进列表、C 做二分 + 结算。典型 **O(n log n)**——与面经 #23 报告的复杂度吻合。

### 4.4 手走标准示例(考场 18–22 分钟要做的事)

| i | token | 动作 | aPos | bPositions(× = 已用) | 结算 |
|---|---|---|---|---|---|
| 0 | A1 | 记位置 | {1→0} | [] | — |
| 1 | B | 追加 | {1→0} | [1] | — |
| 2 | A2 | 记位置 | {1→0, 2→2} | [1] | — |
| 3 | C1 | 二分找 >0 → 位置 1,未用 | 同上 | [1×] | 位置 1 → **B1** |
| 4 | B | 追加 | 同上 | [1×, 4] | — |
| 5 | C2 | 二分找 >2 → 位置 4,未用 | 同上 | [1×, 4×] | 位置 4 → **B2** |

输出:`A1 B1 A2 C1 B2 C2` ✓。再补一个嵌套窗口的心跳测试:`A1 A2 B C2 B C1` → 内层 C2 先关窗,把最早的 B(位置 2)拿走,C1 用位置 4 → `A1 A2 B2 C2 B1 C1`——**B1 出现在 C2 之后完全合法**,每个 B 只对自己的窗口负责。

## 5. 参考实现(ArrayList 版 —— 现场要写的主答案)

定版理由:全程只用你的熟悉区——HashMap、ArrayList、二分模板、boolean 墓碑,**零新 API**,写码时才有余力持续 narrate(IXL 的评分核心是互动,流利度本身就是分数)。两版 2000 轮随机比对输出逐字一致,正确性无差别。

<details>
<summary><b>展开完整代码(ABCLogIdsNoTreeSet.java;测试:面经原例逐字比对 + LIFO 反例 + 与 TreeSet 版 2000 轮随机逐字比对 + 合法性校验全绿)</b></summary>

```java
import java.util.*;

public class ABCLogIdsNoTreeSet {

    public static List<String> assign(List<String> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens must not be null");
        }
        Map<Integer, Integer> aPos = new HashMap<>();
        List<Integer> bPositions = new ArrayList<>();   // 按扫描顺序追加 -> 天然升序
        List<Boolean> used = new ArrayList<>();         // 与 bPositions 同下标:是否已分配(墓碑)
        Map<Integer, Integer> bAssign = new HashMap<>();

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            char type = t.charAt(0);
            if (type == 'A') {
                aPos.put(Integer.parseInt(t.substring(1)), i);
            } else if (type == 'B') {
                bPositions.add(i);
                used.add(false);
            } else if (type == 'C') {
                int id = Integer.parseInt(t.substring(1));
                Integer a = aPos.get(id);
                if (a == null) {
                    throw new IllegalArgumentException("C" + id + " appears before A" + id);
                }
                // 二分(lower bound):第一个位置 > a 的下标
                int lo = 0, hi = bPositions.size();
                while (lo < hi) {
                    int mid = lo + (hi - lo) / 2;
                    if (bPositions.get(mid) > a) {
                        hi = mid;
                    } else {
                        lo = mid + 1;
                    }
                }
                // 向右跳过墓碑,找到第一个未分配的
                int idx = lo;
                while (idx < bPositions.size() && used.get(idx)) {
                    idx++;
                }
                if (idx == bPositions.size()) {
                    throw new IllegalArgumentException("no eligible B for id " + id);
                }
                used.set(idx, true);
                bAssign.put(bPositions.get(idx), id);
            } else {
                throw new IllegalArgumentException("bad token: " + t);
            }
        }
        for (boolean u : used) {
            if (!u) throw new IllegalArgumentException("unassigned B remains");
        }

        List<String> out = new ArrayList<>(tokens);
        for (Map.Entry<Integer, Integer> e : bAssign.entrySet()) {
            out.set(e.getKey(), "B" + e.getValue());
        }
        return out;
    }
}
```

</details>

四张表各司其职:`aPos` 回答"这个 id 的窗口从哪开始";`bPositions` 回答"B 都在哪"(**生来有序**,这是整版的地基,开写前把这句说给面试官);`used` 是并排墓碑,回答"哪些还可用";`bAssign` 攒结算结果,最后回填。回填用 `new ArrayList<>(tokens)` 再 `set`,不改输入——顺手说一句 *"I won't mutate the input."*

### 全版唯一的险处:lower-bound 二分(默写到肌肉记忆)

```java
int lo = 0, hi = bPositions.size();          // 搜索区间 [lo, hi),hi 是 size 不是 size-1
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;            // 防溢出中点
    if (bPositions.get(mid) > a) {           // 判定必须"严格 >"(要 A 之后,不含 A)
        hi = mid;                            // mid 满足 -> 答案在 [lo, mid]
    } else {
        lo = mid + 1;                        // mid 不满足 -> 答案在 (mid, hi)
    }
}
// 结束时 lo == hi == 第一个 > a 的下标(可能 == size,表示不存在)
```

记忆锚点:**满足条件收右边界、不满足挪左边界,收敛点就是"第一个满足"**——和 Find Peak 是同一套骨架,只换判定条件。写完立刻用 `[1]` 里找 `>0`(命中 0 号)和找 `>5`(返回 size)两个口算例自检边界。

### 附:TreeSet 零基础速成(FU2 升级弹药 —— 特性 + 常用 API + 坑,没学过看这一节就够)

**它是什么**:一个**永远保持排序**的 Set。底层是红黑树(自平衡二叉搜索树,其实就是 TreeMap 的 key 集),因此:元素不重复(Set 语义)、遍历天然升序、所有核心操作 **O(log n)**。对比你熟的两位:`HashSet` 快(O(1))但**无序**;`PriorityQueue` 有序但**只露出堆顶**(全局最小)。TreeSet 的独门绝技是**导航查询**(NavigableSet 接口)——在**任意阈值**附近找邻居,这正是本题需要、而那两位都做不到的能力。

**心智模型**:一排永远站好队的数。你可以指着任何位置问:"比 x 大的第一个是谁?比 x 小的最后一个是谁?"它 O(log n) 回答你。

**常用 API(按用途分组;示例值全部实际运行验证)**:

| 组 | 调用 | 语义 | 备注 |
|---|---|---|---|
| 增删查 | `add(x)` / `remove(x)` / `contains(x)` | 同普通 Set | 重复 `add` 返回 false,静默不加;均 O(log n) |
| 端点 | `first()` / `last()` | 最小 / 最大 | 空集**抛异常**(NoSuchElementException) |
| 端点弹出 | `pollFirst()` / `pollLast()` | 取出并删除最小 / 最大 | 空集返回 **null**(不抛) |
| ★ 导航 | `higher(x)` | **严格 > x** 的最小元素 | 没有 → **null**;本题核心 |
| ★ 导航 | `ceiling(x)` | **≥ x** 的最小元素 | 没有 → null |
| ★ 导航 | `lower(x)` | **严格 < x** 的最大元素 | 没有 → null |
| ★ 导航 | `floor(x)` | **≤ x** 的最大元素 | 没有 → null |
| 遍历 | for-each | 升序 | `descendingSet()` 拿降序视图 |
| 范围 | `headSet(x)` / `tailSet(x)` / `subSet(a, b)` | < x / ≥ x / [a,b) 的视图 | 知道存在即可,面试很少用 |
| 构造 | `new TreeSet<>()` / `new TreeSet<>(cmp)` | 自然序 / 自定义序 | 传比较器时注意下面坑 2 |

**四个导航词一张图记牢**(上下 × 带不带等号,四格填满):**higher / lower 严格**(不含 x 自己),**ceiling / floor 含自身**(天花板 ≥ 你,地板 ≤ 你)。本题要"严格在 A 之后",所以必须 `higher`。

**具体感受一下**(set = {1, 4, 7},输出已验证):

```java
TreeSet<Integer> s = new TreeSet<>(List.of(4, 1, 7));   // 存进去自动有序:[1, 4, 7]
s.first();       // 1          s.last();        // 7
s.higher(4);     // 7(严格大于)    s.ceiling(4);    // 4(≥,自己也算)
s.lower(4);      // 1(严格小于)    s.floor(4);      // 4
s.higher(7);     // null(右边没了)  s.floor(0);      // null(左边没了)
s.add(4);        // false(重复,静默拒绝)
```

**三个坑**:

1. **导航方法返回 null,不抛异常**——接进 `int` 会拆箱 NPE。永远用 `Integer` 接、先判 null。本题 `higher` 返回 null 恰好就是"无合格 B"的错误信号,坏事变哨兵。
2. **比较器定义相等**:自定义 Comparator 时,`compare` 返回 0 的两个元素被视为**同一个**——实测 `new TreeSet<>((x,y) -> Integer.compare(x[1],y[1]))` 连加两个不同任务(优先级相同),size 只有 **1**,第二个被静默吞掉。修法:比较器补唯一 tie-break 键(如 id)。本题存的是天然互异的位置(Integer 自然序),无此风险,但被问到要能说。
3. **别拿它当 HashSet 用**:只查存在性时 HashSet O(1) 更快。**只有需要"有序 + 邻居查询 + 边插边删"时才请 TreeSet 出场**——这句话本身就是现成的选型台词。

**选型直觉一句话**:只要全局最小 → `PriorityQueue`;只要存在性 → `HashSet`;要"任意阈值附近的邻居"且动态增删 → `TreeSet`。(TreeMap 同理——它就是带 value 的 TreeSet,导航方法叫 `higherKey/ceilingKey/...`。)

## 6. 复杂度(说出口的版本 —— 主动坦白最坏情况,这句诚实本身是加分项)

> *"One pass. The binary search is O(log n) per C; skipping assigned slots is usually O(1) but can degrade over long tombstone runs — so **typically O(n log n)**, with an **O(n²) worst case**. A TreeSet's successor query would make O(n log n) guaranteed. O(n) space."*

别等面试官问"最坏是多少"——先说,并接一句 TreeSet 升级(FU2)。被动答出 = 及格,主动说出 = 工程判断力。

## 7. Follow-up 全集(带详细答案)

### FU1 — "输入不合法怎么办?"(无解检测,已实现)

贪心**天然自带**检测,三个失败模式各有一个哨兵:① `Cᵢ` 时 `aPos.get(id) == null` → C 在 A 前;② `higher(aPos) == null` → 窗口里没有可用 B(如 `B A1 C1`:B 在 A1 前,C1 时无合格 B);③ 扫描结束 `unassignedB` 非空 → 有 B 没人认领(A/C 数量对不上)。关键论证:**贪心失败 ⟹ 真无解**——因为贪心每一步都保持"仍然可行"(FU5 的交换论证),所以它报错时不是它笨,是题真的无解。三个哨兵都在代码里,主动指给面试官看。

### FU2 — "怎么做到稳定 O(n log n)?"(TreeSet 升级版,已实现)

主答案唯一的软肋是墓碑跳过的最坏 O(n)。标准库的修法是 `TreeSet<Integer>`(红黑树有序集,速成见第 5 节附录):未分配 B 的位置进树,successor 一行 `higher(aPos)`,分配后 `remove(b)`,全部操作稳定 O(log n)。整个 C 分支从"二分 + 跳墓碑"收缩成 6 行:

```java
int id = Integer.parseInt(t.substring(1));
Integer a = aPos.get(id);
if (a == null) throw new IllegalArgumentException("C" + id + " appears before A" + id);
Integer b = unassignedB.higher(a);        // 严格 > aPos 的最小元素;没有则 null
if (b == null) throw new IllegalArgumentException("no eligible B for id " + id);
bAssign.put(b, id);
unassignedB.remove(b);
```

与主答案 2000 轮随机比对**输出逐字一致**——同一贪心,只换"问 successor"这一个动作的实现。完整版如下(校验器在 FU6;测试与模糊脚本见 `ABCLogIds.java`):

<details>
<summary><b>展开 TreeSet 完整版代码(ABCLogIds.java)</b></summary>

```java
import java.util.*;

public class ABCLogIds {

    public static List<String> assign(List<String> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens must not be null");
        }
        Map<Integer, Integer> aPos = new HashMap<>();     // id -> A 的位置
        TreeSet<Integer> unassignedB = new TreeSet<>();   // 未分配 B 的位置(有序,支持 higher)
        Map<Integer, Integer> bAssign = new HashMap<>();  // B 的位置 -> 分配到的 id

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            char type = t.charAt(0);
            if (type == 'A') {
                aPos.put(Integer.parseInt(t.substring(1)), i);
            } else if (type == 'B') {
                unassignedB.add(i);
            } else if (type == 'C') {
                int id = Integer.parseInt(t.substring(1));
                Integer a = aPos.get(id);
                if (a == null) {
                    throw new IllegalArgumentException("C" + id + " appears before A" + id);
                }
                Integer b = unassignedB.higher(a);        // A_id 之后最早的未分配 B
                if (b == null) {                          // 输入保证有解时不会发生;防御性校验
                    throw new IllegalArgumentException("no eligible B for id " + id);
                }
                bAssign.put(b, id);                       // b 必然 < i(集合里只有已扫过的位置)
                unassignedB.remove(b);
            } else {
                throw new IllegalArgumentException("bad token: " + t);
            }
        }
        if (!unassignedB.isEmpty()) {
            throw new IllegalArgumentException("unassigned B remains: " + unassignedB);
        }

        List<String> out = new ArrayList<>(tokens);
        for (Map.Entry<Integer, Integer> e : bAssign.entrySet()) {
            out.set(e.getKey(), "B" + e.getValue());
        }
        return out;
    }
}
```

</details>

对照读法:与主答案逐段对齐——`bPositions + used` 两张表合并成一个 `unassignedB`(树自己管有序和删除);"二分 + 跳墓碑"合并成 `higher(a)` 一行;结尾校验从"遍历 used"变成 `isEmpty()`。其余(aPos、bAssign、错误分支、回填)一个字不差。

**口头升级台词(写完主答案后主动说)**:*"Appending keeps my list naturally sorted, so binary search works; the tombstone skip is the only part that can degrade. If I wanted a guaranteed O(n log n), I'd swap the list for a TreeSet — its `higher()` is exactly this successor query at O(log n) with real deletion."* 先展示用熟悉工具造出解,再精确点出标准工具和它解决的问题——比默写 API 更能体现判断力。

(进阶口述,不写:不引入 TreeSet 也能修——给墓碑加"下一个未分配"跳跃指针(并查集式路径压缩),均摊近似 O(α)。说到即可。)

### FU3 — "每个客户有 k 条 B?"

同一贪心,结算处循环:`Cᵢ` 到来时连做 k 次 `higher(aPosᵢ)` + 分配 + 删除(每次自动拿到"下一个最早")。每条 B 仍只被分配一次,总复杂度不变 O(n log n)(n 为 token 总数)。正确性:交换论证逐条适用。k 因客户而异也一样——只要 C 时刻知道该补几条。

### FU4 — "日志是流,内存放不下怎么办?"

状态侧天然轻:只需保存**开着的窗口**(已见 A 未见 C 的 `aPos` 条目)和**未分配的 B**——窗口一关就结算、条目即删,内存 = 峰值并发窗口数,不是日志总量。输出侧要诚实指出缓冲需求:B 的 id 要等到某个 C 结算才知道,所以**只能输出到"最早未分配 B"为止**,其后的 token 先缓冲,结算后 flush 前缀——和 TCP 重排缓冲同构。极端情况(一个客户的窗口横跨全天)缓冲会被它拖住,这是问题本身的下界,不是实现缺陷。

### FU5 — "证明贪心是对的"(交换论证,两句话)

设贪心把最早合格 B(记 `b*`)给了 i,而某正确答案 M 把 `b*` 给了更晚关窗的客户 j、把更晚的 `b′` 给 i。交换之:`b*` 给 i 合法(它本来就在 i 的窗口里);`b′` 给 j 合法——`b′ > b* > Aⱼ`(b* 在 M 中属于 j),且 `b′ < Cᵢ < Cⱼ`。交换后仍是正确答案,故**贪心的每一步都不消灭可行解**,归纳即得贪心成功。英文版一句:*"Taking the earliest eligible B never hurts — any solution using a later one can be swapped to use it."*

### FU6 — "答案不唯一,你怎么测试?"(隐藏加分点)

多数输入存在多个合法分配,硬编码期望输出的测试是错的。正确姿势:写**校验器**(每个 id 恰好 A、B、C 各一条且位置满足 A<B<C),再配**构造式模糊**——按"每客户依次发 A→B→C"的规则随机交错生成合法序列,剥掉 B 的 id,跑分配,过校验器(本实现 2000 轮全绿)。当场说出 *"the answer isn't unique, so I'll validate properties instead of comparing to one expected list"*——这句和 Random Rectangles 的属性测试是同一个思想,IXL 连着两道题都在奖励它。

<details>
<summary><b>展开校验器代码(isValidAssignment)</b></summary>

```java
/** 输出可能不唯一 —— 测试验证"合法性"而不是逐字比对(这个思维本身就是加分项) */
static boolean isValidAssignment(List<String> out) {
    Map<Integer, int[]> pos = new HashMap<>();        // id -> [aPos, bPos, cPos]
    for (int i = 0; i < out.size(); i++) {
        String t = out.get(i);
        if (t.length() < 2) return false;             // B 必须已带 id
        int id = Integer.parseInt(t.substring(1));
        int[] p = pos.computeIfAbsent(id, k -> new int[]{-1, -1, -1});
        int slot = t.charAt(0) == 'A' ? 0 : t.charAt(0) == 'B' ? 1 : 2;
        if (p[slot] != -1) return false;              // 同类型重复
        p[slot] = i;
    }
    for (int[] p : pos.values()) {
        if (p[0] == -1 || p[1] == -1 || p[2] == -1) return false;
        if (!(p[0] < p[1] && p[1] < p[2])) return false;
    }
    return true;
}
```

</details>

## 8. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **答案不唯一还逐字比对** | 测试误报,被问"输出和我的不一样"时慌 | 校验器验证 A<B<C;主动说出不唯一性 |
| **拿"最晚"或"任意"未分配 B** | 后面的客户被断粮 | 反例 `A1 B A2 B C1 C2` 当场默写;必须最早合格 |
| **二分判定写 `>= a`** | 位置互异时碰巧对,**语义错**("≥" ≠ "严格在 A 之后";TreeSet 版对应 `ceiling` vs `higher` 之辨) | 判定必须严格 `> a`,并把区别说出来 |
| 二分边界 off-by-one | 死循环或漏掉答案 | 半开区间模板:`hi = size` 起步、`hi = mid` / `lo = mid + 1`,收敛点即答案;口算 `[1]` 找 `>0`、`>5` 自检 |
| 墓碑跳过忘查边界 | `used.get(idx)` 越界 | 循环条件先写 `idx < bPositions.size()` 再 `used.get(idx)` |
| `used.set` 误写成 `used.add` | 两列表长度错位,静默出错 | B 分支才 `add`;C 分支只 `set` |
| `charAt(1) - '0'` 解析 id | 多位数 id(`A12`)当场错 | `Integer.parseInt(t.substring(1))` |
| C 在 A 前不检查 | `aPos.get(id)` 为 null → 拆箱 NPE | 显式 null 检查,报 "C before A" |
| 无合格 B 不处理 | 墓碑跳到底后越界 / TreeSet 版 `higher` 返回 null 拆箱 NPE | `idx == size` / `b == null` 即报 "no eligible B" |
| **最坏复杂度不主动说** | 被追问"最坏多少"卡壳,丢掉诚实分 | 主动:典型 O(n log n),最坏 O(n²),TreeSet 可稳定(第 6 节台词) |
| 扫完不查剩余 B | 非法输入静默通过 | 结尾遍历 `used` 校验(TreeSet 版:`isEmpty()`) |
| 想拿 HashMap / 堆做 successor | 无序结构 O(n) 全扫;堆只露全局最小 | 有序性是本题核心需求:天然有序的追加数组或 TreeSet |
| 改输入列表 | 面试官皱眉 | `new ArrayList<>(tokens)` 再回填 |

## 9. 30 分钟考场节奏 + 互动台词

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–3 min | 澄清(第 3 节清单) | "Before coding — ids can be multi-digit? Exactly one B per customer? Any valid assignment accepted?" |
| 3–6 min | 推导 + **要 buy-in** | "Each customer is a window from its A to its C, and its B must fall inside. When a C arrives the window closes, so I settle right there: give it the **earliest** unassigned B after its A. B positions arrive in increasing order, so my list is already sorted — binary search plus a used-marker gives me that successor. Sound good?" |
| 6–18 min | 写代码,持续 narrate | "Four structures: where each A is, the naturally-sorted B positions, a used-marker per B, and the assignments." |
| 18–22 min | 手走原例(第 4.4 表) | "Let me trace the example before edge cases." |
| 22–26 min | **主动**报边界 + 复杂度坦白 | "What would break this: a C before its A, no eligible B, leftover B at the end, multi-digit ids, empty input. And complexity-wise: typically O(n log n), worst O(n²) from tombstone runs — a TreeSet would make it guaranteed." |
| 26–30 min | Follow-up 对话(第 7 节弹药) | "Since valid outputs aren't unique, I'd test with a validator, not a fixed expected list." |

**反例台词**(面试官问"为什么必须最早"时):*"Taking any B isn't safe — in `A1 B A2 B C1 C2`, if C1 grabs the later B, C2 is left with a B that sits before its own A. Earliest-eligible is provably safe by a two-line exchange argument."*

## 10. 30 秒总结陈词(背诵版)

> *"Each customer is an interval from its A to its C, and its B must land inside it. I scan once; the moment a C arrives, that window closes, so I settle immediately — assign the earliest unassigned B after that customer's A. B positions arrive in scan order, so my list is naturally sorted: a lower-bound binary search finds the first B after the A, and tombstones mark assigned slots. Typically O(n log n); the tombstone skip has an O(n²) worst case, which a TreeSet's `higher()` would fix, guaranteed. Earliest-eligible is safe by an exchange argument, and since valid outputs aren't unique, I test with a validator instead of one expected answer."*

**记忆钩子**:窗口配对 → C 关窗时结算 → 最早合格 B(反例护体)→ **生来有序的列表 + 二分 + 墓碑** → 坦白最坏 + TreeSet 一句升级 → 校验器测试。