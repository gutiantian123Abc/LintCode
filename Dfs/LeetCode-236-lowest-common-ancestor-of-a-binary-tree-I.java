/**
 * 236. Lowest Common Ancestor of a Binary Tree I
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree
 *
 * Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.
 *
 * Examples see link
 */

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */

class Solution {
    class ResultType {
        TreeNode potentailLCA;

        public ResultType(TreeNode potentailLCA) {
            this.potentailLCA = potentailLCA;
        }
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        ResultType resType = helper(root, p, q);
        return resType.potentailLCA;
    }

    public ResultType helper(TreeNode root, TreeNode A, TreeNode B) {
        if (root == null) {
            return new ResultType(null);
        }

        if (root.val == A.val || root.val == B.val) {
            return new ResultType(root);
        }

        ResultType left = helper(root.left, A, B);
        ResultType right = helper(root.right, A, B);

        if (left.potentailLCA != null && right.potentailLCA!= null) {
            return new ResultType(root);
        } else if (left.potentailLCA != null) {
            return new ResultType(left.potentailLCA);
        } else if (right.potentailLCA != null) {
            return new ResultType(right.potentailLCA);
        } else {
            return new ResultType(null);
        }
    }
}