/**
 * 270. Closest Binary Search Tree Value
 * https://leetcode.com/problems/closest-binary-search-tree-value
 *
 * Given the root of a binary search tree and a target value,
 * return the value in the BST that is closest to the target.
 * If there are multiple answers, print the smallest.
 *
 * Examples see link
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
    double minDiff = Double.MAX_VALUE;
    int res = 0;

    public int closestValue(TreeNode root, double target) {
        helper(root, target);
        return res;
    }

    private void helper(TreeNode root, double target) {
        if (root == null) {
            return;
        }

        double diff = Math.abs((double) root.val - target);
        if (diff < minDiff) {
            minDiff = diff;
            res = root.val;
        }

        if (diff == minDiff) {
            if (res > root.val) {
                res = root.val;
            }
        }

        if (root.val == target) {
            res = root.val;
            return;
        } else if (root.val > target) {
            helper(root.left, target);
        } else {
            helper(root.right, target);
        }
    }
}