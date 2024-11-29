/**
 * 162. Find Peak Element
 * https://www.lintcode.com/problem/75/
 * https://leetcode.com/problems/find-peak-element/
 * 描述
 * 给定一个长度为 n 的整数数组 nums，其具有以下特点：
 *  相邻位置的数字是不同的
 *  nums 至少存在一个峰值
 * 假定 P 是峰值的位置则满足 nums[P] > nums[P-1] 且 nums[P] > nums[P+1]，返回数组中任意一个峰值的位置。
 *
 * 数组保证至少存在一个峰
 * 如果数组存在多个峰，返回其中任意一个就行
 * 数组至少包含 3 个数
 *
 * 样例 1：
 * 输入：
 * A = [1, 2, 1, 3, 4, 5, 7, 6]
 * 输出：
 * 1
 *
 * 样例 2：
 * 输入：
 * A = [1,2,3,4,1]
 * 输出：
 * 3
 */
public class Solution {
    /**
     * @param a: An integers array.
     * @return: return any of peek positions.
     */
    public int findPeak(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {
            int midIndex = left + (right - left)/2;
            int midVal = nums[midIndex];
            int leftVal = nums[midIndex - 1];
            int rightVal = nums[midIndex + 1];
            if (leftVal < midVal && rightVal < midVal) {
                return midIndex;
            } else if (leftVal < midVal) {
                left = midIndex;
            } else if (rightVal < midVal) {
                right = midIndex;
            } else {
                right = midIndex;
            }
        }

        if (nums[left] > nums[right]) {
            return left;
        }

        return right;
    }
}