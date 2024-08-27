/** https://www.lintcode.com/problem/111/
111 · 爬楼梯

描述
假设你正在爬楼梯，需要n步你才能到达顶部。但每次你只能爬一步或者两步，爬到顶部的方法有多少种？

样例
样例 1：

输入：n = 3
输出：3


样例 2：

输入：n = 1
输出：1
*/

public class Solution {
    /**
     * @param n: An integer
     * @return: An integer
     */
    public int climbStairs(int n) {
        // write your code here
        if(n == 0) {
            return 0; //根据题意， 不固定
        }
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for(int i = 2; i <= n; i++) {
            dp[i] = dp[i - 2] + dp[i - 1];
        }

        return dp[n];
    }
}