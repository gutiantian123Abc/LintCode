/** 420 · 报数
https://www.lintcode.com/problem/420/

描述
报数指的是，按照其中的整数的顺序进行报数，然后得到下一个数。如下所示：

1, 11, 21, 1211, 111221, ...

1 读作 "one 1" -> 11

11 读作 "two 1s" -> 21

21 读作 "one 2, then one 1" -> 1211

给定一个整数 n, 返回 第 n 个顺序。


样例:
	样例 1：
		输入：1
		输出："1"


	样例 2：
		输入：5
		输出："111221"
*/

public class Solution {
    /**
     * @param n: the nth
     * @return: the nth sequence
     */
    public String countAndSay(int n) {
        // write your code here
        String num = "1";
        for(int count = 1; count < n; count++) {
            StringBuilder sb = new StringBuilder();
            String newStrNum = "";
            int i = 0, j = 0;
            while(j < num.length()) {
                char curChar = num.charAt(i);
                while(j < num.length() && num.charAt(j) == curChar) {
                    j++;
                }
                int repeatTime = j - i;
                String repeatTimeStr = String.valueOf(repeatTime);
                sb.append(repeatTimeStr);
                sb.append(curChar);
                i = j;
            }
            num = sb.toString();
        }

        return num;
    }
}