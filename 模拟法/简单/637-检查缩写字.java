/* 637 · 检查缩写字
https://www.lintcode.com/problem/637/

描述
给定一个非空字符串 word 和缩写 abbr，返回字符串是否可以和给定的缩写匹配。
比如一个 “word” 的字符串仅包含以下有效缩写：
["word", "1ord", "w1rd", "wo1d", "wor1", "2rd", "w2d", "wo2", "1o1d",
 "1or1", "w1r1", "1o2", "2r1", "3d", "w3", "4"]


样例
样例 1:
输入 : s = "internationalization", abbr = "i12iz4n"
输出 : true


样例 2:
输入 : s = "apple", abbr = "a2e"
输出 : false
*/

public class Solution {
    /**
     * @param word: a non-empty string
     * @param abbr: an abbreviation
     * @return: true if string matches with the given abbr or false
     */
    public boolean validWordAbbreviation(String word, String abbr) {
        // write your code here
        int m = word.length(), n = abbr.length();
        int i = 0, j = 0; // m. i for word; n, j for abbr
        while(j < n) {
            char c = abbr.charAt(j);
            if(!Character.isDigit(c)) {
                if(word.charAt(i) != c) {
                    return false;
                } else {
                    i++;
                    j++;
                }
            } else {
                int x = j;
                while(x < n && Character.isDigit(abbr.charAt(x))) {
                    x++;
                }
                String subStr = abbr.substring(j, x);
                if(subStr.charAt(0) == '0') {
                    return false;
                }
                int digit = Integer.valueOf(subStr);
                if(i + digit > m) {
                    return false;
                } else {
                    i += digit;
                    j = x;
                }
            }
        }
        return i == m;
    }
}