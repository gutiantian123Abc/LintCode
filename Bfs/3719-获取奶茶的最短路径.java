/**
 * 3719 · 获取奶茶的最短路径
 * https://www.lintcode.com/problem/3719
 *
 * 描述
 * 现有一个 m×n 的 字符矩阵 grid，其包含下列不同类型的格子：
 *
 * '0' 表示 空地，你可以穿过这些格子
 * 'X' 表示 障碍，你不可以穿过这些格子
 * '#' 表示 奶茶，矩阵中可能存在 多杯 奶茶
 * '*' 表示 你的位置，矩阵中 有且只有一个 '*' 格子
 * 你需要从你所在的位置开始，找到一条 最短的路径 以到达一杯奶茶所在的格子。最后，请返回你获取任意一杯奶茶的 最短路径的长度。
 * 如果不存在这样的路径，返回 -1。
 *
 * grid[row][col] 的值只会是 '*'、'#'、'0' 或 'X'。
 *
 * grid 中 有且只有一个 '*', 1 ≤ m, n ≤ 200
 */

public class Solution {
    /**
     * @param grid: A m×n character matrix.
     * @return: The length of the shortest path to obtain any cup of bubble tea.
     */
    public int getBubbleTea(char[][] grid) {
        // --- write your code here ---
        int m = grid.length, n = grid[0].length;
        int x = 0, y = 0;
        boolean[][] v = new boolean[m][n];
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '*') {
                    x = i;
                    y = j;
                }
            }
        }

        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[] {x, y});
        v[x][y] = true;
        int step = 0;
        while (queue.size() != 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] curIndex = queue.poll();
                int p = curIndex[0], q = curIndex[1];

                for (int dir = 0; dir < 4; dir++) {
                    int np = p + dx[dir], nq = q + dy[dir];
                    if (np >= 0 && np < m && nq >= 0 && nq < n && !v[np][nq]) {
                        if (grid[np][nq] == 'O') {
                            int[] nextIndex = {np, nq};
                            queue.add(new int[] {np, nq});
                            v[np][nq] = true;
                        }
                        if (grid[np][nq] == '#') {
                            return step + 1;
                        }
                    }
                }
            }
            step++;
        }

        return -1;
    }
}