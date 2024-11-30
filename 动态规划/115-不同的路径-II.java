/**
 * 115 · 不同的路径 II
 * https://www.lintcode.com/problem/115/
 * https://leetcode.com/problems/unique-paths-ii/
 *
 * 描述
 * "不同的路径" 的跟进问题：
 * 有一个机器人的位于一个
 * m × n 个网格左上角。
 * 机器人每一时刻只能向下或者向右移动一步。机器人试图达到网格的右下角。
 * 现在考虑网格中有障碍物，那样将会有多少条不同的路径？
 * 网格中的障碍和空位置分别用 1 和 0 来表示。
 *
 * 样例 1：
 * 输入：
 * obstacleGrid = [[0]]
 * 输出：
 * 1
 * 解释：
 * 只有一个点
 *
 * 样例 2：
 * 输入：
 * obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
 * 输出：
 * 2
 * 解释：
 * 只有 2 种不同的路径
 */
public class Solution {
    /**
     * @param obstacleGrid: A list of lists of integers
     * @return: An integer
     */
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        // 这道题也可以用backtrack做， 但是太慢。 如果是可以走4个方向， 则必须用backtrack,
        // 但本题只可以走2个方向， 所以可以用dp
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }

        int[][] dp = new int[m][n];
        dp[0][0] = 1;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    continue;
                } else {
                    int leftX = i, leftY = j - 1;
                    int upX = i - 1, upY = j;

                    if (inBound(leftX, leftY, m, n)) {
                        dp[i][j] += dp[leftX][leftY];
                    }

                    if (inBound(upX, upY, m, n)) {
                        dp[i][j] += dp[upX][upY];
                    }
                }
            }
        }

        return dp[m - 1][n - 1];
    }

    private boolean inBound(int x, int y, int m, int n) {
        return x >= 0 && x < m && y >= 0 && y < n;
    }
}