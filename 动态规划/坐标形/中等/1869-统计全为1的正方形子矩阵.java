/**
 * 1869 · 统计全为 1 的正方形子矩阵
 * https://www.lintcode.com/problem/1869
 *
 * 描述
 * 给你一个 m * n 的矩阵，矩阵中的元素不是 0 就是 1，请你统计并返回其中完全由 1 组成的 正方形 子矩阵的个数。
 *
 * 示例 1：
 *
 * 输入：
 * matrix =
 * [
 *   [0,1,1,1],
 *   [1,1,1,1],
 *   [0,1,1,1]
 * ]
 * 输出：
 * 15
 * 解释：
 * 边长为 1 的正方形有 10 个。
 * 边长为 2 的正方形有 4 个。
 * 边长为 3 的正方形有 1 个。
 * 正方形的总数 = 10 + 4 + 1 = 15.
 *
 *
 * 示例 2：
 *
 * 输入：
 * matrix =
 * [
 *   [1,0,1],
 *   [1,1,0],
 *   [1,1,0]
 * ]
 * 输出：
 * 7
 * 解释：
 * 边长为 1 的正方形有 6 个。
 * 边长为 2 的正方形有 1 个。
 * 正方形的总数 = 6 + 1 = 7.
 */

public class Solution {
    /**
     * @param matrix: a matrix
     * @return: return how many square submatrices have all ones
     */
    public int countSquares(int[][] matrix) {
        // write your code here
        int m = matrix.length, n = matrix[0].length;
        //dp[i][j] 表示以 (i, j) 为右下角的正方形的最大边长
        int[][] dp = new int[m][n];
        int ans = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                    }
                } else {
                    dp[i][j] = 0;
                }
                ans += dp[i][j];
            }
        }

        return ans;
    }
}