/**
https://www.lintcode.com/problem/1255

1255 · 移除K位

描述
给定一个以字符串表示的非负整数，从该数字中移除掉k个数位，
让剩余数位组成的数字尽可能小，求可能的最小结果。

样例1：
 输入：num = "1432219", k = 3
 输出："1219"
 说明：移除数位4, 3, 2后生成了最小的新数字1219。


样例2：
 输入：num = "10200", k = 1
 输出："200"
 说明：移除最前面的1之后，剩余数字为200。注意输出结果不能包含前导零。


样例3：
 输入：num = "10", k = 2
 输出："0"
 说明：移除了所有数位，最后什么都不剩了，那么就输出0。
*/

public class Solution {
    /**
     * @param num: a string
     * @param k: an integer
     * @return: return a string
     */
    public String removeKdigits(String num, int k) {
        // write your code here
        // 这道题类似标准模版， 即：一个数组， 删除K个， 使得剩下的数组最小/最大， 并保留原来顺序
        // 本质上与此题一致： https://www.lintcode.com/problem/244/
        // 244 · 删除字符
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