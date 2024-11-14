/**
 * 104 · 合并k个排序链表
 * https://www.lintcode.com/problem/104
 *
 * 描述
 * 合并 k 个排序链表（序列为升序序列），并且返回合并后的排序链表（序列为升序序列）。
 * 尝试分析和描述其复杂度。
 *
 * 样例 1：
 * 输入：
 * lists = [2->4->null,null,-1->null]
 * 输出：
 * -1->2->4->null
 * 解释：
 * 将2->4->null、null和-1->null合并成一个升序的链表。
 *
 * 样例 2：
 * 输入：
 * lists = [2->6->null,5->null,7->null]
 * 输出：
 * 2->5->6->7->null
 * 解释：
 * 将2->6->null、5->null和7->null合并成一个升序的链表。
 */

/**
 * Definition for ListNode.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int val) {
 *         this.val = val;
 *         this.next = null;
 *     }
 * }
 */
public class Solution {
    /**
     * @param lists: a list of ListNode
     * @return: The head of one sorted list.
     */
    public ListNode mergeKLists(List<ListNode> lists) {
        // write your code here
        PriorityQueue<ListNode> pq = new PriorityQueue<>((ListNode a, ListNode b) -> {
            return Integer.compare(a.val, b.val);
        });

        for (ListNode node : lists) {
            if (node != null) {
                pq.add(node);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tmp = dummy;

        while (!pq.isEmpty()) {
            ListNode now = pq.poll();
            tmp.next = now;
            tmp = tmp.next;

            if (now.next != null) {
                pq.add(now.next);
            }
        }

        return dummy.next;
    }
}