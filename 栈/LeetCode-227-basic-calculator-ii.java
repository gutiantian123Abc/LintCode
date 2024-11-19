/**
 * 227. Basic Calculator II
 * https://leetcode.com/problems/basic-calculator-ii
 *
 * Given a string s which represents an expression,
 * evaluate this expression and return its value.
 *
 * The integer division should truncate toward zero.
 * You may assume that the given expression is always valid.
 * All intermediate results will be in the range of [-2^31, 2^31 - 1].
 *
 * Note: You are not allowed to use any built-in function
 * which evaluates strings as mathematical expressions, such as eval().
 *
 * Example 1:
 * Input: s = "3+2*2"
 * Output: 7
 *
 * Example 2:
 * Input: s = " 3/2 "
 * Output: 1
 *
 * Example 3:
 * Input: s = " 3+5 / 2 "
 * Output: 5
 *
 * Constraints:
 * 1 <= s.length <= 3 * 10^5
 * s consists of integers and operators ('+', '-', '*', '/') separated by some number of spaces.
 * s represents a valid expression.
 * All the integers in the expression are non-negative integers in the range [0, 2^31 - 1].
 * The answer is guaranteed to fit in a 32-bit integer.
 */
class Solution {
    public int calculate(String s) {
        s = s.replaceAll(" ", "");
        char operation = '+';
        int currentNum = 0;
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // If the current character is a digit, build the current number
            if (Character.isDigit(c)) {
                currentNum = currentNum * 10 + (c - '0');
            }
            // If the current character is an operator or we're at the last character
            if (!Character.isDigit(c) || i == s.length() - 1) {
                // Perform the operation based on the previous operator
                if (operation == '+') {
                    stack.push(currentNum);
                }

                if (operation == '-') {
                    stack.push(-1 * currentNum);
                }

                if (operation == '*') {
                    stack.push(stack.pop() * currentNum);

                }

                if (operation == '/') {
                    stack.push(stack.pop() / currentNum);
                }

                // Update the operation and reset the current number
                currentNum = 0;
                if (i != s.length() - 1) {
                    operation = c;
                }
            }
        }

        int res = 0;

        for (int num : stack) {
            res += num;
        }

        return res;
    }
}