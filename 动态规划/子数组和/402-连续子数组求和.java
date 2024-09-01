/**
 * 402 · 连续子数组求和
 * https://www.lintcode.com/problem/402
 *
 * 描述
 * 给定一个整数数组，请找出一个连续子数组，使得该子数组的和最大。
 * 输出答案时，请分别返回第一个数字和最后一个数字的下标。（如果存在多个答案，请返回字典序最小的）
 *
 * 样例 1:
 * 输入: [-3, 1, 3, -3, 4]
 * 输出: [1, 4]
 *
 * 样例 2:
 * 输入: [0, 1, 0, 1]
 * 输出: [0, 3]
 * 解释: 字典序最小.
 */

public class Solution {
    /**
     * @param a: An integer array
     * @return: A list of integers includes the index of the first number and the index of the last number
     */
    public List<Integer> continuousSubarraySum(int[] a) {
        List<Integer> ans = new ArrayList<>();
        int n = a.length;
        if (n <= 1) {
            ans.add(0);
            ans.add(0);
            return ans;
        }

        int[] prevSum = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prevSum[i] = prevSum[i - 1] + a[i - 1];
        }

        int leftMinSum = 0;
        int leftMinSumIndex = 0;
        int maxSum = Integer.MIN_VALUE;
        int[] result = new int[2];

        for (int i = 1; i <= n; i++) {
            int numIndex = i - 1;
            // 更新 leftMinSum 和 leftMinSumIndex
            if (prevSum[numIndex] < leftMinSum) {
                leftMinSum = prevSum[numIndex];
                leftMinSumIndex = numIndex;
            }

            // 现在可以更新 result 了
            if (prevSum[i] - leftMinSum > maxSum) {
                maxSum = prevSum[i] - leftMinSum;
                result[0] = leftMinSumIndex;
                result[1] = numIndex;
            }
        }

        ans.add(result[0]);
        ans.add(result[1]);

        return ans;
    }
}