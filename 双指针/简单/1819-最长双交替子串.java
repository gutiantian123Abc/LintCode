/**
 * 1819 · 最长双交替子串
 * https://www.lintcode.com/problem/1819
 *
 * 描述
 * 给定一个长度为 N 且只包含a和b的字符串 S 你需要找出最长的子串长度，使得其中不包含三个连续的字母。即，找出不包含aaa或bbb的最长子串长度。
 * 注意, S是其本身的子串。
 *
 * 样例1
 * 输入: "baaabbabbb"
 * 输出: 7
 * 说明: "aabbabb"是最长符合条件的子串
 *
 * 样例2
 * 输入: "babba"
 * 输出: 5
 * 说明：整个S符合条件
 *
 * 样例3
 * 输入: "abaaaa"
 * 输出: 4
 * 说明: "abaa"是最长符合条件的子串
 */
public class Solution {
    /**
     * @param s: the string
     * @return: length of longest semi alternating substring
     */
    public int longestSemiAlternatingSubstring(String s) {
        // write your code here
        int sL = s.length();
        if (sL <= 2) {
            return sL;
        }

        int left = 0, right = 1;
        int dup = 1;
        int res = 0;

        while (right < sL) {
            if (s.charAt(right) == s.charAt(right - 1)) {
                dup++;
                if (dup == 3) {
                    left = right - 1;
                    dup = 2;
                }
            } else {
                dup = 1;
            }

            res = Math.max(res, right - left + 1);
            right++;
        }

        return res;
    }
}