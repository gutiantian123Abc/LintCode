/**
 * 295. Find Median from Data Stream
 * https://leetcode.com/problems/find-median-from-data-stream/description/
 *
 * The median is the middle value in an ordered integer list.
 * If the size of the list is even, there is no middle value,
 * and the median is the mean of the two middle values.
 *
 * For example, for arr = [2,3,4], the median is 3.
 * For example, for arr = [2,3], the median is (2 + 3) / 2 = 2.5.
 * Implement the MedianFinder class:
 *
 * MedianFinder() initializes the MedianFinder object.
 * void addNum(int num) adds the integer num from the data stream to the data structure.
 * double findMedian() returns the median of all elements so far.
 * Answers within 10-5 of the actual answer will be accepted.
 *
 * Example 1:
 * Input
 * ["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]
 * [[], [1], [2], [], [3], []]
 * Output
 * [null, null, null, 1.5, null, 2.0]
 *
 * Explanation
 * MedianFinder medianFinder = new MedianFinder();
 * medianFinder.addNum(1);    // arr = [1]
 * medianFinder.addNum(2);    // arr = [1, 2]
 * medianFinder.findMedian(); // return 1.5 (i.e., (1 + 2) / 2)
 * medianFinder.addNum(3);    // arr[1, 2, 3]
 * medianFinder.findMedian(); // return 2.0
 */
class MedianFinder {
    private PriorityQueue<Integer> maxHeap;

    // A min-heap for the right side (larger half) of the numbers
    private PriorityQueue<Integer> minHeap;

    public MedianFinder() {
        maxHeap = new PriorityQueue<>((Integer a, Integer b) -> {
            return Integer.compare(b, a);
        });
        minHeap = new PriorityQueue<>();
    }

    /**
     When you add a number, you should not only rely on size conditions. The standard approach is:
     1.	Always add the new number to maxHeap.
     2.	Immediately transfer the largest element from maxHeap to minHeap
     to ensure that maxHeap does not hold elements that belong in the “larger half.”
     3.	If now minHeap has more elements than maxHeap, move one element from minHeap back to maxHeap.
     */
    public void addNum(int num) {
        maxHeap.add(num);
        // This ensures both size and order invariants are maintained at every insertion.
        // Move the largest element from maxHeap to minHeap
        minHeap.add(maxHeap.poll());
        // If minHeap now has more elements, move one back to maxHeap
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.add(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            if (maxHeap.size() == 0) {
                return 0.0;
            } else {
                return (maxHeap.peek() + minHeap.peek())/2.0;
            }
        } else {
            return maxHeap.peek() * 1.0;
        }
    }
}

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */