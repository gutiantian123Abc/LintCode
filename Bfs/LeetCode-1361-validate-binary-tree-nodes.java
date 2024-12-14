/**
 * 1361. Validate Binary Tree Nodes
 * https://leetcode.com/problems/validate-binary-tree-nodes/description/
 *
 * You have n binary tree nodes numbered from 0 to n - 1
 * where node i has two children leftChild[i] and rightChild[i],
 * return true if and only if all the given nodes form exactly one valid binary tree.
 *
 * If node i has no left child then leftChild[i] will equal -1, similarly for the right child.
 *
 * Note that the nodes have no values and that we only use the node numbers in this problem.
 *
 * Examples see link
 *
 * Constraints:
 *
 * n == leftChild.length == rightChild.length
 * 1 <= n <= 10^4
 * -1 <= leftChild[i], rightChild[i] <= n - 1
 */
class Solution {
    public boolean validateBinaryTreeNodes(int n, int[] leftChild, int[] rightChild) {
        int[] inDegree = new int[n];

        for (int i = 0; i < n; i++) {
            if (leftChild[i] != -1) {
                inDegree[leftChild[i]]++;
            }

            if (rightChild[i] != -1) {
                inDegree[rightChild[i]]++;
            }
        }

        int root = -1;
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                if (root == -1) {
                    root = i;
                } else {
                    return false;
                }
            }
        }

        if (root == -1) {
            return false;
        }


        boolean[] v = new boolean[n];
        Queue<Integer> q = new LinkedList<>();

        q.add(root);
        v[root] = true;

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int node = q.poll();
                if (leftChild[node] != -1) {
                    if (v[leftChild[node]]) {
                        return false;
                    } else {
                        q.add(leftChild[node]);
                        v[leftChild[node]] = true;
                    }
                }

                if (rightChild[node] != -1) {
                    if (v[rightChild[node]]) {
                        return false;
                    } else {
                        q.add(rightChild[node]);
                        v[rightChild[node]] = true;
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (!v[i]) {
                return false;
            }
        }

        return true;
    }
}