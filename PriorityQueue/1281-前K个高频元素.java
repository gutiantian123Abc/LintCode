/**
 * LeetCode 347. Top K Frequent Elements
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Given an integer array nums and an integer k, return the k elements that
 * appear most frequently. The answer may be returned in any order.
 *
 * Example 1:
 *   Input:  nums = [1,1,1,2,2,3], k = 2
 *   Output: [1,2]
 *
 * Example 2:
 *   Input:  nums = [1], k = 1
 *   Output: [1]
 *
 * Constraints:
 *   - 1 <= nums.length <= 10^5
 *   - -10^4 <= nums[i] <= 10^4
 *   - k is in the range [1, number of unique elements in nums]
 *   - The answer is guaranteed to be unique.
 *
 * Follow up: The algorithm's time complexity must be better than O(n log n).
 *
 * Approach: Count frequencies with a HashMap, then keep a size-k min-heap
 * ordered by frequency. Whenever the heap grows beyond k, poll the smallest
 * (least frequent) element, so the heap always holds the top k most frequent.
 *
 * Time:  O(n + m log k), where n = nums.length and m = number of unique
 *        elements (m <= n). Commonly stated as O(n log k).
 * Space: O(n) for the frequency map, plus O(k) for the heap and result.
 */
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            if (!map.containsKey(num)) {
                map.put(num, 0);
            }
            map.put(num, map.get(num) + 1);
        }

        // min queue, poll out min element
        PriorityQueue<Integer> pq = new PriorityQueue<>((Integer a, Integer b) -> {
            return Integer.compare(map.get(a), map.get(b));
        });

        for (int num : map.keySet()) {
            pq.add(num);

            if (pq.size() > k) {
                pq.poll();
            }
        }

        int[] res = new int[pq.size()];
        int i = 0;
        while (!pq.isEmpty()) {
            res[i++] = pq.poll();
        }

        return res;
    }
}