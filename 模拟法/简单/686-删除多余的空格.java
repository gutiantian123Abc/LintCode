/**
 * 686 · 删除多余的空格
 * https://www.lintcode.com/problem/686
 *
 * 描述
 * 从句子中删除多余空格
 *
 * 样例
 * 样例 1:
 * 	输入: s = "The  sky   is blue"
 * 	输出: "The sky is blue"
 *
 *
 * 样例 2:
 * 	输入: s = "  low               ercase  "
 * 	输出: "low ercase"
 */

public class Solution {
    /**
     * @param s: the original string
     * @return: the string without arbitrary spaces
     */
    public String removeExtra(String s) {
        // write your code here
        // Trim the input string to remove leading and trailing spaces
        s = s.trim();
        String[] words = s.split("\\s+");// Remove all spaces
        StringBuilder sb = new StringBuilder();

        for(String word : words) {
            sb.append(word);
            sb.append(" ");
        }

        return sb.toString().trim();
    }
}