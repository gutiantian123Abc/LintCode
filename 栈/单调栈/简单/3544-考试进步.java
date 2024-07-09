/* https://www.lintcode.com/problem/3544
3544 · 考试进步

描述
给定一个非空整数数组 nums 表示学生每次考试的分数，返回一个等长的数组 res，
对于 nums 中的每次考试分数 nums[i]，找到它后面最近更高的分数，若不存在，则保留原考试分数。

样例

样例 1：

输入：
nums = [1,4,3,2,6,5]
输出：
[4,6,6,6,6,5]
解释：
在 1 之后比 1 更大且最近的数是 4
在 4 之后比 4 更大且最近的数是 6
在 3 之后比 3 更大且最近的数是 6
在 2 之后比 2 更大且最近的数是 6
在 6 之后没有比 6 更大的数，返回 6 自身
在 5 之后没有比 5 更大的数，返回 5 自身


样例 2：

输入：
nums = [6,5,4,3,2,1]
输出：
[6,5,4,3,2,1]
*/

//此题运用了单调递增栈
public class Solution {
    /**
     * @param nums: 
     * @return: return the nearest higher score
     */
    public int[] getProgress(int[] nums) {
        // write your code here
        Stack<Integer> stack = new Stack<>();
        int n = nums.length;
        int[] res = new int[n];
        for(int i = n - 1; i >= 0; i--) {
            int num = nums[i];
            while(!stack.isEmpty() && num >= stack.peek()) {
                stack.pop();
            }

            if(stack.isEmpty()) {
                res[i] = num;
            } else {
                res[i] = stack.peek();
            }

            stack.push(num);
        }

        return res;
    }
}