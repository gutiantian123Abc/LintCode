# 02 · Order Logs(Established Users + Trust Score)

**优先级:★★★★★ —— InterviewDB 唯一明确标注 (phone) 的题,近期店面有真实记录**

| 项目 | 说明 |
|---|---|
| 出现记录 | 面经 #1(InterviewDB,标注 **phone**)、#23(近期 SDE2 店面原题)、#28 的"按维度聚合成 map"很可能也是此类 |
| 题型 | Part 1:实现(日志聚合) · Part 2:在 Part 1 状态之上实现 Trust Score |
| 核心考点 | 字符串解析、per-user 聚合(Set + min/max)、**可复用的 class 设计**、边界计算 |
| 前车之鉴 | #23 帖主:"第一问写完了发现第二问要 call 到第一问的东西,然后重新设计了一下耽误了一些时间……最后差一点点做完" —— **这题的胜负手在 Part 1 的设计,不在算法** |

> 本文所有 Java 代码已实际编译运行、测试全部通过(含题目给的 4 组 Trust Score 输入输出)。

---

## 一、英文原版题干

### Part 1 — Established Users

> Affirm is building a new system to help our well-established users avoid loan declinations. We would like you to help us determine once a user has reached a threshold where the user has enough existing behavior for that to be a reliable signal.
>
> For the purposes of this interview, we will be passing in **two days' worth of log files** where each entry represents a transaction a user has made. Each log file will be represented by an array of string log lines in the following format: **`"Datestring,LoanType,UserID,LoanAmount"`**
>
> You are asked to iterate through these and find all users that have made transactions on **at least two different days** and with **at least two different loan types**. Your result should be a list of UserID(s) that meet both criteria:
>
> - They had loans on **both days** represented in the log files.
> - They had **at least two different loan types in total**.

```text
Log File 1                              Log File 2
"2025-01-01,Store,uuid1,100"            "2025-01-02,Phone,uuid1,250"
"2025-01-01,Store,uuid2,500"            "2025-01-02,Store,uuid2,1100"
"2025-01-01,Web,uuid4,100"              "2025-01-02,Web,uuid3,900"
"2025-01-01,Web,uuid1,150"              "2025-01-02,Phone,uuid3,600"
                                        "2025-01-02,Store,uuid4,200"

Expected Output: ["uuid1", "uuid4"]
```

为什么:uuid1 两天都有 + 3 种类型 ✓;uuid4 两天都有 + 2 种类型 ✓;uuid2 两天都有但只有 Store 1 种 ✗;uuid3 有 2 种类型但只出现在 day 2 ✗。

原始函数签名(Python):`def find_established_users(log_a: List[str], log_b: List[str]) -> List[str]`。HackerRank 会附带 stdin 解析的 boilerplate(以 `END_LOG_A` 分隔两个文件、跳过含 `#` 的行、少于 4 段的行打 stderr 后跳过)—— 那部分**不用你写**,专注核心函数即可。

### Part 2 — Trust Score

> Now we want to add functionality for measuring an incoming transaction's trust score. **The design of this functionality is up to you**, based on the requirements below.
>
> An incoming transaction's trust score will be generated using **UserID, LoanType, and RequestedAmount** parameters. This new interface will return a **score between 0 and 100** representing how certain we are this transaction is legitimate.
>
> If the user associated with the given transaction is in the established list, then calculate the trust score. **If not, the trust score will be 0.** Trust score is computed in two halves added together:
>
> - **LoanType matching (max 50):** 50 if the LoanType has been seen before for this user, 0 if not.
> - **Amount threshold (max 50):** starts at 50. If the presented amount is greater than the highest the user has taken, or less than the lowest, remove points based on **the percentage away from the current bound: 1 point per percentage**. (e.g. remove 10 points if the amount is 10% greater than the highest loan amount)
>
> **Clarification: Because these new transactions represent pending information, we should NOT use the new information to extend the trusted LoanTypes or Amount bounds.**

官方给的输入输出表(uuid4 的历史:类型 {Web, Store},金额范围 [100, 200]):

| Input | Output | Reason |
|---|---|---|
| ('uuid4', 'Web', 100) | **100** | Matching LoanType, amount within previous bounds |
| ('uuid4', 'Phone', 120) | **50** | New LoanType, amount within previous bounds |
| ('uuid4', 'Store', 80) | **80** | Matching LoanType, amount lower than lowest bound by 20% |
| ('uuid4', 'Phone', 280) | **10** | New LoanType, amount greater than highest bound by 40% |

---

## 二、中文题意精读(动笔前 checklist)

