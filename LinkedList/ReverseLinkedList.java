/**
 * Definition for ListNode
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */

public ListNode reverse(ListNode head) {
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