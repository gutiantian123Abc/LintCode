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


// DP approach
/**
 * LeetCode 121. Best Time to Buy and Sell Stock -- state-machine DP version
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 *
 * >>> Alternative formulation. Solution.java in this folder holds the primary
 * >>> minPrice one-pass sweep; this file rewrites it as the hold/cash DP to
 * >>> show where that sweep comes from and how 121/122/714 share one skeleton.
 *
 * Problem: prices[i] is the stock price on day i. Choose ONE day to buy and a
 * LATER day to sell to maximize profit; return 0 if no profitable trade
 * exists (exactly one transaction allowed).
 *
 * Example: prices = [7,1,5,3,6,4] -> 5   (buy at 1, sell at 6)
 * Constraints: 1 <= prices.length <= 10^5, 0 <= prices[i] <= 10^4
 *
 * State -- the second dimension is a SITUATION, not a size:
 *   dp[i][0] = max profit at end of day i holding NO stock
 *   dp[i][1] = max profit at end of day i HOLDING one stock
 * Cornerstones: dp[0][0] = 0 (did nothing), dp[0][1] = -prices[0] (bought on
 * day 0) -- both REAL reachable values, no sentinel. Answer is dp[n-1][0]:
 * ending while still holding wastes the buy-in.
 *
 * Transition -- the sell edge is IDENTICAL to LC 122; the entire "one
 * transaction only" constraint lives in the buy edge's base:
 *   dp[i][0] = max( dp[i-1][0], dp[i-1][1] + prices[i] )   // rest / sell today
 *   dp[i][1] = max( dp[i-1][1], 0 - prices[i] )            // rest / THE buy
 * Why 0: this is the only buy the rules allow, so no sell can have happened
 * before it -- the empty-pocket ledger must hold exactly 0 at that moment.
 * One constant enforces the whole constraint; no transaction counter needed.
 *
 * The reveal: dp[i][1] = max(prev hold, -price) is exactly the NEGATIVE OF THE
 * RUNNING MINIMUM PRICE, and dp[i][0] is the running best profit. So the
 * minPrice sweep in Solution.java IS this table compressed: minPrice ==
 * -dp[i][1], maxProfit == dp[i][0]. The DP doesn't replace the sweep -- it
 * names the family the sweep belongs to.
 *
 * Divergence from LC 122 on the same array [7,1,5,3,6,4]: at i = 3 (price 3),
 * 122's table lifts hold to 1 by standing on the prior profit of 4 and later
 * reaches 7; here base 0 pins hold at -1, a second trade can never stand up,
 * and the answer stays 5. Merge the two in one function via
 * base = unlimited ? dp[i-1][0] : 0; hang "- fee" on the sell edge and it
 * becomes LC 714 -- one skeleton, whole family.
 *
 * Time:  O(n) -- n rows x 2 cells x O(1) each.
 * Space: O(n); each row depends only on the previous row, so it rolls down to
 *        two variables for O(1) -- which lands you right back on Solution.java.
 */
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        int[][] dp = new int[n][2];
        dp[0][0] = 0;
        dp[0][1] = -prices[0];

        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i]);
            dp[i][1] = Math.max(dp[i - 1][1], -prices[i]);   // base is 0: the one and only buy
        }
        return dp[n - 1][0];
    }
}