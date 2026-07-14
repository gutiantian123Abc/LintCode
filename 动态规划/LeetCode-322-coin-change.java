/**
 * LeetCode 322. Coin Change
 * https://leetcode.com/problems/coin-change/
 *
 * You are given an integer array coins representing coins of different
 * denominations and an integer amount representing a total amount of money.
 * Return the fewest number of coins needed to make up that amount. If that
 * amount cannot be made up by any combination of the coins, return -1.
 * You may assume you have an infinite number of each kind of coin.
 *
 * Example 1:
 *   Input:  coins = [1,2,5], amount = 11
 *   Output: 3   (11 = 5 + 5 + 1)
 *
 * Example 2:
 *   Input:  coins = [2], amount = 3
 *   Output: -1
 *
 * Example 3:
 *   Input:  coins = [1], amount = 0
 *   Output: 0
 *
 * Constraints:
 *   - 1 <= coins.length <= 12
 *   - 1 <= coins[i] <= 2^31 - 1
 *   - 0 <= amount <= 10^4
 *
 * Approach: Unbounded-knapsack DP, filling a table from small amounts up.
 * f[i] = fewest coins to make amount i, with -1 as the "cannot be made"
 * sentinel. f[0] = 0 is the cornerstone: making amount 0 takes zero coins,
 * and every "one coin exactly" case (i == coin) bootstraps from it.
 *
 * Transition -- enumerate the LAST coin: any optimal way to make i ends with
 * some coin, and the part before it must itself be made optimally, which is
 * exactly f[i - coin] (already filled, since i grows left to right). So each
 * candidate path costs f[i - coin] + 1, and f[i] takes the best across coins.
 * Coins are reusable because f[i - coin] reads the SAME array being filled --
 * the sub-answer may already include this coin (unbounded knapsack; a 0/1
 * knapsack would have to read the previous row instead).
 *
 * The -1 sentinel needs two guards, both present in the inner condition
 * (i - coin >= 0 && f[i - coin] != -1):
 *   (1) the coin must not exceed i (no negative subproblem), and
 *   (2) the remainder must itself be makeable -- without this check,
 *       f[i-coin] = -1 would yield -1 + 1 = 0, a bogus "zero coins" answer
 *       that then poisons every later cell that reads it.
 * The two branches exist because -1 cannot join a min: while f[i] is still -1
 * (no champion yet) the first qualified path is written unconditionally;
 * once f[i] holds a real count, Math.min is safe and runs the tournament.
 * Payoff of the sentinel: f[amount] is already the required return value,
 * -1 included -- no final translation step.
 *
 * Time:  O(amount * n), where n = coins.length -- one pass over the coins per
 *        table cell, all guard checks O(1).
 * Space: O(amount) for the one-dimensional table.
 */
class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] f = new int[amount + 1];
        Arrays.fill(f, -1);
        f[0] = 0;

        for (int i = 1; i < amount + 1; i++) {
            for (int coin : coins) {
                if (f[i] == -1) {
                    if (i - coin >= 0 && f[i - coin] != -1) {
                        f[i] = f[i - coin] + 1;
                    }
                } else {
                    if (i - coin >= 0 && f[i - coin] != -1) {
                        f[i] = Math.min(f[i], f[i - coin] + 1);
                    }
                }
            }
        }

        return f[amount];
    }
}