/**
 * 1644. Lowest Common Ancestor of a Binary Tree II
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-ii/
 *
 * Given the root of a binary tree, return the lowest common ancestor (LCA) of two given nodes, p and q.
 * If either node p or q does not exist in the tree, return null. All values of the nodes in the tree are unique.
 *
 * According to the definition of LCA on Wikipedia:
 * "The lowest common ancestor of two nodes p and q in a binary tree T is the
 * lowest node that has both p and q as descendants (where we allow a node to be a descendant of itself)".
 * A descendant of a node x is a node y that is on the path from node x to some leaf node.
 *
 * Exmalpes see link above
 *
 * Constraints:
 * The number of nodes in the tree is in the range [1, 10^4].
 * -109 <= Node.val <= 109
 * All Node.val are unique.
 * p != q
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
        boolean hasP;
        boolean hasQ;
        public ResultType(TreeNode potentailLCA, boolean hasP, boolean hasQ) {
            this.potentailLCA = potentailLCA;
            this.hasP = hasP;
            this.hasQ = hasQ;
        }
    }
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        ResultType res = helper(root, p, q);
        if (res.hasP && res.hasQ) {
            return res.potentailLCA;
        } else {
            return null;
        }

    }

    private ResultType helper(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return new ResultType(null, false, false);
        }

        ResultType left = helper(root.left, p, q);
        ResultType right = helper(root.right, p, q);

        if (root.val == p.val) {
            return new ResultType(root, true, (left.hasQ || right.hasQ));
        }

        if (root.val == q.val) {
            return new ResultType(root, (left.hasP || right.hasP), true);
        }

        if (left.potentailLCA != null && right.potentailLCA != null) {
            return new ResultType(root, true, true);
        } else if (left.potentailLCA != null) {
            return new ResultType(left.potentailLCA, left.hasP, left.hasQ);
        } else if (right.potentailLCA != null) {
            return new ResultType(right.potentailLCA, right.hasP, right.hasQ);
        } else {
            return new ResultType(null, false, false);
        }
    }
}