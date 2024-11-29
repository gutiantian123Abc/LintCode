/**
 * 137 · 克隆图
 * https://www.lintcode.com/problem/137
 * https://leetcode.com/problems/clone-graph/
 *
 * 描述
 * 克隆一张无向图. 无向图的每个节点包含一个 label 和一个列表 neighbors.
 * 保证每个节点的 label 互不相同.
 *
 * 你的程序需要返回一个经过深度拷贝的新图.
 * 新图和原图具有同样的结构, 并且对新图的任何改动不会对原图造成任何影响.
 *
 * 样例1:
 * 输入:
 * {1,2,4#2,1,4#4,1,2}
 * 输出:
 * {1,2,4#2,1,4#4,1,2}
 * 解释:
 * 1------2
 *  \     |
 *   \    |
 *    \   |
 *     \  |
 *       4
 * 节点之间使用 '#' 分隔
 * 1,2,4 表示某个节点 label = 1, neighbors = [2,4]
 * 2,1,4 表示某个节点 label = 2, neighbors = [1,4]
 * 4,1,2 表示某个节点 label = 4, neighbors = [1,2]
 */

/**
 * Definition for Undirected graph.
 * class UndirectedGraphNode {
 *     int label;
 *     List<UndirectedGraphNode> neighbors;
 *     UndirectedGraphNode(int x) {
 *         label = x;
 *         neighbors = new ArrayList<UndirectedGraphNode>();
 *     }
 * }
 */

public class Solution {
    /**
     * @param node: A undirected graph node
     * @return: A undirected graph node
     */
    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
        // write your code here
        if (node == null) {
            return null;
        }

        Queue<UndirectedGraphNode> q = new LinkedList<>();
        // Map 在这里有两个作用
        // 1. visited
        // 2. 给新的Node 添加 neighbor
        Map<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();

        q.add(node);
        map.put(node, new UndirectedGraphNode(node.label));

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                UndirectedGraphNode graphNode = q.poll();
                for (UndirectedGraphNode neighbor : graphNode.neighbors) {
                    if (!map.containsKey(neighbor)) {
                        q.add(neighbor);
                        map.put(neighbor, new UndirectedGraphNode(neighbor.label));
                    }

                    map.get(graphNode).neighbors.add(map.get(neighbor));
                }
            }
        }

        return map.get(node);
    }
}