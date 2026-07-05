/**
 * LeetCode 210. Course Schedule II
 * https://leetcode.com/problems/course-schedule-ii/
 *
 * There are numCourses courses labeled 0 to numCourses-1. You are given
 * prerequisites where prerequisites[i] = [a, b] means you must take course b
 * before course a (an edge b -> a). Return any valid order in which all courses
 * can be taken. If no valid order exists (the prerequisites contain a cycle),
 * return an empty array.
 *
 * Example 1:
 *   Input:  numCourses = 2, prerequisites = [[1,0]]
 *   Output: [0,1]
 *
 * Example 2:
 *   Input:  numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
 *   Output: [0,2,1,3]   ([0,1,2,3] is also accepted)
 *
 * Example 3:
 *   Input:  numCourses = 1, prerequisites = []
 *   Output: [0]
 *
 * Constraints:
 *   - 1 <= numCourses <= 2000
 *   - 0 <= prerequisites.length <= numCourses * (numCourses - 1)
 *   - prerequisites[i].length == 2
 *   - 0 <= a, b < numCourses, a != b
 *   - All prerequisite pairs are distinct.
 *
 * Approach: Topological sort via Kahn's algorithm (BFS). Two adjacency maps are
 * built from the edges: childrenMap[parent] lists the courses unlocked after
 * finishing parent, and parentsMap[child] lists the prerequisites child is still
 * waiting on -- the parents list plays the role of an in-degree counter, where
 * "list emptied" means "in-degree reached zero". Courses with no entry in
 * parentsMap have no prerequisites and seed the queue. Each course polled is
 * appended to order (a course can only be polled once all of its prerequisites
 * were polled before it, so poll order IS a valid topological order); finishing
 * it removes it from each child's parents list, and any child whose list empties
 * becomes ready and is enqueued. Cycle detection: courses on a cycle wait on
 * each other forever, their parents lists never empty, and they never enter the
 * queue -- so if fewer than numCourses courses were ordered, return [].
 * (The level-by-level size loop is a BFS habit and harmless here; topological
 * sort only needs poll order, not levels.)
 *
 * Time:  O(V + E) for the standard algorithm shape, where V = numCourses and
 *        E = prerequisites.length: each course is enqueued/polled at most once
 *        and each edge is relaxed once. One caveat: removing a parent uses
 *        indexOf + remove on an ArrayList, each O(list length), so edge
 *        relaxation is O(V + E * P) in the worst case, where P is the largest
 *        prerequisite count of a single course. Swapping the parents lists for a
 *        plain int[] inDegree array makes that step O(1) and restores a strict
 *        O(V + E). Well within limits either way here.
 * Space: O(V + E) for the two adjacency maps, plus O(V) for the queue and the
 *        output array.
 */
class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> parentsMap = new HashMap<>();
        Map<Integer, List<Integer>> childrenMap = new HashMap<>();

        for (int[] pre : prerequisites) {
            int parent = pre[1];
            int child = pre[0];

            if (!parentsMap.containsKey(child)) {
                parentsMap.put(child, new ArrayList<>());
            }

            parentsMap.get(child).add(parent);

            if (!childrenMap.containsKey(parent)) {
                childrenMap.put(parent, new ArrayList<>());
            }

            childrenMap.get(parent).add(child);
        }

        Queue<Integer> queue = new LinkedList<>();


        for (int course = 0; course < numCourses; course++) {
            if (!parentsMap.containsKey(course)) {
                queue.add(course);
            }
        }

        int[] order = new int[numCourses];
        int orderNum = 0;
        while(!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int course = queue.poll();
                order[orderNum++] = course;

                List<Integer> children = childrenMap.get(course);

                if (children != null && children.size() != 0) {
                    for (int child : children) {
                        int parentIndex = parentsMap.get(child).indexOf(course);
                        parentsMap.get(child).remove(parentIndex);
                        if (parentsMap.get(child).size() == 0) {
                            queue.add(child);
                        }
                    }
                }
            }
        }

        return orderNum == numCourses ? order : new int[0];
    }
}