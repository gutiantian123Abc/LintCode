/**
 * 384 · 最长无重复字符的子串
 * https://www.lintcode.com/problem/384/
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 *
 * 描述
 * 给定一个字符串，请找出其中无重复字符的最长子字符串。
 * s consists of English letters, digits, symbols and spaces.
 *
 * 样例 1:
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 最长子串是 "abc".
 *
 * 样例 2:
 * 输入: "bbbbb"
 * 输出: 1
 * 解释: 最长子串是 "b".
 *
 * 挑战
 * O(n) 时间复杂度
 */
public class Solution {
    /**
     * @param s: a string
     * @return: an integer
     */
    public int lengthOfLongestSubstring(String s) {
        // write your code here
        if (s == null) {
            return 0;
        }

        if (s.length() <= 1) {
            return s.length();
        }

        Map<Character, Integer> map = new HashMap<>();

        int l = 0;
        char[] words = s.toCharArray();
        int res = 0;

        for (int r = 0; r < words.length; r++) {
            char c = words[r];
            /**
             must check map.get(c) >= l
             to make sure that index is within the window
             */
            if (map.containsKey(c) && map.get(c) >= l) {
                l = map.get(c) + 1;
            }
            map.put(c, r);
            res = Math.max(res, r - l + 1);
        }

        return res;
    }
}