/**
 * 617 · 子数组的最大平均值 II
 * https://www.lintcode.com/problem/617/
 *
 * 描述
 * 给出一个整数数组，有正有负。找到这样一个子数组，他的长度大于等于 k，且平均值最大。
 *
 * 例1:
 *
 * 输入:
 * [1,12,-5,-6,50,3]
 * 3
 * 输出:
 * 15.67
 * 解释:
 *  (-6 + 50 + 3) / 3 = 15.67
 *
 *
 * 例2:
 *
 * 输入:
 * [5]
 * 1
 * 输出:
 * 5.00
 */

public class Solution {
    /**
     * @param nums: an array with positive and negative numbers
     * @param k: an integer
     * @return: the maximum average
     */
    public double maxAverage(int[] nums, int k) {
        // write your code here
        int maxNum = Integer.MIN_VALUE;
        int minNum = Integer.MAX_VALUE;

        for (int num : nums) {
            maxNum = Math.max(maxNum, num);
            minNum = Math.min(minNum, num);
        }

        double left = minNum, right = maxNum;

        while(left + 1e-5 < right) {
            double mid = left + (right - left)/2;
            boolean canFind = check(nums, mid, k);
            if (canFind) {
                left = mid;
            } else {
                right = mid;
            }
        }

        if (check(nums, right, k)) {
            return right;
        }
        return left;
    }


    boolean check(int[] nums, double avg, int k) {
        int n = nums.length;
        double[] A = new double[n];
        double[] prevSum = new double[n + 1];
        for (int i = 1; i <= n; i++) {
            int num = nums[i - 1];
            A[i - 1] = (double)num - avg;
            double a = A[i - 1];
            prevSum[i] = prevSum[i - 1] + a;
        }

        double leftMinSum = prevSum[0];
        double result = 0;
        for (int i = k; i <= n; i++) {
            int leftIndex = i - k;
            leftMinSum = Math.min(leftMinSum, prevSum[leftIndex]);
            if (prevSum[i] - leftMinSum >= 0) {
                return true;
            }
        }

        return false;
    }
}