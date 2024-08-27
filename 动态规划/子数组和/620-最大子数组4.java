/**
 * 620 · 最大子数组 IV
 * https://www.lintcode.com/problem/620/
 *
 * 描述
 * 给定一个整数数组，找到长度大于或等于 k 的连续子序列使它们的和最大，
 * 返回这个最大的和，如果数组中少于k个元素则返回 0
 *
 * note: 确保返回结果为整型
 * k > 0
 *
 * 例1:
 * 输入:
 * [-2,2,-3,4,-1,2,1,-5,3]
 * 5
 * 输出:
 * 5
 * 解释:
 * [2,-3,4,-1,2,1]
 * sum=5
 *
 * 例2:
 * 输入:
 * [5,-10,4]
 * 2
 * 输出:
 * -1
 */

public class Solution {
    /**
     * @param nums: an array of integer
     * @param k: an integer
     * @return: the largest sum
     */
    public int maxSubarray4(int[] nums, int k) {
        // write your code here
        int n = nums.length;

        if (k > n) {
            return 0;
        }

        int[] prevSum = new int[n + 1];
        for(int i = 1; i <= n; i++) {
            prevSum[i] = prevSum[i - 1] + nums[i - 1];
        }

        int result = prevSum[k];
        int leftMinSum = Integer.MAX_VALUE;

        for(int i = k; i <= n; i++) {
            leftMinSum = Math.min(leftMinSum, prevSum[i - k]);
            result = Math.max(result, prevSum[i] - leftMinSum);
        }

        return result;
    }
}