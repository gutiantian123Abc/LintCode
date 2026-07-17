/**
 * LeetCode 15. 3Sum
 * https://leetcode.com/problems/3sum/
 *
 * Given an integer array nums, return ALL the unique triplets
 * [nums[i], nums[j], nums[k]] such that i != j != k and
 * nums[i] + nums[j] + nums[k] == 0. The solution set must not contain
 * duplicate triplets ([-1,0,1] and [0,1,-1] count as the same one).
 *
 * Example 1:
 *   Input:  nums = [-1,0,1,2,-1,-4]
 *   Output: [[-1,-1,2],[-1,0,1]]
 *
 * Example 2:
 *   Input:  nums = [0,1,1]
 *   Output: []
 *
 * Example 3:
 *   Input:  nums = [0,0,0]
 *   Output: [[0,0,0]]
 *
 * Constraints:
 *   - 3 <= nums.length <= 3000
 *   - -10^5 <= nums[i] <= 10^5
 *
 * Approach: sort + fix one + two pointers. Sorting is safe because the answer
 * wants VALUES, not indices (unlike Two Sum), and it buys the two weapons this
 * problem needs: the two-pointer monotonic sweep, and "duplicates are
 * adjacent" for dedup. Fixing nums[i] collapses the problem to sorted Two Sum
 * (LC 167) on [i+1, n-1]: sum < 0 -> left++ (current left can't reach 0 even
 * with the max partner, safely eliminated), sum > 0 -> right--. Each step
 * eliminates one position -- exclusion, not search -- so the inner sweep is
 * O(n) with no misses.
 *
 * ---------------------------------------------------------------------------
 * DEDUP DIRECTION -- why (i > 0 && nums[i] == nums[i-1]) and NEVER
 * nums[i] == nums[i+1]:
 *
 *   Both look like "skip duplicates", but they skip DIFFERENT copies. Compare
 *   backward (i-1): later copies skip, the FIRST copy does all the work.
 *   Compare forward (i+1): earlier copies skip, the LAST copy works. The
 *   copies are not interchangeable, because a fixed value only searches to
 *   its RIGHT: the first copy has the largest window, the last the smallest.
 *
 *   Lost-solution demo with nums = [-1,-1,2] (expected [[-1,-1,2]]):
 *     forward-compare: i=0 sees nums[0]==nums[1] -> SKIPPED; i=1 works but its
 *     window is just [2] -- no second -1 left to pair with -> outputs [].
 *     The answer needs TWO -1s, and "a -1 paired with its own copy" can only
 *     happen when the FIRST -1 is fixed and the second sits inside its window.
 *   backward-compare: i=0 works with the full window, left hits the second -1,
 *     right hits 2, sum == 0 -> [-1,-1,2] collected; i=1 skips (pure rework).
 *
 *   Dedup removes DUPLICATE WORK, not duplicate values inside a solution.
 *   Rule of thumb (same in LC 40/90/47): always compare against the direction
 *   ALREADY PROCESSED (i-1), never the unprocessed one (i+1).
 *
 *   Note the hit-branch slides DO compare forward (nums[left] == nums[left+1])
 *   -- correct there, because that runs AFTER the solution is collected: the
 *   current left value has already done its work, so its remaining copies are
 *   the rework to skip. Same principle, opposite point in time.
 * ---------------------------------------------------------------------------
 *
 * On a hit, both sides slide past their whole duplicate run (while-to-last-
 * copy, then one more step onto a new value) -- sliding only one side would
 * let the other side's copies recreate the same triplet. The left < right
 * guard inside the slides prevents crossing/overrun on all-same segments like
 * [0,0,0,0].
 *
 * Time:  O(n^2) -- O(n log n) sort, then n fixed slots x O(n) two-pointer
 *        sweep each.
 * Space: O(1) extra (O(log n) sort stack); output list not counted.
 */
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum < 0) {
                    left++;
                } else if (sum > 0) {
                    right--;
                } else {
                    res.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }

                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }

                    left++;
                    right--;
                }
            }
        }

        return res;
    }
}