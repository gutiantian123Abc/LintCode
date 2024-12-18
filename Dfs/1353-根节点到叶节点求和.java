/**
 * 1353 · 根节点到叶节点求和
 * https://www.lintcode.com/problem/1353/
 * https://leetcode.com/problems/sum-root-to-leaf-numbers/
 *
 * 描述
 * 给定仅包含来自0-9的数字的二叉树，每个根到叶路径可以表示数字。
 * 举个例子：root-to-leaf路径1-> 2-> 3，它代表数字123，找到所有根到叶的数的总和
 *
 * 样例1
 * 输入: {1,2,3}
 * 输出: 25
 * 解释:
 *     1
 *    / \
 *   2   3
 * 路径 1->2 表示数字 12.
 * 路径 1->3 表示数字 13.
 * 因此, sum = 12 + 13 = 25.
 *
 * 样例2
 * 输入: {4,9,0,5,1}
 * 输出: 1026
 * 解释:
 *     4
 *    / \
 *   9   0
 *  / \
 * 5   1
 * 路径 4->9->5 表示数字 495.
 * 路径 4->9->1 表示数字 491.
 * 路径 4->0 表示数字 40.
 * 因此, sum = 495 + 491 + 40 = 1026.
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
     * @param root: the root of the tree
     * @return: the total sum of all root-to-leaf numbers
     */
    class ResultType {
        int sum;
        public ResultType(int sum) {
            this.sum = sum;
        }
    }
    public int sumNumbers(TreeNode root) {
        // write your code here
        return helper(root, 0).sum;
    }

    private ResultType helper(TreeNode node, int prev) {
        if (node == null) {
            return new ResultType(0);
        }

        int sum = 10 * prev + node.val;

        if (node.left == null && node.right == null) {
            return new ResultType(sum);
        } else {
            ResultType left = helper(node.left, sum);
            ResultType right = helper(node.right, sum);
            return new ResultType(left.sum + right.sum);
        }
    }
}