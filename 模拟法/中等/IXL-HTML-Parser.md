# IXL Interview — HTML Parser(italic 分段)

![Topic](https://img.shields.io/badge/topic-String%20Scan%20%2B%20State%20Machine-00695c) ![Frequency](https://img.shields.io/badge/VO%20%E7%AC%AC%E4%B8%80%E8%BD%AE%E5%87%BA%E7%8E%B0-2%20%E6%AC%A1-orange) ![Tests](https://img.shields.io/badge/tests-10%20groups%20passed-brightgreen)

> **一句话**:算法难度接近零的字符串扫描题,**全部分数都在边界行为的"约定"上** —— 空段、未闭合标签、多余标签怎么处理,先问再写。
> 还原自面经 #8 / #16(#16:"第一轮简单的字符串处理题 italic/not italic",VO 第一轮);代码 JDK 21 编译,10 组测试通过。无 LC 原题。

---

## 题面还原

给一个包含纯文本和 HTML `<i>` 标签(表示斜体段)的字符串,写一个函数解析它,**按顺序**返回分段列表,每段标记为 italic 或 non-italic。`italic()` 和 `not_italic()` 构造函数已提供。

```text
输入:
"this is not italic<i>this is italic</i>this is another not italic<i>this is another italic</i>"

输出:
[
  not_italic("this is not italic"),
  italic("this is italic"),
  not_italic("this is another not italic"),
  italic("this is another italic")
]
```

## 这题考什么

15–20 分钟的热身题,面试官看的不是你会不会写,而是:

| 评分点 | 说明 |
|---|---|
| **动手前先约定边界** | 题面只给了最理想的例子,至少 4 个行为没定义(见下方"坑"),一条条问清或主动声明,是这题的主要得分来源 |
| 代码整洁 | 状态布尔 + 单循环的扫描结构,读起来应该像题面本身一样直白 |
| 顺序保持 | 输出是**有序列表**——下意识用 Map 分组就废了 |
| 自测边界 | 写完主动跑:串首标签、相邻标签、未闭合、空串(IXL 的 "what would break your code" 又来了) |

## 解法:单状态布尔 + indexOf 扫描

维护 `inItalic` 状态;每轮**只找能改变当前状态的那个标签**(不在斜体中就找 `<i>`,在斜体中就找 `</i>`),把标签前的文本按当前状态归段,跳过标签、翻转状态。找不到标签时,剩余文本全部归当前状态,结束。

- 每段文本被扫描且消费一次 → **O(n) 时间**,输出 O(n) 空间
- "只找当前状态的标签"这一句同时解决了两个坑:not-italic 状态下的多余 `</i>` 自然变成普通文本;`<b>` 等其他标签从头到尾不被识别,自然当文本

## 坑(约定清单)—— 这题的灵魂

动手前和面试官逐条对齐,下面是本实现取的默认行为:

1. **空段要不要输出?** 串首/串尾是标签、相邻标签 `</i><i>` 之间、空标签对 `<i></i>`,都会产生空段。默认:**跳过不输出**(若面试官要保留,把 `isEmpty` 判断去掉即可——把这句话说出来)
2. **未闭合的 `<i>`**:`"a<i>bc"` 怎么办?默认:剩余文本按 italic 处理;严格模式可改为抛异常。两种都行,**关键是定义过**
3. **多余的 `</i>`**(没有配对的开标签):默认当普通文本原样保留,`"a</i>b"` → `not_italic("a</i>b")`
4. **其他标签**(`<b>`、`<I>` 大写、`< i>` 带空格):默认一律当普通文本,只认小写精确的 `<i>` / `</i>`
5. **嵌套 `<i><i>…</i></i>`**:题面暗示不嵌套;问一句,确认后声明"assume no nesting"
6. **null / 空串**:null 抛异常,空串返回空列表
7. 实现层的坑:`pos = next + tag.length()` —— **漏加 tag 长度会死循环**;两个标签长度不同(3 vs 4),别写死数字

## 参考实现

先自己限时 15 分钟写一遍(先口头列约定再动手),再展开对照 👇

<details>
<summary><b>展开完整代码(含 10 组测试)</b></summary>

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

    // ---------------- 测试 ----------------
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static Segment it(String s) { return Segment.italic(s); }
    private static Segment no(String s) { return Segment.notItalic(s); }

    public static void main(String[] args) {
        // 用例1:面经原例
        check(parse("this is not italic<i>this is italic</i>this is another not italic<i>this is another italic</i>")
                .equals(List.of(no("this is not italic"), it("this is italic"),
                        no("this is another not italic"), it("this is another italic"))), "case1 failed");

        // 用例2:串首是标签 -> 无空的首段
        check(parse("<i>a</i>b").equals(List.of(it("a"), no("b"))), "case2 failed");

        // 用例3:相邻标签 </i><i> 之间的空段不输出
        check(parse("a<i>b</i><i>c</i>").equals(List.of(no("a"), it("b"), it("c"))), "case3 failed");

        // 用例4:空的 <i></i> 不输出
        check(parse("<i></i>x").equals(List.of(no("x"))), "case4 failed");

        // 用例5:没有任何标签 / 空串
        check(parse("hello").equals(List.of(no("hello"))), "case5a failed");
        check(parse("").isEmpty(), "case5b failed");

        // 用例6:未闭合的 <i> -> 剩余文本按 italic(约定2)
        check(parse("a<i>bc").equals(List.of(no("a"), it("bc"))), "case6 failed");

        // 用例7:not-italic 状态下的多余 </i> 当普通文本(约定3)
        check(parse("a</i>b<i>c</i>").equals(List.of(no("a</i>b"), it("c"))), "case7 failed");

        // 用例8:别的标签一律当文本
        check(parse("a<b>x</b><i>y</i>").equals(List.of(no("a<b>x</b>"), it("y"))), "case8 failed");

        // 用例9:italic 段内出现 "<" 或不完整标签片段
        check(parse("<i>a < b</i>").equals(List.of(it("a < b"))), "case9 failed");

        // 用例10:null 输入
        try { parse(null); check(false, "case10 should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }

        System.out.println("all tests passed");
    }
}
```

</details>

## 测试用例一览

| # | 输入 | 期望 | 卡的坑 |
|---|---|---|---|
| 1 | 面经原例 | 4 段交替 | 基础正确性 |
| 2 | `"<i>a</i>b"` | `[it(a), no(b)]` | 串首标签,无空首段 |
| 3 | `"a<i>b</i><i>c</i>"` | `[no(a), it(b), it(c)]` | 相邻标签间空段 |
| 4 | `"<i></i>x"` | `[no(x)]` | 空标签对 |
| 5 | `"hello"` / `""` | `[no(hello)]` / `[]` | 无标签、空串 |
| 6 | `"a<i>bc"` | `[no(a), it(bc)]` | **未闭合标签** |
| 7 | `"a</i>b<i>c</i>"` | `[no(a</i>b), it(c)]` | **多余闭合标签当文本** |
| 8 | `"a<b>x</b><i>y</i>"` | `[no(a<b>x</b>), it(y)]` | 其他标签当文本 |
| 9 | `"<i>a < b</i>"` | `[it(a < b)]` | 文本里的裸 `<` |
| 10 | `null` | 抛异常 | null 追问 |

## 面试当场要确认的澄清点

- [ ] 空段(串首尾标签、相邻标签、`<i></i>`)输出还是跳过?
- [ ] 未闭合 `<i>` / 多余 `</i>`:容错处理还是报错?
- [ ] 会有嵌套 `<i><i>…</i></i>` 吗?(默认假设无嵌套,声明出来)
- [ ] 只有 `<i>` 一种标签吗?大小写敏感吗?
- [ ] null / 空串的返回?
- [ ] 返回结构:段的顺序必须保持(有序 List,不是分组)

## Follow-up 方向(主动提能加分)

- **多种标签 / 嵌套**:`<b>`、`<i>` 混合嵌套时,布尔状态不够用 → **栈**存当前打开的标签,输出段带样式集合——这就是真实 HTML parser 的雏形,也正是这题的自然延伸
- **超长输入**:indexOf + substring 每段拷贝一次,O(n) 总量没问题;若要零拷贝可返回 (start, end, style) 三元组
- 正则 split 的替代方案为什么不选:边界行为(空段、未闭合)全被 split 的语义黑盒吞掉,面试里可控性差——说得出这个理由本身是加分项

## Java 细节(说出来是加分项)

- `pos = next + tag.length()`:两个标签长度不同(3 和 4),用 `tag.length()` 别写死数字
- `Segment` 写了 `equals/hashCode/toString` 才能直接用 `List.equals` 断言——测试友好也是代码素养
- `String.indexOf(str, fromIndex)` 带起点参数,不用每次 substring 再搜(省一次拷贝)
