/**
 * https://www.lintcode.com/problem/1560
 *
 * 1560 · 最小字符串
 *
 * 描述
 * 给定一个长度为n的只含小写字母的字符串s，从里面去掉k个字符，得到一个长度为n-k的新字符串。
 * 设计算法，输出字典序最小的新字符串。
 *
 * 此题中字典序的定义：首先比较两个字符串的长度，长度小的字典序更小，如果长度相同，
 * 则从字符串左边开始逐位比较，找到第一位不同的字符，对应字符小的字符串的字典序更小。
 *
 * 如："abbz"和"abza"，首先两个字符串的长度相同，则从左边开始逐位比较：
 * 第一位都是"a"，则继续比较下一位。
 * 第二位都是"b"，则继续比较下一位。
 * 第三位第一个字符串是"b"，而第二个字符串是"z"，因为"b" < "z"，故第一个字符串的字典序更小。
 *
 *
 * 样例 1:
 *  输入： s = "abccc", k = 2
 *  输出: "abc"`
 *  解释：
 *  删除4号位和5号位的`c`
 *
 *
 * 样例 2:
 *  输入: s = "bacdb", k = 2
 *  输出: "acb"
 *  解释：
 *  删除1号位的`b`和4号位的`d`
 *
 *
 * 样例3:
 *  输入: s="cba",k=2
 *  输出: "a"`
 *  解释：
 *  删除1号位的`c`和2号位的`b`
 */
public class Solution {
    /**
     * @param s: the string
     * @param k: the max time to remove characters
     * @return: Please output the new string with the smallest lexicographic order.
     */
    public String minimumString(String s, int k) {
        // Write your code here
        int n = s.length();
        int deleteCount = k;
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < n; i++) {
            char c = s.charAt(i);
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