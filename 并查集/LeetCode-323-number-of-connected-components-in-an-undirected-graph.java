/**
 * LeetCode 323. Number of Connected Components in an Undirected Graph
 * https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/
 *
 * You have a graph of n nodes labeled from 0 to n-1. You are given n and a list
 * of edges where edges[i] = [a, b] indicates an undirected edge between node a
 * and node b. Return the number of connected components in the graph.
 *
 * Example 1:
 *   Input:  n = 5, edges = [[0,1],[1,2],[3,4]]
 *   Output: 2
 *
 * Example 2:
 *   Input:  n = 5, edges = [[0,1],[1,2],[2,3],[3,4]]
 *   Output: 1
 *
 * Constraints:
 *   - 1 <= n <= 2000
 *   - 0 <= edges.length <= 5000
 *   - edges[i].length == 2
 *   - 0 <= a, b < n
 *   - a != b, and there are no repeated edges or self-loops.
 *
 * Approach: Union-Find (DSU). Every node starts in its own component, so count
 * begins at n. For each edge, if its two endpoints are already in the same
 * component the edge is redundant and count is unchanged; otherwise union the
 * two components and decrement count. After all edges, count is the number of
 * connected components.
 *
 * ---------------------------------------------------------------------------
 * Path compression (recursive form) -- see getRoot below:
 *   getRoot(x) follows parent links up to the component's root. The key step
 *   happens as the recursion UNWINDS: every node visited on the path from x to
 *   the root is re-pointed DIRECTLY at that root via fatherMap.put(x, root).
 *   So a single getRoot call flattens the entire x -> ... -> root chain -- each
 *   node on it ends up exactly one hop from the root. Any later getRoot on those
 *   nodes is then O(1). This is what keeps the trees nearly flat and drives the
 *   amortized cost of find/union down toward constant over many operations.
 *   (Without the put line, getRoot would still return the correct root, but the
 *   tree would never flatten and repeated finds could degrade to O(height).)
 * ---------------------------------------------------------------------------
 *
 * Time complexity: there are two distinct questions here -- the cost of ONE
 * getRoot call, and the AMORTIZED (long-run average) cost per operation. This
 * version uses path compression but NOT union by rank/size, and they answer
 * differently.
 *
 *   Single getRoot call: O(path length), worst case O(n).
 *     One call walks from x up to the root (recursive descent) and then re-points
 *     each node on the way back (unwind) -- both are proportional to the current
 *     chain length. If the chain hasn't been compressed yet it can be as long as
 *     n, so a single call is worst-case O(n). But that same call flattens the
 *     whole chain, so the *next* call on those nodes is O(1). Individual calls
 *     therefore swing between expensive and cheap, which is why the meaningful
 *     figure is the amortized one.
 *
 *   Amortized per operation: O(log n).
 *     Intuition for why it lands at log n:
 *       (a) union here is a naive attach -- fatherMap.put(root1, root2) does not
 *           compare tree heights, so merges can build trees whose height grows on
 *           the order of log(size): each time two comparably-sized trees combine,
 *           the size roughly doubles while the height rises by one.
 *       (b) path compression only ever pays the full length of a long chain ONCE
 *           -- after that traversal every node on it points straight at the root
 *           and collapses to O(1). So an expensive O(length) call effectively
 *           "prepays" the many O(1) calls it just created by flattening the path.
 *     Charging each expensive call against the cheap future it produces (the
 *     potential / accounting method) spreads the cost so that the average per
 *     operation settles at the tree-height scale, O(log n). Compression cleans up
 *     after the fact but, without rank control, cannot stop union from re-growing
 *     trees to log n height -- so it flattens toward, but not below, O(log n).
 *     (The exact Theta(log n) bound for the compression-only variant comes from
 *     Tarjan & van Leeuwen's formal amortized analysis; the sketch above is the
 *     intuition, not a full proof. Adding union by rank/size on top would lower
 *     this to O(alpha(n)), the inverse Ackermann function, <= 4 for any real n
 *     and effectively constant.)
 *
 *   Overall: init is O(n); processing E edges is O(E * log n) amortized, so the
 *   whole algorithm is O(n + E * log n), where E = edges.length. For the given
 *   limits (n <= 2000, E <= 5000) this is effectively linear in practice.
 *
 *   Note: fatherMap is a HashMap, so get/put are O(1) average for Integer keys;
 *   this doesn't change the asymptotics but adds a hashing constant over a plain
 *   int[] backing array.
 *
 * Space: O(n) for fatherMap.
 */
class Solution {
    class UnionFInd {
        public Map<Integer, Integer> fatherMap;
        public UnionFInd() {
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
            fatherMap.put(root1, root2);
        }

        public boolean atSameGroup(int x, int y) {
            return getRoot(x) == getRoot(y);
        }

        public int getRoot(int x) {
            if (fatherMap.get(x) == x) {
                return x;
            }
            int root = getRoot(fatherMap.get(x));
            // Path Compression
            fatherMap.put(x, root);
            return root;
        }
    }

    public int countComponents(int n, int[][] edges) {
        UnionFInd uf= new UnionFInd();
        uf.init(n);
        int count = n;

        for (int[] edge : edges) {
            int x = edge[0];
            int y  = edge[1];
            if (!uf.atSameGroup(x, y)) {
                uf.union(x, y);
                count--;
            }
        }

        return count;
    }
}