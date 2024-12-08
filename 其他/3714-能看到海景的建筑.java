/**
 * 3714 · 能看到海景的建筑
 * https://www.lintcode.com/problem/3714/
 * https://leetcode.com/problems/buildings-with-an-ocean-view/
 *
 * 描述
 * 给定长度为 n 的整数数组 heights，表示有 n 个建筑物，heights[i] 表示了对应位置的建筑物高度。
 * 建筑物的右边是海洋，对于每一个建筑物，如果该建筑物 右侧的所有建筑物都严格低于它时，则这栋建筑物可以看到海景。
 * 返回所有可以看到海景的建筑物的 索引下标 列表，按照升序排列，索引下标从 0 开始。
 *
 * 样例 1：
 * 输入：
 * heights = [5, 2, 3, 4]
 * 输出：
 * [0, 3]
 * 解释：
 * 1 号建筑高度为 2，2 号建筑高度为 3，没有严格高于其右侧的建筑
 *
 * 样例 2：
 * 输入：
 * heights = [2, 2, 2, 2, 2]
 * 输出：
 * [4]
 * 解释：
 * 没有严格高于右侧的建筑，因此只有最右侧的 4 号建筑物能看到海景
 */
public class Solution {
    /**
     * @param heights: An integer array
     * @return: Indexs of buildings with sea view
     */
    public int[] findBuildings(int[] heights) {
        // write your code here
        int maxH = 0;
        List<Integer> ind = new ArrayList<>();
        for (int i = heights.length - 1; i >= 0; i--) {
            if (heights[i] > maxH) {
                ind.add(i);
            }

            maxH = Math.max(maxH, heights[i]);
        }

        Collections.reverse(ind);
        int[] res = new int[ind.size()];

        for (int i = 0; i < ind.size(); i++) {
            res[i] = ind.get(i);
        }

        return res;
    }
}