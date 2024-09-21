/**
 * 406 · 和大于S的最小子数组
 * https://www.lintcode.com/problem/406/
 *
 * 描述
 * 给定一个由 n 个正整数组成的数组和一个正整数 s ，请找出该数组中满足其和 ≥ s 的最小长度子数组。如果无解，则返回 -1。
 *
 * 样例 1:
 *
 * 输入: [2,3,1,2,4,3], s = 7
 * 输出: 2
 * 解释: 子数组 [4,3] 是该条件下的最小长度子数组。
 *
 * 样例 2:
 *
 * 输入: [1, 2, 3, 4, 5], s = 100
 * 输出: -1
 */
public class Solution {
    /**
     * @param nums: an array of integers
     * @param s: An integer
     * @return: an integer representing the minimum size of subarray
     */
    public int minimumSize(int[] nums, int s) {
        // write your code here
        int n = nums.length;

        int minLength = 1, maxLength = n;

        while (minLength + 1 < maxLength) {
            int midLength = minLength + (maxLength - minLength)/2;
            if (check(midLength, s, nums)) {
                maxLength = midLength;
            } else {
                minLength = midLength;
            }
        }

        if (check(minLength, s, nums)) {
            return minLength;
        }
        if (check(maxLength, s, nums)) {
            return maxLength;
        }

        return -1;
    }

    boolean check(int length, int s, int[] nums) {
        int n = nums.length;
        int[] prevSum = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            prevSum[i] = prevSum[i - 1] + nums[i - 1];
        }

        for (int j = length; j <= n; j++) {
            int i = j - length;
            if (prevSum[j] - prevSum[i] >= s) {
                return true;
            }
        }

        return false;
    }
}
