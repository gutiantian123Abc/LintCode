/**
https://www.lintcode.com/problem/1060

1060 · 每日温度

描述
给定一个每日temperatures的列表，产生一个列表，对于输入的每天，
告诉我们你要等多少天才能够等到一个更高的温度。如果没有可能的未来日期，输出0作为替代。

比如，给定列表 temperatures = [73, 74, 75, 71, 69, 72, 76, 73]，
你的输出应该是[1, 1, 4, 2, 1, 1, 0, 0]。


样例 1:
	输入:  temperatures = [73, 74, 75, 71, 69, 72, 76, 73]
	输出:  [1, 1, 4, 2, 1, 1, 0, 0]
	
	解释:
	找到每个数字后面第一个大于自己的数字，输出两者的距离。

	
样例 2:
	输入: temperatures = [50, 40, 39, 30]
	输出:  [0,0,0,0]
*/


public class Solution {
    /**
     * @param temperatures: a list of daily temperatures
     * @return: a list of how many days you would have to wait until a warmer temperature
     */
    public int[] dailyTemperatures(int[] temperatures) {
        // Write your code here
        // 解题的关键是维护一个单调递增栈
        int n = temperatures.length;
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();

        for(int i = n - 1; i >= 0; i--) {
            int curTemp = temperatures[i];
            while(!stack.isEmpty() && temperatures[stack.peek()] <= curTemp) {
                stack.pop();
            }

            if(stack.isEmpty()) {
                res[i] = 0;
            } else {
                res[i] = stack.peek() - i;
            }

            stack.push(i);
        }

        return res;
    }
}