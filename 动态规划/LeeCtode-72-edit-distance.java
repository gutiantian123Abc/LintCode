/**
 * LeetCode 72. Edit Distance
 * https://leetcode.com/problems/edit-distance/
 *
 * Given two strings word1 and word2, return the minimum number of operations
 * required to convert word1 to word2. Three operations are allowed on word1:
 * insert a character, delete a character, replace a character.
 * (This is the classic Levenshtein distance, 1965 -- a named, textbook DP.)
 *
 * Example 1:
 *   Input:  word1 = "horse", word2 = "ros"
 *   Output: 3   (horse -> rorse (replace h) -> rose (delete r) -> ros (delete e))
 *
 * Example 2:
 *   Input:  word1 = "intention", word2 = "execution"
 *   Output: 5
 *
 * Constraints:
 *   - 0 <= word1.length, word2.length <= 500
 *   - word1 and word2 consist of lowercase English letters.
 *
 * State: dp[i][j] = min operations to turn the first i chars of word1 into
 * the first j chars of word2. THE READING THAT MAKES IT CLICK: the indices
 * are not "string lengths" but a TODO LIST -- "word1 still has its first i
 * chars unprocessed; word2 still has its first j chars unmatched." The
 * strings themselves never change; only the todo list shrinks. Base cases:
 * dp[i][0] = i (empty target -> delete all i), dp[0][j] = j (empty source ->
 * insert all j).
 *
 * Transition -- stare at the two tails, enumerate the last operation:
 *   Tails equal  -> they pair off for free: dp[i][j] = dp[i-1][j-1].
 *   Tails differ -> one operation must settle this pair; three candidates,
 *   each shrinking the todo list differently (mnemonic: replace consumes
 *   BOTH, delete consumes MINE, insert consumes YOURS):
 *
 *     replace  dp[i-1][j-1] + 1 : rewrite word1's tail to match word2's tail;
 *                                 both tails pair off -> both sides shrink.
 *     delete   dp[i-1][j]   + 1 : discard word1's tail; word2's tail is STILL
 *                                 WAITING for a partner -> only i shrinks.
 *     insert   dp[i][j-1]   + 1 : append a new char equal to word2[j-1]; the
 *                                 newcomer pairs off word2's tail instantly.
 *
 * WHY INSERT IS j-1 WHEN word2 LOSES NO CHARACTER (the classic confusion):
 * word2 is intact -- but its j-th char's TASK is now DONE. dp[i][j-1] does
 * not mean "word2 with its last char removed"; it means "word2's first j-1
 * chars still need matching". The inserted char is created, matched, and
 * crossed off in one motion -- it never enters word1's todo list, so i stays;
 * word2's tail leaves the todo list, so j drops. Refutation of the intuitive
 * dp[i][j] + 1: (a) the equation would reference itself -- the cell depends
 * on itself and the table can never be filled; (b) semantically it claims
 * "one operation was spent and the todo list did not shrink at all", i.e. the
 * insert accomplished nothing -- but it demonstrably matched word2's tail.
 * Iron rule of DP transitions: every operation must strictly shrink the todo
 * list on at least one side. Tiny proof: word1="a", word2="ab" -- insert 'b',
 * it pairs with word2's 'b', remaining task is "a" vs "a" = dp[1][1] = 0, so
 * dp[1][2] = 1. Correct, and word2 was never touched.
 *
 * The three candidates point at the three neighbors (diagonal / up / left);
 * cells with equal tails inherit the diagonal for free -- that inheritance is
 * the engine that makes the table converge.
 *
 * Time:  O(m * n) -- each cell does O(1) work.
 * Space: O(m * n); reducible to O(n) with a rolling row (each cell only needs
 *        the previous row and the current row's left neighbor).
 */
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i < m + 1; i++) {
            dp[i][0] = i;
        }

        for (int i = 1; i < n + 1; i++) {
            dp[0][i] = i;
        }

        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int replace = dp[i - 1][j - 1] + 1;
                    int delete = dp[i - 1][j] + 1;
                    int add = dp[i][j - 1] + 1;
                    dp[i][j] = Math.min(replace, Math.min(delete, add));
                }
            }
        }

        return dp[m][n];
    }
}