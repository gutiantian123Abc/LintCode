/**
 * 3678 · 删除区间
 * https://www.lintcode.com/problem/3678
 *
 * 描述
 * 实数集合可以表示为若干不相交区间的并集，其中每个区间的形式为 [a, b)（左闭右开），表示满足
 * 𝑎 ≤ 𝑥 < 𝑏
 * a ≤ x < b 的所有实数 x 的集合。如果某个区间 [a, b) 中包含实数 x ，则称实数 x 在集合中。
 *
 * 现给你一个 有序的 不相交区间列表 intervals，它表示一个实数集合，
 * 其中每一项 intervals[i] = [ai, bi] 都表示一个区间
 * [𝑎𝑖, 𝑏𝑖)。 再给你一个要删除的区间 toBeRemoved。
 *
 * 你需要返回 一组实数，该实数表示 intervals 中 删除 了 toBeRemoved 的部分 。
 * 换句话说，返回实数集合，并满足集合中的每个实数 x 都在 intervals 中，
 * 但不在 toBeRemoved 中。你的答案应该是一个如上所述的 有序的 不相连的间隔列表。
 *
 *
 * 样例一
 * 输入
 * intervals = [[0,2],[3,4],[5,7]]
 * toBeRemoved = [1,6]
 * 输出
 * [[0,1],[6,7]]
 *
 *
 * 样例二
 * 输入
 * intervals = [[0,5]]
 * toBeRemoved = [2,3]
 * 输出
 * [[0,2],[3,5]]
 *
 *
 * 样例三
 * 输入
 * intervals = [[-5,-4],[-3,-2],[1,2],[3,5],[8,9]]
 * toBeRemoved = [-1,4]
 * 输出
 * [[-5,-4],[-3,-2],[4,5],[8,9]]
 */

public class Solution {
    /**
     * @param intervals: A sorted list of disjoint intervals.
     * @param toBeRemoved: An interval to be removed.
     * @return: A set of real numbers.
     */
    public int[][] deleteInterval(int[][] intervals, int[] toBeRemoved) {
        // --- write your code here ---
        int a = toBeRemoved[0];
        int b = toBeRemoved[1];

        List<int[]> res = new ArrayList<>();

        for (int[] interval : intervals) {
            int x = interval[0];
            int y = interval[1];
            if ((b <= x || a >= y)) {
                res.add(new int[]{x, y});
            } else if (a <= x && b <= y) {
                res.add(new int[]{b, y});
            } else if (a >= x && b <= y) {
                res.add(new int[]{x, a});
                res.add(new int[]{b, y});
            } else if (a <= x && b >= y) {
                continue;
            } else if (a >= x && b >= y) {
                res.add(new int[]{x, a});
            }
        }

        int[][] result = new int[res.size()][2];
        for (int i = 0; i < res.size(); i++) {
            result[i] = res.get(i);
        }

        return result;
    }
}