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

## 七、必备 Follow-up 问答(零基础详解版)

这道题代码本身不难,写得快会剩下 15~20 分钟——**这段时间的 follow-up 表现基本决定评级**。追问主轴就四个方向:**数据变多**(第三天、大文件、亿级)、**规则变化**(阈值、权重)、**钱的敏感性**(金额精度、脏行),以及**上线服务化**(物化、配置、A/B)。每问配英文问法和可照着说的回答脚本。

### Q1:再来第三天的日志怎么办?

面试官的英文问法:"What if we get a third day of logs?" / "How does this extend?"

**为什么我们的设计天然支持:**关键在 Part 1 建模时就没把"两个文件"写死。反面教材是最直觉的写法——"文件 A 的用户集合 ∩ 文件 B 的用户集合"(`usersInA.retainAll(usersInB)`):它把**输入的形态**(恰好两个文件)编码进了逻辑,第三个文件一来就要推倒重写。我们的写法是把**规则的语义**("在 ≥2 个不同日期活跃")建模成 `activeDates` 这个 Set——第三天就是再调一次 `ingest(logC)`、重跑一次 `computeEstablishedUsers()`,核心逻辑一行不改。通用面试心法:**按语义建模,不按输入形态建模。**

**English 回答脚本:**
> "The design already supports it — I modeled 'days active' as a set of dates rather than 'appears in file A and file B', so a third file is just another ingest call, and the rule keeps working off the count of distinct dates. That's exactly why I avoided the set-intersection approach: it hard-codes the shape of the input instead of the meaning of the rule."

### Q2:established 的判定条件将来会变怎么办?(比如 ≥3 天、≥5 种类型)

面试官的英文问法:"What if the definition of 'established' changes?"

**背景概念——魔法数字(magic number):**直接写死在代码里的 `2`,三个月后没人记得它是什么意思、敢不敢改。规则里的数字应该抽成**有名字的参数**:构造器参数 `minActiveDays` / `minLoanTypes`,判定收进一个 `isEstablished(profile)` 方法。再进一步放**配置中心**(公司里专门存"可调参数"的服务),风控团队改配置即生效,不改代码、不发版。trust score 的 50/50 和"1 分每 1%"同理——**它们是产品旋钮,不是代码**。

```java
public EstablishedUserService(List<String> logA, List<String> logB,
                              int minActiveDays, int minLoanTypes) { ... }

private boolean isEstablished(UserProfile p) {
    return p.activeDates.size() >= minActiveDays && p.loanTypes.size() >= minLoanTypes;
}
```

**English 回答脚本:**
> "I'd pull the thresholds out as constructor parameters or config — minDays and minLoanTypes — and keep the rule in a single isEstablished method. Ideally they live in a config service so the risk team can tune them without a deploy. Same for the scoring weights: the 50/50 split and one-point-per-percent are product knobs, not code."

### Q3:为什么用 HashSet 存 dates/types?为什么金额只存 min/max 两个数?

面试官的英文问法:"Why sets? And why keep only min and max?"

**Set 的部分:**规则要的是"**不同**天数 ≥ 2"和"这个类型**见过没有**"——正好是 Set 的两个本能:自动去重、O(1) 包含性查询。用 List 会埋一个无声 bug:同一天两笔交易,List 里存两条 `"2025-01-01"`,数 `size()` 得 2 天,用户被错判为 established;Set add 两次还是一个,天然正确。

**min/max 的部分(主动说出来是亮点):**金额只存两个 long,因为规则只需要**边界**。这叫**可增量聚合**——每来一条日志 O(1) 更新、每用户 O(1) 空间,不必保留任何历史金额。值得补一句:**这个选择是跟着规则走的**——如果哪天规则改成"金额中位数"或"P95",min/max 就不够了,因为分位数不可增量(要么保留全部数据,要么用 t-digest 这类专门估算分位数的近似结构)。能说出"聚合结构的选择取决于规则需要什么",就是数据工程的真功夫。

**English 回答脚本:**
> "Sets give me exactly the semantics the rule needs: a distinct-day count and a 'have I seen this loan type' check. With a list, two transactions on the same day would silently count as two days. For amounts I keep only min and max, because the rule only needs the bounds — that's an O(1)-space, O(1)-update aggregate. If the rule ever asked for a median or a P95, I'd switch to something like a t-digest — the aggregate follows the rule."

### Q4:金额处理有什么要注意的?(钱和 double 的经典问题)

面试官的英文问法:"Any concerns with how you're handling the amounts?"

**背景概念——为什么 double 存不准钱:**double 是**二进制**小数。十进制的 0.1 换成二进制是无限循环小数(0.000110011…),64 位装不下只能截断,存进去的是**最接近的近似值**。误差平时看不见,一做运算就现形。这不是理论,本机 JDK 21 实跑结果:

```java
System.out.println(0.1 + 0.2);        // 0.30000000000000004
System.out.println(0.1 + 0.2 == 0.3); // false
new BigDecimal("0.1").add(new BigDecimal("0.2"))  // 0.3(精确)
```

