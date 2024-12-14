/**
 * 723. Candy Crush
 * https://leetcode.com/problems/candy-crush/description/
 *
 * This question is about implementing a basic elimination algorithm for Candy Crush.
 *
 * Given an m x n integer array board representing the grid of candy
 * where board[i][j] represents the type of candy. A value of board[i][j] == 0 represents that the cell is empty.
 *
 * The given board represents the state of the game following the player's move.
 * Now, you need to restore the board to a stable state by crushing candies according to the following rules:
 *
 * If three or more candies of the same type are adjacent vertically or horizontally,
 * crush them all at the same time - these positions become empty.
 * After crushing all candies simultaneously, if an empty space on the board has candies on top of itself,
 * then these candies will drop until they hit a candy or bottom at the same time.
 * No new candies will drop outside the top boundary.
 * After the above steps, there may exist more candies that can be crushed.
 * If so, you need to repeat the above steps.
 * If there does not exist more candies that can be crushed (i.e., the board is stable),
 * then return the current board.
 * You need to perform the above rules until the board becomes stable, then return the stable board.
 *
 * Examples see original link
 */
class Solution {
    public int[][] candyCrush(int[][] board) {
        int m = board.length, n = board[0].length;
        while (true) {
            boolean[][] toCrush = new boolean[m][n];
            boolean needCrush = false;

            //1. horizontal check
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n - 2; j++) {
                    if (board[i][j] != 0 && board[i][j] == board[i][j + 1] && board[i][j + 1] == board[i][j + 2]) {
                        toCrush[i][j] = true;
                        toCrush[i][j + 1] = true;
                        toCrush[i][j + 2] = true;
                        needCrush = true;
                    }
                }
            }

            //2. vertical check
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < m - 2; i++) {
                    if (board[i][j] != 0 && board[i][j] == board[i + 1][j] && board[i + 1][j] == board[i + 2][j]) {
                        toCrush[i][j] = true;
                        toCrush[i + 1][j] = true;
                        toCrush[i + 2][j] = true;
                        needCrush = true;
                    }
                }
            }

            if (!needCrush) {
                break;
            }


            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (toCrush[i][j]) {
                        board[i][j] = 0;
                    }
                }
            }


            for (int j = 0; j < n; j++) {
                int writePos = m - 1;
                for (int i = m - 1; i >= 0; i--) {
                    if (board[i][j] != 0) {
                        board[writePos][j] = board[i][j];
                        writePos--;
                    }
                }

                while (writePos >= 0) {
                    board[writePos][j] = 0;
                    writePos--;
                }
            }
        }

        return board;
    }