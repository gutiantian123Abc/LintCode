/**
 * 41 · 最大子数组
 * https://www.lintcode.com/problem/41/
 * https://leetcode.com/problems/maximum-subarray/
 *
 * 描述
 * 给定一个整数数组，找到一个具有最大和的子数组，返回其最大和。
 * 每个子数组的数字在数组中的位置应该是连续的。
 *
 * 样例 1：
 * 输入：nums = [−2,2,−3,4,−1,2,1,−5,3]
 * 输出：6
 * 解释：符合要求的子数组为[4,−1,2,1]，其最大和为 6。
 *
 * 样例 2:
 * 输入：nums = [1,2,3,4]
 * 输出：10
 * 解释：符合要求的子数组为[1,2,3,4]，其最大和为 10。
 *
 * 挑战
 * 要求时间复杂度为O(n)
 */
public class Solution {
    /**
     * @param nums: A list of integers
     * @return: A integer indicate the sum of max subarray
     */
    public int maxSubArray(int[] nums) {
        // write your code here
        int n = nums.length;
        int[] prevSum = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            prevSum[i] = prevSum[i - 1] + nums[i - 1];

        }
        int minSum = prevSum[0];
        int res = Integer.MIN_VALUE;
        for (int i = 1; i <= n; i++) {
            res = Math.max(res, prevSum[i] - minSum);
            minSum = Math.min(minSum, prevSum[i]);
        }

        return res;
    }
}