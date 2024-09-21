/**
 * 221 · 链表求和 II
 * 描述
 * 假定用链表表示两个数，其中每个节点仅包含一个数字。
 * 假设这两个数的数字顺序排列，请设计一种方法将两个数相加，并将其结果表现为链表的形式。
 *
 * 样例 1:
 *
 * 输入: 6->1->7   2->9->5
 * 输出: 9->1->2
 *
 *
 * 样例 2:
 *
 * 输入: 1->2->3   4->5->6
 * 输出: 5->7->9
 *
 */

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

public class Solution {
    /**
     * @param l1: The first list.
     * @param l2: The second list.
     * @return: the sum list of l1 and l2.
     */
    public ListNode addLists2(ListNode l1, ListNode l2) {
        // write your code here
        ListNode p1 = reverse(l1);
        ListNode p2 = reverse(l2);

        int carry = 0;
        ListNode node = new ListNode(0);
        ListNode dummy = node;
        while (p1 != null && p2 != null) {
            int sum = p1.val + p2.val + carry;
            int val = sum % 10;
            carry = sum / 10;
            node.next = new ListNode(val);
            node = node.next;
            p1 = p1.next;
            p2 = p2.next;
        }

        while (p1 != null) {
            int sum = p1.val + carry;
            int val = sum % 10;
            carry = sum / 10;
            node.next = new ListNode(val);
            node = node.next;
            p1 = p1.next;
        }

        while (p2 != null) {
            int sum = p2.val + carry;
            int val = sum % 10;
            carry = sum / 10;
            node.next = new ListNode(val);
            node = node.next;
            p2 = p2.next;
        }

        if (carry != 0) {
            node.next = new ListNode(carry);
        }

        ListNode res = reverse(dummy.next);

        return res;
    }

    // Reverse LinkedList
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