/**
 * 919 · 会议室 II
 * https://www.lintcode.com/problem/919
 *
 * 描述
 * 给定一系列的会议时间间隔intervals，包括起始和结束时间[[s1,e1],[s2,e2],...] (si < ei)，找到所需的最小的会议室数量。
 * 注意： (0,8),(8,10)在8这一时刻不冲突
 *
 * 样例1
 * 输入: intervals = [(0,30),(5,10),(15,20)]
 * 输出: 2
 * 解释:
 * 需要两个会议室
 * 会议室1:(0,30)
 * 会议室2:(5,10),(15,20)
 *
 *
 * 样例2
 * 输入: intervals = [(2,7)]
 * 输出: 1
 * 解释:
 * 只需要1个会议室就够了
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
     * @param intervals: an array of meeting time intervals
     * @return: the minimum number of conference rooms required
     */
    public int minMeetingRooms(List<Interval> intervals) {
        // Write your code here
        int maxEnd = 0;
        for (Interval interval : intervals) {
            maxEnd = Math.max(maxEnd, interval.end);
        }
        int length = maxEnd + 1;

        int[] actions = new int[length];
        for (Interval interval : intervals) {
            int start = interval.start;
            int end = interval.end;
            actions[start] += 1;
            actions[end] -= 1; //注意： 要求： (0,8),(8,10)在8这一时刻不冲突， 边界不能重合， 否则为： if (end + 1 < length)
                                // 参考： 903 · 范围加法
        }

        int[] psum = new int[length];
        psum[0] = actions[0];
        int res = 0;

        for (int i = 1; i < length; i++) {
            psum[i] = psum[i - 1] + actions[i];
            res = Math.max(res, psum[i]);
        }
        return res;
    }
}