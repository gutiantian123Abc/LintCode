/**
 * 293 · 隧道的深度
 * https://www.lintcode.com/problem/293
 *
 * 描述
 * 当一个二进制矩阵除了从左上角到右上角的一条路径上值为1，
 * 其他值均为0时，我们称这个矩阵有一条隧道。除了起点和终点只有一个相邻的”1“，
 * 在这条路径上的每一个“1"都恰好有两个相邻的”1“。
 * 现在给你一个有隧道的矩阵，请回答这条隧道的深度
 * (这条隧道能到达的最后一行的序号,从0开始计数）
 *
 * 样例:
 * 输入: [[1,0,0,0,1],[1,1,0,0,1],[0,1,0,1,1],[0,1,1,1,0],[0,0,0,0,0]]
 * 输出:3
 */

public class Solution {
    /**
     * @param matrix: the matrix in problem
     * @return: the depth of the tunnel.
     */
    public int findDepth(int[][] matrix) {
        int row = matrix.length, col = matrix[0].length;
        int upRow = 0, botRow = row - 1;

        while(upRow + 1 < botRow) {
            int midRow = upRow + (botRow - upRow)/2;
            if(hasNum(matrix, midRow)) {
                upRow = midRow;
            } else {
                botRow = midRow;
            }
        }

        if(hasNum(matrix, botRow)) {
            return botRow;
        } else {
            return upRow;
        }
    }

    boolean hasNum(int[][] matrix, int rowNum) {
        for(int j = 0; j < matrix[0].length; j++) {
            if(matrix[rowNum][j] == 1) {
                return true;
            }
        }
        return false;
    }
}