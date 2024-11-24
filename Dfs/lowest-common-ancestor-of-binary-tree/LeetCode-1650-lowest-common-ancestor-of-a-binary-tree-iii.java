/**
 * 1650. Lowest Common Ancestor of a Binary Tree III
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/
 *
 * Given two nodes of a binary tree p and q, return their lowest common ancestor (LCA).
 *
 * Each node will have a reference to its parent node. The definition for Node is below:
 *
 * class Node {
 *     public int val;
 *     public Node left;
 *     public Node right;
 *     public Node parent;
 * }
 * According to the definition of LCA on Wikipedia:
 * "The lowest common ancestor of two nodes p and q in a tree T
 * is the lowest node that has both p and q as descendants (where we allow a node to be a descendant of itself)."
 *
 * Examples see link above
 */
/*
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;
};
*/

class Solution {
    //将两个结点移动到相同的高度，然后同时向上移动，直到移动到相同的点
    public Node lowestCommonAncestor(Node p, Node q) {
        List<Node> pathP = pathToRoot(p);
        List<Node> pathQ = pathToRoot(q);
        Collections.reverse(pathP);
        Collections.reverse(pathQ);

        int indexP = pathP.size() - 1;
        int indexQ = pathQ.size() - 1;

        Node LCA = null;

        while (indexP >= 0 && indexQ >= 0 && indexP != indexQ) {
            if (indexP > indexQ) {
                indexP--;
            } else if (indexQ > indexP) {
                indexQ--;
            }
        }

        if (pathP.get(indexP).val == pathQ.get(indexQ).val) {
            LCA = pathP.get(indexP);
        } else {
            while (indexP >= 0 && indexQ >= 0) {
                if (pathP.get(indexP).val == pathQ.get(indexQ).val) {
                    LCA = pathP.get(indexP);
                    break;
                } else {
                    indexP--;
                    indexQ--;
                }
            }

        }

        return LCA;
    }

    private List<Node> pathToRoot(Node node) {
        List<Node> path = new ArrayList<>();

        while (node != null) {
            path.add(node);
            node = node.parent;
        }

        return path;
    }
}