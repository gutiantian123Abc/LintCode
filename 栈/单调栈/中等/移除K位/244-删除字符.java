/**
https://www.lintcode.com/problem/244
244 · 删除字符

描述
给定一个字符串str，现在要对该字符串进行删除操作，
保留字符串中的k个字符且相对位置不变，并且使它的字典序最小，返回这个子串。


例1:
 输入:str="fskacsbi",k=2
 输出:"ab"
 解释:“ab“是str中长度为2并且字典序最小的子串


例2:
 输入:str="fsakbacsi",k=3
 输出:"aac"
*/

public class Solution {
    /**
     * @param str: the string
     * @param k: the length
     * @return: the substring with  the smallest lexicographic order
     */
    public String deleteChar(String str, int k) {
        // Write your code here.
        // 这道题类似标准模版， 即：一个数组， 删除K个， 使得剩下的数组最小/最大， 并保留原来顺序
        // 本质上与此题一致： https://www.lintcode.com/problem/1255/
        // 1255 · 移除K位
        int n = str.length();
        int deleteCount = n - k;
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < n; i++) {
            char c = str.charAt(i);
            while(!stack.isEmpty() && stack.peek() > c && deleteCount > 0) {
                stack.pop();
                deleteCount--;
            }
            stack.push(c);
        }

        while(deleteCount > 0) {
            stack.pop();
            deleteCount--;
        }

        StringBuilder sb = new StringBuilder();
        while(!stack.isEmpty()) {
            sb.append(stack.pop());
        }
        return sb.reverse().toString();
    }
}