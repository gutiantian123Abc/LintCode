/**
 * https://www.lintcode.com/problem/285/
 *
 * 285 · 高楼大厦
 *
 * 描述
 * 小Q在周末的时候和他的小伙伴来到大城市逛街，一条步行街上有很多高楼，
 * 共有n座高楼排成一行，楼高用arr表示。
 * 小Q从第一栋一直走到了最后一栋，小Q从来都没有见到这么多的楼，
 * 所以他想知道他在每栋楼的位置处能看到多少栋楼呢？（
 * 当前面的楼的高度大于等于后面的楼时，后面的楼将被挡住）
 *
 *
 * 样例
 * 例1:
 *
 *
 * 输入:[5,3,8,3,2,5]
 * 输出:[3,3,5,4,4,4]
 * 解释:
 * 当小Q处于位置0时，他能看到位置0，1，2的3栋高楼。
 * 当小Q位于位置1时，他能看到位置0，1，2的3栋高楼。
 * 当小Q处于位置2时，他可以向前看到位置0，1处的楼，向后看到位置3,5处的楼，加上第3栋楼，共可看到5栋楼。
 * 当小Q处于位置3时，他能看到位置2，3，4，5的4栋高楼。
 * 当小Q处于位置4时，他能看到位置2，3，4，5的4栋高楼。
 * 当小Q处于位置5时，他能看到位置2，3，4，5的4栋高楼。
 */

public class Solution {
    /**
     * @param arr: the height of all buildings
     * @return: how many buildings can he see at the location of each building
     */
    public int[] tallBuilding(int[] arr) {
        // Write your code here.
        // 解题思路：单调递增栈
        int n = arr.length;
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();

        // Front <-
        for(int i = 0; i < n; i++) {
            int curHeight = arr[i];
            while(!stack.isEmpty() && stack.peek() <= curHeight) {
                stack.pop();
                res[i] += 1;
            }

            if(stack.isEmpty()) {
                res[i] += 0;
            } else {
                res[i] += stack.size();
            }

            stack.push(curHeight);
        }

        // Back ->
        stack = new Stack<>();
        for(int i = n - 1; i >= 0; i--) {
            int curHeight = arr[i];
            while(!stack.isEmpty() && stack.peek() <= curHeight) {
                stack.pop();
                res[i] += 1;
            }

            if(stack.isEmpty()) {
                res[i] += 0;
            } else {
                res[i] += stack.size();
            }

            stack.push(curHeight);
        }

        for(int i = 0; i < n; i++) {
            res[i] += 1;
        }

        return res;
    }
}