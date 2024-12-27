/**
 * 1070 · 账户合并
 * https://www.lintcode.com/problem/1070/
 * https://leetcode.com/problems/accounts-merge/
 *
 * 描述
 * 给定一个帐户列表，每个元素accounts [i]是一个字符串列表，其中第一个元素accounts [i] [0]是账户名称，其余元素是这个帐户的电子邮件。
 * 现在，我们想合并这些帐户。
 * 如果两个帐户有相同的电子邮件地址，则这两个帐户肯定属于同一个人。
 * 请注意，即使两个帐户具有相同的名称，它们也可能属于不同的人，因为两个不同的人可能会使用相同的名称。
 * 一个人可以拥有任意数量的账户，但他的所有帐户肯定具有相同的名称。
 * 合并帐户后，按以下格式返回帐户：每个帐户的第一个元素是名称，其余元素是按字典序排序后的电子邮件。
 * 帐户本身可以按任何顺序返回。
 *
 * 账户个数在1~1000之间
 * 每个账户下的电子邮件在1~10之间
 * 每个字符串的长度在1~30之间
 *
 * 样例:
 * 样例 1:
 * 	输入:
 * 	[
 * 		["John", "johnsmith@mail.com", "john00@mail.com"],
 * 		["John", "johnnybravo@mail.com"],
 * 		["John", "johnsmith@mail.com", "john_newyork@mail.com"],
 * 		["Mary", "mary@mail.com"]
 * 	]
 *
 * 	输出:
 * 	[
 * 		["John", 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com'],
 * 		["John", "johnnybravo@mail.com"],
 * 		["Mary", "mary@mail.com"]
 * 	]
 *
 * 	解释:
 * 	第一个第三个John是同一个人的账户，因为这两个账户有相同的邮箱："johnsmith@mail.com".
 * 	剩下的两个账户分别是不同的人。因为他们没有和别的账户有相同的邮箱。
 *
 * 	你可以以任意顺序返回结果。比如：
 *
 * 	[
 * 		['Mary', 'mary@mail.com'],
 * 		['John', 'johnnybravo@mail.com'],
 * 		['John', 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com']
 * 	]
 * 	也是可以的。
 */
public class Solution {
    /**
     * @param accounts: List[List[str]]
     * @return: return a List[List[str]]
     *          we will sort your return value in output
     */
    class UnionFind {
        HashMap<String, String> parent;

        public UnionFind() {
            parent = new HashMap<>();
        }

        public String getRoot(String email) {
            if (!parent.containsKey(email)) {
                parent.put(email, email);
                return email;
            }

            String cur = email;
            while (!parent.get(cur).equals(cur)) {
                cur = parent.get(cur);
            }

            /**
             Add optimzation here
             */

            return cur;
        }

        public void union(String a, String b) {
            String root_a = getRoot(a);
            String root_b = getRoot(b);
            if (!root_a.equals(root_b)) {
                parent.put(root_b, root_a);
            }
        }
    }

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, String> email2Name = new HashMap<>();
        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                if (!email2Name.containsKey(email)) {
                    email2Name.put(email, name);
                }
            }
        }

        UnionFind union = new UnionFind();
        for (List<String> account : accounts) {
            if (account.size() >= 2) {
                String firstEmail = account.get(1);
                for (int i = 2; i < account.size(); i++) {
                    String email = account.get(i);
                    union.union(firstEmail, email);
                }
            }
        }

        Map<String, List<String>> root2Emails = new HashMap<>();
        for (String email : email2Name.keySet()) {
            String root = union.getRoot(email);
            if (!root2Emails.containsKey(root)) {
                root2Emails.put(root, new ArrayList<>());
            }
            root2Emails.get(root).add(email);
        }

        List<List<String>> result = new ArrayList<>();
        for (String root : root2Emails.keySet()) {
            List<String> res = new ArrayList<>();
            List<String> emails = root2Emails.get(root);
            Collections.sort(emails);
            res.add(email2Name.get(emails.get(0)));
            for (String email : emails) {
                res.add(email);
            }
            result.add(res);
        }

        return result;
    }
}