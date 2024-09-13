/**
 * 737 · 查找矩阵
 * https://www.lintcode.com/problem/737
 *
 * 描述
 * 给一矩阵, 找到矩阵中每一行都出现的元素. 你可以假设矩阵中只有一个满足条件的元素.
 *
 * 样例
 * 样例 1:
 * 	输入 :
 * 	[
 * 	  [2,5,3],
 * 	  [3,2,1],
 * 	  [1,3,5]
 * 	]
 * 	输出 : 3
 */
public class Solution {
    /**
     * @param matrix: the input
     * @return: the element which appears every row
     */
    public int findElements(int[][] matrix) {
        // write your code here
        List<Integer> list = new ArrayList<>();
        for(int j = 0; j < matrix[0].length; j++) {
            if(!list.contains(matrix[0][j])) {
                list.add(matrix[0][j]);
            }
        }

        for(int i = 1; i < matrix.length; i++) {
            Set<Integer> set = new HashSet<>();
            for(int j = 0; j < matrix[0].length; j++) {
                set.add(matrix[i][j]);
            }
            /*
            注意， 以下不对：
            for(Integer num : list) {
                if(!set2.contains(num)) {
                    list.remove(num);
                }
            }*/

            //注意， List.remove时， 一定要用如下
            for(int j = 0; j < list.size(); j ++){
                if(!set.contains(list.get(j))){
                    list.remove(list.get(j));
                }
            }
        }

        return list.get(0);
    }
}