/* 272 · 爬楼梯 II
https://www.lintcode.com/problem/272

描述
一个小孩爬一个 n 层台阶的楼梯。
他可以每次跳 1 步， 2 步 或者 3 步。
实现一个方法来统计总共有多少种不同的方式爬到最顶层的台阶。


样例
样例1
	输入: 3
	输出: 4
	解释: 1 + 1 + 1 = 2 + 1 = 1 + 2 = 3 = 3 , 一共4种方法。


样例2:
	输入: 4
	输出: 7
	解释: 1 + 1 + 1 + 1 = 1 + 1 + 2 = 1 + 2 + 1 = 2 + 1 + 1 = 2 + 2 = 1 + 3 = 3 + 1 = 4 , 一共7种方法
*/

public class Solution {
    /**
     * @param n: An integer
     * @return: An Integer
     */
    public int climbStairs2(int n) {
        // write your code here
        if(n == 0) {
            return 1; //根据题意， 不固定
        } else {
            if(n == 1) {
                return 1;
            } else if(n == 2) {
                return 2;
            } else {
                int[] dp = new int[n + 1];
                dp[0] = 1;
                dp[1] = 1;
                dp[2] = 2;
                for(int i = 3; i <= n; i++) {
                    dp[i] = dp[i - 3] + dp[i - 2] + dp[i - 1];
                }
                return dp[n];
            }
        }
    }
}