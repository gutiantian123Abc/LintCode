/**
 * 1018 · 香槟塔
 * https://www.lintcode.com/problem/1018/
 * https://leetcode.com/problems/champagne-tower/
 *
 * 描述
 * 我们将玻璃杯按照金字塔的方式摆放，其中第一行有1个玻璃杯，第二行有2个玻璃杯，
 * 第三行有3个玻璃杯，依此类推直到第100行。 每个杯子可以装一杯（250毫升）香槟。
 *
 * 然后，从顶部的第一个玻璃杯中开始倒入香槟。 当最顶部的玻璃杯已满时，任何多余的液体将立即等量地流到玻璃杯的左右两侧。
 * 当第二行玻璃杯变满时，任何多余的香槟都会同样流到这些玻璃杯的左右两侧，依此类推（最底部的玻璃杯倒满后多余的液体会流到地面上）。
 *
 * 例如，在倒入一杯香槟后，最顶部的玻璃杯已满。 倒两杯香槟后，第二排的两个杯子都是半满的。
 * 在倒了三杯香槟之后，这两个杯子变满了 - 现在共有3个全杯。 倒入四杯香槟后，第三排中间的玻璃杯半满，两个外侧的玻璃杯四分之一满，
 * 如下图所示：
 * 返回在倒入一些非负整数杯香槟之后，第i行中第j个玻璃的满杯程度（i和j均从0索引）。
 *
 * 香槟倒入的数量范围为 [0, 10 ^ 9].
 * query_glass和query_row的范围为 [0, 99].
 *
 * 样例1:
 * 输入: poured = 1, query_glass = 1, query_row = 1
 * 输出: 0.0
 * 解释: 我们从香槟塔的顶端倒入一杯香槟（顶端坐标为(0, 0)）。并没有多余的液体流出，所以顶端杯子下方的任何杯子都是空的。
 *
 * 样例2:
 * 输入: poured = 2, query_glass = 1, query_row = 1
 * 输出: 0.5
 * 解释: 我们从香槟塔的顶端倒入两杯香槟（顶端坐标为(0, 0)）。有一杯多余的液体由位于(1, 0)和(1, 1)的杯子平分，它们各自有半杯香槟。
 */
public class Solution {
    /**
     * @param poured: an integer
     * @param queryRow: an integer
     * @param queryGlass: an integer
     * @return: return a double
     */
    public double champagneTower(int poured, int queryRow, int queryGlass) {
        // write your code here
        double[][] dp = new double[102][102];
        dp[0][0] = poured;

        for (int row = 0; row <= queryRow; row++) {
            for (int col = 0; col <= row; col++) {
                double overflow = dp[row][col] - 1.0;
                if (overflow > 0) {
                    dp[row + 1][col] += overflow / 2.0;
                    dp[row + 1][col + 1] += overflow / 2.0;
                }
            }
        }

        return Math.min(1.0, dp[queryRow][queryGlass]);
    }
}