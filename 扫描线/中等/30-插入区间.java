/**
 * 30 · 插入区间
 * https://www.lintcode.com/problem/30/
 *
 * 描述
 * 给出一个无重叠的按照区间起始端点排序的区间列表。
 * 在列表中插入一个新的区间，你要确保列表中的区间仍然有序且不重叠（如果有必要的话，可以合并区间。
 *
 * 样例 1：
 * 输入：
 * 区间列表 = [(1,2), (5,9)]
 * 新的区间 = (2, 5)
 *
 * 输出：
 * [(1,9)]
 * 解释：
 * 插入后区间有重叠，需要合并区间。
 *
 *
 * 样例 2：
 * 输入：
 * 区间列表 = [(1,2), (5,9)]
 * 新的区间 = (3, 4)
 *
 * 输出：
 * [(1,2), (3,4), (5,9)]
 * 解释：
 * 区间按起始端点有序。
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
     * @param intervals: Sorted interval list.
     * @param newInterval: new interval.
     * @return: A new interval list.
     */
    public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
        // write your code here
        int i = 0;
        int n = intervals.size();
        int s = newInterval.start;
        int e = newInterval.end;
        List<Interval> res = new ArrayList<>();

        while (i < n) {
            Interval interval = intervals.get(i);
            if (s > interval.end) {
                res.add(interval);
                if (i == n -  1) {
                    res.add(new Interval(s, e));
                }
                i++;
            } else { // s <= interval.end

                int start = Math.min(s, interval.start);
                while (i + 1 < n && e >= intervals.get(i + 1).start) {
                    i++;
                }
                if (e < interval.start) {
                    res.add(new Interval(start, e));
                    res.add(interval);
                } else {
                    int end = Math.max(intervals.get(i).end, e);
                    res.add(new Interval(start, e));
                }

                i++;
            }
        }

        return res;
    }
}