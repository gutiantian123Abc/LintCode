/**
 * 460 · 在排序数组中找最接近的K个数
 * https://www.lintcode.com/problem/460/
 * https://leetcode.com/problems/find-k-closest-elements/
 *
 * 描述
 * 给一个目标数 target, 一个非负整数 k, 一个按照升序排列的数组 A。
 * 在A中找与target最接近的k个整数。返回这k个数并按照与target的接近程度从小到大排序，
 * 如果接近程度相当，那么小的数排在前面。
 *
 * 样例 1:
 * 输入: A = [1, 2, 3], target = 2, k = 3
 * 输出: [2, 1, 3]
 *
 * 样例 2:
 * 输入: A = [1, 4, 6, 8], target = 3, k = 3
 * 输出: [4, 1, 6]
 */

// Solution 1:
public class Solution {
    /**
     * @param a: an integer array
     * @param target: An integer
     * @param k: An integer
     * @return: an integer array
     */
    public int[] kClosestNumbers(int[] a, int target, int k) {
        // write your code here
        PriorityQueue<Integer> pq = new PriorityQueue<>((Integer A, Integer B) -> {
            int diffA = Math.abs(A - target);
            int diffB = Math.abs(B - target);

            if (diffA == diffB) {
                return Integer.compare(B, A);
            } else {
                return Integer.compare(diffB, diffA);
            }
        });

        for (int i = 0; i < a.length; i++) {
            int num = a[i];
            pq.add(num);
            if (pq.size() > k) {
                pq.poll();
            }
        }

        int[] res = new int[k];

        for (int i = k - 1; i >= 0; i--) {
            res[i] = pq.poll();
        }

        return res;
    }
}