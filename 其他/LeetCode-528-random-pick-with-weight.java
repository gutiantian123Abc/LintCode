/**
 * 528. Random Pick with Weight
 * https://leetcode.com/problems/random-pick-with-weight/description/
 *
 * You are given a 0-indexed array of positive integers w where w[i] describes the weight of the ith index.
 *
 * You need to implement the function pickIndex(),
 * which randomly picks an index in the range [0, w.length - 1] (inclusive) and returns it.
 * The probability of picking an index i is w[i] / sum(w).
 *
 * For example, if w = [1, 3], the probability of picking index 0 is 1 / (1 + 3) = 0.25 (i.e., 25%),
 * and the probability of picking index 1 is 3 / (1 + 3) = 0.75 (i.e., 75%).
 *
 * Example 1:
 * Input
 * ["Solution","pickIndex"]
 * [[[1]],[]]
 * Output
 * [null,0]
 *
 * Explanation
 * Solution solution = new Solution([1]);
 * solution.pickIndex();
 * // return 0. The only option is to return 0 since there is only one element in w.
 *
 * Example 2:
 * Input
 * ["Solution","pickIndex","pickIndex","pickIndex","pickIndex","pickIndex"]
 * [[[1,3]],[],[],[],[],[]]
 * Output
 * [null,1,1,1,1,0]
 *
 * Explanation
 * Solution solution = new Solution([1, 3]);
 * solution.pickIndex(); // return 1. It is returning the second element (index = 1) that has a probability of 3/4.
 * solution.pickIndex(); // return 1
 * solution.pickIndex(); // return 1
 * solution.pickIndex(); // return 1
 * solution.pickIndex(); // return 0. It is returning the first element (index = 0) that has a probability of 1/4.
 *
 * Since this is a randomization problem, multiple answers are allowed.
 * All of the following outputs can be considered correct:
 * [null,1,1,1,1,0]
 * [null,1,1,1,1,1]
 * [null,1,1,1,0,0]
 * [null,1,1,1,0,1]
 * [null,1,0,1,0,0]
 * ......
 * and so on.
 */

class Solution {
    Random rand;
    int[] prevSum;
    int totalSum;

    public Solution(int[] w) {
        rand = new Random();
        int n = w.length;
        // 用prevSum的原因只是方便反向找index
        prevSum = new int[n + 1];
        for (int i = 1; i < n + 1; i++) {
            prevSum[i] = prevSum[i - 1] + w[i - 1];
            totalSum += w[i - 1];
        }
    }

    public int pickIndex() {
        // 本题的本质是找到第一个包含target的线段
        int target = rand.nextInt(totalSum) + 1;
        //rand.nextInt(num) 会返回一个范围在 0（包含）到 num（不包含）之间的随机整数。
        int left = 1, right = prevSum.length - 1;
        while (left + 1 < right) {
            int midIndex = left + (right - left)/2;
            if (prevSum[midIndex] == target) {
                return midIndex - 1;
            } else if (prevSum[midIndex] < target) {
                left = midIndex;
            } else {
                right = midIndex;
            }
        }

        if (prevSum[left] >= target) {
            //left 对应原数组里 left - 1
            return left - 1;
        } else {
            return right - 1;
        }
    }
}

/**
 * Your Solution object will be instantiated and called as such:
 * Solution obj = new Solution(w);
 * int param_1 = obj.pickIndex();
 */