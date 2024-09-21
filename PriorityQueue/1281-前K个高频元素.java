/**
 * 1281 · 前K个高频元素
 * https://www.lintcode.com/problem/1281
 *
 * 描述
 * 给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
 *
 * 样例 1:
 *
 * 输入: nums = [1,1,1,2,2,3], k = 2
 * 输出: [1,2]
 *
 *
 * 样例 2:
 *
 * 输入: nums = [1], k = 1
 * 输出: [1]
 *
 * 你的算法的时间复杂度必须优于 O(nlogn), n 是数组的大小
 */

public class Solution {
    /**
     * @param nums: the given array
     * @param k: the given k
     * @return: the k most frequent elements
     *          we will sort your return value in output
     */
    public List<Integer> topKFrequent(int[] nums, int k) {
        // Write your code here
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            if (!freqMap.containsKey(num)) {
                freqMap.put(num, 0);
            }
            freqMap.put(num, freqMap.get(num) + 1);
        }

        PriorityQueue<Integer> pq = new PriorityQueue<>(k, (Integer a, Integer b) -> { // minPQ
            int aFreq = freqMap.get(a);
            int bFreq = freqMap.get(b);
            return aFreq - bFreq;
        });


        for (int num : freqMap.keySet()) {
            pq.add(num);
            if (pq.size() > k) {
                pq.poll();
            }
        }

        Stack<Integer> stack = new Stack<>();
        while(!pq.isEmpty()) {
            stack.push(pq.poll());
        }

        List<Integer> res = new ArrayList<>();
        while(!stack.isEmpty()) {
            res.add(stack.pop());
        }

        return res;
    }
}

