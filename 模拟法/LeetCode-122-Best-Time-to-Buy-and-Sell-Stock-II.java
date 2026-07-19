/**
 * LeetCode 122. Best Time to Buy and Sell Stock II
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/
 *
 * >>> Read together with LC 121 (one trade) and LC 714 (unlimited + fee) --
 * >>> this is the middle of the family, and the only one pure greedy solves.
 *
 * Given prices where prices[i] is the stock price on day i, you may complete
 * UNLIMITED transactions, but may hold at most ONE share at any time -- you
 * must sell before buying again. Return the maximum profit.
 *
 * (On the one-share rule: "at most one share" is the state constraint;
 * "sell before re-buying" is the same rule projected onto actions -- holding
 * a share forbids buying, so the action sequence is forced to alternate
 * buy -> sell -> buy -> sell. Practically it means chosen trade intervals
 * cannot overlap; same-day sell-then-buy is legal, which is exactly what the
 * greedy below exploits.)
 *
 * Example 1:
 *   Input:  prices = [7,1,5,3,6,4]
 *   Output: 7   (buy 1 sell 5 -> 4; buy 3 sell 6 -> 3)
 *
 * Example 2:
 *   Input:  prices = [1,2,3,4,5]
 *   Output: 4   (one ride up -- or equivalently, four daily hops)
 *
 * Example 3:
 *   Input:  prices = [7,6,4,3,1]
 *   Output: 0   (prices only fall; never trade)
 *
 * Constraints:
 *   - 1 <= prices.length <= 3 * 10^4
 *   - 0 <= prices[i] <= 10^4
 *
 * Approach: greedy -- eat every rise. The enabling identity: a long rise
 * equals the sum of its adjacent daily rises (1->3->5: one trade earns 4,
 * daily hops earn 2+2=4 -- the middle terms telescope away). Since trades
 * are unlimited and same-day sell-then-buy is legal, "buy yesterday, sell
 * today whenever today is higher" is a legal strategy, and its total is
 *   sum of max(0, prices[i] - prices[i-1]).
 * It misses no rise (every rise is swallowed hop by hop), loses on no fall
 * (negative diffs are skipped), and no legal plan can exceed the sum of all
 * rises (any single trade's profit <= the rises inside its interval). So the
 * greedy doesn't just happen to work -- it constructively ACHIEVES the upper
 * bound. Counterintuitively, more freedom made the problem easier: LC 121 is
 * hard because you must CHOOSE one interval; here there is no choice -- every
 * rise is money, take it all.
 *
 * Where greedy ends: add a per-trade fee (LC 714) and the telescoping
 * identity breaks -- each split pays the fee again -- so the family moves on
 * to the hold/cash state-machine DP. Explicitly hunting valleys and peaks is
 * mathematically equivalent to this diff-sum but fiddlier to code (plateaus,
 * boundaries); the four-line version below is the safe form.
 *
 * Time:  O(n) -- one sweep over adjacent pairs.
 * Space: O(1).
 */
class Solution {
    public int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1]; // eat every rise, skip every fall
            }
        }
        return profit;
    }
}