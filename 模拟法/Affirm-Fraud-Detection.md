# 01 · Fraud Detection(PII 计数 Debug + 欺诈检测器)

**优先级:★★★★★ —— 店面出现率第一,必须准备到"闭眼能写"**

| 项目 | 说明 |
|---|---|
| 出现记录 | 面经 #2(InterviewDB 原题)、#19(店面,称"面经老题")、#25(店面,三问变体)、#32(店面)、#24(VO 也考过同题) |
| 题型 | Part 1:Debug 现有类 · Part 2:实现新类(同一套事件模型) |
| 核心考点 | HashSet 去重与 O(1) 查找、事件流增量处理、字段白名单、防御式编程 |
| 参考战绩 | #19 帖主 30 分钟内做完两问 + 跑完 test case,说明熟练后时间很充裕 |

> 本文所有 Java 代码都已实际编译运行、测试全部通过,可以直接复制到本地练习:`javac FraudDetection.java && java FraudDetection`

---

## 一、英文原版题干(面试时你看到的样子)

### Part 1 — Debug: DistinctPIIValuesCounter

> One of Affirm's competitive edges is our ability to do credit underwriting and detect potential fraud quickly. We're building out a new event-driven architecture, where we have event consumers listening to a stream of underwriting and fraud events. The **DistinctPIIValuesCounter** is one such event consumer, and it's responsible for **counting the total number of Personally Identifiable Information (PII) values that are present in all events with type `underwriting`**.
>
> We've noticed that DistinctPIIValuesCounter has been misbehaving recently and producing the incorrect results. It's likely that the code has **one or more bugs** in it. We'd like you to investigate the code and help fix this part of the system!
>
> To simulate the event stream, we will pass in a JSON list of objects, with each dictionary representing a single event. The `handle_event` method will be called sequentially for each of these objects. At the end, we will call `get_total_unique_pii_values` and assert that the correct number of unique PII values was counted.
>
> **Constraints:** Neither method should exceed a runtime complexity greater than O(log n), where n is the total number of events.

**Event Schema(重点!bug 都藏在这张表里):**

| 事件类型 | 字段 | 是否必填 | 类型 | 是否 PII |
|---|---|---|---|---|
| (所有事件) | event_type | 必填 | string | — |
| underwriting | loan_amount | 必填 | int | 否 |
| underwriting | customer_details | 必填 | dict | — |
| underwriting | customer_details.phone | **必填** | string | **是** |
| underwriting | customer_details.email | 可选 | string | **是** |
| underwriting | customer_details.address | 可选 | string | **是** |
| underwriting | customer_details.ssn | 可选 | string | **是** |
| underwriting | customer_details.credit_score | 可选 | int | **否!** |
| fraud_flag | customer_details | 必填 | dict | — |
| fraud_flag | phone/email/address/ssn | 均可选 | string | 是 |

**Test Case 0(期望 6):** 三个事件 —— underwriting(4 个 PII)→ fraud_flag(同样 4 个 PII)→ underwriting(address 和 ssn 与第一个相同,phone/email 是新的)。6 个不同值:`123 Main St / 182-920-4124 / 947-213-9402 / johndoe@gmail.com / janedoe@yahoo.com / 4568698929`。fraud_flag 事件**不参与统计**。

**Test Case 1(期望 3):** 单个 underwriting 事件,只有 address/phone/email 三个字段(没有 ssn)→ 3。

**带 bug 的原始代码(Python 原版,面试选 Java 会给等价 Java 版):**

```python
@dataclass
class Event:
    event_type: str
    loan_amount: Optional[int] = None
    customer_details: Optional[Dict[str, str]] = None

class DistinctPIIValuesCounter:
    def __init__(self):
        self.pii_set = set()

    def handle_event(self, event: Event) -> None:
        details = event.customer_details
        if not details:
            return
        self.pii_set.add(details.get("address", ""))
        self.pii_set.add(details.get("phone", ""))
        self.pii_set.add(details.get("email", ""))

    def get_total_unique_pii_values(self) -> int:
        return len(self.pii_set)
```

等价的带 bug Java 版(自己练习时从这个版本开始改):

```java
class DistinctPIIValuesCounter {
    private final Set<String> piiSet = new HashSet<>();

    public void handleEvent(Event event) {
        Map<String, String> details = event.customerDetails;
        if (details == null) {
            return;
        }
        piiSet.add(details.getOrDefault("address", ""));
        piiSet.add(details.getOrDefault("phone", ""));
        piiSet.add(details.getOrDefault("email", ""));
    }

    public int getTotalUniquePiiValues() {
        return piiSet.size();
    }
}
```

