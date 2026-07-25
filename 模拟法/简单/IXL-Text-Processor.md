# IXL Interview — Text Processor(最后一个字符几字节)

![Topic](https://img.shields.io/badge/topic-Array%20Scan%20%2B%20Parity-00695c) ![Frequency](https://img.shields.io/badge/%E7%94%B5%E9%9D%A2%E5%87%BA%E7%8E%B0-2%20%E6%AC%A1-orange) ![Tests](https://img.shields.io/badge/tests-11%20passed-brightgreen)

> **一句话**:[LC 717. 1-bit and 2-bit Characters](https://leetcode.com/problems/1-bit-and-2-bit-characters/) 换皮 —— 把 bit 换成 byte,阈值换成 127。
> 还原自面经 #2 / #13(电面 / tech screening 高频);代码 JDK 21 编译,11 组测试通过。

---

## 题面还原

我们需要处理一种特殊编码的文本文件。文件中每个字符编码为 1 个或 2 个字节:

- 若字符的首字节 **≤ 127**,该字节单独构成一个字符(单字节字符)
- 若字符的首字节 **> 127**,它是双字节字符的第一个字节
- 双字节字符的**第二个字节可以是任意值**(0–255)

写一个方法,计算文件**最后一个字符的大小**:是 1 字节还是 2 字节?

```text
签名:int getSizeOfLastChar(int[] data)   // data 为文件内容

例1: [127, 128, 126] → 2    解析:127 | (128, 126)
例2: [122, 123, 124] → 1    解析:122 | 123 | 124
```

可以假设文件是 **well-formed** 的(不会出现残缺的双字节字符)。

## 与 LC 717 的差异

| 维度 | LC 717 | IXL Text Processor |
|---|---|---|
| 单位 | bit(0 / 1) | byte(0–255) |
| 单字符标志 | `0` | `≤ 127` |
| 双字符首位 | `1` | `> 127` |
| 返回 | boolean(最后是否 1-bit) | int(1 或 2) |
| 包装 | 裸数组 | "文件处理"场景 → 引出**大文件只读尾部**的 follow-up |

结构完全同构,解法一一对应。

## 两种解法

### 解法一:正向扫描 O(n) —— 先写这个

从头模拟解析:遇 ≤127 跳 1 格,遇 >127 跳 2 格,但**永远不消费最后一个字节**(循环条件 `i < n-1`)。循环结束时:

- `i == n-1`:指针恰好停在最后一个字节上 → 它自成一个字符 → **1**
- `i == n`:上一个字符把倒数第二个字节当作首字节吞了 → 最后两字节是一个字符 → **2**

### 解法二:反向扫描 —— follow-up 的答案

数**最后一个字节之前、连续 > 127 的字节个数** `k`:

- `k` 为偶数 → 最后一个字节自成字符 → **1**
- `k` 为奇数 → 它是双字节字符的第二字节 → **2**

**为什么成立**:设这段连续 >127 的区间从下标 `s` 开始(`s-1` 处 ≤127 或 `s=0`)。下标 `s` 必然是某个字符的**开头**——因为如果它是某对的第二字节,那这对的首字节在 `s-1`,而首字节必须 >127,与 `s-1 ≤ 127` 矛盾。从 `s` 起字符两两成对消费这段区间,所以最后一个字节归属只由区间长度的奇偶性决定。

**为什么这是亮点**:题面说的是"处理文件"。文件很大时,正向解法必须从第 0 个字节读到尾;反向解法只需 seek 到文件末尾、往回读到第一个 ≤127 的字节就停(通常几个字节)。面试官问"如果文件有几个 GB 怎么办",答案就是它。这也正是 UTF-8 这类自同步编码在真实世界的设计动机,提一句会很出彩。

## 参考实现

先自己限时 15 分钟写一遍(两种解法都写),再展开对照 👇

<details>
<summary><b>展开完整代码(含 11 组测试)</b></summary>

```java
import java.util.*;

public class TextProcessor {

    /** 解法一:正向扫描 O(n) —— 面试先写这个 */
    public static int getSizeOfLastCharForward(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty"); // 澄清点:null/空文件
        }
        int i = 0;
        while (i < data.length - 1) {
            if (data[i] > 127) {
                i += 2;   // 双字节字符,跳两格
            } else {
                i += 1;   // 单字节字符
            }
        }
        // 恰好落在最后一个下标上 = 最后是单字节;越过去了 = 最后两个字节是一个字符
        return i == data.length - 1 ? 1 : 2;
    }

    /** 解法二:反向扫描 —— follow-up("文件很大怎么办")的答案,只需读文件尾部 */
    public static int getSizeOfLastCharBackward(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty");
        }
        // 数最后一个字节之前、连续 > 127 的字节个数
        int count = 0;
        int i = data.length - 2;
        while (i >= 0 && data[i] > 127) {
            count++;
            i--;
        }
        // 偶数个 -> 最后一个字节自成一个字符;奇数个 -> 它是双字节字符的第二个字节
        return count % 2 == 0 ? 1 : 2;
    }

    /** Java 特有陷阱:如果给的是 byte[],byte 是有符号的(-128..127),"> 127" 永远为假!
     *  必须用 (b & 0xFF) > 127,或等价地 b < 0 来判断。 */
    public static int getSizeOfLastChar(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty");
        }
        int count = 0;
        int i = data.length - 2;
        while (i >= 0 && (data[i] & 0xFF) > 127) {
            count++;
            i--;
        }
        return count % 2 == 0 ? 1 : 2;
    }

    // ---------------- 测试 ----------------
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static void verifyBoth(int[] data, int expected, String name) {
        check(getSizeOfLastCharForward(data) == expected, name + " forward failed");
        check(getSizeOfLastCharBackward(data) == expected, name + " backward failed");
    }

    public static void main(String[] args) {
        verifyBoth(new int[]{127, 128, 126}, 2, "case1");        // 面经原例:127 | (128,126)
        verifyBoth(new int[]{122, 123, 124}, 1, "case2");        // 面经原例:全单字节
        verifyBoth(new int[]{100}, 1, "case3");                  // 单元素(well-formed 必 <=127)
        verifyBoth(new int[]{200, 201}, 2, "case4");             // 恰好一对
        verifyBoth(new int[]{200, 200, 200, 200}, 2, "case5");   // 全是 >127:两对
        verifyBoth(new int[]{100, 200, 150}, 2, "case6");        // 第二字节 >127:100 | (200,150)
        verifyBoth(new int[]{126, 128, 128, 126}, 1, "case7");   // 126 | (128,128) | 126
        verifyBoth(new int[]{200, 128, 128, 126}, 2, "case8");   // (200,128) | (128,126)
        verifyBoth(new int[]{128, 0}, 2, "case9");               // 第二字节可以是 0

        // byte[] 版:0xC8 = 200,有符号下是 -56,验证 & 0xFF 处理正确
        check(getSizeOfLastChar(new byte[]{(byte) 200, (byte) 201}) == 2, "byte[] case failed");
        check(getSizeOfLastChar(new byte[]{100, 101}) == 1, "byte[] case2 failed");

        // null / 空输入(IXL 的 "what would break your code" 固定追问)
        try { getSizeOfLastCharForward(null); check(false, "null should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }
        try { getSizeOfLastCharBackward(new int[]{}); check(false, "empty should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }

        System.out.println("all tests passed");
    }
}
```

</details>

## 测试用例一览

| # | 输入 | 期望 | 场景 |
|---|---|---|---|
| 1 | `[127, 128, 126]` | 2 | 面经原例,127 恰好压着阈值边界 |
| 2 | `[122, 123, 124]` | 1 | 面经原例,全单字节 |
| 3 | `[100]` | 1 | 单元素 |
| 4 | `[200, 201]` | 2 | 整个文件就是一对 |
| 5 | `[200, 200, 200, 200]` | 2 | 全部 >127,连成两对 |
| 6 | `[100, 200, 150]` | 2 | **第二字节也 >127**,别把它当首字节 |
| 7 | `[126, 128, 128, 126]` | 1 | 中间夹一对,最后独立 |
| 8 | `[200, 128, 128, 126]` | 2 | 连续 >127 奇偶判断 |
| 9 | `[128, 0]` | 2 | 第二字节可以是 0 |
| 10 | `byte[]{(byte)200, ...}` | 2 | **signed byte 陷阱**(见下) |
| 11 | `null` / `[]` | 抛异常 | IXL 固定追问的边界 |

## 面试当场要确认的澄清点

- [ ] 输入类型是 `int[]` 还是 `byte[]`?(Java 的 `byte` 有符号,直接影响写法)
- [ ] `null` / 空数组怎么处理?(本实现:抛异常;IXL 必追问这个)
- [ ] 返回形式:1/2、boolean、还是枚举?
- [ ] well-formed 是否保证?(题面说保证;若不保证,残缺对怎么报错)
- [ ] 文件很大、无法全部载入内存时怎么办?(→ 反向解法,seek 到尾部往回读)

## Java 细节(说出来是加分项)

- **signed byte 陷阱**:`byte` 范围是 −128..127,`data[i] > 127` 对 `byte[]` 永远为假;要写 `(data[i] & 0xFF) > 127`(或等价的 `data[i] < 0`)。题面原型是 `byte`,面试官很可能就在等这个
- 127/128 恰好是阈值两侧,测试用例 1 里的 `127` 是故意的,别写成 `>= 127`
- 反向解法讲清正确性:连续 >127 区间的起点必是字符开头(反证:若是第二字节,则其首字节在区间外却 >127,矛盾)→ 区间内两两成对 → 奇偶定归属
