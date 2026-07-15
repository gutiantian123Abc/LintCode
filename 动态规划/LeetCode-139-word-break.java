/**
 * LeetCode 139. Word Break
 * https://leetcode.com/problems/word-break/
 *
 * Given a string s and a dictionary of strings wordDict, return true if s can
 * be segmented into a space-separated sequence of one or more dictionary
 * words. The same word in the dictionary may be reused multiple times in the
 * segmentation.
 *
 * Example 1:
 *   Input:  s = "leetcode", wordDict = ["leet","code"]
 *   Output: true   ("leet code")
 *
 * Example 2:
 *   Input:  s = "applepenapple", wordDict = ["apple","pen"]
 *   Output: true   ("apple pen apple" -- words may be reused)
 *
 * Example 3:
 *   Input:  s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
 *   Output: false
 *
 * Constraints:
 *   - 1 <= s.length <= 300
 *   - 1 <= wordDict.length <= 1000
 *   - 1 <= wordDict[i].length <= 20
 *   - s and wordDict[i] consist of only lowercase English letters.
 *   - All the strings of wordDict are unique.
 *
 * Approach: DP over prefixes -- the boolean sibling of Coin Change (unbounded
 * knapsack). canSegment[i] = can the prefix s[0..i) be split into dictionary
 * words. canSegment[0] = true is the cornerstone: the empty prefix needs no
 * splitting, and every "whole prefix is one word" case bootstraps from it.
 *
 * Transition -- enumerate the LAST word: any valid split of s[0..i) ends with
 * some dictionary word. This version iterates the dictionary directly and
 * tests each word against the tail: the path through `word` is valid iff
 *   (1) i - wordLen >= 0                    (the word fits inside the prefix),
 *   (2) canSegment[i - wordLen] is true     (the part before it splits -- the
 *       cheap table lookup runs FIRST, skipping the costly substring compare
 *       on dead ends), and
 *   (3) s.substring(i - wordLen, i).equals(word)   (the tail IS that word).
 * One success settles the cell: break exits the inner (dictionary) loop only
 * -- the outer loop must keep filling later cells. Being a feasibility
 * problem (OR, not min), false is a harmless default and no sentinel
 * machinery is needed. Words are inherently reusable since canSegment[i -
 * wordLen] reads the same table being filled.
 *
 * Compared with the other classic formulation (enumerate the last word's
 * LENGTH and look the substring up in a HashSet), enumerating dictionary
 * words needs no set and no maxLen precomputation; it wins when the
 * dictionary is small, while the length+set version wins when the dictionary
 * is huge but words are short. Same skeleton either way.
 *
 * Time:  O(n * m * L), where n = s.length(), m = wordDict.size(), and L is
 *        the average word length -- each cell tries every word, and each try
 *        costs O(wordLen) for substring + equals.
 * Space: O(n) for the DP table (the substring temporaries are transient).
 */
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        boolean[] canSegment = new boolean[s.length() + 1];
        canSegment[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            canSegment[i] = false;
            for (String word : wordDict) {
                int wordLen = word.length();
                if (i - wordLen >= 0) {
                    if (canSegment[i - wordLen] == false) {
                        continue;
                    } else {
                        if (s.substring(i - wordLen, i).equals(word)) {
                            canSegment[i] = true;
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        
        return canSegment[s.length()];
    }
}