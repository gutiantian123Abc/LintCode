/**
 * 708. Insert into a Sorted Circular Linked List
 * https://leetcode.com/problems/insert-into-a-sorted-circular-linked-list/
 *
 * Given a Circular Linked List node, which is sorted in non-descending order,
 * write a function to insert a value insertVal into the list such that
 * it remains a sorted circular list.
 * The given node can be a reference to any single node in the list
 * and may not necessarily be the smallest value in the circular list.
 *
 * If there are multiple suitable places for insertion,
 * you may choose any place to insert the new value.
 * After the insertion, the circular list should remain sorted.
 *
 * If the list is empty (i.e., the given node is null),
 * you should create a new single circular list and return
 * the reference to that single node. Otherwise,
 * you should return the originally given node.
 *
 * Examples see the link
 */

/*
// Definition for a Node.
class Node {
    public int val;
    public Node next;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _next) {
        val = _val;
        next = _next;
    }
};
*/

class Solution {
    public Node insert(Node head, int insertVal) {
        Node node = new Node(insertVal);

        if (head == null) {
            node.next = node;
            return node;
        }

        // Note: Objects can use == , != to compare reference, not only val
        if (head.next == head) {
            head.next = node;
            node.next = head;
            return head;
        }

        Node tempNode= head.next;
        Node realHead = null;
        int minVal = head.val;
        // Find the first smallest node, which is the realHeadNode
        while (tempNode != head) {
            if (tempNode.val < minVal) {
                realHead = tempNode;
                minVal = realHead.val;
            }
            tempNode = tempNode.next;
        }

        if (realHead == null) {
            realHead = head;
        }

        Node prev = realHead;
        Node cur = realHead.next;

        // Note: Objects can use == , != to compare reference, not only val
        while (cur != realHead) {
            if (prev.val <= insertVal && insertVal <= cur.val) {
                prev.next = node;
                node.next = cur;
                return head;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }

        prev.next = node;
        node.next = realHead;

        return head;
    }
}