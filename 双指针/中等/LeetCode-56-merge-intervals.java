/**
 * LeetCode 56. Merge Intervals
 * https://leetcode.com/problems/merge-intervals/
 *
 * Given an array of intervals where intervals[i] = [start_i, end_i], merge all
 * overlapping intervals and return an array of the non-overlapping intervals
 * that together cover every interval in the input. Two intervals are considered
 * overlapping when one's start is not greater than the other's end (touching
 * endpoints like [1,4] and [4,5] count as overlapping).
 *
 * Example 1:
 *   Input:  [[1,3],[2,6],[8,10],[15,18]]
 *   Output: [[1,6],[8,10],[15,18]]   ([1,3] and [2,6] overlap -> [1,6])
 *
 * Example 2:
 *   Input:  [[1,4],[4,5]]
 *   Output: [[1,5]]
 *
 * Constraints:
 *   - 1 <= intervals.length <= 10^4
 *   - intervals[i].length == 2
 *   - 0 <= start_i <= end_i <= 10^4
 *
 * Approach: Sort the intervals by start. Then scan with two pointers: p1 anchors
 * the interval currently being built, and p2 walks forward while the current
 * interval's end still reaches the next interval's start, extending the end to
 * the larger of the two (merge only ever grows the end, so an interval fully
 * contained in the current one is absorbed without shrinking it). When the
 * overlap chain breaks, the merged interval is added and p1 jumps to p2.
 *
 * Time:  O(n log n), dominated by the sort. The merge scan is O(n) overall,
 *        since both pointers only ever move forward.
 * Space: O(n) for the output list (plus the sort's internal working space).
 */
class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> {
            return Integer.compare(a[0],b[0]);
        });

        List<int[]> ans = new ArrayList<>();
        int p1 = 0;
        int n = intervals.length;
        while (p1 < n) {
            int[] cur = intervals[p1];
            int p2 = p1;
            while (p2 < n && cur[1] >= intervals[p2][0]) {
                cur = merge(cur, intervals[p2]);
                p2++;
            }

            ans.add(cur);
            p1 = p2;
        }

        int[][] res = new int[ans.size()][2];
        for (int i = 0; i < res.length; i++) {
            res[i] = ans.get(i);
        }

        return res;
        
    }

    private int[] merge(int[] intervalA, int[] intervalB) {
        int[] res = new int[2];
        res[0] = Math.min(intervalA[0], intervalB[0]);
        res[1] = Math.max(intervalA[1], intervalB[1]);
        return res;
    }
}