/**
 * LeetCode 560. Subarray Sum Equals K
 * https://leetcode.com/problems/subarray-sum-equals-k/
 *
 * Given an integer array nums and an integer k, return the total number of
 * contiguous subarrays whose sum equals exactly k.
 *
 * Example 1:
 *   Input:  nums = [1,1,1], k = 2
 *   Output: 2   ([1,1] twice)
 *
 * Example 2:
 *   Input:  nums = [1,2,3], k = 3
 *   Output: 2   ([1,2] and [3])
 *
 * Constraints:
 *   - 1 <= nums.length <= 2 * 10^4
 *   - -1000 <= nums[i] <= 1000   (negatives! -> sliding window is out, since
 *     the window sum is not monotonic)
 *   - -10^7 <= k <= 10^7
 *
 * Approach: prefix sum + hashmap -- the COUNTING version of Two Sum.
 * prevSum[i] = sum of the first i elements (prevSum[0] = 0, the empty
 * prefix), so sum of subarray nums[j..i) = prevSum[i] - prevSum[j]. The
 * range problem becomes a pair problem: count pairs (j < i) with
 * prevSum[i] - prevSum[j] == k, i.e. walking right, each i asks "my partner
 * is prevSum[i] - k -- how many times has it appeared before me?" Exactly the
 * Two Sum question, with two upgrades: the map stores prefix-sum -> COUNT
 * (not index), because with negatives the same prefix sum can repeat and each
 * occurrence is a distinct left endpoint (one hit can harvest several
 * subarrays: res += count, not res++); and the loop keeps going instead of
 * returning, since ALL solutions are wanted.
 *
 * ---------------------------------------------------------------------------
 * WHY freqMap.put(0, 1) -- the cornerstone:
 *   The map's job is to hold every candidate LEFT endpoint's prefix sum.
 *   j = 0 (subarray cut from the very start) is a legal left endpoint, and
 *   its prefix sum is prevSum[0] = 0 -- but the loop starts at i = 1, so
 *   prevSum[0] never gets its turn to register itself. put(0, 1) does that
 *   registration by hand; it is the SAME act as the loop's own inserts, just
 *   performed before the loop because its owner sits outside it.
 *   Concrete: nums=[5], k=5 -- at i=1, rest = 5-5 = 0; without the entry the
 *   lookup misses and the answer is 0 (subarray [5] lost); with it, hit, 1.
 *   And it never gives away free points: nums=[1], k=0 -- rest = 1-0 = 1,
 *   the 0 entry is simply not consulted. It serves exactly one clientele:
 *   subarrays that start at index 0 whose sum happens to be k.
 * ---------------------------------------------------------------------------
 *
 * Check-BEFORE-insert (same discipline as Two Sum): at step i the map holds
 * only prefixes with j < i, so the partner is strictly to the left and the
 * subarray is non-empty. Inserting first would let k = 0 match the current
 * prefix against itself and count a phantom empty subarray.
 *
 * (The prevSum[] array is kept for clarity; since each prevSum[i] is only
 * used in its own iteration, it can be replaced by one running int -- same
 * O(n) time, drops the array. The map remains O(n) either way.)
 *
 * Time:  O(n) -- one pass to build prefix sums, one pass with O(1) average
 *        hash work per step.
 * Space: O(n) -- the prefix array and the map (worst case all prefix sums
 *        distinct).
 */
class Solution {
    public int subarraySum(int[] nums, int k) {
        int[] prevSum = new int[nums.length + 1];
        int res = 0;

        for (int i = 1; i < nums.length + 1; i++) {
            prevSum[i] = prevSum[i - 1] + nums[i - 1];
        }

        Map<Integer, Integer> freqMap = new HashMap<>();

        freqMap.put(0, 1);
        for (int i = 1; i < nums.length + 1; i++) {
            int rest = prevSum[i] - k;
            if (freqMap.containsKey(rest)) {
                res += freqMap.get(rest);
            }

            if (!freqMap.containsKey(prevSum[i])) {
                freqMap.put(prevSum[i], 0);
            }
            freqMap.put(prevSum[i], freqMap.get(prevSum[i]) + 1);
        }

        return res;
    }
}