/**
 * 401 · 排序矩阵中的从小到大第k个数
 * https://www.lintcode.com/problem/401/description
 *
 * 描述
 * 在一个排序矩阵中找从小到大的第 k 个整数。
 * 排序矩阵的定义为：每一行递增，每一列也递增。
 *
 * 样例 1:
 * 输入:
 * [
 *   [1 ,5 ,7],
 *   [3 ,7 ,8],
 *   [4 ,8 ,9],
 * ]
 * k = 4
 * 输出: 5
 *
 * 样例 2:
 * 输入:
 * [
 *   [1, 2],
 *   [3, 4]
 * ]
 * k = 3
 * 输出: 3
 *
 * 挑战
 * 时间复杂度 O(klogn), n 是矩阵的宽度和高度的最大值
 */

public class Solution {
    class Pair {
        int val, x, y;
        public Pair(int val, int x, int y) {
            this.val = val;
            this.x = x;
            this.y = y;
        }
    }
    /**
     * @param matrix: a matrix of integers
     * @param k: An integer
     * @return: the kth smallest number in the matrix
     */
    public int kthSmallest(int[][] matrix, int k) {
        // write your code here
        // min heap
        PriorityQueue<Pair> pq = new PriorityQueue<>((Pair a, Pair b) -> {
            return Integer.compare(a.val, b.val);
        });
        /**
         Data Type	Comparison Method	                Example Usage
         int	    Integer.compare(int x, int y)	    Integer.compare(a, b)
         String	    a.compareTo(b)	                    s1.compareTo(s2)
         char	    Character.compare(char x, char y)	Character.compare(c1, c2)
         double	    Double.compare(double x, double y)	Double.compare(d1, d2)
         */

        int m = matrix.length, n = matrix[0].length;
        boolean[][] v = new boolean[m][n];
        pq.add(new Pair(matrix[0][0], 0, 0));
        v[0][0] = true;

        int count = 0;
        int ans = matrix[0][0];
        while(count < k) {
            Pair pair = pq.poll();
            count++;
            ans = pair.val;
            // right
            int x1 = pair.x, y1 = pair.y + 1;
            // down
            int x2 = pair.x + 1, y2 = pair.y;

            if (x1 < m && y1 < n && !v[x1][y1]) {
                pq.add(new Pair(matrix[x1][y1], x1, y1));
                v[x1][y1] = true;
            }

            if (x2 < m && y2 < n && !v[x2][y2]) {
                pq.add(new Pair(matrix[x2][y2], x2, y2));
                v[x2][y2] = true;
            }
        }

        return ans;
    }
}}