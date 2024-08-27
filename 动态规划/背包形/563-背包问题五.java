/** https://www.lintcode.com/problem/563
563 · 背包问题五
描述
给出 n 个物品, 以及一个数组, nums[i] 代表第i个物品的大小, 
保证大小均为正数, 正整数 target 表示背包的大小, 找到能填满背包的方案数。
每一个物品只能使用一次

样例
样例 1：
	输入：
	nums = [1,2,3,3,7]
	target = 7
	输出：
	2
	解释：
	结果的集合为:
	[7]
	[1,3,3]
	返回 2


样例 2：
	输入：
	nums = [1,1,1,1]
	target = 3
	输出：
	4
	解释：
	从 4 个物品中任选 3 个，共 4 种选法
*/

public class Solution {
    /**
     * @param nums: an integer array and all positive numbers
     * @param target: An integer
     * @return: An integer
     */
    public int backPackV(int[] nums, int target) {
        // write your code here
        int n = nums.length;
        int[][] dp = new int[n + 1][target + 1];
        dp[0][0] = 1;

        for(int i = 1; i <= n; i++) {
            int I = i - 1;
            for(int j = 0; j <= target; j++) {
                dp[i][j] = dp[i - 1][j];
                if(j >= nums[I]) {
                    dp[i][j] += dp[i - 1][j - nums[I]];
                }
            }
        }

        return dp[n][target];
    }
}