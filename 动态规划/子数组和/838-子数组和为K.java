/**
 * 838 · 子数组和为K
 * https://www.lintcode.com/problem/838
 * https://leetcode.com/problems/subarray-sum-equals-k
 *
 * 描述
 * 给定一个整数数组和一个整数k，你需要找到连续子数列的和为k的总个数。
 *
 * 样例1
 * 输入: nums = [1,1,1] 和 k = 2
 * 输出: 2
 * 解释:
 * 子数组 [0,1] 和 [1,2]
 *
 * 样例2
 * 输入: nums = [2,1,-1,1,2] 和 k = 3
 * 输出: 4
 * 解释:
 * 子数组 [0,1], [1,4], [0,3] and [3,4]
 */
public class Solution {
    /**
     * @param nums: a list of integer
     * @param k: an integer
     * @return: return an integer, denote the number of continuous subarrays whose sum equals to k
     */
    public int subarraySumEqualsK(int[] nums, int k) {
        // write your code here
        int n = nums.length;

        int[] prevSum = new int[n + 1];
        for (int i = 1; i < n + 1; i++) {
            prevSum[i] = nums[i - 1] + prevSum[i - 1];
        }
        /**
         * prevSum[i] - prevSum[j] represents the sum of the subarray from nums[j] to nums[i - 1].
         */
        Map<Integer, Integer> freqMap = new HashMap<>();

        /** Q: why freqMap.put(0, 1); ?
         * In your loop, you calculate restTarget = curSum - k;.
         * If curSum is exactly k, then restTarget becomes 0.
         * You then check if restTarget exists in freqMap to find how many times a subarray
         * sum leading up to the current index has occurred.
         * 也就是说
         * 这里必须照顾到prevSum[0]的情况， prevSum[0]也是有意义的
         */
        freqMap.put(0, 1);
        int res = 0;
        for (int i = 1; i < n + 1; i++) {
            int curSum = prevSum[i];
            int restTarget = curSum - k;
            if (freqMap.containsKey(restTarget)) {
                res += freqMap.get(restTarget);
            }

            if (!freqMap.containsKey(curSum)) {
                freqMap.put(curSum, 0);
            }
            freqMap.put(curSum, freqMap.get(curSum) + 1);
        }

        return res;
    }
}