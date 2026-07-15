/**
 * LeetCode 472. Concatenated Words
 * https://leetcode.com/problems/concatenated-words/
 *
 * >>> Read together with LeetCode 139 (Word Break) -- this problem IS Word
 * >>> Break, run once per candidate word with the rest of the list as the
 * >>> dictionary. Same table, same "enumerate the last word" transition.
 *
 * Given an array of strings words (WITHOUT duplicates), return all the
 * concatenated words in it. A concatenated word is a string that is comprised
 * entirely of at least two shorter words (not necessarily distinct) from the
 * given array.
 *
 * Example:
 *   Input:  words = ["cat","cats","catsdogcats","dog","dogcatsdog",
 *                    "hippopotamuses","rat","ratcatdogcat"]
 *   Output: ["catsdogcats","dogcatsdog","ratcatdogcat"]
 *           (e.g. "dogcatsdog" = "dog" + "cats" + "dog")
 *
 * Constraints:
 *   - 1 <= words.length <= 10^4
 *   - 1 <= words[i].length <= 30
 *   - words[i] consists of only lowercase English letters and all are unique.
 *   - 1 <= sum(words[i].length) <= 10^5
 *
 * Approach: for each candidate word w, temporarily REMOVE it from the set
 * (a word must not use itself as its own piece -- otherwise wordBreak(w) is
 * trivially true), run the LC 139 DP against the remaining dictionary, then
 * ADD IT BACK (it may still serve as a piece of other words). The remove/
 * add-back cleanly separates the two roles "is a concatenated word" vs "is a
 * piece of one". The "at least two pieces" requirement comes free: with w
 * removed, no equal-length match exists (the array has no duplicates), so
 * every piece of a successful split is strictly shorter than w -- filling the
 * full length therefore takes >= 2 pieces. canForm == true is exactly the
 * answer condition, no piece counting needed.
 *
 * WHICH Word Break variant matters here (the TLE lesson): LC 139 has two
 * equivalent formulations -- (a) iterate dictionary words per cell, or
 * (b) enumerate the LAST WORD'S LENGTH per cell and look the substring up in
 * a HashSet. For a single string either works; here the DP runs 10^4 times
 * against a 10^4-word dictionary, so variant (a) costs
 * 10^4 x 30 x 10^4 x 30 ~ 10^10 and times out, while variant (b) caps the
 * inner loop at the word length bound (<= 30):
 * 10^4 x 30 x 30 x 30 ~ 3x10^7. Dictionary size multiplies into the total
 * when the DP is repeated -- always pick the length+set version here. The
 * cheap table check (canSegment[i - len]) runs before the costly substring,
 * and break settles a cell as soon as one split works (feasibility = OR).
 *
 * Time:  O(N * L^3), where N = words.length and L <= 30 is the max word
 *        length -- each word's DP tries O(L^2) (cell, length) pairs, each
 *        costing O(L) for substring + hash. Building the set is O(total
 *        characters).
 * Space: O(total characters) for the HashSet, plus O(L) per DP run.
 */
class Solution {
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        Set<String> dic = new HashSet<>(Arrays.asList(words));
        List<String> res = new ArrayList<>();

        for (String word : words) {
            dic.remove(word);
            if (canForm(word, dic)) {
                res.add(word);
            }
            dic.add(word);
        }

        return res;
    }

    public boolean canForm(String s, Set<String> dic) {
        boolean[] canSegment = new boolean[s.length() + 1];
        canSegment[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            canSegment[i] = false;
            for (int len = 1; len <= i; len++) {
                if (!canSegment[i - len]) {
                    continue;
                } else {
                    if (dic.contains(s.substring(i - len, i))) {
                        canSegment[i] = true;
                        break;
                    }
                }
            }
        }

        return canSegment[s.length()];
    }
}