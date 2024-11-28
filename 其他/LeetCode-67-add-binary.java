/**
 * 67. Add Binary
 * https://leetcode.com/problems/add-binary/
 *
 * Given two binary strings a and b, return their sum as a binary string.
 * Example 1:
 * Input: a = "11", b = "1"
 * Output: "100"
 *
 * Example 2:
 * Input: a = "1010", b = "1011"
 * Output: "10101"
 *
 * Constraints:
 * 1 <= a.length, b.length <= 104
 * a and b consist only of '0' or '1' characters.
 * Each string does not contain leading zeros except for the zero itself.
 */
class Solution {
    public String addBinary(String a, String b) {
        int p1 = a.length() - 1;
        int p2 = b.length() - 1;
        StringBuilder sb = new StringBuilder();

        int carry = 0;
        while (p1 >= 0 && p2 >= 0) {
            char c1 = a.charAt(p1);
            char c2 = b.charAt(p2);
            int num1 = c1 - '0';
            int num2 = c2 - '0';
            int curSum = num1 + num2 + carry;
            int digit = curSum % 2;
            carry = curSum / 2;
            sb.append((char)(digit + '0'));
            p1--;
            p2--;
        }

        while (p1 >= 0) {
            char c1 = a.charAt(p1);
            int num1 = c1 - '0';
            int curSum = num1 + carry;
            carry = curSum / 2;
            int digit = curSum % 2;
            sb.append((char)(digit + '0'));
            p1--;
        }

        while (p2 >= 0) {
            char c2 = b.charAt(p2);
            int num2 = c2 - '0';
            int curSum = num2 + carry;
            carry = curSum / 2;
            int digit = curSum % 2;
            sb.append((char)(digit + '0'));
            p2--;
        }

        if (carry == 1) {
            sb.append('1');
        }

        return sb.reverse().toString();
    }
}