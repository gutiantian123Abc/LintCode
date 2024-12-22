/**
 * 105 · 复制带随机指针的链表
 * https://www.lintcode.com/problem/105/
 * https://leetcode.com/problems/copy-list-with-random-pointer/
 *
 * 描述
 * 给出一个链表，每个节点包含一个额外增加的随机指针，其可以指向链表中的任何节点或空的节点。
 * 返回其链表的深度拷贝。
 *
 * 挑战
 * 可否使用O(1)的空间
 */

/**
 * Definition for singly-linked list with a random pointer.
 * class RandomListNode {
 *     int label;
 *     RandomListNode next, random;
 *     RandomListNode(int x) { this.label = x; }
 * };
 */
public class Solution {
    /**
     * @param head: The head of linked list with a random pointer.
     * @return: A new head of a deep copy of the list.
     */
    public RandomListNode copyRandomList(RandomListNode head) {
        // write your code here
        RandomListNode cur = head;
        // handle next
        while (cur != null) {
            RandomListNode newNode = new RandomListNode(cur.label);
            newNode.next = cur.next;
            cur.next = newNode;
            cur = newNode.next;
        }

        // handle random
        cur = head;
        while (cur != null) {
            RandomListNode newNode = cur.next;
            if (cur.random != null) {
                // 如果 cur.random 不为空，则 newNode.random 指向 cur.random 的复制节点（即 cur.random.next）
                newNode.random = cur.random.next;
            } else {
                // 如果原节点的 random 就是 null，那么复制节点的 random 也应该是 null
                newNode.random = null;
            }
            cur = newNode.next;
        }

        // DisConnect old nodes
        cur = head;
        RandomListNode newHead = head.next;
        while (cur != null) {
            RandomListNode newNode = cur.next;
            cur.next = newNode.next;
            // 2. 如果 newNode.next 不为空，让它指向下一个复制节点
            if (newNode.next != null) {
                newNode.next = newNode.next.next;
            }
            cur = cur.next;
        }

        return newHead;
    }
}