/**
 * 1703 · 最小面积矩形
 * https://www.lintcode.com/problem/1703/
 * https://leetcode.com/problems/minimum-area-rectangle/
 *
 * 描述
 * 给定二维平面中的一组点，确定由这些点形成的矩形的最小面积，其边平行于x和y轴。
 *
 * 如果没有任何矩形，则返回0。
 *
 * 1 <= points.length <= 500
 * 0 <= points[i][0] <= 40000
 * 0 <= points[i][1] <= 40000
 * 所有点的位置均不相同
 */

public class Solution {
    /**
     * @param points: a set of points
     * @return: the min area
     */
    public int minimumAreaRectangle(int[][] points) {
        // Write your code here.
        /**
         *          算法核心：枚举对角点
         *          •	我们可以枚举所有点对 (p_1, p_2)。当且仅当这对点满足 x_1 != x_2 且 y_1 != y_2 时，
         *              这两个点才有可能成为一个轴对齐矩形的对角。
         *          •	随后检查另两个点 (x_1, y_2) 和 (x_2, y_1) 是否在集合中存在。
         *          •	如果存在，则可以计算这个矩形的面积
         */
        Set<String> set = new HashSet<>();
        for (int[] point : points) {
            int x = point[0], y = point[1];
            String p = x + "," + y;
            set.add(p);
        }

        int n = points.length;
        int res = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int[] p1 = points[i];
                int[] p2 = points[j];
                int x1 = p1[0], y1 = p1[1];
                int x2 = p2[0], y2 = p2[1];
                if (x1 != x2 && y1 != y2) {
                    int x3 = p1[0], y3 = p2[1];
                    int x4 = p2[0], y4 = p1[1];
                    if (set.contains(x3 + "," + y3) && set.contains(x4 + "," + y4)) {
                        res = Math.min(res, Math.abs(y3 - y1) * Math.abs(x4 - x1));
                    }
                }
            }
        }

        if (res == Integer.MAX_VALUE) {
            res = 0;
        }
        return res;
    }
}