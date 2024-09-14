/**
 * 903 · 范围加法
 * https://www.lintcode.com/problem/903/
 *
 * 描述
 * 假设你有一个长度为n的数组，数组的所有元素初始化为0，并且给定k个更新操作。
 * 每个更新操作表示为一个三元组：[startIndex, endIndex, inc]。
 * 这个更新操作给子数组 A[startIndex ... endIndex]（包括startIndex和endIndex）中的每一个元素增加 inc。
 *
 * 返回执行k个更新操作后的新数组。
 *
 * 样例
 * 给定：
 * 长度 = 5,
 * 更新操作 =
 * [
 * [1,  3,  2],
 * [2,  4,  3],
 * [0,  2, -2]
 * ]
 * 返回 [-2, 0, 3, 5, 3]
 *
 * 解释:
 * 初始状态：
 * [ 0, 0, 0, 0, 0 ]
 * 完成 [1, 3, 2]操作后：
 * [ 0, 2, 2, 2, 0 ]
 * 完成[2, 4, 3]操作后：
 * [ 0, 2, 5, 5, 3 ]
 * 完成[0, 2, -2]操作后：
 * [-2, 0, 3, 5, 3 ]
 */

public class Solution {
    /**
     * @param length: the length of the array
     * @param updates: update operations
     * @return: the modified array after all k operations were executed
     */
    public int[] getModifiedArray(int length, int[][] updates) {
        // Write your code here
        int[] actions = new int[length];
        for (int[] update : updates) {
            int start = update[0];
            int end = update[1];
            int inc = update[2];
            actions[start] += inc;
            if (end + 1 < length) { //注意，这里是因为【包括startIndex和endIndex】
                actions[end + 1] -= inc;
            }
        }

        int[] psum = new int[length];
        psum[0] = actions[0];

        for (int i = 1; i < length; i++) {
            psum[i] = psum[i - 1] + actions[i];
        }

        return psum;
    }
}