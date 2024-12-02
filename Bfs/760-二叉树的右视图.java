/**
 * 760 · 二叉树的右视图
 * https://www.lintcode.com/problem/760/
 * https://leetcode.com/problems/binary-tree-right-side-view/
 *
 * 描述
 * 给定一棵二叉树，想象自己站在它的右侧，按照从顶部到底部的顺序，返回从右侧所能看到的节点值。
 *
 * 样例1:
 * 输入: {1,2,3,#,5,#,4}
 * 输出: [1,3,4]
 * 说明:
 *    1
 *  /   \
 * 2     3
 *  \     \
 *   5     4
 *
 * 样例2:
 * 输入: {1,2,3}
 * 输出: [1,3]
 * 说明:
 *    1
 *  /   \
 * 2     3
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
     * @param root: the root of the given tree
     * @return: the values of the nodes you can see ordered from top to bottom
     */
    public List<Integer> rightSideView(TreeNode root) {
        // write your code here
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode curNode = queue.poll();
                if (i == size - 1) {
                    res.add(curNode.val);
                }

                if (curNode.left != null) {
                    queue.add(curNode.left);
                }

                if (curNode.right != null) {
                    queue.add(curNode.right);
                }
            }
        }

        return res;
    }
}