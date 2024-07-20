/* 562 · 背包问题四
https://www.lintcode.com/problem/562

描述
给出 n 个物品, 以及一个数组, nums[i]代表第i个物品的大小, 
保证大小均为正数并且没有重复, 正整数 target 表示背包的大小, 找到能填满背包的方案数。
每一个物品可以使用无数次


样例
样例1：
	输入: nums = [2,3,6,7] 和 target = 7
	输出: 2
	解释:
	方案有: 
	[7]
	[2, 2, 3]


样例2：
	输入: nums = [2,3,4,5] 和 target = 7
	输出: 3
	解释:
	方案有: 
	[2, 5]
	[3, 4]
	[2, 2, 3]
*/

public class Solution {
    /**
     * @param nums: an integer array and all positive numbers, no duplicates
     * @param target: An integer
     * @return: An integer
     */
    public int backPackIV(int[] nums, int target) {
        // write your code here
        int n = nums.length;
        int[][] dp = new int[n + 1][target + 1];
        dp[0][0] = 1;

        for(int i = 1; i <= n; i++) {
            int I = i - 1;
            for(int j = 0; j <= target; j++) {
                int addNum = 0;
                while(j >= addNum * nums[I]) {
                    dp[i][j] += dp[i - 1][j - addNum*nums[I]];
                    addNum++;
                }
            }
        }

        return dp[n][target];
    }
}