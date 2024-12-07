/**
 * 911 · 最大子数组之和为k
 * https://www.lintcode.com/problem/911/
 * https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/
 *
 * 描述
 * 给一个数组nums和目标值k，找到数组中最长的子数组，使其中的元素和为k。如果没有，则返回0。
 *
 * 样例1:
 * 输入: nums = [1, -1, 5, -2, 3], k = 3
 * 输出: 4
 * 解释:
 * 子数组[1, -1, 5, -2]的和为3，且长度最大
 *
 *
 * 样例2:
 * 输入: nums = [-2, -1, 2, 1], k = 1
 * 输出: 2
 * 解释:
 * 子数组[-1, 2]的和为1，且长度最大
 */

public class Solution {
    /**
     * @param nums: an array
     * @param k: a target value
     * @return: the maximum length of a subarray that sums to k
     */
    public int maxSubArrayLen(int[] nums, int k) {
        // Write your code here
        int n = nums.length;
        int[] prevSum = new int[n + 1];

        for (int i = 1; i < n + 1; i++) {
            prevSum[i] = nums[i - 1] + prevSum[i - 1];
        }

        Map<Integer, Integer> map = new HashMap<>();
        /** why map.put(0, 0) ?
         •	prevSum[i] 代表数组前 i 个元素的和（从 nums[0] 到 nums[i-1]）。
         •	当 prevSum[i] = k 时，意味着从数组开头到 i-1 的这段子数组之和为 k。
         此时，子数组起点是0，长度就是 i - 0 = i。
         •	如果事先没有在 map 中存入 (0, 0)，当 prevSum[i] = k 时，
         你想要找到 prevSum[i] - k = 0 在 map 中的位置，却发现 map 并无记录，
         就无法计算这段从头开始的子数组的长度。
         */
        map.put(0, 0);
        int maxLen = 0;

        for (int i = 1; i < n + 1; i++) {
            int sum = prevSum[i];
            if (map.containsKey(sum - k)) {
                /**
                 当 j >= i 时，prevSum[j] - prevSum[i] 表示
                 从数组下标 i 开始到 j-1 结束的那段连续子数组的和,
                 长度为 (j - 1) - i + 1 = j - i
                 */
                maxLen = Math.max(maxLen, i - map.get(sum - k));
            }

            if (!map.containsKey(sum)) {
                map.put(sum, i);
            }
        }

        return maxLen;
    }
}