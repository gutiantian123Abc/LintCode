/**
 * LeetCode 547. Number of Provinces
 * https://leetcode.com/problems/number-of-provinces/
 *
 * There are n cities. Some are connected directly, others only indirectly (via
 * other cities). A province is a group of cities that are all directly or
 * indirectly connected, with no other cities outside the group. You are given an
 * n x n matrix isConnected where isConnected[i][j] == 1 means city i and city j
 * are directly connected (and 0 means they are not). Return the number of
 * provinces.
 *
 * This is LC 323 (connected components) with a different input format: the edges
 * come as an adjacency matrix instead of an edge list. Provinces == connected
 * components, so the same Union-Find approach applies -- only the edge traversal
 * changes.
 *
 * Example 1:
 *   Input:  isConnected = [[1,1,0],[1,1,0],[0,0,1]]
 *   Output: 2   (cities 0 and 1 form one province, city 2 forms another)
 *
 * Example 2:
 *   Input:  isConnected = [[1,0,0],[0,1,0],[0,0,1]]
 *   Output: 3
 *
 * Constraints:
 *   - 1 <= n <= 200
 *   - n == isConnected.length == isConnected[i].length
 *   - isConnected[i][j] is 1 or 0
 *   - isConnected[i][i] == 1
 *   - isConnected[i][j] == isConnected[j][i]  (the matrix is symmetric)
 *
 * Approach: Union-Find. Every city starts as its own province, so count begins
 * at n. Scan the matrix; whenever isConnected[i][j] == 1 and cities i and j are
 * not already in the same component, union them and decrement count. The
 * atSameGroup guard makes the symmetric entry (j,i) and the diagonal (i,i) no-ops,
 * so no merge is counted twice. getRoot uses recursive path compression: on the
 * way back up, each node on the path is re-pointed straight at the root, keeping
 * the trees nearly flat.
 *
 * Time:  O(n^2). Scanning the full matrix is O(n^2) and dominates -- it is also
 *        the lower bound, since every pair must be inspected. (This version scans
 *        both triangles rather than just the upper one: a constant factor more
 *        work, same O(n^2).) The Union-Find operations layered on top are a
 *        lower-order term: with path compression alone each find/union is
 *        O(log n) amortized, which is dwarfed by the n^2 scan.
 * Space: O(n) for fatherMap. The matrix itself is input, not extra space.
 */
class Solution {
    class UnionFind {
        public Map<Integer, Integer> fatherMap;

        public UnionFind() {
            fatherMap = new HashMap<>();
        }

        public void init(int n) {
            for (int i = 0; i < n; i++) {
                fatherMap.put(i, i);
            }
        }

        public void union(int x, int y) {
            int root1 = getRoot(x);
            int root2 = getRoot(y);
            if (root1 != root2) {
                fatherMap.put(root1, root2);
            }
        }

        public boolean atSameGroup(int x, int y) {
            return getRoot(x) == getRoot(y);
        }

        public int getRoot(int x) {
            if (fatherMap.get(x) == x) {
                return x;
            }

            int root = getRoot(fatherMap.get(x));
            fatherMap.put(x, root);

            return root;
        }
    }

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        int count = n;
        UnionFind uf = new UnionFind();
        uf.init(n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    int x = i;
                    int y = j;
                    if (!uf.atSameGroup(x, y)) {
                        uf.union(x, y);
                        count--;
                    }
                } 
            }
        }

        return count;
    }
}