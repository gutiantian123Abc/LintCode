/**
 * 680. Valid Palindrome II
 * https://leetcode.com/problems/valid-palindrome-ii/
 *
 * Given a string s, return true if the s can be palindrome after deleting at most one character from it.
 *
 * Example 1:
 * Input: s = "aba"
 * Output: true
 *
 * Example 2:
 * Input: s = "abca"
 * Output: true
 * Explanation: You could delete the character 'c'.
 *
 * Example 3:
 * Input: s = "abc"
 * Output: false
 */
class Solution {
    public boolean validPalindrome(String s) {
        int len = s.length();
        int left = 0, right = len - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return isPalindrome(s, left, right - 1) || isPalindrome(s, left + 1, right);
            }
            left++;
            right--;
        }

        return true;
    }

    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            } else {
                left++;
                right--;
            }
        }

        return true;

    }
}