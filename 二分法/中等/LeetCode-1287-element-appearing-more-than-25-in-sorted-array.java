/**
 * 1287. Element Appearing More Than 25% In Sorted Array
 * https://leetcode.com/problems/element-appearing-more-than-25-in-sorted-array/
 *
 * Given an integer array sorted in non-decreasing order,
 * there is exactly one integer in the array that occurs more than 25% of the time, return that integer.
 *
 * Example 1:
 * Input: arr = [1,2,2,6,6,6,6,7,10]
 * Output: 6
 *
 * Example 2:
 * Input: arr = [1,1]
 * Output: 1
 */
class Solution {
    /**
     下面是一个利用二分查找实现 O(log n) 时间复杂度的方法思路。
     此方法利用已排序数组的特性以及对可能的候选元素进行有限次检测来完成。

     核心思路：
     1.	若某个元素出现次数超过 25%，在长度为 n 的数组中，该元素的数量大于 n/4。
     2.	将数组按四等分的比例位置挑选出三个潜在的候选元素： arr[n/4], arr[n/2], arr[3n/4]。
     因为若有一个元素超过 n/4 的长度，那么在这三个位置中，至少有一个位置会落在该元素的连续重复区间内。
     3.	对这三个候选元素中的每一个，通过二分查找来确定该元素在数组中第一次出现的位置和最后一次出现的位置，
     从而计算出它的出现次数。
     •	使用二分查找找到元素的左边界（第一次出现）和右边界（最后一次出现）。
     •	计算出现次数 = （右边界索引 - 左边界索引 + 1）。
     4.	若出现次数 > n/4，那么该元素即为结果。
     5.	因为只检查三个候选元素，每个候选元素的查找是 O(log n)，总的时间复杂度为 O(log n)。
     */
    public int findSpecialInteger(int[] arr) {
        int n = arr.length;
        int[] candidates = {arr[n/4], arr[n/2], arr[3*n/4]};

        for (int candidate : candidates) {
            int firstIndex = findFirstIndex(arr, candidate);
            int lastIndex = findLastIndex(arr, candidate);
            if (firstIndex != -1 && lastIndex != -1) {
                if (lastIndex - firstIndex + 1 > n/4) {
                    return candidate;
                }
            }
        }
        return -1;
    }

    int findFirstIndex(int[] arr, int target) {
        int n = arr.length;
        int start = 0, end = n - 1;
        while (start + 1 < end) {
            int midIndex = start + (end - start) / 2;
            if (arr[midIndex] < target) {
                start = midIndex;
            } else {
                end = midIndex;
            }
        }

        if (arr[start] == target) {
            return start;
        }

        if (arr[end] == target) {
            return end;
        }

        return -1;
    }

    int findLastIndex(int[] arr, int target) {
        int n = arr.length;
        int start = 0, end = n - 1;
        while (start + 1 < end) {
            int midIndex = start + (end - start) / 2;
            if (arr[midIndex] <= target) {
                start = midIndex;
            } else {
                end = midIndex;
            }
        }

        if (arr[end] == target) {
            return end;
        }

        if (arr[start] == target) {
            return start;
        }

        return -1;
    }
}