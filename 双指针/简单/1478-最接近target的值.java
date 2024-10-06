/**
 * 1478 · 最接近target的值
 * https://www.lintcode.com/problem/1478
 *
 * 描述:
 * 给出一个数组，在数组中找到两个数，使得它们的和最接近目标值但不超过目标值，返回它们的和
 *
 * 样例1:
 * 输入: target = 15 和 array = [1,3,5,11,7]
 * 输出: 14
 * 解释:
 * 11+3=14
 *
 * 样例2:
 * 输入: target = 16 和 array = [1,3,5,11,7]
 * 输出: 16
 * 解释:
 * 11+5=16
 */
public class Solution {
    /**
     * @param target: the target
     * @param array: an array
     * @return: the closest value
     */
    public int closestTargetValue(int target, int[] array) {
        // Write your code here
        int diff = Integer.MAX_VALUE;
        int p1 = 0, p2 = array.length - 1;
        Arrays.sort(array);
        int res = 0;

        while (p1 < p2) {
            int sum = array[p1] + array[p2];
            if (sum == target) {
                return sum;
            } else if (sum < target) {
                int curDiff = target - sum;
                if (curDiff < diff) {
                    diff = curDiff;
                    res = sum;
                }
                p1++;
            } else {
                p2--;
            }
        }

        if (diff == Integer.MAX_VALUE) {
            return -1;
        }

        return res;
    }
}


