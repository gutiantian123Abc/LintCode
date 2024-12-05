/**
 * 1721 · 使括号有效的最少添加
 * https://www.lintcode.com/problem/1721/
 * https://leetcode.com/problems/minimum-add-to-make-parentheses-valid/
 *
 * 描述
 * 给定一个由 '(' 和 ')' 括号组成的字符串 S，我们需要添加最少的括号（ '(' 或是 ')'，可以在任何位置），
 * 以使得到的括号字符串有效。
 *
 * 从形式上讲，只有满足下面几点之一，括号字符串才是有效的：
 *
 * 它是一个空字符串，或者
 * 它可以被写成 AB （A 与 B 连接）, 其中 A 和 B 都是有效字符串，或者
 * 它可以被写作 (A)，其中 A 是有效字符串。
 * 给定一个括号字符串，返回为使结果字符串有效而必须添加的最少括号数。
 *
 * 样例 1:
 * 输入: "())"
 * 输出: 1
 *
 * 样例 2:
 * 输入: "((("
 * 输出: 3
 *
 * 样例 3:
 * 输入: "()"
 * 输出: 0
 *
 * 样例 4:
 * 输入: "()))(("
 * 输出: 4
 */
public class Solution {
    /**
     * @param s: the given string
     * @return: the minimum number of parentheses we must add
     */
    public int minAddToMakeValid(String s) {
        // Write your code here
        Stack<Character> stack = new Stack<>();
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '(') {
                stack.push(c);
            } else { // ')'
                if (!stack.isEmpty()) {
                    stack.pop();
                } else {
                    res++;
                }
            }
        }

        if (!stack.isEmpty()) {
            res += stack.size();
        }

        return res;
    }
}