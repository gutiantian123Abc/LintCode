/**
 * 1534 · 将二叉搜索树转换为已排序的双向链接列表
 * https://www.lintcode.com/problem/1534/
 * https://leetcode.com/problems/convert-binary-search-tree-to-sorted-doubly-linked-list/
 * 描述
 * 将BST转换为已排序的循环双向链表。可以将左右指针视为双向链表中上一个和下一个指针的同义词。
 *
 * 样例参考 https://www.lintcode.com/problem/1534/
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
     * @param root: root of a tree
     * @return: head node of a doubly linked list
     */

    List<TreeNode> nodes;
    public TreeNode treeToDoublyList(TreeNode root) {
        // Write your code here.
        if (root == null) {
            return null;
        }
        nodes = new ArrayList<>();
        helper(root);
        for (int i = 0; i < nodes.size() - 1; i++) {
            TreeNode cur = nodes.get(i);
            TreeNode next = nodes.get(i + 1);
            cur.right = next;
            next.left = cur;
        }

        nodes.get(0).left = nodes.get(nodes.size() - 1);
        nodes.get(nodes.size() - 1).right = nodes.get(0);
        return nodes.get(0);
    }

    private void helper(TreeNode node) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            nodes.add(node);
            return;
        }

        helper(node.left);
        nodes.add(node);
        helper(node.right);
    }
}