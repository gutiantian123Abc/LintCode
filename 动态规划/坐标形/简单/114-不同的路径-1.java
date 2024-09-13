/**
 * 114 · 不同的路径
 * https://www.lintcode.com/problem/114
 *
 * 描述
 * 有一个机器人的位于一个 𝑚 × 𝑛 个网格左上角。
 *
 * 机器人每一时刻只能向下或者向右移动一步。机器人试图达到网格的右下角。
 *
 * 问有多少条不同的路径？
 *
 *
 * 样例
 * 样例 1：
 * 	输入:
 * 	n = 1
 * 	m = 3
 * 	输出：1
 *
 * 	解释：
 * 	只有一条通往目标位置的路径
 *
 *
 * 样例 2:
 * 	n = 3
 * 	m = 3
 * 	输出：6
 *
 * 	解释：
 * 	D : Down
 * 	R : Right
 *
 * 	DDRR
 * 	DRDR
 * 	DRRD
 * 	RRDD
 * 	RDRD
 * 	RDDR
 */

public class Solution {
	/**
	 * @param m: positive integer (1 <= m <= 100)
	 * @param n: positive integer (1 <= n <= 100)
	 * @return: An integer
	 */
	public int uniquePaths(int m, int n) {
		// write your code here
		int[][] dp = new int[m][n];
		dp[0][0] = 1;
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				if(i == 0 && j == 0) {
					continue;
				}
				int up = i > 0 ? dp[i - 1][j] : 0;
				int left = j > 0 ? dp[i][j - 1] : 0;
				dp[i][j] = up + left;
			}
		}

		return dp[m - 1][n - 1];
	}
}