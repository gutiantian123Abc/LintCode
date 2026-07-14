/**
 * LeetCode 98. Validate Binary Search Tree
 * https://leetcode.com/problems/validate-binary-search-tree/
 *
 * Given the root of a binary tree, determine if it is a valid binary search
 * tree (BST). A valid BST is defined as:
 *   - The left subtree of a node contains only nodes with keys STRICTLY less
 *     than the node's key.
 *   - The right subtree of a node contains only nodes with keys STRICTLY
 *     greater than the node's key.
 *   - Both the left and right subtrees must also be binary search trees.
 *
 * Example 1:
 *   Input:  root = [2,1,3]
 *   Output: true
 *
 * Example 2:
 *   Input:  root = [5,1,4,null,null,3,6]
 *   Output: false   (4 sits in the right subtree of 5, but 3 < 5)
 *
 * Constraints:
 *   - The number of nodes is in the range [1, 10^4].
 *   - -2^31 <= Node.val <= 2^31 - 1   (i.e. the full int range)
 *
 * Approach: Divide-and-conquer with a ResultType (post-order). Each subtree
 * reports three facts upward: (isBST, minVal, maxVal). A node is a valid BST
 * root iff both children are BSTs AND left.maxVal < node.val < right.minVal.
 * Comparing against the LEFT SUBTREE'S MAX (not just the left child) is what
 * defeats the classic trap: a violation buried deep in a subtree -- e.g. a 3
 * hiding in the right subtree of a 5 -- still surfaces through the reported
 * extremes, whereas parent-child checks alone would miss it.
 *
 * Two paired design points:
 *   (1) Reversed sentinels for the empty tree: null returns
 *       (min = Long.MAX_VALUE, max = Long.MIN_VALUE). An empty subtree must
 *       never fail a comparison -- its max of -INF passes any "< node.val"
 *       check, its min of +INF passes any "> node.val" check.
 *   (2) Self-inclusive rollup: minVal = min(left.minVal, node.val) and
 *       maxVal = max(right.maxVal, node.val). This folds the node's own value
 *       into the report AND neatly absorbs empty children (min against +INF
 *       collapses to node.val) without an if. The two points only work as a
 *       pair: sentinels without the min/max rollup would swallow leaf values
 *       and let violations slip through.
 *   Bounds are long, not int, because Node.val may be exactly
 *   Integer.MIN_VALUE / MAX_VALUE -- int sentinels would collide with real
 *   values and wrongly reject single-node trees like [Integer.MAX_VALUE].
 *   Note the invalid case still rolls up min/max normally and lets isBST
 *   propagate via &&, keeping the code branch-free.
 *
 * Time:  O(n) -- every node is visited exactly once with O(1) work to combine
 *        the children's reports.
 * Space: O(h) for the recursion stack, where h is the tree height: O(log n)
 *        for a balanced tree, O(n) worst case for a degenerate chain.
 */
class Solution {
    class ResultType {
        boolean isBST;
        long minVal;
        long maxVal;

        public ResultType(boolean isBST, long minVal, long maxVal) {
            this.isBST = isBST;
            this.minVal = minVal;
            this.maxVal = maxVal;
        }
    }
    public boolean isValidBST(TreeNode root) {
        ResultType res = helper(root);
        return res.isBST;
    }

    public ResultType helper(TreeNode node) {
        if (node == null) {
            return new ResultType(true, Long.MAX_VALUE, Long.MIN_VALUE);
        }

        ResultType left = helper(node.left);
        ResultType right = helper(node.right);

        boolean isBST = left.isBST && right.isBST &&(left.maxVal < node.val) && (node.val < right.minVal);
        long minVal = Math.min(node.val, left.minVal);
        long maxVal = Math.max(node.val, right.maxVal);


        return new ResultType(isBST, minVal, maxVal);
    }
}