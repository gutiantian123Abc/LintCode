/**
 * 994 · 连续数组
 * https://www.lintcode.com/problem/994
 * 描述
 * 给一个二进制数组，找到 0 和 1 数量相等的子数组的最大长度
 *
 * 样例 1:
 * 输入: [0,1]
 * 输出: 2
 * 解释: [0, 1] 是具有相等数量的 0 和 1 的最长子数组。
 *
 * 样例 2:
 * 输入: [0,1,0]
 * 输出: 2
 * 解释: [0, 1] (或者 [1, 0]) 是具有相等数量 0 和 1 的最长子数组。
 */

public class Solution {
    /**
     * @param nums: a binary array
     * @return: the maximum length of a contiguous subarray
     */
    public int findMaxLength(int[] nums) {
        int n = nums.length;
        int[] prevSum = new int[n + 1];

        // 将0视为-1，计算前缀和
        for (int i = 1; i <= n; i++) {
            int num = nums[i - 1] == 0 ? -1 : 1;
            prevSum[i] = prevSum[i - 1] + num;
        }

        Map<Integer, Integer> map = new HashMap<>();
        int maxLen = 0;

        for (int i = 0; i <= n; i++) {
            int sum = prevSum[i];
            if (!map.containsKey(sum)) {
                map.put(sum, i);
            } else {
                maxLen = Math.max(maxLen, i - map.get(sum));
            }
        }

        return maxLen;
    }
}