# IXL VO — Italic Parser 终极版(Java · 状态机扫描 + 栈版嵌套 Follow-up)

> **情报卡**:VO **第一轮热身位**(面经旧清单 #8/#16、总清单 #23;#16 原话"第一轮简单的字符串处理题 italic/not italic")。无 LC 原题。押题榜第四位(与 Random Rectangles 并列)。
> **代码状态**:两版 JDK 21 编译通过,各 10 组测试全绿:`ItalicParser.java`(主版)/ `NestedTagParser.java`(嵌套栈版 follow-up)。
> **本题特殊性**:算法难度 ≈ 0,**分数全部长在"约定"上**——题面故意留白,考的是你先问什么、怎么声明默认行为。这正是 IXL"互动为纲"评分的完美标本,也和你 PI 赢过的 Text Processor 同族(同一招 `indexOf(tag, pos)` 扫描)。

---

## 1. 原题还原(英文,面试官视角)

> *You're given a string containing plain text and HTML `<i>` tags marking italic ranges. Write a function that parses it and returns, **in order**, a list of segments, each labeled italic or not-italic. Constructors `italic(text)` and `not_italic(text)` are provided.*

```text
输入:
"this is not italic<i>this is italic</i>this is another not italic<i>this is another italic</i>"

输出:
[ not_italic("this is not italic"),
  italic("this is italic"),
  not_italic("this is another not italic"),
  italic("this is another italic") ]
```

## 2. 这题真正考什么(热身题为什么也能挂人)

15–20 分钟的第一道题,面试官看的不是你会不会写循环:

| 评分点 | 说明 |
|---|---|
| **动手前先约定边界** | 题面只给了最理想的例子,至少 **6 个行为没定义**(第 3 节清单)。一条条问清或主动声明,是这题的主要得分来源;反面是直接开写,被一句 `"a<i>bc"` 问哑 |
| 代码整洁 | 一个布尔状态 + 单循环,读起来应该像题面一样直白 |
| **顺序保持** | 输出是**有序列表**——下意识用 Map 按 italic/not 分组就废了 |
| 自测边界 | 写完主动跑:串首标签、相邻标签、未闭合、空串(IXL 的 "what would break your code" 又来了) |

## 3. 开场 3 分钟:澄清问题(附建议默认值)

| 要问的问题 | 建议默认 | 为什么要问 |
|---|---|---|
| 空段要不要输出?(串首/串尾是标签、相邻 `</i><i>`、空对 `<i></i>` 都会产生) | **跳过不输出**;要保留就去掉一个 `isEmpty` 判断 | 例子里看不出来,两种都合理 |
| 未闭合的 `<i>`(`"a<i>bc"`)? | 剩余文本按 italic 处理;严格模式抛异常 | 容错 or 报错,**定义过就赢** |
| 多余的 `</i>`(没配对的闭标签)? | 当普通文本原样保留 | 同上 |
| 其他标签(`<b>`)、大写 `<I>`、带空格 `< i>`? | 一律当普通文本,只认小写精确 `<i>`/`</i>` | 划清识别边界 |
| 会嵌套吗(`<i><i>…</i></i>`)? | 题面暗示不嵌套;声明 "assume no nesting",嵌套是 FU1 | 铺垫栈版 follow-up |
| null / 空串? | null 抛异常;空串返回空列表 | 防御性习惯 |
| 返回结构? | **有序 List**,不是分组 Map | 顺序是题目灵魂 |

## 4. 解法推导(讲给面试官听的顺序)

### 4.1 一个布尔的状态机,一句话吃掉两个坑

维护 `inItalic` 状态;每轮**只找能改变当前状态的那个标签**——不在斜体中就只找 `<i>`,在斜体中就只找 `</i>`。这一句话同时解决两个边界:not-italic 状态下的多余 `</i>` **自然**变成普通文本(根本没找它);`<b>` 等其他标签从头到尾不被识别,**自然**当文本。不需要任何特判——把这层"设计出来的省事"讲给面试官听,是本题最亮的一句。

### 4.2 扫描骨架(四个动作循环)

```text
while pos 没到头:
  tag  = inItalic ? "</i>" : "<i>"        ← 只找当前状态的标签
  next = input.indexOf(tag, pos)          ← 带起点的 indexOf,不用 substring 再搜
  没找到 → 剩余文本全归当前状态,结束
  找到   → [pos, next) 出段 → pos = next + tag.length() → 翻转状态
```

### 4.3 手走 `a<i>b</i>c<i>d</i>`(考场要做的事)

| 轮 | pos | inItalic | 找 | next | 出段 | 新 pos | 新状态 |
|---|---|---|---|---|---|---|---|
| 1 | 0 | false | `<i>` | 1 | no("a") | 4 | true |
| 2 | 4 | true | `</i>` | 5 | it("b") | 9 | false |
| 3 | 9 | false | `<i>` | 10 | no("c") | 13 | true |
| 4 | 13 | true | `</i>` | 14 | it("d") | 18 = len,结束 | false |

再补未闭合的心跳测试 `a<i>bc`:第 2 轮 `indexOf("</i>", 4) = -1` → 剩余 `"bc"` 按 italic 收尾 → `[no(a), it(bc)]`,正是约定 2 的行为。

## 5. 参考实现(现场要写的)

<details>
<summary><b>展开完整代码(ItalicParser.java;10 组测试:原例 / 串首标签 / 相邻标签 / 空对 / 无标签与空串 / 未闭合 / 多余闭合 / 其他标签 / 裸 `<` / null)</b></summary>

```java
import java.util.*;

public class ItalicParser {

    /** 题面说 italic() / not_italic() 已给定,这里用一个小类模拟 */
    static class Segment {
        final boolean italic;
        final String text;

        private Segment(boolean italic, String text) {
            this.italic = italic;
            this.text = text;
        }

        static Segment italic(String text) { return new Segment(true, text); }
        static Segment notItalic(String text) { return new Segment(false, text); }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Segment)) return false;
            Segment s = (Segment) o;
            return italic == s.italic && text.equals(s.text);
        }

        @Override
        public int hashCode() { return Objects.hash(italic, text); }

        @Override
        public String toString() { return (italic ? "italic(" : "not_italic(") + text + ")"; }
    }

    private static final String OPEN = "<i>";
    private static final String CLOSE = "</i>";

    /**
     * 行为约定(面试时先和面试官对齐,这里取的默认):
     *  1. 空段不输出(串首/串尾是标签、相邻标签之间、<i></i> 都会产生空段)
     *  2. 未闭合的 <i>:其后的剩余文本按 italic 处理(严格模式可改为抛异常)
     *  3. 非当前期待的标签按普通文本处理(如 not-italic 状态下的多余 </i>、<b> 等)
     *  4. 大小写敏感:只认小写 <i> 和 </i>
     */
    public static List<Segment> parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        List<Segment> result = new ArrayList<>();
        int pos = 0;
        boolean inItalic = false;

        while (pos < input.length()) {
            String tag = inItalic ? CLOSE : OPEN;      // 当前状态只找"能改变状态"的那个标签
            int next = input.indexOf(tag, pos);

            String text;
            if (next == -1) {                          // 后面没有标签了:剩余全部归当前状态
                text = input.substring(pos);
                pos = input.length();
            } else {
                text = input.substring(pos, next);
                pos = next + tag.length();             // 坑:必须加 tag.length(),漏加会死循环
            }

            if (!text.isEmpty()) {                     // 约定1:空段不输出
                if (inItalic) {
                    result.add(Segment.italic(text));
                } else {
                    result.add(Segment.notItalic(text));
                }
            }
            if (next != -1) {
                inItalic = !inItalic;                  // 消费掉标签,翻转状态
            }
        }
        return result;
    }
}
```

</details>

三个值得边写边说的细节:`input.indexOf(tag, pos)` 带起点参数,不用每次 substring 再搜(省一次拷贝——Text Processor 里你用过的同一招);`pos = next + tag.length()`,两个标签长度不同(3 和 4),用 `tag.length()` 别写死数字;`Segment` 补齐 `equals/hashCode/toString` 三件套,`List.equals` 才能直接断言——测试友好也是代码素养。

## 6. 复杂度(说出口的版本)

> *"Each character is scanned and consumed once — the `indexOf` calls never revisit consumed text — so **O(n)** time and O(n) for the output. Substring copies total O(n); if that mattered I'd return (start, end, style) triples instead."*

## 7. Follow-up 全集(带详细答案)

### FU1 — "如果标签可以嵌套、还有 `<b>` `<u>` 呢?"(栈版,已实现 —— 本题最重要的 follow-up)

布尔状态只能表达"在/不在",嵌套需要"在**哪些**里面"——**布尔升级成栈**:

| 原版 | 嵌套版 |
|---|---|
| `boolean inItalic` | `Deque<String> open`(当前打开的标签栈,用 `ArrayDeque` 别用 legacy `Stack`) |
| 翻转状态 | 开标签 `push` / 匹配的闭标签 `pop` |
| "只找能翻转状态的标签" | "只认**与栈顶匹配**的闭标签;不匹配的当文本"(lenient) |
| 段的标记 = 布尔 | 段的样式 = **emit 时刻栈的快照** |

**全版最大的坑:快照必须拷贝。** `new Segment(text, new HashSet<>(open))`——如果直接存活栈引用,栈后续 push/pop 会把**所有已输出段**的样式一起改掉(测试用例 2 专门卡这个 aliasing bug)。样式即将变化(push 或 pop 之前)先把手头文本出段,是第二个关键时序。

<details>
<summary><b>展开嵌套栈版核心代码(NestedTagParser.java;10 组测试含退化一致性 / 嵌套快照 / 交错嵌套 / 未知标签 / 残缺标签)</b></summary>

```java
public static List<Segment> parse(String input) {
    if (input == null) {
        throw new IllegalArgumentException("input must not be null");
    }
    List<Segment> result = new ArrayList<>();
    Deque<String> open = new ArrayDeque<>();   // 栈:当前打开的标签
    StringBuilder text = new StringBuilder();  // 当前段的文本累积
    int i = 0;

    while (i < input.length()) {
        if (input.charAt(i) == '<') {
            TagToken tag = tryReadTag(input, i);           // 不合法(未知名/残缺)返回 null
            if (tag != null) {
                if (!tag.closing) {
                    emit(result, text, open);              // 样式即将变化:先出段
                    open.push(tag.name);
                    i = tag.end;
                    continue;
                }
                if (!open.isEmpty() && open.peek().equals(tag.name)) {
                    emit(result, text, open);              // 同上:pop 前先出段
                    open.pop();
                    i = tag.end;
                    continue;
                }
                // 闭标签不匹配栈顶(交错嵌套/多余闭合):lenient 落到下面当文本
                // strict 模式在此改为 throw new IllegalArgumentException(...)
            }
        }
        text.append(input.charAt(i));
        i++;
    }
    emit(result, text, open);                              // 收尾:未闭合 = 隐式闭合
    return result;
}

private static void emit(List<Segment> result, StringBuilder text, Deque<String> open) {
    if (text.length() == 0) {
        return;                                            // 空段不输出
    }
    // ★ 必须拷贝快照!存活栈引用会让后续 push/pop 污染所有已出段的样式
    result.add(new Segment(text.toString(), new HashSet<>(open)));
    text.setLength(0);
}

// ---- 验票员:从 '<' 尝试读一个合法标签;不合法返回 null(整段当文本)----
// "未知标签/残缺/大小写"三条约定全部免费来自这一个 null 合同,主循环零特判。
private static final Set<String> KNOWN_TAGS = Set.of("i", "b", "u");

private static class TagToken {
    final String name;       // "i" / "b" / "u"
    final boolean closing;   // true = </...>
    final int end;           // '>' 的后一位,调用方用它跳过整个标签
    TagToken(String name, boolean closing, int end) {
        this.name = name; this.closing = closing; this.end = end;
    }
}

private static TagToken tryReadTag(String s, int i) {
    int j = i + 1;                                 // 跳过 '<'
    boolean closing = false;
    if (j < s.length() && s.charAt(j) == '/') {    // 紧跟 '/' → 闭标签
        closing = true;
        j++;
    }
    int k = s.indexOf('>', j);                     // 找 '>'
    if (k == -1) {
        return null;                               // 残缺(如串尾 "a<b")→ 当文本
    }
    String name = s.substring(j, k);
    if (!KNOWN_TAGS.contains(name)) {
        return null;                               // 未知名(<x>、< i>、<I>)→ 当文本
    }
    return new TagToken(name, closing, k + 1);
}
```

</details>

标准嵌套的行为长这样:`plain<b>bold<i>both</i>tail</b>end` → `plain{} bold{b} both{b,i} tail{b} end{}`——每段带的就是当时栈里的东西。交错嵌套 `<b>1<i>2</b>3</i>4` 里不匹配的 `</b>` 被当文本(lenient),strict 模式一行换成抛异常。这就是真实 HTML parser 的雏形,把这句说出来收尾。

### FU2 — "空段保留呢?"

去掉 `emit` 里的一个 `isEmpty` 判断即可——答案本身一句话,价值在于你**当初约定时就说过**"要保留就去掉这个判断",前后呼应显得从容。

### FU3 — "非法输入要报错(严格模式)?"

三处改动点都已在代码注释里标好:未闭合(收尾时 `inItalic`/栈非空 → 抛)、多余闭合(不匹配分支 → 抛)、残缺标签(`tryReadTag` 返回 null 处 → 抛)。容错和严格是**同一骨架的两个配置**,不是两个算法。

### FU4 — "输入超长,内存敏感?"

`substring` 每段拷贝一次,总量 O(n) 通常没问题;真要零拷贝就返回 `(start, end, style)` 三元组,消费方自己切。流式(Reader 逐块)也是同一状态机,只是标签可能跨块——保留一个小尾巴缓冲即可,口述到这层就够。

### FU5 — "为什么不用正则 / split?"

`split("<i>|</i>")` 会把所有边界行为(空段、未闭合、多余闭合、顺序归属)全部吞进黑盒,出了偏差没法解释;而这题的分数恰恰全在边界行为的**可控**上。说出这个理由本身是加分项——工具选择要为"可解释性"服务。

## 8. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **`pos` 忘加 `tag.length()`** | 死循环,当场卡死 | `pos = next + tag.length()`;两个标签长度不同(3/4),别写死数字 |
| **用 Map 按 italic/not 分组** | 顺序丢失,题目灵魂没了 | 输出是有序 List |
| 空段行为没约定 | 被 `"<i></i>"` 一问一个不吱声 | 先问;默认跳过并声明可切换 |
| 未闭合/多余闭合没定义 | 同上 | 容错 or 报错,定义过就赢 |
| `substring` 边界 off-by-one | 段文本带上标签字符 | 半开区间心智:`[pos, next)` |
| `Segment` 缺 equals/hashCode | `List.equals` 断言失效 | 三件套补齐(或用 record) |
| 大写 `<I>`、带空格 `< i>` 当标签 | 未约定的识别范围 | 默认只认小写精确,声明 |
| 栈版:快照存活引用 | 已出段样式被后续 push/pop 污染 | `new HashSet<>(open)` 拷贝(测试卡点) |
| 栈版:用 legacy `Stack` | 面试官皱眉 | `ArrayDeque` |
| 栈版:push/pop 前忘了先出段 | 文本挂错样式 | "样式即将变化 → 先 emit"的时序纪律 |

## 9. 20 分钟考场节奏 + 互动台词(热身题节奏更快)

| 时间 | 动作 | 台词 |
|---|---|---|
| 0–3 min | **澄清(本题占比最高)** | "The example doesn't pin down a few behaviors — empty segments? an unclosed `<i>`? a stray `</i>`? other tags or nesting? I'll state defaults as I go: skip empties, treat unmatched tags as plain text, case-sensitive, no nesting." |
| 3–5 min | 方案 + **要 buy-in** | "A single boolean state machine: I only search for the tag that can flip the current state, so stray closers and unknown tags naturally fall through as text. One pass, O(n). Sound good?" |
| 5–12 min | 写代码,持续 narrate | "Searching from `pos` with `indexOf`'s two-arg form, so I never rescan consumed text." |
| 12–15 min | 手走 4.3 表 + **主动**报边界 | "What would break this: leading tag, adjacent tags, empty pair, unclosed open, stray close, null, empty string." |
| 15–20 min | Follow-up 对话(第 7 节弹药) | "For nesting I'd upgrade the boolean to a stack; each segment carries a snapshot of the open tags — a **copy**, or later pushes would mutate already-emitted segments." |

**接提示**:面试官任何插话 → *"Oh, good point — let me fold that in."* 热身题写太快反而少了互动,**节奏刻意放慢一档**,把约定和边界说满。

## 10. 30 秒总结陈词(背诵版)

> *"It's a linear scan with one boolean state. The key design choice: I only search for the tag that can flip the current state — so a stray closing tag or an unknown tag naturally falls through as plain text, no special-casing. Segments come out in order; empty ones are skipped by the convention we agreed on, unclosed tags fall back to italic-to-end. O(n). For nesting, the boolean upgrades to a stack of open tags, and each segment carries a snapshot — a copy — of that stack."*

**记忆钩子**:一个布尔 + **只找能翻转状态的标签**(一句吃两坑)→ 边界靠约定(6 连问)→ `pos += tag.length()` 防死循环 → 嵌套 = 布尔升级成栈 + **快照要拷贝**。
