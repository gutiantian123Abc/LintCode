/**
 * 1091. Shortest Path in Binary Matrix
 * https://leetcode.com/problems/shortest-path-in-binary-matrix
 *
 * Given an n x n binary matrix grid,
 * return the length of the shortest clear path in the matrix. If there is no clear path, return -1.
 *
 * A clear path in a binary matrix is a path
 * from the top-left cell (i.e., (0, 0)) to the bottom-right cell (i.e., (n - 1, n - 1)) such that:
 *
 * All the visited cells of the path are 0.
 * All the adjacent cells of the path are 8-directionally connected
 * (i.e., they are different and they share an edge or a corner).
 * The length of a clear path is the number of visited cells of this path.
 *
 * Example see the link above
 *
 * Constraints:
 * n == grid.length
 * n == grid[i].length
 * 1 <= n <= 100
 * grid[i][j] is 0 or 1
 */
class Solution {

    int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dy = {-1, 0, 1, 1, -1, 0, -1, 1};

    public int shortestPathBinaryMatrix(int[][] grid) {
        // shortest path use BFS
        int n = grid[0].length;
        boolean[][] v = new boolean[n][n];
        Queue<int[]> q = new LinkedList<>();
        if (grid[0][0] == 1 || grid[n - 1][n -1 ] == 1) {
            return -1;
        }

        int[] start = {0, 0};
        int step = 0;
        q.add(start);
        // Make sure to set v = true once added to queue, not when it poll() out
        v[0][0] = true;

        while (!q.isEmpty()) {
            int size = q.size();
            step++;
            for (int i = 0; i < size; i++) {
                int[] index = q.poll();
                int x = index[0];
                int y = index[1];
                if (x == n - 1 && y == n - 1) {
                    return step;
                }

                for (int dir = 0; dir < 8; dir++) {
                    int nx = x + dx[dir];
                    int ny = y + dy[dir];
                    if (inBound(nx, ny, n) && !v[nx][ny] && grid[nx][ny] == 0) {
                        int[] newIndex = {nx, ny};
                        q.add(newIndex);
                        // Make sure to set v = true once added to queue
                        v[nx][ny] = true;
                    }
                }
            }
        }

        return -1;
    }

    private boolean inBound(int x, int y, int n) {
        return x >= 0 && x < n && y >= 0 && y < n;
    }
}