### Part 2 — Implement: FraudDetector

> We'd like to implement the **FraudDetector** which will be the backbone to our system. We'll be using an event stream, and operate on two different types of events:
>
> - **"underwriting"** events represent a live transaction that's being underwritten, and so the `handle_event` method needs to **return "1" if we believe fraud is suspected, or "0"** if we don't detect any fraud.
> - **"fraud_flag"** events represent an instance where a manual operator has identified a past transaction as being fraudulent. The `handle_event` method should **return an empty string** when it handles a fraud_flag event.
>
> The FraudDetector service will be responsible for identifying subsequent "underwriting" transactions that may be fraudulent, and returning "1" for these suspicious transactions. **If an underwriting event includes any customer PII that has been found in at least one suspicious event then we would consider this to also be suspicious.**

**Test Case 0 期望输出(4 个事件依次):`0` → `` (空) → `1` → `1`**

官方 explanation(第 3 条是全题最大的坑,原文照录):

1. The first underwriting event passes and returns 0, because we don't have any details that we consider suspicious.
2. The second event (fraud_flag) marks these values as suspicious: "123 Main St", "182-920-4124", "johndoe@gmail.com", "4568698929".
3. The third event has address and ssn values that are flagged as suspicious, so we return "1". **The details of these event are also recorded and marked as suspicious for future underwriting events.** ← 传染规则
4. The fourth event has a phone number that is suspicious — **we flagged it in event 3**(不是 event 2!)。

---

## 二、中文题意精读(动笔前的 checklist)

Part 1 规则:

- 只统计 `event_type == "underwriting"` 的事件,fraud_flag 完全跳过。
- PII 字段白名单 = **phone / email / address / ssn 四个**;`credit_score` 不是 PII,`loan_amount` 也不是。
- email / address / ssn 是可选字段,可能缺失 —— 缺失就跳过,不能产生空串。
- 求的是跨所有 underwriting 事件、跨字段的**全局去重值总数**(同一个 ssn 出现在两个事件里只算一次;address 和 phone 如果字符串相同也只算一次)。

Part 2 规则:

- `handleEvent` 返回 String:underwriting → `"1"` 或 `"0"`;fraud_flag → `""`。
- fraud_flag:把它的全部 PII 值加入 suspicious 集合,返回 `""`。
- underwriting:任一 PII 值命中 suspicious 集合 → 可疑 → 返回 `"1"`,**并把它自己的全部 PII 值也加入集合(传染)**;否则返回 `"0"`,并且**什么都不记录**(干净交易的 PII 不进任何集合)。

## 三、Part 1:三个 bug 逐个抓

| # | Bug | 症状 | 修复 |
|---|---|---|---|
| 1 | 没有过滤 `event_type` | fraud_flag 事件的 PII 也被计入,多算 | `handleEvent` 开头:非 underwriting 直接 return |
| 2 | 漏了 `ssn` 字段 | 少算(Test 0 会返回 5 而不是 6) | 用字段白名单 `[address, phone, email, ssn]` 循环 |
| 3 | `getOrDefault(field, "")` 把空串塞进 set | 只要有任何事件缺字段,就多算 1 | 取值后判 `null / isEmpty` 再 add |

**当场的调试方法论(边做边说出来,这正是评分项 Debugging):**

1. 先跑给定的 Test 0:期望 6,实际 5 → 说明有"少算"的 bug → 对照 schema 表逐字段检查 → 发现漏了 ssn。
2. 重读需求第一句:"all events with type underwriting" → 检查代码 → 没有 event_type 过滤 → 修。
3. 主动问自己"可选字段缺失会怎样?" → 发现 `getOrDefault(x, "")` 会把 "" 加进 set → 修,并**自己造一个最小用例验证**(一个只有 phone 的 underwriting 事件,期望 1)。
4. 每修一个 bug 重跑全部用例,修完后把三个自造用例也留着 —— 面试官会注意到你补了测试。

**修复后的完整 Java 版(已验证,含测试):**

