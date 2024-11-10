/**
 * 486 · 合并k个排序数组
 * https://www.lintcode.com/problem/486
 *
 * 描述
 * 将 k 个有序数组合并为一个大的有序数组。
 *
 * 样例 1:
 * 输入:
 *   [
 *     [1, 3, 5, 7],
 *     [2, 4, 6],
 *     [0, 8, 9, 10, 11]
 *   ]
 * 输出: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
 *
 * 样例 2:
 * 输入:
 *   [
 *     [1,2,3],
 *     [1,2]
 *   ]
 * 输出: [1,1,2,2,3]
 *
 * 挑战
 * 在 O(N log k) 的时间复杂度内完成：
 * N 是所有数组包含的整数总数量。
 * k 是数组的个数。
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
     * @param arrays: k sorted integer arrays
     * @return: a sorted array
     */
    public int[] mergekSortedArrays(int[][] arrays) {
        // write your code here
        int m = arrays.length;

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

        for (int i = 0; i < m; i++) {
            int n = arrays[i].length;
            if (n > 0) {
                pq.add(new Pair(arrays[i][0], i, 0));
            }
        }

        List<Integer> list = new ArrayList<>();

        while(!pq.isEmpty()) {
            Pair now = pq.poll();
            list.add(now.val);
            int nowX = now.x, nowY = now.y;
            int n = arrays[nowX].length;

            if (nowY < n - 1) {
                pq.add(new Pair(arrays[nowX][nowY + 1], nowX, nowY + 1));
            }
        }

        int[] res = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }

        return res;

    }
}