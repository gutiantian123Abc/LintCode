/**
 * 1704 · 二叉搜索树的范围和
 * https://www.lintcode.com/problem/1704/
 * https://leetcode.com/problems/range-sum-of-bst/
 *
 * 描述
 * 给定二叉搜索树的根结点 root，返回 L 和 R（含）之间的所有结点的值的和。
 * 二叉搜索树保证具有唯一的值。
 *
 * 样例 1: (参见LeetCode)
 * 输入：root = [10,5,15,3,7,null,18], L = 7, R = 15
 * 输出：32
 *
 * 样例 2:
 * 输入：root = [10,5,15,3,7,13,18,1,null,6], L = 6, R = 10
 * 输出：23
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
     * @param root: the root node
     * @param l: an integer
     * @param r: an integer
     * @return: the sum
     */

    class ResultType {
        int sum;
        public ResultType(int sum) {
            this.sum = sum;
        }
    }

    public int rangeSumBST(TreeNode root, int l, int r) {
        // write your code here.
        return helper(root, l, r).sum;
    }

    private ResultType helper(TreeNode node, int low, int high) {
        if (node == null) {
            return null;
        }

        if (node.val < low) {
            return helper(node.right, low, high);
        } else if (node.val > high) {
            return helper(node.left, low, high);
        } else {
            int sum = node.val;
            ResultType left = helper(node.left, low, high);
            ResultType right = helper(node.right, low, high);
            if (left != null) {
                sum += left.sum;
            }
            if (right != null) {
                sum += right.sum;
            }
            return new ResultType(sum);
        }
    }
}