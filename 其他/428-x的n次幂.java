/**
 * 428 · x的n次幂
 * https://www.lintcode.com/problem/428/
 * https://leetcode.com/problems/powx-n/
 *
 * 描述
 * 实现 pow(x, n)。(n是一个整数)
 *
 * 样例 1:
 * 输入: x = 9.88023, n = 3
 * 输出: 964.498
 *
 * 样例 2:
 * 输入: x = 2.1, n = 3
 * 输出: 9.261
 *
 * 样例 3:
 * 输入: x = 1, n = 0
 * 输出: 1
 *
 * 挑战
 * 时间复杂度
 * O(logn)
 */

public class Solution {
    /**
     * @param x: the base number
     * @param n: the power number
     * @return: the result
     */
    public double myPow(double x, int n) {
        // write your code here
        if (n == 0) {
            return 1.0;
        }

        // 将 n 转换为 long 类型，防止在 n 为 Integer.MIN_VALUE 时溢出
        long N = n;
        // 如果 n 是负数，则将 x 取倒数，并将 N 变为正数
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        return powHelper(x, N);
    }

    private double powHelper(double x, long n) {
        // 递归终止条件
        if (n == 0) {
            return 1.0;
        }
        // 递归计算一半的次方
        double half = powHelper(x, n / 2);
        // 如果 n 是偶数
        if (n % 2 == 0) {
            return half * half;
        } else { // 如果 n 是奇数
            return half * half * x;
        }
    }
    /**
     快速幂算法的核心思想是每次将指数 n 减半，从而将计算复杂度从 O(n) 降低到 O(log n)。
     这是因为每次递归调用或循环迭代都会使指数 n 减少一半，
     最终在 O(log n) 次操作后，指数 n 会减少到 0。
     */
}