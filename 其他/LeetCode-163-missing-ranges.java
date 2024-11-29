/**
 * 163. Missing Ranges
 * https://leetcode.com/problems/missing-ranges
 *
 * You are given an inclusive range [lower, upper] and a sorted unique integer array nums,
 * where all elements are within the inclusive range.
 *
 * A number x is considered missing if x is in the range [lower, upper] and x is not in nums.
 *
 * Return the shortest sorted list of ranges that exactly covers all the missing numbers.
 * That is, no element of nums is included in any of the ranges,
 * and each missing number is covered by one of the ranges.
 *
 * Example 1:
 * Input: nums = [0,1,3,50,75], lower = 0, upper = 99
 * Output: [[2,2],[4,49],[51,74],[76,99]]
 * Explanation: The ranges are:
 * [2,2]
 * [4,49]
 * [51,74]
 * [76,99]
 *
 * Example 2:
 * Input: nums = [-1], lower = -1, upper = -1
 * Output: []
 * Explanation: There are no missing ranges since there are no missing numbers.
 *
 * Constraints:
 * -109 <= lower <= upper <= 109
 * 0 <= nums.length <= 100
 * lower <= nums[i] <= upper
 * All the values of nums are unique.
 */

class Solution {
    public List<List<Integer>> findMissingRanges(int[] nums, int lower, int upper) {

        List<List<Integer>> res = new ArrayList<>();
        if (nums.length == 0) {
            List<Integer> list = new ArrayList<>();
            list.add(lower);
            list.add(upper);
            res.add(list);
            return res;
        }

        if (lower < nums[0]) {
            List<Integer> list = new ArrayList<>();
            list.add(lower);
            list.add(nums[0] - 1);
            res.add(list);
        }

        for (int i = 1; i < nums.length; i++) {
            int curNum = nums[i];
            int prevNum = nums[i - 1];
            if (prevNum + 1 < curNum) {
                List<Integer> list = new ArrayList<>();
                list.add(prevNum + 1);
                list.add(curNum - 1);
                res.add(list);
            }
        }

        if (nums[nums.length - 1] < upper) {
            List<Integer> list = new ArrayList<>();
            list.add(nums[nums.length - 1] + 1);
            list.add(upper);
            res.add(list);
        }

        return res;
    }
}