/** https://www.lintcode.com/problem/3741/
 * 3741 · 范围和相等的最宽索引对
 * 描述
 * 给定两个二进制数组 nums1 和 nums2，找出 最宽 的索引对 (i, j)，
 * 使得 i <= j 并且 nums1[i] + nums1[i + 1] + ... + nums1[j] == nums2[i] + nums2[i + 1] + ... + nums2[j]。
 *
 * 最宽 是指索引 i 与索引 j 之间的距离最大，其中距离为 j - i + 1。
 * 返回符合条件的 最宽索引对的距离，如果没有满足条件的索引对，则返回 0。
 *
 * 样例 1：
 *  输入：
 *  nums1 = [1, 0, 1, 0, 1]
 *  nums2 = [0, 1, 1, 1, 1]
 *  输出：
 *  3
 *  解释：
 *  当 i = 0，j = 2 时满足条件且最宽
 *  nums1[0] + nums1[1] + nums1[2] = nums2[0] + nums2[1] + nums2[2]
 *  宽度为 3
 *
 *
 * 样例 2：
 *  输入：
 *  nums1 = [0]
 *  nums2 = [1]
 *  输出：
 *  0
 *  解释：
 *  没有符合要求的索引对
 */

public class Solution {
    /**
     * @param nums1: An integer array
     * @param nums2: An integer array
     * @return: Widest index pair width
     */
    public int pairOfIndices(int[] nums1, int[] nums2) {
        // write your code here
        int n = nums1.length;
        int[] diff = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            diff[i] = diff[i - 1] + nums1[i - 1] - nums2[i - 1];
        }

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 0); // 前缀和差异为0时从index 0开始

        int maxWidth = 0;
        for (int j = 1; j <= n; j++) {
            /**
             * 当 diff[j] = diff[i] 时，说明在 i 和 j 之间的 nums1 和 nums2 的子数组和相等，
             * 计算并比较 j - i 的值来找到最宽的索引对的距离
             */
            if (map.containsKey(diff[j])) {
                // 计算宽度
                int i = map.get(diff[j]);
                maxWidth = Math.max(maxWidth, j - i);
            } else {
                // 记录第一次出现的前缀和差异及其对应的索引
                map.put(diff[j], j);
            }
        }

        return maxWidth;
    }
}