- Established 定义 = **两天都有交易** 且 **两天合计 ≥ 2 种 loan type**。注意不是"每天都 ≥2 种"——uuid4 每天各 1 种,合计 2 种,算 established;uuid3 一天内 2 种但只有一天,不算。
- 日志行固定 4 段:日期、类型、用户、金额。可能有脏行(boilerplate 已提示这种意识)。
- Part 2 得分 = LoanType 半区(0 或 50) + 金额半区(0~50):
  - 金额在 [min, max] 内(**含边界**)→ 50。
  - 低于 min:按 `(min - amount) / min × 100` 的百分比扣分;高于 max:按 `(amount - max) / max × 100` 扣分 —— **除数是被越过的那个 bound**。
  - 每 1% 扣 1 分,扣到 0 为止(**不能是负数**,例:超出 400% → 该半区 0 分,不是 -350)。
- 非 established 用户(包括从没见过的用户)→ 直接 0,连 LoanType 都不用看。
- **Pending 交易不改历史**:算完分不能把新 LoanType/新金额并进 profile(题目 Clarification 明确写了,面试官就等你踩这一步)。

## 三、设计:Part 1 就要为 Part 2 铺路(本题最大考点)

#23 的血泪:Part 1 只写了个返回 list 的函数,做 Part 2 时发现需要每个用户的历史类型和金额边界,当场重构,时间没了。

**正确姿势:一开始就维护 per-user 的 profile。**Part 1 需要 `activeDates` 和 `loanTypes`,Part 2 需要 `loanTypes` 和 `min/max`,一次遍历全部算好:

```text
UserProfile {
    activeDates:  Set<String>   // Part 1: 天数 >= 2
    loanTypes:    Set<String>   // Part 1: 类型 >= 2;Part 2: LoanType 半区
    minAmount:    long          // Part 2: 金额半区
    maxAmount:    long
}
```

开写前对面试官说一句:"I'll keep a per-user profile with dates, loan types and amount bounds — I suspect later parts will need more than just the established list."(这句话本身就是 Design 分。)

## 四、参考实现(已验证,含题目全部 4 组期望输出)

```java
import java.util.*;

public class OrderLogsTrustScore {

    /** Per-user aggregated history. Designed up-front so Part 2 can reuse it. */
    static class UserProfile {
        final Set<String> activeDates = new HashSet<>();
        final Set<String> loanTypes = new HashSet<>();
        long minAmount = Long.MAX_VALUE;
        long maxAmount = Long.MIN_VALUE;
    }

    static class EstablishedUserService {
        // LinkedHashMap keeps first-seen order so the output order is deterministic.
        private final Map<String, UserProfile> profiles = new LinkedHashMap<>();
        private final Set<String> establishedUsers = new LinkedHashSet<>();

        EstablishedUserService(List<String> logA, List<String> logB) {
            ingest(logA);
            ingest(logB);
            computeEstablishedUsers();
        }

        /** Parse one log file; skip malformed lines instead of crashing. */
        private void ingest(List<String> logLines) {
            for (String raw : logLines) {
                if (raw == null) continue;
                String line = raw.trim();
                if (line.isEmpty() || line.contains("#")) continue; // same rule as the given boilerplate

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.err.println("Skipping malformed loan line: \"" + line + "\"");
                    continue;
                }
                String date = parts[0].trim();
                String loanType = parts[1].trim();
                String userId = parts[2].trim();
                long amount;
                try {
                    amount = Long.parseLong(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed amount: \"" + line + "\"");
                    continue;
                }

                UserProfile profile = profiles.computeIfAbsent(userId, k -> new UserProfile());
                profile.activeDates.add(date);
                profile.loanTypes.add(loanType);
                profile.minAmount = Math.min(profile.minAmount, amount);
                profile.maxAmount = Math.max(profile.maxAmount, amount);
            }
        }

        /** Established = active on >= 2 distinct days AND >= 2 distinct loan types overall. */
        private void computeEstablishedUsers() {
            for (Map.Entry<String, UserProfile> entry : profiles.entrySet()) {
                UserProfile p = entry.getValue();
                if (p.activeDates.size() >= 2 && p.loanTypes.size() >= 2) {
                    establishedUsers.add(entry.getKey());
                }
            }
        }

        public List<String> getEstablishedUsers() {
            return new ArrayList<>(establishedUsers);
        }

        // ===================== Part 2: Trust Score =====================

        /**
         * Trust score in [0, 100] for a PENDING transaction.
         * NOTE: pending data must NOT update loanTypes or amount bounds.
         */
        public int trustScore(String userId, String loanType, long requestedAmount) {
            if (!establishedUsers.contains(userId)) {
                return 0; // not established -> no trust
            }
            UserProfile profile = profiles.get(userId);
            int loanTypeScore = profile.loanTypes.contains(loanType) ? 50 : 0;
            int amountScore = amountScore(profile, requestedAmount);
            return loanTypeScore + amountScore;
        }

        /** 50 points inside [min, max]; otherwise lose 1 point per % away from the crossed bound. */
        private static int amountScore(UserProfile profile, long amount) {
            if (amount >= profile.minAmount && amount <= profile.maxAmount) {
                return 50;
            }
            double percentAway;
            if (amount > profile.maxAmount) {
                percentAway = (amount - profile.maxAmount) * 100.0 / profile.maxAmount;
            } else {
                percentAway = (profile.minAmount - amount) * 100.0 / profile.minAmount;
            }
            int penalty = (int) Math.round(percentAway);
            return Math.max(0, 50 - penalty); // clamp: never negative
        }
    }

    /** Thin wrapper matching the original function signature from the prompt. */
    static List<String> findEstablishedUsers(List<String> logA, List<String> logB) {
        return new EstablishedUserService(logA, logB).getEstablishedUsers();
    }
}
```

