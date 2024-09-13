/**
 * 156 · 合并区间
 * https://www.lintcode.com/problem/156/
 *
 * 描述
 * 我们以一个 Interval 类型的列表 intervals 来表示若干个区间的集合，
 * 其中单个区间为 (start, end)。你需要合并所有重叠的区间，
 * 并返回一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间。
 *
 * 样例
 * 样例1:
 *  输入: [(1,3)]
 *  输出: [(1,3)]
 *
 * 样例 2:
 *  输入:  [(1,3),(2,6),(8,10),(15,18)]
 *  输出: [(1,6),(8,10),(15,18)]
 */


//Definition of Interval:
public class Interval {
    int start, end;
    Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

public class Solution {
    /**
     * @param intervals: interval list.
     * @return: A new interval list.
     */
    public List<Interval> merge(List<Interval> intervals) {
        // write your code here
        List<Interval> ans = new ArrayList<>();
        Collections.sort(intervals, (a, b) -> {
            return a.start - b.start;
        });

        int p1 = 0;
        while(p1 < intervals.size()) {
            Interval cur = intervals.get(p1);
            int p2 = p1;
            while(p2 < intervals.size() && cur.end >= intervals.get(p2).start) {
                cur = merge(cur, intervals.get(p2));
                p2++;
            }
            ans.add(cur);
            p1 = p2;
        }

        return ans;
    }

    Interval merge(Interval I1, Interval I2) {
        if(I1.end <= I2.end) {
            I1.end = I2.end;
        }
        return I1;
    }
}