钱差一分就是对账事故。行业规矩两条路:**用最小货币单位的整数**(以"分"为单位的 long——本题样例全是整数,long 直接够),或 **BigDecimal**(按十进制存、绝对精确,代价是慢和啰嗦,常用在系统出入口)。

**那我们代码里的 double 百分比呢?**没问题,但要能说明白:`percentAway` 只用于算扣分,最后 `Math.round` 到整数分,double 的误差量级(约 1e-15)远小于 0.5 分的取整档位,不影响结果;取整用 round 还是 floor 是产品决定,主动和面试官确认——这句本身就是加分点。

**English 回答脚本:**
> "The sample amounts are integers, so I used long. I'd avoid double for money entirely — binary floating point can't represent decimals exactly; the classic demo is 0.1 plus 0.2 printing 0.30000000000000004. In production I'd store cents as a long, or use BigDecimal at the boundaries. Using double just for the percentage penalty is fine because we round to whole points and the error is many orders of magnitude smaller than the rounding step — but I'd confirm the rounding rule."

### Q5:日志文件大到内存放不下怎么办?

面试官的英文问法:"What if the log file doesn't fit in memory?"

**背景概念——先分清两种"放不下"。**把处理过程想成水管接水缸:日志行是**流过水管的水**,profiles 是**沉淀在水缸里的东西**。

- **水太多(日志行数多):**完全没问题。profile 本来就是逐行增量更新的,把 `List<String>` 换成 `BufferedReader` 逐行流式读,任一时刻内存里只有"当前一行 + 水缸",与文件大小无关。这是本设计免费送的能力,主动说。
- **缸太小(用户数多到几千万个 profile 都装不下):**这才是真问题,升级到 Q6。

**English 回答脚本:**
> "Structurally nothing changes — profiles are built incrementally line by line, so I'd swap the in-memory list for a buffered reader and stream the file. Memory is bounded by the number of distinct users, not the number of log lines. If even the profiles don't fit, that's a different problem — then I'd partition by user."

**追问澄清一:缸太小时,能不能"把 HashMap 换成 Redis 或数据库"?**

不能——这是最常见的错误直觉。中间聚合意味着**每条日志**都要对某个 userId 做一次"读出来 → 改一下 → 写回去":

- **Redis**:几亿条日志 = 几亿次网络往返,QPS 和延迟先爆。Redis 适合放**最终结果**的热缓存,不适合当高频增量聚合的中间状态。
- **PostgreSQL**:同理且更慢,每条日志触发一次数据库读写完全不现实。它适合存**最终物化结果**(见 Q7)。

正确做法:**聚合永远发生在本地内存里**——数据按 userId 分区到多台机器,每台只在自己内存里维护自己那份用户的 HashMap,聚合完成后一次性把**结果**批量写回持久化存储。一句话:**中间状态留在内存,只有最终结果才落库。**

### Q6:每天几亿条日志、几千万用户,怎么算?

面试官的英文问法:"How would you scale this to hundreds of millions of lines?"

**背景概念——按 key 分组的分而治之。**像邮局分拣:先按省份把信分堆,每个省的分拣员只处理自己那堆,互不打扰。这里"省份" = `hash(userId) % N`:日志按 userId 哈希切成 N 份 → **同一个用户的所有行必然落进同一份** → N 台机器各自独立跑我们这套 profile 聚合 → 各自输出自己那份用户的 established 名单 → 结果**直接拼接**,不需要任何跨机器合并。这正是 MapReduce / Spark `groupBy` 的思想;这题特别适合,因为 per-user 聚合量是常数大小(两个 Set + 两个 long)。

数据已经在数仓里时,一句 SQL 就是答案(风控同事真的这么跑):

```sql
SELECT user_id
FROM loan_logs
GROUP BY user_id
HAVING COUNT(DISTINCT log_date)  >= 2
   AND COUNT(DISTINCT loan_type) >= 2;
```

**一个显深度的对比:**这题能"切完各算各的",是因为规则**只依赖单个用户自己的数据**。如果规则含全局信息(比如"金额排进全站前 1%"),单靠分区不够,需要两阶段(先各自算局部,再汇总全局)。主动指出这个边界,说明你真理解了为什么能并行。

**English 回答脚本:**
> "This aggregation is embarrassingly parallel: hash-partition the logs by userId so all of a user's lines land on the same worker; each worker builds its profiles independently and the outputs just concatenate — no cross-worker merge, because the rule only depends on each user's own data. That's the standard MapReduce groupBy pattern. And if the data already sits in a warehouse, it's a four-line SQL with GROUP BY and HAVING on two COUNT DISTINCTs."

**追问澄清二:分区(partitioning)≠ 负载均衡(Load Balancer)。**

