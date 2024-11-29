/**
 * 64 · 合并排序数组
 * https://www.lintcode.com/problem/64/
 * https://leetcode.com/problems/merge-sorted-array
 *
 * 描述
 * 合并两个排序的整数数组 A 和 B 变成一个新的数组。
 * 原地修改数组 A 把数组 B 合并到数组 A 的后面。
 *
 * 你可以假设A具有足够的空间（A数组的大小大于或等于m+n）去添加B中的元素。数组A和B分别含有m和n个数。
 *
 * 样例 1
 * 输入:
 * A = [1,2,3]
 * m = 3
 * B = [4,5]
 * n = 2
 * 输出：
 * [1,2,3,4,5]
 *
 * 样例 2
 * 输入:
 * A = [1,2,5]
 * m = 3
 * B = [3,4]
 * n = 2
 * 输出:
 * [1,2,3,4,5]
 */
public class Solution {
    /*
     * @param A: sorted integer array A which has m elements, but size of A is m+n
     * @param m: An integer
     * @param B: sorted integer array B which has n elements
     * @param n: An integer
     * @return: nothing
     */
    public void mergeSortedArray(int[] A, int m, int[] B, int n) {
        // write your code here
        int p1 = m - 1, p2 = n - 1, tail = m + n - 1;

        while (p1 >= 0 && p2 >= 0) {
            if (A[p1] > B[p2]) {
                A[tail--] = A[p1--];
            } else if (A[p1] < B[p2]) {
                A[tail--] = B[p2--];
            } else {
                A[tail--] = A[p1--];
                A[tail--] = B[p2--];
            }
        }

        while (p1 >= 0) {
            A[tail--] = A[p1--];
        }

        while (p2 >= 0) {
            A[tail--] = B[p2--];
        }
    }
}