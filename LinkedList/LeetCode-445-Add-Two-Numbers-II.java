/**
 * LeetCode 445. Add Two Numbers II
 * https://leetcode.com/problems/add-two-numbers-ii/
 *
 * You are given two non-empty linked lists representing two non-negative
 * integers. The most significant digit comes first (list head), and each node
 * holds a single digit. Add the two numbers and return the sum as a linked
 * list in the same most-significant-first order. Neither input has leading
 * zeros (except the number 0 itself).
 *
 * Example:
 *   Input:  l1 = [7,2,4,3], l2 = [5,6,4]   (7243 + 564)
 *   Output: [7,8,0,7]                        (7807)
 *
 * Constraints:
 *   - The number of nodes in each list is in the range [1, 100].
 *   - 0 <= Node.val <= 9
 *   - Each list represents a number without leading zeros.
 *
 * Follow up: Can you solve it without reversing the input lists?
 *
 * Approach (this solution): Reverse both lists so the least significant digit
 * comes first, add digit by digit exactly like LC 2 (Add Two Numbers) carrying
 * over as needed, then reverse the result back to most-significant-first.
 * Reversing the input is the trade-off here -- it keeps extra space at O(1)
 * (the two-stack approach avoids mutating the input but costs O(n+m) space).
 *
 * Time:  O(n + m), where n and m are the two list lengths. A constant number
 *        of linear passes (two input reversals + one add + one output reversal).
 * Space: O(1) excluding the output list; the reversals are done in place.
 */
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        l1 = reverse(l1);
        l2 = reverse(l2);
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int carry = 0;
        while(l1 != null && l2 != null) {
            int sum = l1.val + l2.val + carry;
            carry = sum / 10;
            int curVal = sum % 10;
            cur.next = new ListNode(curVal);
            cur = cur.next;
            l1 = l1.next;
            l2 = l2.next;
        }

        while(l1 != null) {
            int sum = l1.val + carry;
            carry = sum / 10;
            int curVal = sum % 10;
            cur.next = new ListNode(curVal);
            cur = cur.next;
            l1 = l1.next;
        }

        while(l2 != null) {
            int sum = l2.val + carry;
            carry = sum / 10;
            int curVal = sum % 10;
            cur.next = new ListNode(curVal);
            cur = cur.next;
            l2 = l2.next;
        }

        if (carry != 0) {
            cur.next = new ListNode(carry);
            cur = cur.next;
        }

        return reverse(dummy.next);
        
    }

    private ListNode reverse(ListNode head) {
        if(head == null || head.next == null) {
            return head;
        }

        ListNode prev = null;

        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }

        return prev;
    }
}