Load Balancer 的目标是"把请求摊匀",通常轮询分发——同一个用户的两条日志可能被发到不同机器,那样每台机器都只有半份数据,聚合就错了。数据分区的目标是**亲和性**:`hash(userId) % N` 保证同一个用户的所有日志**必然**落到同一台机器,摊匀只是副产品,"**同 key 同机**"才是本质。

**追问澄清三:某台机器宕机了怎么办?**

单点故障在生产不可接受,但这些细节**框架(Spark / Flink)已经封装好**,业务代码基本不用自己写,面试说出机制即可:

- **数据有副本**:输入数据在存储层(HDFS / S3)本来就存多份,某台机器挂了,换一台重读它负责的那个分片即可;
- **计算可恢复**:定期把中间状态刷盘(**checkpoint**,Flink 的招牌),挂了从上一个 checkpoint 接着算;Spark 则靠 **lineage**(记录"这份数据是从哪一步算出来的"),丢了哪个分片就按谱系从源头重算哪个分片。

### Q6.5(关键澄清):生产链路全景 —— Spark 到底怎么用?Part 1 和 Part 2 各在哪跑?

面试官的英文问法:"Walk me through how this would actually run in production."

**最重要的一个纠偏:不是在原来的 Java 代码里把 HashMap 换成什么"MapReduce 类"。**我们写的 Java 代码是本地实现(数据装得进内存时完全够用);到了生产规模,聚合这件事**整个搬出去**,变成一个定时批处理任务,和线上服务彻底分开:

```text
日志 → S3(对象存储,先落盘)
     → Spark / MapReduce 周期性 batch job(每小时或每天跑一次;分区、聚合、容错全由框架自动完成)
     → 结果批量写入 PostgreSQL 物化表(每用户一行:活跃天数、loanTypes、min/max、established 标志)
     → 线上实时 Java 服务查这张表,毫秒级算出 trust score
```

分工按 Part 拆开记:

- **Part 1(聚合)= 离线批处理。**Spark job 周期性重算,结果物化进 Postgres。它**不是"一直在线的实时大 HashMap"**,而是定时跑完就退出的任务。
- **Part 2(算分)= 在线实时服务。**普通 Java 服务,收到 (userId, loanType, amount) 就查物化表、按规则算分返回——完全不碰 Spark。**pending 交易绝不回写物化表**(题目 Clarification 的生产版)。
- **要更新鲜的 profile 怎么办:**把 batch 换成 **Flink 流式任务**——日志一条条流进来、增量聚合、upsert 进 Postgres。架构从"定时全量"变"持续增量",查询侧完全不用改。

**English 回答脚本:**
> "In production I'd split this by part. Aggregation runs as a scheduled Spark batch job: logs land in S3, the job partitions by userId, builds each user's profile — distinct days, loan types, min and max — and bulk-writes a materialized table in Postgres. The online side is a plain Java service that computes the trust score by reading that table in milliseconds, and never writes pending data back. If we needed fresher profiles, I'd swap the batch job for a Flink streaming job doing incremental upserts — the serving path doesn't change."

### Q7:上 production 前改什么?(汇总清单)

面试官的英文问法:"What would you change before shipping this?"

金额(Q4)、规模(Q5/Q6)、配置化(Q2)前面已覆盖,剩下按三个桶:

- **数据质量:**解析层单独成类(`LogParser`,可单测);malformed 行不打 stderr,进死信通道并出**脏行率**指标 + 告警(概念同 01 文档的 DLQ 一节)。**脏行率 = malformed 行数 ÷ 总行数**,是持续采集的监控指标——突然升高 = 上游日志格式八成变了,立刻排查。
- **服务化:**established 名单 + 每用户的 loanTypes/min/max **物化**成 **PostgreSQL 表(注意:不是 Redis)**。"物化"= 把每次要现算的结果**提前算好、存成一张表**,放款决策路径毫秒级查表,而不是每次全量扫日志重算;定时任务(每日/每小时)刷新。**为什么选 Postgres 不选 Redis:**这张表需要持久化、批量刷新、被决策路径当真相源直接查——Redis 是内存缓存,擅长热数据,不适合当持久真相源;最多在 Postgres 前面再加一层 Redis 热缓存。这和 01 文档 Part 2 的 read-optimized view 是同一个思想。
- **可调可实验:**阈值和评分权重进配置且**版本化**;做 **A/B 测试**(把用户分两组用不同权重,比较放款率/坏账率,用数据定参数)时只改配置不发版。
- **可观测:**每日处理行数、脏行率、established 用户数及环比(突然掉一半 = 上游丢数据)。

**English 回答脚本(挑三条说透):**
> "Three buckets. Data quality: a separate, unit-tested parser, and malformed lines go to a dead-letter path with a bad-line-rate metric instead of stderr. Serving: materialize the established list into a table the decision path can query in milliseconds, refreshed on a schedule, instead of rescanning logs. And tunability: thresholds and score weights live in versioned config so the risk team can tune or A/B them without a deploy. Plus the money handling we discussed — cents as longs, never double."

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
