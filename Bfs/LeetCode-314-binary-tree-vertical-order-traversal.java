/**
 * 314. Binary Tree Vertical Order Traversal
 * https://leetcode.com/problems/binary-tree-vertical-order-traversal/
 *
 * Given the root of a binary tree, return the vertical order traversal of its nodes' values.
 * (i.e., from top to bottom, column by column).
 *
 * If two nodes are in the same row and column, the order should be from left to right.
 *
 * Examples see the original link
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

class Node {
    TreeNode treeNode;
    int col;
    Node(TreeNode treeNode, int col) {
        this.treeNode = treeNode;
        this.col = col;
    }
}

class Solution {
    public List<List<Integer>> verticalOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) {
            return res;
        }

        Map<Integer, List<Integer>> map = new HashMap<>();

        // bfs
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(root, 0));

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                int col = node.col;
                int val = node.treeNode.val;

                if (!map.containsKey(col)) {
                    map.put(col, new ArrayList<>());
                }

                map.get(col).add(val);

                if (node.treeNode.left != null) {
                    queue.add(new Node(node.treeNode.left, col - 1));
                }

                if (node.treeNode.right != null) {
                    queue.add(new Node(node.treeNode.right, col + 1));
                }
            }
        }

        List<Integer> cols = new ArrayList<>();
        for (int col : map.keySet()) {
            cols.add(col);
        }

        Collections.sort(cols);

        for (int col : cols) {
            res.add(map.get(col));
        }

        return res;
    }
}