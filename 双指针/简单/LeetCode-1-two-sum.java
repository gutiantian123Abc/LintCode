/**
 * LeetCode 1. Two Sum
 * https://leetcode.com/problems/two-sum/
 *
 * Given an array of integers nums and an integer target, return the indices of
 * the two numbers that add up to target. Each input has exactly one solution,
 * and the same element may not be used twice. The answer may be returned in
 * any order.
 *
 * Example 1:
 *   Input:  nums = [2,7,11,15], target = 9
 *   Output: [0,1]   (nums[0] + nums[1] == 9)
 *
 * Example 2:
 *   Input:  nums = [3,2,4], target = 6
 *   Output: [1,2]
 *
 * Example 3:
 *   Input:  nums = [3,3], target = 6
 *   Output: [0,1]
 *
 * Constraints:
 *   - 2 <= nums.length <= 10^4
 *   - -10^9 <= nums[i], target <= 10^9
 *   - Only one valid answer exists.
 *
 * Approach: one-pass HashMap. Walking left to right, each number asks: "my
 * partner is target - nums[i] -- has it already appeared?" The map stores
 * value -> index for everything walked past so far. Found: return both
 * indices; not found: register myself and move on. The CHECK-BEFORE-INSERT
 * order is the crux: when nums[i] is processed, the map only holds elements
 * strictly before i, so even when the needed partner equals nums[i] itself
 * (e.g. [3,3] with target 6), the lookup can only hit an EARLIER copy -- the
 * "same element twice" bug is impossible by construction. Inserting first and
 * checking second would let a number match itself.
 *
 * (This solution returns [current index, earlier index]; the problem accepts
 * any order. The trailing return is unreachable since exactly one solution is
 * guaranteed.)
 *
 * Time:  O(n) -- each element does one O(1) average hash lookup and one
 *        insert.
 * Space: O(n) -- worst case (answer at the very end) the map holds nearly the
 *        whole array. Brute force would be O(n^2)/O(1); sorting + two
 *        pointers O(n log n) but scrambles the indices this problem asks for.
 */
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        int[] res = new int[2];
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            int left = target - num;
            if (seen.containsKey(left)) {
                
                res[0] = i;
                res[1] = seen.get(left);
                return res;
            }

            seen.put(num, i);
        }

        return res;
    }
}