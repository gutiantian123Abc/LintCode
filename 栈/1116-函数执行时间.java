/**
 * LeetCode 636. Exclusive Time of Functions
 * https://leetcode.com/problems/exclusive-time-of-functions/
 *
 * On a single-threaded CPU, functions run and their calls nest like a stack:
 * if A calls B, then B must finish before A can resume. Given n functions with
 * IDs 0..n-1 and a list of logs, return each function's exclusive time -- the
 * total time it spends running itself, NOT counting time spent inside the
 * functions it calls.
 *
 * Each log has the form "{id}:{start|end}:{timestamp}":
 *   - "start" at t: the function begins at the START of time unit t.
 *   - "end"   at t: the function stops at the END of time unit t, i.e. it
 *                   occupies the whole unit t (t itself counts).
 *
 * Example:
 *   Input:  n = 2, logs = ["0:start:0","1:start:2","1:end:5","0:end:6"]
 *   Output: [3,4]
 *   (fn 0 runs on units {0,1} and {6} -> 3; fn 1 runs on units {2,3,4,5} -> 4)
 *
 * Constraints:
 *   - 1 <= n <= 100
 *   - 1 <= logs.length <= 500
 *   - 0 <= timestamp <= 10^9
 *   - Two start events never share a timestamp; same for two end events.
 *   - Every function has a matching end log for each start log.
 *
 * Approach: A stack holds the IDs of the functions currently in the call chain;
 * its top is whatever is running on the CPU right now. Before every event we
 * settle the top function's elapsed time since prevTime, then update the stack.
 *
 * Time:  O(L), where L = logs.length. Each log is processed once.
 * Space: O(n + d) -- O(n) for the result, O(d) for the stack, where d is the
 *        maximum call-nesting depth.
 */

/*
 * ===== prevTime 详解 =====
 *
 * prevTime 的语义:
 *   "当前栈顶函数从哪个时刻开始,连续占用 CPU 且这段时间还没被结算。"
 *   换句话说,prevTime 永远指向"下一段独占时间的起点"。每次事件到来时,我们
 *   先把栈顶函数从 prevTime 到当前时刻的时间算给它,再更新 prevTime。
 *
 * 两种时间戳的含义(这是理解 +1 的关键):
 *   - start:t  ->  被打断的那个函数在 t 这一刻就停了,它【不占用】第 t 个单位。
 *                  所以它跑了 [prevTime, t) 这个半开区间,长度 = t - prevTime。
 *   - end:t    ->  结束的函数把【整个】第 t 个单位占满了,t 本身要算进去。
 *                  所以它跑了 [prevTime, t] 这个闭区间,长度 = t - prevTime + 1。
 *
 * 为什么 start 分支里 prevTime = curTime:
 *   新函数从第 curTime 个单位开始运行,所以它这一段独占时间的起点就是 curTime。
 *
 * 为什么 end 分支里 prevTime = curTime + 1(而不是 curTime):
 *   结束的函数占满了第 curTime 个单位,所以它的调用者(出栈后新的栈顶)要等到
 *   【下一个】单位才能恢复。恢复的起点是 curTime + 1,而不是 curTime。
 *   这一步是最容易搞错的地方:虽然直觉上会想写 res[...] += curTime - prevTime,
 *   但只要参照点用对(prevTime 记成 curTime + 1)、区间用对(end 是闭区间要 +1),
 *   两者就自洽了。少了任何一个都会算错。
 *
 * 拿题目的例子走一遍(关注 prevTime 的变化):
 *   0:start:0  结算(栈空,跳过);   push 0;   prevTime = 0
 *   1:start:2  res[0] += 2 - 0 = 2; push 1;   prevTime = 2
 *   1:end:5    res[1] += 5 - 2 + 1 = 4(闭区间); pop 1; prevTime = 5 + 1 = 6
 *   0:end:6    res[0] += 6 - 6 + 1 = 1(闭区间); pop 0; prevTime = 6 + 1 = 7
 *   结果 [3, 4]。
 *
 *   注意 fn 0 的最后一段:prevTime 是 6(fn 1 结束后的下一个单位),不是 5。
 *   因为 fn 1 占满了第 5 个单位,fn 0 是从第 6 个单位才恢复的。所以这里
 *   curTime(6) - prevTime(6) + 1 = 1 才是对的;若把 prevTime 记成 5 再省掉
 *   +1,虽然对 fn 0 碰巧凑对了,但对 fn 1 那种"紧跟自己 start 之后的第一段"
 *   (5 - 2 = 3)就会少算一个单位。所以 +1 不能笼统去掉。
 */
class Solution {
    public int[] exclusiveTime(int n, List<String> logs) {
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();
        int prevTime = 0;
        for (String log : logs) {
            String[] parts = log.split(":");
            int id = Integer.parseInt(parts[0]);
            String action = parts[1];
            int curTime = Integer.parseInt(parts[2]);
            if (action.equals("start")) {
                if (!stack.isEmpty()) {
                    res[stack.peek()] += curTime - prevTime;
                }
                stack.push(id);
                prevTime = curTime;
            } else {
                res[stack.pop()] += curTime - prevTime + 1;
                prevTime = curTime + 1;
            }
        }

        return res;
    }
}