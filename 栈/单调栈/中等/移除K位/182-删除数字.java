/*
https://www.lintcode.com/problem/182
182 · 删除数字

描述
给出一个字符串 A, 表示一个 n 位正整数, 删除其中 k 位数字, 
使得剩余的数字仍然按照原来的顺序排列产生一个新的正整数。

找到删除 k 个数字之后的最小正整数。

N <= 240, k <= N

样例 1：
 输入: A = "178542", k = 4
 输出:"12"


样例 2：
 输入: A = "568431", k = 3
 输出:"431"
*/

public class Solution {
    /**
     * @param a: A positive integer which has N digits, A is a string
     * @param k: Remove k digits
     * @return: A string
     */
    public String deleteDigits(String a, int k) {
        // write your code here
        int n = a.length();
        int deleteCount = k;
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < n; i++) {
            char c = a.charAt(i);
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