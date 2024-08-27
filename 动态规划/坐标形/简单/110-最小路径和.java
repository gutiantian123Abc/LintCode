/** 110 · 最小路径和
https://www.lintcode.com/problem/110

描述
给定一个只含非负整数的 𝑚 * 𝑛 网格，找到一条从左上角到右下角的可以使数字和最小的路径。

样例
样例 1：
	输入： grid = [[1,3,1],[1,5,1],[4,2,1]]
	输出： 7

	解释：路线为： 1 -> 3 -> 1 -> 1 -> 1

样例 2：
	输入：grid = [[1,3,2]]
	输出：6
	解释：

	路线是： 1 -> 3 -> 2
*/

public class Solution {
    /**
     * @param grid: a list of lists of integers
     * @return: An integer, minimizes the sum of all numbers along its path
     */
    public int minPathSum(int[][] grid) {
        // write your code here
        int m = grid.length, n = grid[0].length;

        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                dp[i][j] = grid[i][j];
                int up = i >= 1 ? dp[i - 1][j] : Integer.MAX_VALUE;
                int left = j >= 1 ? dp[i][j - 1] : Integer.MAX_VALUE;
                dp[i][j] += Math.min(up, left);
            }
        }

        return dp[m - 1][n - 1];
    }
}