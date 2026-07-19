/**
 * LeetCode 121. Best Time to Buy and Sell Stock
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 *
 * You are given an array prices where prices[i] is the price of a stock on
 * the i-th day. Choose ONE day to buy and a LATER day to sell to maximize
 * profit. Return the maximum profit; if no profitable trade exists, return 0.
 *
 * Example 1:
 *   Input:  prices = [7,1,5,3,6,4]
 *   Output: 5   (buy at 1 on day 2, sell at 6 on day 5)
 *
 * Example 2:
 *   Input:  prices = [7,6,4,3,1]
 *   Output: 0   (prices only fall -- best move is not to trade)
 *
 * Constraints:
 *   - 1 <= prices.length <= 10^5
 *   - 0 <= prices[i] <= 10^4
 *
 * Approach: one pass, carrying the historical minimum. Fix the SELL day: if
 * selling today, the best buy day is the cheapest day seen so far. So walk
 * left to right with minPrice = lowest price up to yesterday; each day, first
 * settle "what if I sell today" (prices[i] - minPrice, challenge the record),
 * THEN fold today's price into minPrice for future days. The settle-before-
 * update order is what guarantees buy-before-sell -- minPrice only ever
 * contains strictly-earlier days (the same discipline as Two Sum's
 * check-before-insert: the running state holds only the left-side history).
 * No need to foresee the future: every day gets its turn as the sell day, so
 * the eventual peak is settled when the sweep reaches it.
 *
 * Initialization notes: minPrice starts at prices[0] -- a REAL price, not a
 * MAX_VALUE sentinel -- and the loop starts at i = 1; safe because the
 * constraints guarantee a non-empty array. maxProfit starts at 0, which
 * natively covers the "never trade" case with no special-casing. The
 * (todayPrice > minPrice) guard is optional -- Math.max would ignore a
 * non-positive profit anyway -- but kept for explicitness: only a price above
 * the historical low is worth selling at.
 *
 * Time:  O(n) -- one sweep, O(1) per day; also the lower bound (every price
 *        must be seen at least once).
 * Space: O(1) -- two variables.
 *
 * Family: 122 (unlimited trades -> greedily eat every rise), 123/188 (at most
 * 2/k trades -> state-machine DP), 309/714 (cooldown / fee). This problem is
 * also equivalent to Maximum Subarray (LC 53, Kadane) on the day-to-day price
 * differences -- the same "walk once carrying the running best" idea.
 */
class Solution {
    public int maxProfit(int[] prices) {
        int minPrice = prices[0];
        int maxProfit = 0;

        for (int i = 1; i < prices.length; i++) {
            int todayPrice = prices[i];
            if (todayPrice > minPrice) {
                int todayProfit = todayPrice - minPrice;
                maxProfit = Math.max(maxProfit, todayProfit);
            }
            minPrice = Math.min(minPrice, todayPrice);
        }

        return maxProfit;
    }
}