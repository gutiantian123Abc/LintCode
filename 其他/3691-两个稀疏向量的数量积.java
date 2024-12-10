/**
 * 3691 · 两个稀疏向量的数量积
 * https://www.lintcode.com/problem/3691/
 * https://leetcode.com/problems/dot-product-of-two-sparse-vectors/
 *
 * 描述
 * 给定两个稀疏向量，计算它们的数量积（点积）。
 *
 * 实现类 SparseVector：
 *
 * SparseVector(nums) 通过向量 nums 初始化对象
 * dotProduct(vec) 计算该向量与 vec 向量的数量积
 * 稀疏向量 是指绝大多数分量为 0 的向量，通过设计 SparseVector 类 高效 地存储这个向量。
 *
 * 数量积的计算公式如下：
 * a . b = a[0]*b[0] + a[1]*b[1] + ... a[n-1]*b[n-1]
 *
 * Constraints:
 * nums1.length==nums2.length
 *
 * 样例 1：
 * 输入：
 * nums1 = [0,0,1,2,0,3]
 * nums2 = [4,0,1,0,0,3]
 * 输出：
 * 10
 * 解释：
 * v1 = SparseVector(nums1), v2 = SparseVector(nums2)
 * v1.dotProduct(v2) = 0*4 + 0*0 + 1*1 + 2*0 + 0*0 + 3*3 = 10
 *
 * 样例 2:
 * 输入：
 * nums1 = [1,0,0,0]
 * nums2 = [0,0,2,0]
 * 输出：
 * 0
 * 解释：
 * v1 = SparseVector(nums1), v2 = SparseVector(nums2)
 * v1.dotProduct(v2) = 1*0 + 0*0 + 0*2 + 0*0 = 0
 */

public class SparseVector {
    // Your SparseVector object will be instantiated and called as such:
    // SparseVector v1(nums1);
    // SparseVector v2(nums2);
    // int ans = v1.dotProduct(v2);
    Map<Integer, Integer> map;
    SparseVector(int[] nums) {
        // do intialization here
        map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                map.put(i, nums[i]);
            }
        }
    }

    // Return the dotProduct of two sparse vectors
    public int dotProduct(SparseVector vec) {
        // write your code here
        Map<Integer, Integer> map2 = vec.map;
        int ans = 0;

        if (map.size() < map2.size()) {
            for (int index : map.keySet()) {
                if (map2.containsKey(index)) {
                    ans += map.get(index) * map2.get(index);
                }
            }
        } else {
            for (int index : map2.keySet()) {
                if (map.containsKey(index)) {
                    ans += map.get(index) * map2.get(index);
                }
            }
        }

        return ans;
    }
}