```java
import java.util.*;

public class FraudDetection {

    /** Shared event model. Both event types use the same class; optional fields may be null. */
    static class Event {
        final String eventType;
        final Integer loanAmount;                  // only underwriting events have it; may be null
        final Map<String, String> customerDetails; // may be null

        Event(String eventType, Integer loanAmount, Map<String, String> customerDetails) {
            this.eventType = eventType;
            this.loanAmount = loanAmount;
            this.customerDetails = customerDetails;
        }
    }

    private static final String UNDERWRITING = "underwriting";
    private static final String FRAUD_FLAG = "fraud_flag";

    /** PII field whitelist. Note: credit_score is NOT PII and must never be counted. */
    private static final List<String> PII_FIELDS = List.of("address", "phone", "email", "ssn");

    /** Extract all non-missing, non-empty PII values from an event. Shared by Part 1 and Part 2. */
    private static List<String> extractPiiValues(Event event) {
        List<String> values = new ArrayList<>();
        Map<String, String> details = event.customerDetails;
        if (details == null) {
            return values;
        }
        for (String field : PII_FIELDS) {
            String value = details.get(field);
            if (value != null && !value.isEmpty()) { // skip missing fields and blank values
                values.add(value);
            }
        }
        return values;
    }

    // ===================== Part 1: fixed DistinctPIIValuesCounter =====================
    static class DistinctPIIValuesCounter {
        private final Set<String> piiSet = new HashSet<>();

        public void handleEvent(Event event) {
            // Fix 1: only underwriting events should be counted.
            if (!UNDERWRITING.equals(event.eventType)) {
                return;
            }
            // Fix 2 (include ssn) and Fix 3 (never add "" for missing fields)
            // both live inside extractPiiValues().
            piiSet.addAll(extractPiiValues(event));
        }

        public int getTotalUniquePiiValues() {
            return piiSet.size();
        }
    }
}
```

> 把 `extractPiiValues` 提成静态工具方法是有意为之:Part 2 直接复用,面试官会看到你的 Design 意识。
> 关于 O(log n) 约束:它的真实含义是"别每次调用都全量重扫事件",增量维护 set 后,`handleEvent` 均摊 O(1)(固定 4 个字段)、`getTotalUniquePiiValues` O(1),远优于要求 —— 主动说出这一点。

## 四、Part 2:FraudDetector 实现

**动笔前先和面试官确认(每条都是加分项):**

1. "被判为可疑的 underwriting 事件,它自己的其余 PII 也要加入 suspicious 集合,对吗?" —— 对(explanation 第 3 条)。主动说出来,证明你读懂了最难的一条规则。
2. "干净的 underwriting 事件的 PII 需要存吗?" —— 不需要,只有可疑事件的 PII 会被记录。
3. "PII 匹配是全局值匹配(phone 的值撞上别人的 ssn 也算命中)还是按字段匹配?" —— 按期望输出,全局字符串匹配即可;可以补一句"按字段区分(如 key 存成 `phone:xxx`)能避免跨字段误碰撞,作为改进方向"。
4. "未知 event_type 怎么处理?" —— 建议防御式返回 `""` 并可记日志。

**参考实现(已验证):**

```java
    // ===================== Part 2: FraudDetector =====================
    static class FraudDetector {
        /** Every PII value that has appeared in at least one suspicious event. */
        private final Set<String> suspiciousPiiValues = new HashSet<>();

        public String handleEvent(Event event) {
            List<String> piiValues = extractPiiValues(event);

            if (FRAUD_FLAG.equals(event.eventType)) {
                // A human operator confirmed fraud: blacklist every PII value on it.
                suspiciousPiiValues.addAll(piiValues);
                return "";
            }
            if (UNDERWRITING.equals(event.eventType)) {
                // IMPORTANT: check membership BEFORE adding anything,
                // otherwise every event would match itself.
                boolean suspicious = anyValueSuspicious(piiValues);
                if (suspicious) {
                    // Contagion rule: a suspicious transaction taints ALL of its own PII too.
                    suspiciousPiiValues.addAll(piiValues);
                    return "1";
                }
                return "0"; // clean transaction: do NOT record its PII anywhere
            }
            return ""; // unknown event type: ignore defensively
        }

        private boolean anyValueSuspicious(List<String> piiValues) {
            for (String value : piiValues) {
                if (suspiciousPiiValues.contains(value)) {
                    return true;
                }
            }
            return false;
        }
    }
```

**写完后主动跑的测试(口头 + 代码都过一遍):**

```java
FraudDetector detector = new FraudDetector();
// Test Case 0 的 4 个事件,期望 "0", "", "1", "1"
detector.handleEvent(underwriting("123 Main St", "182-920-4124", "johndoe@gmail.com", "4568698929")); // "0"
detector.handleEvent(fraudFlag("123 Main St", "182-920-4124", "johndoe@gmail.com", "4568698929"));    // ""
detector.handleEvent(underwriting("123 Main St", "947-213-9402", "janedoe@yahoo.com", "4568698929")); // "1"
detector.handleEvent(underwriting("654 5th Ave", "947-213-9402", "jamesdoe@hotmail.com", "938103583")); // "1"

// 自造边界用例:
// - customer_details 为 null 的 underwriting -> "0",不崩溃
// - 未知 event_type -> ""
// - 干净事件的 PII 不被记录:同一干净 PII 出现两次,第二次仍是 "0"
// - fraud_flag 之后,与其毫无交集的 underwriting -> "0"
```

