/**
 * 134 · LRU缓存策略
 * https://www.lintcode.com/problem/134
 * https://leetcode.com/problems/lru-cache/
 *
 * 描述
 * 为最近最少使用（LRU）缓存策略设计一个数据结构，它应该支持以下操作：获取数据和写入数据。
 *
 * get(key) 获取数据：如果缓存中存在key，则获取其数据值（通常是正数），否则返回-1。
 * set(key, value) 写入数据：如果key还没有在缓存中，则设置或插入其数据值。
 * 当缓存达到上限，它应该在写入新数据之前删除最近最少使用的数据用来腾出空闲位置。
 * 最终, 你需要返回每次 get 的数据.
 *
 * 样例 1:
 * 输入：
 * LRUCache(2)
 * set(2, 1)
 * set(1, 1)
 * get(2)
 * set(4, 1)
 * get(1)
 * get(2)
 * 输出：[1,-1,1]
 * 解释：
 * cache上限为2，set(2,1)，set(1, 1)，get(2) 然后返回 1，set(4,1) 然后 delete (1,1)，
 * 因为 （1,1）最少使用，get(1) 然后返回 -1，get(2) 然后返回 1。
 *
 * 样例 2:
 * 输入：
 * LRUCache(1)
 * set(2, 1)
 * get(2)
 * set(3, 2)
 * get(2)
 * get(3)
 * 输出：[1,-1,2]
 * 解释：
 * cache上限为 1，set(2,1)，get(2) 然后返回 1，set(3,2)
 * 然后 delete (2,1)，get(2) 然后返回 -1，get(3) 然后返回 2。
 */
public class LRUCache {
    class Node {
        int key;
        int val;
        Node prev;
        Node next;

        public Node(int key, int val) {
            this.val = val;
            this.key = key;
            prev = null;
            next = null;
        }
    }

    Node head;
    Node tail;
    int capacity;
    Map<Integer, Node> map;
    /*
     * @param capacity: An integer
     */public LRUCache(int capacity) {
        // do intialization if necessary
        this.capacity = capacity;
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        map = new HashMap<>();
    }

    /*
     * @param key: An integer
     * @return: An integer
     */
    public int get(int key) {
        // write your code here
        if (!map.containsKey(key)) {
            return -1;
        }

        Node node = map.get(key);
        // remove the current node from the linked list
        removeNode(node);

        // Move the node to tail
        addToTail(node);

        return node.val;
    }

    /*
     * @param key: An integer
     * @param value: An integer
     * @return: nothing
     */
    public void set(int key, int value) {
        // write your code here
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.val = value;
            removeNode(node);
            addToTail(node);
            return;
        }

        if (map.size() == capacity) {
            Node firstNode = head.next;
            removeNode(firstNode);
            map.remove(firstNode.key);
        }

        Node newNode = new Node(key, value);
        addToTail(newNode);
        map.put(key, newNode);
    }

    private void addToTail(Node node) {
        node.next = tail;
        tail.prev.next = node;
        node.prev = tail.prev;
        tail.prev = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}