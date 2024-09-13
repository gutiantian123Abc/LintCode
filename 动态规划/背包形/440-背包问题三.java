/**
 * 440 · 背包问题（三）
 * https://www.lintcode.com/problem/440
 *
 * 描述
 * 给定 n 种物品, 每种物品都有无限个. 第 i 个物品的体积为 A[i], 价值为 V[i].
 * 再给定一个容量为 m 的背包. 问可以装入背包的最大价值是多少?
 *
 *
 * 样例
 * 样例 1:
 * 	输入: A = [2, 3, 5, 7], V = [1, 5, 2, 4], m = 10
 * 	输出: 15
 * 	解释: 装入三个物品 1 (A[1] = 3, V[1] = 5), 总价值 15.
 *
 *
 * 样例 2:
 * 	输入: A = [1, 2, 3], V = [1, 2, 3], m = 5
 * 	输出: 5
 * 	解释: 策略不唯一. 比如, 装入五个物品 0 (A[0] = 1, V[0] = 1).
 */
public class Solution {
    /**
     * @param a: an integer array
     * @param v: an integer array
     * @param m: An integer
     * @return: an array
     */
    public int backPackIII(int[] a, int[] v, int m) {
        // write your code here
        int n = a.length;
        int[][] dp = new int[n + 1][m + 1];

        for(int i = 1; i <= n; i++) {
            int I = i - 1;
            for(int j = 0; j <= m; j++) {
                int addNum = 0;
                while(j >= addNum * a[I]) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - addNum*a[I]] + addNum * v[I]);
                    addNum++;
                }
            }
        }

        return dp[n][m];
    }
}