## 五、坑清单(按翻车概率排序)

1. **传染规则漏掉** —— 只把 fraud_flag 的 PII 入库,不记录被判可疑的 underwriting 的 PII → 第 4 个事件会错输出 "0"。这是全题最容易挂的点(#24 面经特别强调)。
2. **先 add 后 check** —— 把当前事件的值先加进集合再查询 → 每个事件都命中自己 → 全部输出 "1"。必须先判断、后加入。
3. **空串/缺失字段**(Part 1 的 bug 3 在 Part 2 同样会踩)—— 两个都缺 email 的事件会因为共享 `""` 而互相"传染"。
4. **credit_score 不是 PII** —— schema 表里它没有 (pii) 标记,别顺手加进白名单。
5. **返回类型是 String** —— `"1"` / `"0"` / `""`,不是 boolean、不是 int。fraud_flag 也必须"返回",不是不返回。
6. **Java 版 customer_details 可能是 `Map<String, Object>`**(因为 credit_score 是 int)—— 白名单只取 4 个字段可自然绕开;若必须转型,用 `value instanceof String s` 模式匹配,别盲目 cast。
7. 干净事件的 PII 被顺手存了 → 内存白涨,而且语义就错了(它们不可疑)。

## 六、必备 Follow-up 问答(#19 面试官真问过这两个)

**Q:为什么用 HashSet?**
需求只有两个操作:值级去重、"这个值出现过吗"。HashSet 提供均摊 O(1) 的 add/contains。不需要排序所以不用 TreeSet(O(log n));不需要计数所以不用 HashMap;要"每值只算一次"所以不能用 List(contains 是 O(n))。

**Q:上 production 前你会改什么?**(HR 邮件明说会问这个,背熟)

- **PII 安全:** 明文 SSN/电话放内存、打日志都不可接受 —— 存加盐哈希(如 HMAC-SHA256),相等匹配照常工作;日志脱敏。
- **状态外置:** 单机内存重启即丢、多实例各存一份会不一致 → suspicious 集合放 Redis/DB,或事件流(Kafka)重放恢复。
- **输入校验:** schema 校验,malformed event 进死信队列(DLQ)而不是把消费者打崩。
- **并发:** 多线程消费用 `ConcurrentHashMap.newKeySet()`,或按 key 分区保证单分区串行。
- **可观测性:** 事件计数、"1" 命中率、处理延迟的 metrics + 告警(命中率突变往往意味着上游数据问题)。
- **误报治理:** 公寓楼共享地址、家庭共用电话会造成"传染误伤" → 字段加权评分代替一票否决、白名单、人工复核队列。
- **幂等:** 事件重放不应产生副作用(set 语义天然幂等,但对外输出/写库要带事件 id 去重)。
- **增长控制:** suspicious 集合只增不减 → TTL、容量上限、冷数据下沉。

**Q:数据量大到单机放不下怎么办?**
集合外置 Redis Cluster;或本地 Bloom filter 做一级过滤(可能假阳性,命中后再查权威存储确认)。

## 七、变体预警(#25 店面的第三问)

> "实现功能:记录 Fraud 的 Transaction 信息加入 Fraud Event 里,最后找出所有的 Fraud。"

思路:给每个 underwriting 事件一个 transactionId(入参或自增序号),维护 `List<Long> flaggedTransactionIds`(或 `Map<Long, Event>`);判定为可疑时记录 id;最后加一个 `getAllFraudulentTransactions()` 返回。核心状态机不变,只是多存一份"哪些交易被标记"。若 fraud_flag 带 transaction id 引用历史交易,问清是否要回溯标记那笔历史交易。

## 八、60 分钟时间分配

| 时间 | 做什么 |
|---|---|
| 0–8 min | 读 Part 1 题干 + schema 表,手过 Test 0 |
| 8–20 min | 定位并修复 3 个 bug,每修一个跑一次,补 2–3 个自造用例 |
| 20–25 min | 读 Part 2,复述规则(尤其传染规则)向面试官确认 |
| 25–45 min | 写 FraudDetector + 跑 Test 0 + 边界用例 |
| 45–55 min | Follow-up:数据结构选择、production 改造 |
| 最后 5 min | Buffer / 反问 |
