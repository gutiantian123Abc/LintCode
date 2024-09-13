/**
 * 353 · 最大字母
 * https://www.lintcode.com/problem/353
 *
 * 描述
 * 给定字符串S，找到一个字母字符，其大写和小写字母均出现在S中，
 * 则返回此字母的大写，若存在多个答案，返回最大的字母，不存在则返回"NO"。
 *
 *
 * 样例:
 * 	输入: S = "admeDCAB"
 * 	输出: "D"
 *
 * 	输入: S = "adme"
 * 	输出: "NO"
 */

public class Solution {
    /**
     * @param s: a string
     * @return: a string
     */
    public String largestLetter(String s) {
        // write your code here
        boolean[] vSmall = new boolean[26];
        boolean[] vBig = new boolean[26];
        char maxChar = '!';
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c >= 'a' && c <= 'z') { // Lower
                vSmall[c - 'a'] = true;
                char upperC = Character.toUpperCase(c);
                if(vBig[upperC - 'A']) {
                    if(maxChar == '!') {
                        maxChar = upperC;
                    } else {
                        maxChar = upperC > maxChar ? upperC : maxChar;
                    }
                }
            } else { // Capital
                vBig[c - 'A'] = true;
                char lowerC = Character.toLowerCase(c);
                if(vSmall[lowerC - 'a']) {
                    if(maxChar == '!') {
                        maxChar = c;
                    } else {
                        maxChar = c > maxChar ? c : maxChar;
                    }
                }
            }
        }

        return maxChar == '!' ? "NO" : String.valueOf(maxChar);
    }

}