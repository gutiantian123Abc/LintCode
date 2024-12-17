/**
 * 1506 · 二叉树中所有距离为 K 的结点
 * https://www.lintcode.com/problem/1506/
 * https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/
 *
 * 描述
 * 给定一个二叉树（具有根结点 root）， 一个目标结点 target ，和一个整数值 K 。
 * 返回到目标结点 target 距离为 K 的所有结点的值的列表。 答案可以以任何顺序返回。
 *
 * 给定的树是非空的，且最少有 K 个结点。
 * 树上的每个结点都具有唯一的值 0 <= node.val <= 500 。
 * 目标结点 target 是树上的结点。
 * 0 <= K <= 1000.
 *
 * 样例 1：
 * 输入：
 * {3,5,1,6,2,0,8,#,#,7,4}
 * 5
 * 2
 *
 * 输出：[7,4,1]
 *
 * 解释：
 * 所求结点为与目标结点（值为 5）距离为 2 的结点，
 * 值分别为 7，4，以及 1
 *
 * 样例2
 * 输入:
 * {1,2,3,4}
 * 2
 * 1
 *
 * 输出: [1,4]
 *
 * 说明:
 * 这棵二叉树如下所示：
 *     1
 *    / \
 *   2   3
 *  /
 * 4
 * 1和4这两个节点和2距离为1
 *
 * 详尽样例看原题
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
     * @param target: the target
     * @param k: the given K
     * @return: All Nodes Distance K in Binary Tree
     *          we will sort your return value in output
     */
    public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        // Write your code here
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();

        // bfs build parentMap
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode parentNode = queue.poll();
                TreeNode left = parentNode.left;
                TreeNode right = parentNode.right;
                if (left != null) {
                    queue.add(left);
                    parentMap.put(left, parentNode);
                }
                if (right != null) {
                    queue.add(right);
                    parentMap.put(right, parentNode);
                }
            }
        }

        // bfs from target node
        Set<TreeNode> v = new HashSet<>();
        queue.add(target);
        v.add(target);
        int dis = 0;
        List<Integer> res = new ArrayList<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (dis == k) {
                    res.add(node.val);
                } else {
                    TreeNode left = node.left;
                    TreeNode right = node.right;
                    TreeNode parent = null;
                    if (parentMap.containsKey(node)) {
                        parent = parentMap.get(node);
                    }

                    if (left != null && !v.contains(left)) {
                        v.add(left);
                        queue.add(left);
                    }

                    if (right != null && !v.contains(right)) {
                        v.add(right);
                        queue.add(right);
                    }

                    if (parent != null && !v.contains(parent)) {
                        v.add(parent);
                        queue.add(parent);
                    }
                }
            }

            if (dis == k) {
                return res;
            }

            dis++;
        }

        return res;

    }
}