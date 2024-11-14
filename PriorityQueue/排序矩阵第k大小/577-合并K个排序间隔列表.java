/**
 * 577 · 合并K个排序间隔列表
 * https://www.lintcode.com/problem/577
 *
 * 描述
 * 将K个排序的间隔列表合并到一个排序的间隔列表中，你需要合并重叠的间隔。
 *
 * 样例1:
 * 输入: [
 *   [(1,3),(4,7),(6,8)],
 *   [(1,2),(9,10)]
 * ]
 * 输出: [(1,3),(4,8),(9,10)]
 *
 * 样例2:
 * 输入: [
 *   [(1,2),(5,6)],
 *   [(3,4),(7,8)]
 * ]
 * 输出: [(1,2),(3,4),(5,6),(7,8)]
 */

/**
 * Definition of Interval:
 * public classs Interval {
 *     int start, end;
 *     Interval(int start, int end) {
 *         this.start = start;
 *         this.end = end;
 *     }
 * }
 */

public class Solution {
    /**
     * @param intervals: the given k sorted interval lists
     * @return:  the new sorted interval list
     */
    class Node {
        Interval interval;
        int rowIndex;
        int colIndex;

        public Node(Interval interval, int rowIndex, int colIndex) {
            this.interval = interval;
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;

        }
    }
    public List<Interval> mergeKSortedIntervalLists(List<List<Interval>> intervals) {
        // write your code here

        PriorityQueue<Node> pq = new PriorityQueue<>((Node a, Node b) -> {
            if (a.interval.start != b.interval.start) {
                return Integer.compare(a.interval.start, b.interval.start);
            } else {
                return Integer.compare(a.interval.end, b.interval.end);
            }
        });

        for (int i = 0; i < intervals.size(); i++) {
            if (intervals.get(i).size() > 0) {
                pq.add(new Node(intervals.get(i).get(0), i, 0));
            }
        }


        List<Interval> res = new ArrayList<>();
        int resIndex = -1;

        while (!pq.isEmpty()) {
            Node now = pq.poll();
            int nowRowIndex = now.rowIndex;
            int nowColIndex = now.colIndex;
            Interval nowInterval = now.interval;

            if (nowColIndex < intervals.get(nowRowIndex).size() - 1) {
                Interval nextInterval = intervals.get(nowRowIndex).get(nowColIndex + 1);
                pq.add(new Node(nextInterval, nowRowIndex, nowColIndex + 1));
            }


            if (resIndex == -1) {
                res.add(nowInterval);
                resIndex = 0;

            } else {
                Interval resCurInterval = res.get(resIndex);
                if (nowInterval.start > resCurInterval.end) {
                    res.add(nowInterval);
                    resIndex++;
                } else {
                    res.set(resIndex, merge(resCurInterval, nowInterval));
                    /**
                     Key Takeaway: When working with collections in Java,
                     updating a local reference doesn't change the object in the collection.
                     You must update the collection directly to reflect any changes.
                     So:
                     resCurInterval = merge(resCurInterval, nowInterval); is wrong
                     res.set(resIndex, merge(resCurInterval, nowInterval)); is correct
                     */
                }
            }
        }

        return res;
    }

    private Interval merge(Interval a, Interval b) {
        int start = Math.min(a.start, b.start);
        int end = Math.max(a.end, b.end);
        return new Interval(start, end);
    }
}