> 如果题目坚持要 `findEstablishedUsers(logA, logB)` 这个签名,就保留上面的薄封装 —— 对外满足签名,对内 class 持有状态,两头都不耽误。

## 五、测试清单(面试时按这个顺序口头+代码过)

```java
// 1. 题目原例
getEstablishedUsers()                      -> [uuid1, uuid4]

// 2. Trust Score 官方 4 组(uuid4: types {Web,Store}, bounds [100,200])
trustScore("uuid4", "Web",   100) -> 100   // 类型命中 + 区间内
trustScore("uuid4", "Phone", 120) -> 50    // 新类型 + 区间内
trustScore("uuid4", "Store",  80) -> 80    // 类型命中 + 低于下界 20%
trustScore("uuid4", "Phone", 280) -> 10    // 新类型 + 高于上界 40%

// 3. 自造边界(至少口头说到)
trustScore("uuid2", "Store", 500) -> 0     // 两天都有但单一类型:非 established
trustScore("ghost", "Web", 100)   -> 0     // 从没见过的用户
trustScore("uuid4", "Web", 200)   -> 100   // 恰好等于 max:含边界
trustScore("uuid4", "Phone", 1000)-> 0     // 超 400%:半区扣穿归 0,不是负数
trustScore("uuid4", "Phone", 120) -> 50    // 再查一次仍是 50:pending 没污染 profile!
// 4. 脏数据:"garbage" / 少字段 / 金额非数字 -> 跳过不崩
```

## 六、坑清单(按翻车概率排序)

1. **Part 1 不留状态**(#23 现场翻车点)—— 一开始就写 UserProfile + Service,别写一次性函数。
2. **用 pending 交易更新 profile** —— 题目 Clarification 点名禁止;连续两次同样查询结果必须一样,拿这个自测。
3. **除数用错**:低于下界除以 **min**、高于上界除以 **max**("percentage away from the current bound"),别统一除以 max。
4. **忘记 clamp**:扣穿后返回负分 → 总分可能变成 -300;`Math.max(0, ...)`。
5. **边界含不含**:恰好等于 min/max 属于区间内(例 1 的 100 就是 min 本身,得满分)。
6. **Established 判定理解错**:是"总类型数 ≥2",不是"每天 ≥2 种";同一天出现两次不等于两天(用 date 的 **Set** 而不是计数器)。
7. **输出顺序**:示例是首次出现顺序。主动问一句"结果需要排序吗?"——自动判题可能吃 sorted,确认后加一行 `Collections.sort` 即可。
8. **百分比取整**:官方例子都是整百分比;先按 `Math.round` 写,口头说明"如果 spec 要求向下取整我改成 floor"——这就是 Communication 分。
9. 金额类型:例子全是整数,用 `long` 即可;口头补一句"生产上钱用 BigDecimal 或以分为单位的 long"。

## 七、必备 Follow-up 问答

**Q:再来第三天的日志怎么办?**
`ingest(logC)` 直接可用(date 是 Set,天数自然累加),再跑一次 `computeEstablishedUsers()`。这正是 profile 设计的红利 —— 主动展示扩展性。

**Q:established 的判定条件将来会变(比如 ≥3 天、≥5 种类型)?**
把 `2 / 2` 抽成构造器参数或配置,判定逻辑单独一个方法。

**Q:上 production 前改什么?**
解析层与业务层分离(单独 LogParser,可单测);malformed 行进 DLQ 并出 metrics 而不是 stderr;金额用分/BigDecimal;日志文件大到内存放不下就流式逐行处理(本设计天然支持,profile 是增量更新的);established 结果可物化成表供风控实时查;trust score 权重(50/50、1 分每 1%)做成配置以便 A/B 调参。

**Q:为什么用 HashSet 存 dates/types?**
只需要"去重后的个数"和"包含性查询",Set 语义精确匹配,均摊 O(1)。

## 八、60 分钟时间分配

| 时间 | 做什么 |
|---|---|
| 0–7 min | 读题 + 手过示例(uuid2/uuid3 为什么不算,向面试官复述确认) |
| 7–10 min | 讲设计:UserProfile + Service,声明"为后续部分预留状态" |
| 10–25 min | 写 Part 1(解析 + 聚合 + 判定)+ 跑示例 |
| 25–30 min | 读 Part 2,复述计分规则(尤其"1 分每 1%"和"不更新 bounds") |
| 30–45 min | 写 trustScore + 跑官方 4 组 + 自造边界 |
| 45–55 min | Follow-up + production 讨论 |
| 最后 5 min | Buffer / 反问 |
