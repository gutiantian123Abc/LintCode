/**
 * 821 · 时间交集
 * https://www.lintcode.com/problem/821/
 *
 * 描述
 * 目前有两个用户的有序在线时间序列，每一个区间记录了该用户的登录时间点x和离线时间点y，
 * 请找出这两个用户同时在线的时间段，输出的时间段请从小到大排序。你需要返回一个intervals的列表
 *
 * 样例 1：
 * 输入：seqA = [(1,2),(5,100)], seqB = [(1,6)]
 * 输出：[(1,2),(5,6)]
 * 解释：在 (1,2), (5,6) 这两个时间段内，两个用户同时在线。
 *
 * 样例 2：
 * 输入：seqA = [(1,2),(10,15)], seqB = [(3,5),(7,9)]
 * 输出：[]
 * 解释：不存在任何时间段，两个用户同时在线。
 */

/**
 * Definition of Interval:
 * public class Interval {
 *     int start, end;
 *     Interval(int start, int end) {
 *         this.start = start;
 *         this.end = end;
 *     }
 * }
 */

public class Solution {
    /**
     * @param seqA: the list of intervals
     * @param seqB: the list of intervals
     * @return: the time periods
     */
    public List<Interval> timeIntersection(List<Interval> seqA, List<Interval> seqB) {
        // Write your code here
        int i = 0, j = 0;
        List<Interval> res = new ArrayList<>();

        while (i < seqA.size() && j < seqB.size()) {
            Interval A = seqA.get(i);
            Interval B = seqB.get(j);
            int start = Math.max(A.start, B.start);
            int end = Math.min(A.end, B.end);
            if (start < end) {
                res.add(new Interval(start, end));
            }

            if (A.end < B.end) {
                i++;
            } else {
                j++;
            }
        }

        return res;
    }
}