/**
 * 1116 · 函数执行时间
 * https://www.lintcode.com/problem/1116/description
 * https://leetcode.com/problems/exclusive-time-of-functions/description/
 *
 * 描述
 * 给定一个不可中断单线程CPU的n个函数的运行日志，返回这些函数的执行时间片。
 * 每个函数都有一个唯一的id，从0到n-1。一个函数可能会被递归调用或者被其他函数调用。
 * 日志是一串字符串，其格式为：function_id:start_or_end:timestamp。
 * 例如：0:start:0意味着函数0从时间片0开始时执行。0:end:0意味着函数0从时间片0末尾结束。
 * 函数的“独占时间”是指这个函数所花费的时间片，
 * 调用其他函数所花费的时间片不会被算入该函数的独占时间。按照函数id升序返回每一个函数的独占时间。
 *
 * 输入的日志依据时间戳排序，不是id。
 * 输出应该依据函数id升序，也即输出的第0个元素对应第0个函数的执行时间。
 * 两个函数不会在同一个时间开始或结束。
 * 函数可以递归调用，并且一定会结束。
 * 1 <= n <= 100
 *
 * 样例 1：
 * 输入:
 *  2
 * 0:start:0
 * 1:start:2
 * 1:end:5
 * 0:end:6
 * 输出:
 * 3 4
 *
 * 解释:
 * 函数0从时间片0开始，执行2个时间片，到达时间片1末尾。
 * 现在函数0调用了函数1，函数1从时间片2开始，执行了4个时间片，到达时间片5末尾。
 * 函数0又从时间片6开始时执行，到时间片6末尾时结束，执行了1个时间片。
 * 所以函数0一共执行了3个时间片，函数1一共执行了4个时间片。
 *
 *
 * 样例 2:
 * 输入:
 * 3
 * 0:start:0
 * 1:start:2
 * 2:start:3
 * 2:end:4
 * 1:end:5
 * 0:end:6
 * 1:start:7
 * 1:end:10
 * 输出:
 * 3 6 2
 *
 * 解释：
 * 函数0从时间片0开始，执行2个时间片，到达时间片1末尾。
 * 现在函数0调用了函数1，函数1从时间片2开始，执行了1个时间片。
 * 函数1调用了函数2，函数2从时间片3开始，执行了2个时间片。
 * 函数1又从时间片5开始时执行，并在时间片5末尾时结束，执行了1个时间片。
 * 函数0又从时间片6开始时执行，并在时间片6末尾时结束，执行了1个时间片。
 * 函数1从时间片7开始时执行，并在时间片10末尾结束，执行了4个时间片。
 * 所以函数0一共执行了2 + 1 = 3个时间片，函数1一共执行了1 + 1 + 4 = 6个时间片，函数2一共执行了 2 个时间片。
 *
 * 更形象样例参照LeetCode
 */

public class Solution {
    /**
     * @param n: a integer
     * @param logs: a list of strings
     * @return: return a list of integers
     */
    class Node {
        int id;
        public Node(int id) {
            this.id = id;
        }
    }

    public int[] exclusiveTime(int n, List<String> logs) {
        // write your code here
        int[] res = new int[n];
        Stack<Node> stack = new Stack<>();
        int lastTime = 0;
        for (String log : logs) {
            String[] info = log.split(":");
            int id = Integer.parseInt(info[0]);
            String flag = info[1];
            int curTime = Integer.parseInt(info[2]);

            if (flag.equals("start")) { // 当前是开始事件
                if (!stack.isEmpty()) {
                    // 更新栈顶函数的独占时间
                    res[stack.peek().id] += curTime - lastTime;
                }
                // 将当前函数压入栈
                stack.push(new Node(id));
                lastTime = curTime;
            } else { // 当前是结束事件
                // 弹出栈顶函数，并更新其独占时间
                res[stack.pop().id] += curTime - lastTime + 1;
                lastTime = curTime + 1;
            }
        }
        return res;
    }
}