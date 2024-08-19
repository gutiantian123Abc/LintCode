/* 937 · 可以完成的题目数量
https://www.lintcode.com/problem/937

描述
给定一个正整数n，表示一场比赛的时间，比赛中题目的难度是递增的，
你每完成一个题目，就要花费k × i的时间，其中k是输入的一个系数，
i表示题目的序号(从1开始)。根据这些信息，返回这场比赛中，你最多能完成几个题目。



样例
样例1
输入: n = 30 和 k = 1
输出: 7
解释:
因为 1 * 1 + 1 * 2 + 1 * 3 + 1 * 4 + 1 * 5 + 1 * 6 + 1 * 7 = 28 < 30
且  1 * 1 + 1 * 2 + 1 * 3 + 1 * 4 + 1 * 5 + 1 * 6 + 1 * 7 + 1 * 8 = 36 > 30


样例2

输入: n = 31, k = 2
输出: 5
解释:
因为 2 * 1 + 2 * 2 + 2 * 3 + 2 * 4 + 2 * 5 = 30 < 31 
且 2 * 1 + 2 * 2 + 2 * 3 + 2 * 4 + 2 * 5 + 2 * 6 = 42 > 31
*/

public class Solution {
    /**
     * @param n: an integer
     * @param k: an integer
     * @return: how many problem can you accept
     */
    public long canAccept(long n, int k) {
        // Write your code here
        long left = 0 , right = (long) Math.sqrt(2*n/k);
        while(left + 1 < right) {
            long mid = left + (right - left)/2;
            long totalTime = totalTime(mid, k);
            if(totalTime == n) {
                return mid;
            } else if(totalTime < n) {
                left = mid;
            } else {
                right = mid;
            }
        }

        if(totalTime(right, k) <= n) {
            return right;
        }
        return left;
    }

    long totalTime(long n, int k) {
        return k * (1 + n) * n / 2;
    }
}