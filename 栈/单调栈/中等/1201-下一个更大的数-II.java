/*
https://www.lintcode.com/problem/1201

1201 · 下一个更大的数 II

描述
给定一个环形数组（最后一个元素的下一个元素是数组的第一个元素），为每个元素打印下一个更大的元素。 
数字x的下一个更大的数是数组中下一个遍历顺序中出现的第一个更大的数字，
这意味着您可以循环搜索以查找其下一个更大的数字。 如果它不存在，则为此数字输出-1。


例1:
 输入: [1,2,1]
 输出: [2,-1,2]
 解释：第一个1的下一个更大的数字是2;
 数字2找不到下一个更大的数字;
 第二个1的下一个更大的数字需要循环搜索，答案也是2。


例2:
 输入: [1]
 输出: [-1]
 解释：
 数字1找不到下一个更大的数字

*/

public class Solution {
    /**
     * @param nums: an array
     * @return: the Next Greater Number for every element
     */
    public int[] nextGreaterElements(int[] nums) {
        // Write your code here
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);

        Stack<Integer> stack = new Stack<>();

        for(int i = 2 * n - 2; i >= 0; i--) {
            int curHeight = nums[i%n];
            while(!stack.isEmpty() && stack.peek() <= curHeight ) {
                stack.pop();
            }

            if(stack.isEmpty()) {
                res[i%n] = -1;
            } else {
                res[i%n] = stack.peek();
            }

            stack.push(curHeight);
        }

        return res;
    }
}