/**
 * 1897 · 会议室 3
 * https://www.lintcode.com/problem/1897
 *
 * 描述
 * 你有一个当前会议列表intervals，里面表明了每个会议的开始和结束时间，以及一些会议室rooms。
 * 现在有一系列会议ask需要加入，逐个判断他们能否被安排进当前的会议列表中而不产生冲突。
 * 一个会议室在同一时间只能进行一场会议。每个询问都相互独立。
 *
 * 被安排的会议可以拆分。如果需要安排一个[2, 4]的会议，你可以把它分成[2, 3]和[3, 4]两段来进行。
 *
 * Note:
 * 保证输入可以被安排在rooms个会议室之中
 * 保证任意一个会议的起始时间取值范围是 [1, 50000]
 * |Intervals| <= 50000
 * |ask| <= 50000
 * 1 <= rooms <= 20
 *
 * 例 1:
 * 输入:
 * Intervals:[[1,2],[4,5],[8,10]],rooms = 1,ask: [[2,3],[3,4]]
 * 输出:
 * [true,true]
 * 解释:
 * 对于[2,3]的询问，我们可以安排一个会议室room0。
 * 以下是room0的会议列表：
 * [[1,2],[2,3],[4,5],[8,10]]
 * 对于[3,4]的询问，我们可以安排一个会议室room0。
 * 以下是room0的会议列表：
 * [[1,2],[3,4],[4,5],[8,10]]
 *
 * 例 2:
 * 输入:
 * [[1,2],[4,5],[8,10]]
 * 1
 * [[4,5],[5,6]]
 * 输出:
 * [false,true]
 */

public class Solution {
    /**
     * @param intervals: the intervals
     * @param rooms: the sum of rooms
     * @param ask: the ask
     * @return: true or false of each meeting
     */
    public boolean[] meetingRoomIII(int[][] intervals, int rooms, int[][] ask) {
        // Write your code here.
        int n = 50001;
        boolean[] res = new boolean[ask.length];
        int[] actions = new int[n];
        for (int i = 0; i < intervals.length; i++) {
            int[] interval = intervals[i];
            int start = interval[0], end = interval[1];
            actions[start] += 1;
            actions[end] -= 1;
        }

        int[] psum = new int[n];
        psum[0] = actions[0];

        for (int i = 1; i < n; i++) {
            psum[i] = psum[i - 1] + actions[i];
        }

        for (int i = 0; i < ask.length; i++) {
            int[] newInterval = ask[i];
            res[i] = helper(psum, newInterval, rooms);
        }
        return res;
    }

    private boolean helper(int[] psum, int[] newInterval, int rooms) {
        int newStart = newInterval[0], newEnd = newInterval[1];
        int n = psum.length;
        for (int i = newStart; i < newEnd; i++) {
            int occupiedRooms = psum[i];
            if (rooms <= occupiedRooms) {
                return false;
            }
        }
        return true;
    }
}

