/**
 * LeetCode 714. Best Time to Buy and Sell Stock with Transaction Fee
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/
 *
 * >>> Read together with LC 121 / 122 -- this is where the stock family
 * >>> graduates from greedy to the hold/cash state machine.
 *
 * Given prices where prices[i] is the stock price on day i, and an integer
 * fee charged per completed transaction (one buy + one sell), return the
 * maximum profit. Unlimited transactions, but at most one share held at any
 * time (must sell before buying again).
 *
 * Example 1:
 *   Input:  prices = [1,3,2,8,4,9], fee = 2
 *   Output: 8   (buy 1 sell 8 -> net 5; buy 4 sell 9 -> net 3)
 *
 * Example 2:
 *   Input:  prices = [1,3,7,5,10,3], fee = 3
 *   Output: 6
 *
 * Constraints:
 *   - 1 <= prices.length <= 5 * 10^4
 *   - 1 <= prices[i] < 5 * 10^4
 *   - 0 <= fee < 5 * 10^4
 *
 * Why LC 122's greedy dies here: "eat every rise" rested on the identity
 * big rise == sum of adjacent small rises -- splitting was free. With a fee,
 * every split pays once (1->3->5, fee 2: one trade nets 2, split trades net
 * 0), and rises smaller than the fee shouldn't be touched at all. Every day
 * now has a real decision, so a framework must do the accounting.
 *
 * State-machine DP -- the second dimension is a SITUATION, not a size:
 *   dp[i][0] = max profit at end of day i holding NO stock
 *   dp[i][1] = max profit at end of day i HOLDING one stock
 * Transition -- enumerate what today did to land in each situation:
 *   dp[i][0] = max( dp[i-1][0],                  // stayed empty
 *                   dp[i-1][1] + prices[i] - fee) // sold today (fee settles
 *                                                 //  at the sale -> exactly
 *                                                 //  once per transaction)
 *   dp[i][1] = max( dp[i-1][1],                  // kept holding
 *                   dp[i-1][0] - prices[i] )     // bought today, standing on
 *                                                //  ALL prior profit -- this
 *                                                //  is "unlimited trades"
 * The max does the deciding: selling-and-rebuying mid-rise costs an extra
 * fee, so the ledger automatically prefers holding through -- no explicit
 * "merge small rises" logic needed.
 *
 * Cornerstones: dp[0][0] = 0 (do nothing), dp[0][1] = -prices[0] (buy day
 * one; money out, profit negative). Both are REAL reachable values, so no
 * MIN_VALUE sentinel is needed -- the loop starts at i = 1. Answer is
 * dp[n-1][0]: ending while still holding means the buy-in was wasted.
 *
 * Time:  O(n) -- n rows x 2 cells x O(1) each.
 * Space: O(n) for the table. Each row depends only on the previous row, so
 *        it compresses to two rolling variables (cash = dp[i][0],
 *        hold = dp[i][1], with a prevCash temp guarding "yesterday's" value)
 *        for O(1) -- the same rolling-array move as Backpack I, taken to its
 *        limit. Setting fee = 0 collapses this machine back to LC 122;
 *        forcing the buy to stand on 0 instead of prior cash collapses it to
 *        LC 121's minPrice sweep -- one skeleton, whole family.
 */
class Solution {
    public int maxProfit(int[] prices, int fee) {
        int n = prices.length;

        int[][] dp = new int[n][2];
        dp[0][0] = 0;
        dp[0][1] = -1 * prices[0];

        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i] - fee);
            dp[i][1] = Math.max(dp[i - 1][0] - prices[i], dp[i - 1][1]);
        }

        return dp[n - 1][0];
        
    }
}