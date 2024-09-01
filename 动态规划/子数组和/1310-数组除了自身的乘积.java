/**
 * 1310 · 数组除了自身的乘积
 * https://www.lintcode.com/problem/1310/
 *
 * 描述
 * 给定 n 个整数的数组 nums，其中 n > 1，返回一个数组输出，使得 output [i] 等于 nums 的所有除了 nums [i] 的元素的乘积。
 *
 * 样例1
 *  输入: [1,2,3,4]
 *  输出: [24,12,8,6]
 *  解释:
 *  2*3*4=24
 *  1*3*4=12
 *  1*2*4=8
 *  1*2*3=6
 *
 * 样例2
 *  输入: [2,3,8]
 *  输出: [24,16,6]
 *  解释:
 *  3*8=24
 *  2*8=16
 *  2*3=6
 */

public class Solution {
    /**
     * @param nums: an array of integers
     * @return: the product of all the elements of nums except nums[i].
     */
    public int[] productExceptSelf(int[] nums) {
        // write your code here
        int n = nums.length;
        int[] leftPrevSum = new int[n + 1];
        leftPrevSum[0] = 1;
        int[] rightPrevSum = new int[n + 1];
        rightPrevSum[n] = 1;

        for (int i = 1; i <= n; i++) {
            leftPrevSum[i] = leftPrevSum[i - 1] * nums[i - 1];
        }

        for (int i = n - 1; i >= 0; i--) {
            rightPrevSum[i] = rightPrevSum[i + 1] * nums[i];
        }

        int[] result = new int[n];

        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                result[i - 1] = rightPrevSum[i];
            } else {
                result[i - 1] = leftPrevSum[i - 1] * rightPrevSum[i];
            }
        }

        return result;
    }
}