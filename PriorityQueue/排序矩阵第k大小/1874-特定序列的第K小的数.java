/**
 * 1874 · 特定序列的第K小的数
 * https://www.lintcode.com/problem/1874/
 *
 * 描述
 * 给定一个每行中的元素个数不等的序列，其中同一行的元素单调递增。请在这个特定序列中找出第 K 小的数。
 *
 * 每行的元素个数范围在[0, 500]
 * 总行数<=500
 * 所有元素的大小在[1, 1000000]
 * 保证序列中元素个数>=K
 *
 * 样例 1:
 * 输入:
 * [
 *   [1, 5, 7, 9],
 *   [3, 4],
 *   [2, 7, 8]
 * ]
 * k = 5
 * 输出:
 * 5
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
     * @param arr: an array of integers
     * @param k: an integer
     * @return: the Kth smallest element in a specific array
     */
    public int kthSmallest(int[][] arr, int k) {
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

        int m = arr.length;

        for (int i = 0; i < m; i++) {
            int n = arr[i].length;
            if (n > 0) {
                pq.add(new Pair(arr[i][0], i, 0));
            }
        }

        int count = 0, ans = 0;

        while (count < k) {
            Pair p = pq.poll();
            count++;
            ans = p.val;

            int x = p.x, y = p.y;
            int n = arr[x].length;
            if (y < n - 1) {
                pq.add(new Pair(arr[x][y + 1], x, y + 1));
            }
        }

        return ans;
    }
}