/** 92 · 背包问题
https://www.lintcode.com/problem/92/


描述
在 n 个物品中挑选若干物品装入背包，最多能装多满？
假设背包的大小为m，每个物品的大小为
𝐴[i]（每个物品只能选择一次且物品大小均为正整数）

样例 1：
	输入：
	数组 = [3,4,8,5]
	backpack size = 10
	输出：
	9

	解释：
	装4和5


样例 2：
	输入：
	数组 = [2,3,5,7]
	backpack size = 12

	输出：
	12

	解释：
	装5和7.
*/

public class Solution {
    /**
     * @param m: An integer m denotes the size of a backpack
     * @param a: Given n items with size A[i]
     * @return: The maximum size
     */
    public int backPack(int m, int[] a) {
        // write your code here
        // 参考答案： https://www.lintcode.com/problem/92/solution/17186
        int n = a.length;
        int[][] dp = new int[n + 1][m + 1];
        for(int i = 1; i <= n; i++) {
            for(int j = 0; j <= m; j++) {
                int I = i - 1;
                dp[i][j] = dp[i - 1][j];
                if(j >= a[I]) {
                    dp[i][j] = Math.max(dp[i - 1][j], a[I] + dp[i - 1][j - a[I]]);
                }
            }
        }

        return dp[n][m];
    }
}