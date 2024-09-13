/**
 * 213 · 字符串压缩
 * https://www.lintcode.com/problem/213/
 *
 *
 * 描述
 * 设计一种方法，通过给重复字符计数来进行基本的字符串压缩。
 *
 * 例如，字符串 aabcccccaaa 可压缩为 a2b1c5a3 。
 * 而如果压缩后的字符数不小于原始的字符数，则返回原始的字符串。
 *
 * 可以假设字符串仅包括 a-z 的大/小写字母。
 *
 *
 * 样例:
 *
 * 样例 1：
 * 输入：str = "aabcccccaaa"
 * 输出："a2b1c5a3"
 *
 *
 * 样例 2：
 * 输入：str = "aabbcc"
 * 输出："aabbcc"
 */

public class Solution {
    /**
     * @param originalString: a string
     * @return: a compressed string
     */
    public String compress(String originalString) {
        // write your code here
        StringBuilder sb = new StringBuilder();
        int p1 = 0, p2 = 0;
        while(p2 < originalString.length()) {
            while(p2 < originalString.length() && originalString.charAt(p2) == originalString.charAt(p1)) {
                p2++;
            }

            int count = p2 - p1;
            if(p2 - p1 > 0) {
                sb.append(originalString.charAt(p1));
                sb.append(String.valueOf(count));
            }
            p1 = p2;
        }

        if(sb.toString().length() < originalString.length()) {
            return sb.toString();
        }

        return originalString;
    }
}