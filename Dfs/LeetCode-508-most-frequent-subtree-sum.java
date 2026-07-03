/**
 * 508. Most Frequent Subtree Sum
 * https://leetcode.com/problems/most-frequent-subtree-sum/
 *
 * Given the root of a binary tree, return the most frequent subtree sum. If
 * there is a tie, return all the values with the highest frequency in any
 * order.
 *
 * The subtree sum of a node is defined as the sum of all the node values
 * formed by the subtree rooted at that node (including the node itself).
 *
 *
 * Example 1:
 *        5
 *       / \
 *      2  -3
 * Input: root = [5,2,-3]
 * Output: [2,-3,4]
 * Explanation: subtree sums are 2, -3, and (5 + 2 + -3) = 4, each once.
 *
 * Example 2:
 *        5
 *       / \
 *      2  -5
 * Input: root = [5,2,-5]
 * Output: [2]
 * Explanation: subtree sums are 2, -5, and 2; sum 2 occurs twice.
 *
 *
 * Constraints:
 * The number of nodes in the tree is in the range [1, 10^4].
 * -10^5 <= Node.val <= 10^5
 */

/**
 * Approach: Post-order DFS.
 * The subtree sum of a node is leftSum + rightSum + node.val, so the value
 * must be computed bottom-up. Each call returns its subtree sum to the parent
 * and, along the way, records that sum in a frequency map. After the traversal,
 * collect every sum whose frequency equals the running maximum.
 *
 * Time Complexity: O(n)
 *   - Each node is visited once; map operations are O(1); the final scan over
 *     the map is O(n).
 *
 * Space Complexity: O(n)
 *   - The frequency map holds up to n distinct sums, and the recursion stack
 *     is O(h) where h is the tree height.
 */
class Solution {
    private Map<Integer, Integer> countMap = new HashMap<>();
    private int maxFreq = 0;

    public int[] findFrequentTreeSum(TreeNode root) {
        helper(root);
        List<Integer> res = new ArrayList<>();
        for (int sum : countMap.keySet()) {
            int freq = countMap.get(sum);
            if (freq == maxFreq) {
                res.add(sum);
            }
        }

        int[] ans = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {
            ans[i] = res.get(i);
        }

        return ans;
    }

    // Returns the subtree sum rooted at `root`; records each sum's frequency.
    private int helper(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftSum = helper(root.left);
        int rightSum = helper(root.right);
        int sum = root.val + leftSum + rightSum;
        if (!countMap.containsKey(sum)) {
            countMap.put(sum, 0);
        }

        countMap.put(sum, countMap.get(sum) + 1);
        maxFreq = Math.max(maxFreq, countMap.get(sum));

        return sum;
    }
}