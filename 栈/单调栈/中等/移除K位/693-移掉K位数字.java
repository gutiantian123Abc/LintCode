/* 
https://www.lintcode.com/problem/693

693 · 移掉K位数字

描述
给定一个以字符串表示的非负整数 num，移除这个数中的 k 位数字，使得剩下的数字最小。

样例 1 :
 输入: num = "1432219", k = 3
 输出: "1219"
 解释: 移除掉三个数字 4, 3, 和 2 形成一个新的最小的数字 1219。


样例 2 :
 输入: num = "10200", k = 1
 输出: "200"
 解释: 移掉首位的 1 剩下的数字为 200. 注意输出不能有任何前导零。


样例 3 :
 输入: num = "10", k = 2
 输出: "0"
 解释: 从原数字移除所有的数字，剩余为空就是0。
*/

public class Solution {
    /**
     * @param num: a number
     * @param k: the k digits
     * @return: the smallest number
     */
    public String removeKdigits(String num, int k) {
        // write your code here.
        int n = num.length();
        int deleteCount = k;
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < n; i++) {
            char c = num.charAt(i);
            while(!stack.isEmpty() && stack.peek() > c && deleteCount > 0) {
                stack.pop();
                deleteCount--;
            }
            stack.push(c);
        }

        while(deleteCount > 0) {
            stack.pop();
            deleteCount--;
        }

        StringBuilder sb = new StringBuilder();
        while(!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        if(sb.length() == 0) {
            return "0";
        }
        
        String ans = sb.reverse().toString();

        // Remove leading 0s
        int i = 0;
        while(i < ans.length()) {
            if(ans.charAt(i) != '0') {
                break;
            } else {
                i++;
            }
        }
        if(i == ans.length()) {
            return "0";
        }

        return ans.substring(i, ans.length());
    }
}