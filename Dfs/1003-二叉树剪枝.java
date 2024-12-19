/**
 * 1003 · 二叉树剪枝
 * https://www.lintcode.com/problem/1003/
 * https://leetcode.com/problems/binary-tree-pruning/
 *
 * 描述
 * 给定二叉树的根节点，树上每个节点的值是0或1。
 *
 * 返回这棵树，其中每个不包含1的子树已被删除。
 * 注意，节点X的子树是X，加上X的所有子孙节点。）
 *
 * 二叉树最多只有100个节点
 * Node.val==0,1
 *
 * 样例看LeetCode原题
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
    class ResultType {
        boolean containsOne;

        public ResultType(boolean containsOne) {
            this.containsOne = containsOne;
        }
    }
    /**
     * @param root: the root
     * @return: the same tree where every subtree (of the given tree) not containing a 1 has been removed
     */
    public TreeNode pruneTree(TreeNode root) {
        // Write your code here
        ResultType res = helper(root);
        if (res == null) {
            return null;
        }
        if (!res.containsOne) {
            return null;
        } else {
            return root;
        }
    }

    private ResultType helper(TreeNode node) {
        if (node == null) {
            return null;
        }

        boolean isOne = node.val == 1;
        ResultType left = helper(node.left);
        ResultType right = helper(node.right);

        if (left != null) {
            if (!left.containsOne) {
                node.left = null;
            } else {
                isOne = true;
            }
        }

        if (right != null) {
            if (!right.containsOne) {
                node.right = null;
            } else {
                isOne = true;
            }
        }

        return new ResultType(isOne);
    }
}