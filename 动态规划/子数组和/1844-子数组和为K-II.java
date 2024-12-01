/**
 * 1844 · 子数组和为K II
 * https://www.lintcode.com/problem/1844/description
 *
 * 描述
 * 给定一个整数数组和一个整数k，你需要找到和为k的最短非空子数组，并返回它的长度。
 *
 * 如果没有这样的子数组，返回-1.
 *
 * 样例1：
 * 输入:
 * nums = [1,1,1,2] and k = 3
 * 输出:
 * 2
 *
 * 样例2：
 * 输入:
 * nums = [2,1,-1,4,2,-3] and k = 3
 * 输出:
 * 2
 */
public class Solution {
    /**
     * @param nums: a list of integer
     * @param k: an integer
     * @return: return an integer, denote the minimum length of continuous subarrays whose sum equals to k
     */
    public int subarraySumEqualsKII(int[] nums, int k) {
        // write your code here
        int n = nums.length;
        int[] prevSum = new int[n + 1];
        /**
         * prevSum[i] - prevSum[j] represents the sum of the subarray from nums[j] to nums[i - 1].
         */
        for (int i = 1; i < n + 1; i++) {
            prevSum[i] = prevSum[i - 1] + nums[i - 1];
            if (nums[i - 1] == k) {
                return 1;
            }
        }

        Map<Integer, Integer> indexMap = new HashMap<>();
        indexMap.put(0, 0);
        int res = n + 1;
        for (int i = 1; i < n + 1; i++) {
            int curSum = prevSum[i];
            if (indexMap.containsKey(curSum - k)) {
                int index = indexMap.get(curSum - k);
                res = Math.min(res, i - index);
                /**
                 * 	prevSum[i] - prevSum[j]:
                 * 	This expression calculates the sum of the subarray from nums[j] to nums[i - 1].
                 *
                 * 	Subarray Indices: The subarray starts at index j and ends at index i - 1 in the nums array.
                 * 	Length Calculation: The length of this subarray is  (i - 1) - j + 1 = i - j .
                 *
                 * 	Explanation:
                 * 	•	Start Index (s): j
                 * 	•	End Index (e): i - 1
                 * 	•	Length (L):  L = e - s + 1 = (i - 1) - j + 1 = i - j
                 */
            }

            indexMap.put(curSum, i);
        }

        if (res == n + 1) {
            res = -1;
        }

        return res;
    }
}