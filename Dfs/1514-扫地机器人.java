/**
 * 1514 · 扫地机器人
 * lintcode.com/problem/1514/description
 * https://leetcode.com/problems/robot-room-cleaner/
 *
 * 描述
 * 给定一个清洁机器人，其处于一个模型化为网格地图的房间内。
 *
 * 网格中的每个格子可能是空空的或者阻塞的。
 *
 * 这个拥有4个给定的API的清洁机器人可以向前，向左，向右移动。每次转向是90度的。
 *
 * 当它尽力去移动进入一个阻塞的格子中，它的保险传感器探测到障碍并且它会待在合适的格子中。
 *
 * 设计一个算法去清理整个房间利用仅有的4个如下给定的API。
 * interface Robot {
 *   // 如果下一个格子开放并机器人可以进入返回true。
 *   // 如果下一个格子阻塞并且机器人处于合适的格子中返回false。
 *   boolean move();
 *
 *   // 在向左或向右转后，机器人还会待在原来的格子中。
 *   // 每次转向是90度。
 *   void turnLeft();
 *   void turnRight();
 *
 *   // 清理合适的格子。
 *   void clean();
 * }
 *
 *
 * 1.输入仅仅给出初始化的房间和机器人的位置。你必须解决这个"蒙上眼睛"的问题。
 * 换言之，你必须控制机器人调用仅提到的4个API，不知道房间布局和机器人起初的位置。
 * 2.机器人的起始位置总是在可通行的小格子中。
 * 3.机器人起始的方向将会朝上。
 * 4.所有可连通的格子是连通的，这意味着对于机器人所有被标记为1的小格子都是可通行的。
 * 5.假设所有网格的四边都被墙包围着。
 *
 * 样例 1:
 * 输入：
 * room = [
 *   [1,1,1,1,1,0,1,1],
 *   [1,1,1,1,1,0,1,1],
 *   [1,0,1,1,1,1,1,1],
 *   [0,0,0,1,0,0,0,0],
 *   [1,1,1,1,1,1,1,1]
 * ],
 * row = 1,
 * col = 3
 * 解释：
 * 房间中的网格均被标记为0或1。
 * 0代表格子是阻塞的，而1代表格子是可以通行的。
 * 机器人最初从1行3列的位置开始。
 * 从左上角，它的位置是向下1行并且向右3列。
 *
 * 样例 2:
 * 输入：
 * room = [
 *   [1,1,1,1,1,0,1,1],
 *   [1,1,1,1,1,0,1,1],
 *   [1,0,1,1,1,1,1,1],
 *   [1,0,0,1,0,0,0,1],
 *   [1,1,1,1,1,1,1,1]
 * ],
 * row = 2,
 * col = 3
 * 解释：
 * 房间中的网格均被标记为0或1。
 * 0代表格子是阻塞的，而1代表格子是可以通行的。
 * 机器人最初从2行3列的位置开始。
 * 从左上角，它的位置是向下2行并且向右3列。
 */

/**
 * // This is the robot's control interface.
 * // You should not implement it, or speculate about its implementation
 * class Robot {
 *   public:
 *     // Returns true if the cell in front is open and robot moves into the cell.
 *     // Returns false if the cell in front is blocked and robot stays in the current cell.
 *     bool move();
 *
 *     // Robot will stay in the same cell after calling turnLeft/turnRight.
 *     // Each turn will be 90 degrees.
 *     void turnLeft();
 *     void turnRight();
 *
 *     // Clean the current cell.
 *     void clean();
 * };
 */
public class Solution {
    // 注意， 顺序本题重要
    int[] dx = {-1, 0, 1, 0};
    int[] dy = {0, 1, 0, -1};
    Set<String> v;

    public void cleanRoom(Robot robot) {
        // write tour code here
        // 记住， 这里HashSet 里不能用int[], 无法区分， 只能用string 代替
        v = new HashSet<>();
        backTrack(robot, 0, 0, 0);

    }

    private void backTrack(Robot robot, int x, int y, int direction) {
        // direction 指的是刚进入的方向， 0 ~ 3
        v.add(x + "," + y);
        robot.clean();

        for (int i = 0; i < 4; i++) {
            int dir = (direction + i) % 4;
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (!v.contains(nx + "," + ny)) {
                if (robot.move()) {
                    backTrack(robot, nx, ny, dir);
                    robot.turnRight();
                    robot.turnRight();

                    robot.move();

                    robot.turnRight();
                    robot.turnRight();
                }
            }
            robot.turnRight();
        }
    }
}