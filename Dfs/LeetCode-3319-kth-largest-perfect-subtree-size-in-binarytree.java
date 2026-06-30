/**
 * 3319. K-th Largest Perfect Subtree Size in Binary Tree
 * Difficulty: Medium
 *
 * You are given the root of a binary tree and an integer k.
 * Return the number of nodes in the k-th largest perfect binary subtree,
 * or -1 if fewer than k such subtrees exist.
 *
 * A binary tree is "perfect" when every parent node has exactly two
 * children and all leaf nodes lie at the same level. The "size" of a
 * subtree is its total node count. Note that a single leaf node counts
 * as a perfect binary subtree of size 1.
 *
 * Example 1:
 *   Input:  root = [5,3,6,5,2,5,7,1,8,null,null,6,8], k = 2
 *   Output: 3
 *   Explanation: The perfect-subtree sizes, sorted in non-increasing
 *   order, are [3, 3, 1, 1, 1, 1, 1, 1]. The 2nd largest is 3.
 *
 * Example 2:
 *   Input:  root = [1,2,3,4,5,6,7], k = 1
 *   Output: 7
 *   Explanation: The perfect-subtree sizes are [7, 3, 3, 1, 1, 1, 1].
 *   The whole tree is perfect, so the largest size is 7.
 *
 * Example 3:
 *   Input:  root = [1,2,3,null,4], k = 3
 *   Output: -1
 *   Explanation: Only two perfect subtrees exist, both of size 1, so
 *   there is no 3rd largest.
 *
 * Constraints:
 *   - The number of nodes in the tree is in the range [1, 2000].
 *   - 1 <= Node.val <= 2000
 *   - 1 <= k <= 1024
 *
 * Source: https://leetcode.com/problems/k-th-largest-perfect-subtree-size-in-binary-tree/
 */

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    class ResultType {
        boolean isPerfect;
        int size;
        public ResultType(boolean isPerfect, int size) {
            this.isPerfect = isPerfect;
            this.size = size;
        }
    }

    private List<Integer> sizes = new ArrayList<>();
    
    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        helper(root);
        if (sizes.size() < k) {
            return -1;
        }

        sizes.sort(Collections.reverseOrder());
        return sizes.get(k - 1);
    }

    public ResultType helper(TreeNode node) {
        if (node == null) {
            return new ResultType(true, 0);
        }

        ResultType left = helper(node.left);
        ResultType right = helper(node.right);

        if (left.isPerfect && right.isPerfect && left.size == right.size) {
            int size = 1 + left.size + right.size;
            sizes.add(size);
            return new ResultType(true, size);
        }

        return new ResultType(false, 0);
    }
}

/*
 * Time: O(n + m log m), where n = total nodes, m = number of perfect subtrees.
 *   - The helper DFS visits every node exactly once, O(1) work per node -> O(n).
 *   - Sorting the sizes list (m entries) -> O(m log m).
 *   - Total: O(n + m log m). Worst case m approaches n, so O(n log n); note m <= n.
 *
 * Space: O(n), from two sources:
 *   - The sizes list stores up to m entries -> O(m), which is O(n) worst case.
 *   - The recursion call stack goes as deep as the tree height -> O(h):
 *     O(n) for a skewed tree, O(log n) for a balanced tree.
 *   - Combined: O(n) worst case.
 */