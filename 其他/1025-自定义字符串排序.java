/**
 * 1025 · 自定义字符串排序
 * https://www.lintcode.com/problem/1025/
 * https://leetcode.com/problems/custom-sort-string/
 *
 * 描述
 * 字符串 S 和 T 只包含小写字符。在S中，所有字符只会出现一次。
 *
 * S 已经根据某种规则进行了排序。我们要根据 S 中的字符顺序对 T 进行排序。
 * 更具体地说，如果 S 中 x 在 y 之前出现，那么返回的字符串中 x 也应出现在 y 之前。
 * 字符串 T 中不存在于 S 之中的字符，按照字典序排序放在字符串最后即可。
 *
 * 示例:
 * 输入:
 * S = "cba"
 * T = "abcd"
 * 输出: "cbad"
 * 解释:
 * S 中出现了字符 "a", "b", "c", 所以 "a", "b", "c" 的顺序应该是 "c", "b", "a"
 * 由于 "d" 没有在 S 中出现, 因此它放在 T 的末端。
 */
public class Solution {
    /**
     * @param s: The given string S
     * @param t: The given string T
     * @return: any permutation of T (as a string) that satisfies this property
     */
    public String customSortString(String s, String t) {
        // Write your code here
        if (t == null || t.length() <= 1) {
            return t;
        }

        Set<Character> set = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            set.add(c);
        }

        Map<Character, Integer> countMap = new HashMap<>();
        List<Character> notIn = new ArrayList<>();

        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (!set.contains(c)) {
                notIn.add(c);
            } else {
                if (!countMap.containsKey(c)) {
                    countMap.put(c, 0);
                }
                countMap.put(c, countMap.get(c) + 1);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (countMap.containsKey(c)) {
                int count = countMap.get(c);
                for (int j = 0; j < count; j++) {
                    sb.append(c);
                }
            }
        }

        Collections.sort(notIn);
        for (char c : notIn) {
            sb.append(c);
        }

        return sb.toString();
    }
}