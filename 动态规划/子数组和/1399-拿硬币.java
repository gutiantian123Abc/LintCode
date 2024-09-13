/**
 * 1399 · 拿硬币
 * https://www.lintcode.com/problem/1399
 *
 * 描述
 * 有n个硬币排成一排，每次要你从最左边或者最右侧拿出一个硬币。总共拿k次，写一个算法，使能拿到的硬币的和最大。
 *
 * 样例 1：
 * 输入： list = [5,4,3,2,1], k = 2,
 * 输出：9
 * 解释：从左边开始连取两个硬币即可。
 *
 * 样例 2：
 * 输入： list = [5,4,3,2,1,6], k = 3
 * 输出：15
 * 解释：从左边开始连取两个硬币,右边取一个即可。
 */

public class Solution {
    /**
     * @param list: The coins
     * @param k: The k
     * @return: The answer
     */
    public int takeCoins(int[] list, int k) {
        // Write your code here
        int n = list.length;
        int[] prevSum = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int curSum = list[i - 1];
            prevSum[i] = prevSum[i - 1] + curSum;
        }

        int result = 0;

        for (int leftTakeTime = 0; leftTakeTime <= k; leftTakeTime++) {
            int rightTakeTime = k - leftTakeTime;
            int leftSum = prevSum[leftTakeTime];
            int rightSum = prevSum[n] - prevSum[n - rightTakeTime];
            result = Math.max(result, leftSum + rightSum);
        }
        return result;
    }
}

