/**
 * LeetCode 1091. Shortest Path in Binary Matrix
 * https://leetcode.com/problems/shortest-path-in-binary-matrix/
 *
 * Given an n x n binary matrix grid, return the length of the shortest clear
 * path from the top-left cell (0,0) to the bottom-right cell (n-1,n-1). If no
 * such path exists, return -1.
 *
 * A clear path is one where every visited cell holds a 0, consecutive cells are
 * connected 8-directionally (horizontally, vertically, or diagonally), and the
 * path runs from the top-left to the bottom-right. The length of the path is the
 * number of cells it visits (the start cell counts as 1).
 *
 * Example 1:
 *   Input:  grid = [[0,1],[1,0]]
 *   Output: 2
 *
 * Example 2:
 *   Input:  grid = [[0,0,0],[1,1,0],[1,1,0]]
 *   Output: 4
 *
 * Example 3:
 *   Input:  grid = [[1,0,0],[1,1,0],[1,1,0]]
 *   Output: -1   (the start cell is blocked)
 *
 * Constraints:
 *   - n == grid.length == grid[i].length
 *   - 1 <= n <= 100
 *   - grid[i][j] is 0 or 1
 *
 * Approach: BFS from (0,0), since BFS on an unweighted grid finds the fewest
 * cells to the target. Each cell has up to 8 neighbors (dx/dy cover all diagonal
 * and orthogonal moves). Key details: (1) guard against a blocked start or end
 * cell up front and return -1, since the start is enqueued directly and never
 * passes through the neighbor filter that screens out walls; (2) mark a cell
 * visited the moment it is enqueued, not when dequeued, so the same cell is
 * never queued twice; and (3) increment step once per BFS level, so step equals
 * the number of cells on the path (start counts as 1, giving the correct answer
 * 1 for a 1x1 open grid).
 *
 * Time:  O(n^2). Each of the n*n cells is enqueued at most once, and each does a
 *        constant amount of work (8 neighbor checks).
 * Space: O(n^2) for the visited matrix and, in the worst case, the BFS queue.
 */
class Solution {
    private static int[] dx = {1, -1, 1, -1, 0, 0, 1, -1};
    private static int[] dy = {1, -1, -1, 1, 1, -1, 0, 0};

    public int shortestPathBinaryMatrix(int[][] grid) {
        int r = grid.length;
        int c = grid[0].length;
        boolean[][] v = new boolean[r][c];
        Queue<int[]> queue = new LinkedList<>();
        if (grid[0][0] == 1 || grid[r - 1][c -1 ] == 1) {
            return -1;
        }
 
        int[]start = {0, 0};
        int step = 0;
        queue.add(start);
        v[0][0] = true;

        while(!queue.isEmpty()) {
            int size = queue.size();
            step++;
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int x = cell[0];
                int y = cell[1];
                if (x == r - 1 && y == c - 1) {
                    return step;
                }

                for (int dir = 0; dir < 8; dir++) {
                    int nx = x + dx[dir];
                    int ny = y + dy[dir];
                    if (inBound(nx, ny, r, c) && !v[nx][ny] && grid[nx][ny] == 0) {
                        int[] newCell = {nx, ny};
                        queue.add(newCell);
                        v[nx][ny] = true;
                    }
                }
            }
        }

        return -1;
    }

    private boolean inBound(int x, int y, int r, int c) {
        return x >= 0 && x < r && y >= 0 && y < c;
    }
}