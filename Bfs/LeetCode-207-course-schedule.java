/**
 * 207. Course Schedule
 * https://leetcode.com/problems/course-schedule/
 *
 * There are a total of numCourses courses you have to take, labeled from 0 to
 * numCourses - 1. You are given an array prerequisites where
 * prerequisites[i] = [ai, bi] indicates that you must take course bi first if
 * you want to take course ai.
 *
 * For example, the pair [0, 1], indicates that to take course 0 you have to
 * first take course 1.
 *
 * Return true if you can finish all courses. Otherwise, return false.
 *
 *
 * Example 1:
 * Input: numCourses = 2, prerequisites = [[1,0]]
 * Output: true
 * Explanation: There are a total of 2 courses to take.
 * To take course 1 you should have finished course 0. So it is possible.
 *
 * Example 2:
 * Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
 * Output: false
 * Explanation: There are a total of 2 courses to take.
 * To take course 1 you should have finished course 0, and to take course 0 you
 * should also have finished course 1. So it is impossible.
 *
 *
 * Constraints:
 * 1 <= numCourses <= 2000
 * 0 <= prerequisites.length <= 5000
 * prerequisites[i].length == 2
 * 0 <= ai, bi < numCourses
 * All the pairs prerequisites[i] are unique.
 */

/**
 * Approach: Topological sort (Kahn's algorithm / BFS).
 * The problem reduces to cycle detection in a directed graph. Repeatedly take
 * courses that have no remaining prerequisites, and "release" their children by
 * removing this course from each child's prerequisite list. If every course can
 * be finished, the graph is acyclic; otherwise a cycle exists.
 *
 * Let V = numCourses (nodes), E = prerequisites.length (edges).
 *
 * Time Complexity: O(V * E)
 *   - Building the graph uses List.contains() for dedup, which is O(k) per edge
 *     -> O(E * V) worst case.
 *   - The BFS uses indexOf() + remove(index) on a List, both O(k) per edge
 *     -> O(E * V) worst case.
 *   - Finding start nodes is O(V).
 *   - Dominated by the O(V * E) terms. (A standard indegree-array version would
 *     be O(V + E); this version trades some speed for readability and still
 *     passes within the constraints V <= 2000, E <= 5000.)
 *
 * Space Complexity: O(V + E)
 *   - childMap + parentMap store both directions of every edge -> O(V + E).
 *   - The queue holds at most O(V) nodes.
 */
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // childMap:  parent -> list of children it unlocks (adjacency list)
        // parentMap: child  -> list of prerequisites it still owes (acts as indegree)
        Map<Integer, List<Integer>> childMap = new HashMap<>();
        Map<Integer, List<Integer>> parentMap = new HashMap<>();

        // Build both directions of the graph. For edge [child, parent] meaning
        // "take `parent` before `child`", record parent->child and child->parent.
        for (int[] p : prerequisites) {
            int parent = p[1];
            int child = p[0];

            if (!childMap.containsKey(parent)) {
                childMap.put(parent, new ArrayList<>());
            }

            if (!parentMap.containsKey(child)) {
                parentMap.put(child, new ArrayList<>());
            }

            // contains() guards against duplicate edges; the constraints already
            // guarantee unique pairs, so this is a safety net (costs O(k) each).
            if (!childMap.get(parent).contains(child)) {
                childMap.get(parent).add(child);
            }

            if (!parentMap.get(child).contains(parent)) {
                parentMap.get(child).add(parent);
            }
        }

        // Seed the queue with every course that has no prerequisites (indegree 0),
        // i.e. courses that never appear as a key in parentMap.
        Queue<Integer> queue = new LinkedList<>();

        for (int course = 0; course < numCourses; course++) {
            if (!parentMap.containsKey(course)) {
                queue.add(course);
            }
        }

        // BFS / topological processing. `finished` counts how many courses we
        // manage to complete; the outer while + inner for(size) just walks the
        // graph level by level (doesn't change the total work).
        int finished = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int course = queue.poll();
                finished++; // this course can be taken

                // Release each child by removing `course` from its prerequisites.
                List<Integer> children = childMap.get(course);
                if (children != null) {
                    for (int child : children) {
                        // `course` is guaranteed to be present here, because
                        // childMap and parentMap were written as a matched pair,
                        // so indexOf never returns -1.
                        int parentIndex = parentMap.get(child).indexOf(course);
                        parentMap.get(child).remove(parentIndex);
                        // Child now owes nothing -> it can be taken next.
                        if (parentMap.get(child).size() == 0) {
                            queue.add(child);
                        }
                    }
                }
            }
        }

        // If we finished every course, the graph was acyclic -> true.
        // Otherwise some courses were stuck in a cycle -> false.
        return finished == numCourses;
    }
}