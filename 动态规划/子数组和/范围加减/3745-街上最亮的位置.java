/**
 * 3745 · 街上最亮的位置
 * https://www.lintcode.com/problem/3745
 *
 * 描述
 * 一条街上有很多的路灯，路灯的坐标由数组 lights 的形式给出，每个 lights[i] = [position, range]
 * 表示坐标位于 position 的路灯，可以照亮包括边界点在内的范围 [position - range, position + range]。
 *
 * 当某个位置 p，存在被多个路灯照亮时，这个位置会更亮一点，现在给定 lights ，
 * 返回 最亮的横坐标位置，如果 存在多个亮度一样的位置，返回坐标最小的。
 *
 * 样例 1：
 * 输入：
 *  lights = [[-3, 2], [1, 2], [3, 2]]
 *  输出：
 *  -1
 *  解释：
 *  第一个路灯照亮的范围是 [-5, -1]
 *  第二个路灯照亮的范围是 [-1, 3]
 *  第三个路灯照亮的范围是 [1, 5]
 *  位置 -1，1，2，3 都被两个路灯照亮
 *  因此返回坐标最小的 -1
 *
 * 样例 2：
 *
 *  输入：
 *  lights = [[1, 0], [0, 1]]
 *  输出：
 *  1
 */

public class Solution {
    /**
     * @param lights: Location and extent of illumination of street lights
     * @return: The brightest position
     */
    public int brightestPosition(int[][] lights) {
        // write your code here
        Map<Integer, Integer> changeMap = new HashMap<>();
        for (int i = 0; i < lights.length; i++) {
            int[] light = lights[i];
            int pos = light[0];
            int range = light[1];

            int leftChangeIndex = pos - range, rightChangeIndex = pos + range + 1;
            if (!changeMap.containsKey(leftChangeIndex)) {
                changeMap.put(leftChangeIndex, 0);
            }
            if (!changeMap.containsKey(rightChangeIndex)) {
                changeMap.put(rightChangeIndex, 0);
            }
            changeMap.put(leftChangeIndex, changeMap.get(leftChangeIndex) + 1);
            changeMap.put(rightChangeIndex, changeMap.get(rightChangeIndex) - 1);
        }

        List<Integer> sortedIndex = new ArrayList<>(changeMap.keySet());
        Collections.sort(sortedIndex);

        int brightness = 0;
        int maxBrightness = 0;
        int maxIndex = 0;

        for (int index : sortedIndex) {
            int change = changeMap.get(index);
            brightness += change;
            if (brightness > maxBrightness) {
                maxBrightness = brightness;
                maxIndex = index;
            }
        }

        return maxIndex;
    }
}