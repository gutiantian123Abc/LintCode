/**
 * 902 · BST中第K小的元素
 * https://www.lintcode.com/problem/902/
 * https://leetcode.com/problems/kth-smallest-element-in-a-bst/
 *
 * 描述
 * 给一棵二叉搜索树，写一个 KthSmallest 函数来找到其中第 K 小的元素。
 *
 * 你可以假设 k 总是有效的， 1 ≤ k ≤ 树的总节点数。
 *
 * 样例 1:
 * 输入：{1,#,2},2
 * 输出：2
 * 解释：
 * 	1
 * 	 \
 * 	  2
 * 第二小的元素是2。
 *
 * 样例 2:
 * 输入：{2,1,3},1
 * 输出：1
 * 解释：
 *   2
 *  / \
 * 1   3
 * 第一小的元素是1。
 *
 * 挑战
 * 如果这棵 BST 经常会被修改(插入/删除操作)并且你需要很快速的找到第 K 小的元素呢？
 * 你会如何优化这个 KthSmallest 函数？
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
     * @param root: the given BST
     * @param k: the given k
     * @return: the kth smallest element in BST
     */
    Map<TreeNode, Integer> map;
    int totalSize = 0;
    public int kthSmallest(TreeNode root, int k) {
        // write your code here
        map = new HashMap<>();
        totalSize = countSizeForEachNode(root);
        return findKth(root, k).val;
    }
    /**
     Follow up:
     Upon node insert / delete, update map and size
     This approach still ensures it runs fast
     */

    private TreeNode findKth(TreeNode node, int k) {
        if (node == null) {
            return null;
        }

        int leftSize = 0;
        if (node.left != null) {
            leftSize = map.get(node.left);
        }

        if (k <= leftSize) {
            return findKth(node.left, k);
        } else if (k == leftSize + 1) {
            return node;
        } else {
            return findKth(node.right, k - (leftSize + 1));
        }
    }


    private int countSizeForEachNode(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int leftSize = countSizeForEachNode(node.left);
        int rightSize = countSizeForEachNode(node.right);
        int size = 1 + leftSize + rightSize;
        map.put(node, size);
        return size;
    }
}