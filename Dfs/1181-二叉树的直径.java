/**
 * 1181 · 二叉树的直径
 * https://www.lintcode.com/problem/1181/
 * https://leetcode.com/problems/diameter-of-binary-tree/
 *
 * 描述
 * 给定一颗二叉树，您需要计算树的直径长度。 二叉树的直径是树中任意两个节点之间最长路径的长度。
 *
 * 样例 1:
 * 给定一棵二叉树
 *           1
 *          / \
 *         2   3
 *        / \
 *       4   5
 * 返回3, 这是路径[4,2,1,3] 或者 [5,2,1,3]的长度.
 *
 * 样例 2:
 * 输入:[2,3,#,1]
 * 输出:2
 *
 * 解释:
 *       2
 *     /
 *    3
 *  /
 * 1
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
     * @param root: a root of binary tree
     * @return: return a integer
     */
    int res = 0;
    class ResultType {
        int maxDepth;
        public ResultType(int m) {
            maxDepth = m;
        }
    }

    public int diameterOfBinaryTree(TreeNode root) {
        // write your code here
        helper(root);
        return res - 1;
    }

    private ResultType helper(TreeNode root) {
        if (root == null) {
            return new ResultType(0);
        }

        ResultType left = helper(root.left);
        ResultType right = helper(root.right);

        res = Math.max(res, 1 + left.maxDepth + right.maxDepth);

        return new ResultType(1 + Math.max(left.maxDepth, right.maxDepth));
    }
}