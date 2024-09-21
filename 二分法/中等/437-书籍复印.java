/**
 * https://www.lintcode.com/problem/437
 * 描述
 * 给定n本书，第i本书有pages[i]页。有k个人来抄这些书。
 * 这些书排成一行，每个人都可以索取连续一段的书。
 * 例如，一个抄书人可以连续地将书从第i册复制到第j册，
 * 但是他不能复制第1册、第2册和第4册（没有第3册）。
 * 他们在同一时间开始抄书，每抄一页书都要花1分钟。
 * 为了让最慢的抄书人能在最早的时间完成书的分配，最好的策略是什么？
 * 请返回最慢抄书人花费的最短时间。
 *
 * 样例 1:
 *
 * 输入: pages = [3, 2, 4], k = 2
 * 输出: 5
 * 解释: 第一个人复印前两本书, 耗时 5 分钟. 第二个人复印第三本书, 耗时 4 分钟.
 *
 * 样例 2:
 *
 * 输入: pages = [3, 2, 4], k = 3
 * 输出: 4
 * 解释: 三个人各复印一本书.
 * 挑战
 * 时间复杂度 O(nk)
 */

public class Solution {
    /**
     * @param pages: an array of integers
     * @param k: An integer
     * @return: an integer
     */
    public int copyBooks(int[] pages, int k) {
        // write your code here
        // 贪心法常常用binray search解
        int start = 0, end = getSum(pages);
        int peopleNum = 0;
        while(start + 1 < end) {
            int mid = start + (end - start)/2;
            peopleNum = peopleNeededGivenMaxRangeSum(pages, mid);
            if(peopleNum < k) {
                end = mid;
            } else if(peopleNum == k) {
                end = mid;
            } else {
                start = mid;
            }
        }

        if(peopleNeededGivenMaxRangeSum(pages, start) <= k) {
            return start;
        }

        return end;
    }

    private int peopleNeededGivenMaxRangeSum(int[] pages, int limit) {
        int peopleNum = 0;
        int lastRangeSum = limit;
        for(int page : pages) {
            if(page > limit) { // 表明limit太小， 赢缩小limit
                return Integer.MAX_VALUE;
            }
            if(lastRangeSum + page <= limit) {
                lastRangeSum += page;
            } else {
                lastRangeSum = page;
                peopleNum++;
            }
        }

        return peopleNum;
    }

    private int getSum(int[] pages) {
        int sum = 0;
        for(int page : pages) {
            sum += page;
        }
        return sum;
    }
}