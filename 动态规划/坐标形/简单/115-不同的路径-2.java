/**
 * 115 · 不同的路径 II
 * https://www.lintcode.com/problem/115/
 *
 * 描述
 * "不同的路径" 的跟进问题：
 * 有一个机器人的位于一个 𝑚×𝑛 个网格左上角。
 *
 * 机器人每一时刻只能向下或者向右移动一步。机器人试图达到网格的右下角。
 *
 * 现在考虑网格中有障碍物，那样将会有多少条不同的路径？
 *
 * 网格中的障碍和空位置分别用 1 和 0 来表示。
 *
 *
 * 样例
 * 样例 1：
 * 	输入：obstacleGrid = [[0]]
 * 	输出：1
 * 	解释：只有一个点
 *
 * 样例 2：
 * 	输入：obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
 * 	输出：2
 * 	解释：只有 2 种不同的路径
 */

public class Solution {
    /**
     * @param obstacleGrid: A list of lists of integers
     * @return: An integer
     */
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        // write your code here
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        if(obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }
        int[][] dp = new int[m][n];
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(i == 0 && j == 0) {
                    dp[i][j] = 1;
                } else {
                    if(obstacleGrid[i][j] == 1) {
                        dp[i][j] = 0;
                    } else {
                        int up = i > 0 ? dp[i - 1][j] : 0;
                        int left = j > 0 ? dp[i][j - 1] : 0;
                        dp[i][j] = up + left;
                    }
                }
            }
        }

        return dp[m - 1][n - 1];
    }
}