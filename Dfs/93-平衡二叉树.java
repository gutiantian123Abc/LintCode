/**
 * 93 · 平衡二叉树
 * https://www.lintcode.com/problem/93/
 * https://leetcode.com/problems/balanced-binary-tree/
 *
 * 描述
 * 给定一个二叉树,确定它是高度平衡的。对于这个问题,一棵高度平衡的二叉树的定义是：一棵二叉树中每个节点的两个子树的深度相差不会超过1。
 *
 * 样例 1：
 * 如下，是一个平衡的二叉树。
 *           1
 *          / \
 *         2   3
 *
 * 样例 2:
 * 如下，是一个不平衡的二叉树。1的左右子树高度差2
 *            1
 *             \
 *             2
 *            /  \
 *           3   4
 */

/**
 * Definition of TreeNode:
 * public class TreeNode {
 *     public int val;
 *     public TreeNode left, right;
 *     public TreeNode(int val) {
 *         this.val = val;
 *         this.left = this.right = null;
 *     }
 * }
 */

public class Solution {
    /**
     * @param root: The root of binary tree.
     * @return: True if this Binary tree is Balanced, or false.
     */
    class ResultType {
        int maxDepth;
        boolean balanced;
        public ResultType(int maxDepth, boolean balanced) {
            this.maxDepth = maxDepth;
            this.balanced = balanced;
        }
    }

    public boolean isBalanced(TreeNode root) {
        // write your code here
        return helper(root).balanced;
    }

    private ResultType helper(TreeNode node) {
        if (node == null) {
            return new ResultType(0, true);
        }

        ResultType left = helper(node.left);
        ResultType right = helper(node.right);

        int leftDepth = left.maxDepth;
        int rightDepth = right.maxDepth;

        boolean balanced = Math.abs(leftDepth - rightDepth) <= 1 && left.balanced && right.balanced;
        return new ResultType(Math.max(leftDepth, rightDepth) + 1, balanced);
    }
}