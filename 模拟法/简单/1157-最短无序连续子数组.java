/* 1157 · 最短无序连续子数组
https://www.lintcode.com/problem/1157

描述
给定一个整数数组，你需要找到一个连续子数组，
如果你只按升序对这个子数组进行排序，那么整个数组也将按升序排序。

你需要找到最短的这样的子数组并输出它的长度。


样例
输入: [2, 6, 4, 8, 10, 9, 15]
输出: 5
解释: 你需要对[6, 4, 8, 10, 9]按升序排列从而整个数组也变为升序。
*/

public class Solution {
    /**
     * @param nums: an array
     * @return: the shortest subarray's length
     */
    public int findUnsortedSubarray(int[] nums) {
        // Write your code here
        int n = nums.length;
        if(n <= 1) {
            return n;
        }

        int left = 0, right = n - 1;

        while(left < n - 1 && nums[left] <= nums[left + 1]) {
            left++;
        }

        while(right > 0 && nums[right - 1] <= nums[right]) {
            right--;
        }

        if(left > right) {
            return 0;
        }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for(int i = left; i <= right; i++) {
            max = Math.max(max, nums[i]);
            min = Math.min(min, nums[i]);
        }

        while(left > 0 && nums[left - 1] > min) {
            left--;
        }

        while(right < n - 1 && nums[right + 1] < max) {
            right++;
        }

        return right - left + 1;
    }
}