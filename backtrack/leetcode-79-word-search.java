/**
 * LeetCode 79. Word Search
 * https://leetcode.com/problems/word-search/
 *
 * Given an m x n grid of characters (board) and a string word, return true if
 * word can be formed by a path in the grid. The path is built from letters of
 * sequentially adjacent cells, where "adjacent" means horizontally or vertically
 * neighboring. The same cell may not be used more than once within a path.
 *
 * Example:
 *   board = [['A','B','C','E'],
 *            ['S','F','C','S'],
 *            ['A','D','E','E']]
 *   word = "ABCCED"  -> true
 *   word = "SEE"     -> true
 *   word = "ABCB"    -> false  (the 'B' cell would have to be reused)
 *
 * Constraints:
 *   - m == board.length, n == board[i].length
 *   - 1 <= m, n <= 6
 *   - 1 <= word.length <= 15
 *   - board and word consist of only uppercase and lowercase English letters.
 *
 * Approach: Backtracking / DFS. Try starting the word from every cell. From a
 * cell, if the current character matches, mark the cell as '#' so it can't be
 * reused within the same path, then recurse into the four neighbors (encoded by
 * the dx/dy direction arrays) for the next character. Short-circuit as soon as
 * one direction succeeds. The cell is always restored to its original value
 * after the loop, so the board is left unchanged once the call returns.
 *
 * Time:  O(m * n * 4^L), where L = word.length -- each of the m*n cells can
 *        start a DFS that branches up to 4 ways for L levels. A tighter bound is
 *        O(m * n * 3^L), since after the first step a path never revisits the
 *        direction it came from.
 * Space: O(L) for the recursion stack depth. Marking cells in place avoids an
 *        extra visited array (which would otherwise cost O(m * n)).
 */
class Solution {
    private static final int[] dx = {-1, 1, 0, 0};
    private static final int[] dy = {0, 0, 1, -1};
    public boolean exist(char[][] board, String word) {
        int r = board.length;
        int c = board[0].length;
        boolean found = false;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                found = backtrack(board, i, j, 0, word);
                if (found) {
                    return true;
                }
            }
        }

        return found; 
    }

    private boolean backtrack(char[][] board, int r, int c, int startIndex, String word) {
        if (startIndex == word.length()) {
            return true;
        }

        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != word.charAt(startIndex)) {
            return false;
        }

        char temp = board[r][c];
        board[r][c] = '#';
        boolean found = false;
        for (int dir = 0; dir < 4; dir++) {
            if (backtrack(board, r + dx[dir], c + dy[dir], startIndex + 1, word)) {
                found = true;
                break;
            }
        }

        board[r][c] = temp;
        return found;
    }
}