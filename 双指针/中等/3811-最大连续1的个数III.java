/**
 * 3811 · 最大连续 1 的个数（三）
 * https://www.lintcode.com/problem/3811/description
 * https://leetcode.com/problems/max-consecutive-ones-iii/
 *
 * 描述
 * 给定一个二进制数组 nums（数组中仅包含元素 0 或 1）和一个整数 k，
 * 如果可以翻转最多 k 个 0，则返回数组中连续的 1 的最大个数。
 *
 * 样例 1：
 * 输入：
 * nums = [1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0]
 * k = 2
 * 输出：
 * 6
 * 解释：
 * 一种可能的翻转方式如下，连续 1 的最大个数为 6
 * [1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0]
 *
 * 样例 2:
 * 输入：
 * nums = [0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1]
 * k = 3
 * 输出：
 * 10
 * 解释：
 * 翻转方式如下，连续 1 的最大个数为 10
 * [0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1]
 */
public class Solution {
    /**
     * @param nums: An integer array
     * @param k: At most k 0 can be flipped
     * @return: maximum number of consecutive 1
     */
    public int longestOnes(int[] nums, int k) {
        // write your code here
        int left = 0, right = 0, maxLength = 0, n = nums.length, balance = k;

        while (right < n) {
            if (nums[right] == 0) {
                balance--;
                while (balance < 0 && left <= right) {
                    if (nums[left] == 0) {
                        balance++;
                    }
                    left++;
                }
            }
            maxLength = Math.max(maxLength, right - left + 1);
            right++;
        }

        return maxLength;
    }
}