# 01 · Fraud Detection(PII 计数 Debug + 欺诈检测器)

**优先级:★★★★★ —— 店面出现率第一,必须准备到"闭眼能写"**

| 项目 | 说明 |
|---|---|
| 出现记录 | 面经 #2(InterviewDB 原题)、#19(店面,"面经老题")、#25(店面,三问变体)、#32(店面)、#24(VO 也考过同题) |
| 题型 | Part 1:Debug 现有类 · Part 2:实现新类(同一套事件模型) |
| 核心考点 | HashSet 去重与 O(1) 查找、事件流增量处理、字段白名单、防御式编程 |
| 参考战绩 | #19 帖主 30 分钟内做完两问 + 跑完 test case,说明熟练后时间很充裕 |

> 本文所有 Java 代码都已实际编译运行、测试全部通过;第三节的"修复路径 × 测试矩阵"里每一个数字都是把对应版本的代码真跑出来的结果,不是推测。

---

## 一、英文原版题干(面试页面还原)

### Part 1 — Debug: DistinctPIIValuesCounter(HackerRank 页面原样)

**Description**

One of Affirm's competitive edges is our ability to do credit underwriting and detect potential fraud quickly. We've put a great deal of engineering work into optimizing every part of the process, and our merchant partners rely on us to reliably produce a decision in seconds.

We're building out a new event-driven architecture, where we have event consumers listening to a stream of underwriting and fraud events. The `DistinctPIIValuesCounter` is one such event consumer, and it's responsible for **counting the total number of Personally Identifiable Information (PII) values that are present in all events with type `underwriting`**.

We've noticed that `DistinctPIIValuesCounter` has been misbehaving recently and producing the incorrect results. It's likely that the code has **one or more bugs** in it. We'd like you to investigate the code and help fix this part of the system!

To simulate the event stream, we will pass in a JSON list of objects, with each object representing a single event in the stream. The `handleEvent` method will be called sequentially for each of these objects. At the end, we will call the `getTotalUniquePiiValues` function and assert that the correct number of unique PII values was counted.

**Example (Test Case 0)**

```json
[
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "123 Main St",
            "phone": "182-920-4124",
            "email": "johndoe@gmail.com",
            "ssn": "4568698929"
        },
        "loan_amount": 3000
    },
    {
        "event_type": "fraud_flag",
        "customer_details": {
            "address": "123 Main St",
            "phone": "182-920-4124",
            "email": "johndoe@gmail.com",
            "ssn": "4568698929"
        }
    },
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "123 Main St",
            "phone": "947-213-9402",
            "email": "janedoe@yahoo.com",
            "ssn": "4568698929"
        },
        "loan_amount": 3000
    }
]
```

Expected result:

```java
assert eventConsumer.getTotalUniquePiiValues() == 6;
```

*Explanation: There are 6 distinct PII values across all underwriting type events: `123 Main St` / `182-920-4124` / `947-213-9402` / `johndoe@gmail.com` / `janedoe@yahoo.com` / `4568698929`*

**Example (Test Case 1)**

```json
[
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "123 Main St",
            "phone": "182-920-4124",
            "email": "johndoe@gmail.com"
        },
        "loan_amount": 3000
    }
]
```

Expected result:

```java
assert eventConsumer.getTotalUniquePiiValues() == 3;
```

**Function Description:** Complete the methods `handleEvent` and `getTotalUniquePiiValues` in the editor.

**Returns:** int — the total number of unique PII values across all `underwriting` type events

**Constraints:** Neither method should exceed a runtime complexity greater than O(log n), where n is the total number of events.

**Event Schema**

| Required | Field - Type |
|---|---|
| yes | event_type - string |

**underwriting Event Schema:**

| Required | Field - Type |
|---|---|
| yes | event_type - str: "underwriting" |
| yes | loan_amount - int |
| yes | customer_details - dict |
| yes | customer_details.phone - string **(pii)** |
| no | customer_details.email - string **(pii)** |
| no | customer_details.address - string **(pii)** |
| no | customer_details.ssn - string **(pii)** |
| no | customer_details.credit_score - int |

**fraud_flag Event Schema:**

| Required | Field - Type |
|---|---|
| yes | event_type - str: "fraud_flag" |
| yes | customer_details - dict |
| no | customer_details.phone - string **(pii)** |
| no | customer_details.email - string **(pii)** |
| no | customer_details.address - string **(pii)** |
| no | customer_details.ssn - string **(pii)** |

**Existing code with bugs(Java 版 —— 你选 Java 就会拿到这种版本,与面经 Python 原版一比一对应、保留同样的 bug):**

```java
import java.util.*;

class Event {
    String eventType;
    Integer loanAmount;                  // may be null
    Map<String, String> customerDetails; // may be null

    Event(String eventType, Integer loanAmount, Map<String, String> customerDetails) {
        this.eventType = eventType;
        this.loanAmount = loanAmount;
        this.customerDetails = customerDetails;
    }
}

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

编辑器里还会有一段标着 "do not modify" 的 harness:读 stdin 的 JSON → 构造 Event → 循环调用 `handleEvent` → 打印/断言计数。那部分不用你动,你只改 `DistinctPIIValuesCounter`。

**面经收录的 Python 原版(出处对照用,bug 完全相同):**

```python
from dataclasses import dataclass
from typing import Dict, Optional

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

**中文对照 schema(合并版):**

| 事件类型 | 字段 | 是否必填 | 类型 | 是否 PII |
|---|---|---|---|---|
| (所有事件) | event_type | 必填 | string | — |
| underwriting | loan_amount | 必填 | int | 否 |
| underwriting | customer_details | 必填 | dict | — |
| underwriting | customer_details.phone | 必填 | string | 是 |
| underwriting | customer_details.email | 可选 | string | 是 |
| underwriting | customer_details.address | 可选 | string | 是 |
| underwriting | customer_details.ssn | 可选 | string | 是 |
| underwriting | customer_details.credit_score | 可选 | int | **否!** |
| fraud_flag | customer_details | 必填 | dict | — |
| fraud_flag | phone/email/address/ssn | 均可选 | string | 是 |

### Part 2 — Implement: FraudDetector(HackerRank 页面原样)

**Description**

We'd like to implement the **FraudDetector** which will be the backbone to our system.

We'll be using an event stream, and operate on two different types of events: "underwriting" events, and "fraud_flag" events, following the schema below *(schema 与 Part 1 完全相同,即上面那两张 underwriting / fraud_flag 表)*:

- **"underwriting"** events represent a live transaction that's being underwritten, and so the `handleEvent` method needs to **return "1"** if we believe fraud is suspected, **or "0"** if we don't detect any fraud.
- **"fraud_flag"** events represent an instance where a manual operator has identified a past transaction as being fraudulent. The `handleEvent` method should **return an empty string** when it handles a fraud_flag event.

The FraudDetector service will be responsible for identifying subsequent "underwriting" transactions that may be fraudulent, and returning "1" for these suspicious transactions. **If an underwriting event includes any customer PII that has been found in at least one suspicious event then we would consider this to also be suspicious.**

**Example / Test Case 0**

```json
[
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "123 Main St",
            "phone": "182-920-4124",
            "email": "johndoe@gmail.com",
            "ssn": "4568698929"
        },
        "loan_amount": 3000
    },
    {
        "event_type": "fraud_flag",
        "customer_details": {
            "address": "123 Main St",
            "phone": "182-920-4124",
            "email": "johndoe@gmail.com",
            "ssn": "4568698929"
        }
    },
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "123 Main St",
            "phone": "947-213-9402",
            "email": "janedoe@yahoo.com",
            "ssn": "4568698929"
        },
        "loan_amount": 3000
    },
    {
        "event_type": "underwriting",
        "customer_details": {
            "address": "654 5th Ave",
            "phone": "947-213-9402",
            "email": "jamesdoe@hotmail.com",
            "ssn": "938103583"
        },
        "loan_amount": 9000
    }
]
```

Expected results:

```text
0   # no fraud detected
    # not an underwriting event, return blank line
1   # suspicious underwriting event!
1   # suspicious underwriting event!
```

判题方式:harness 对每个事件调用一次 `handleEvent`,把返回的字符串**原样打印成一行**。fraud_flag 返回 `""`,所以第 2 行是空行。`#` 后面是题面给的注释,实际比对的只有 `0` / 空行 / `1` / `1` 这四行。注意 Part 2 的 Test Case 0 比 Part 1 **多了第 4 个事件**(654 5th Ave 那条),它就是专门验证传染规则的。

**Explanation(原文照录,第 3 条是全题最大的坑):**

1. The first underwriting event passes and returns False, because we don't have any details that we consider suspicious *(原文笔误:实际返回的是字符串 "0")*
2. The second event marks the following values as being suspicious, and the service records these values for future reference: "123 Main St", "182-920-4124", "johndoe@gmail.com", "4568698929"
3. The third event has address and ssn values that are flagged as suspicious, and so we return "1". **The details of these event are also recorded and marked as suspicious for future underwriting events** ← 传染规则
4. The fourth event has a phone number that is suspicious (**we flagged it in event 3**,不是 event 2!)

**Starter code(Java 版 —— 你选 Java 拿到的样子,Event 类与 Part 1 相同):**

```java
import java.util.*;

class Event {
    String eventType;
    Integer loanAmount;                  // may be null
    Map<String, String> customerDetails; // may be null

    Event(String eventType, Integer loanAmount, Map<String, String> customerDetails) {
        this.eventType = eventType;
        this.loanAmount = loanAmount;
        this.customerDetails = customerDetails;
    }
}

class FraudDetector {

    public FraudDetector() {
    }

    public String handleEvent(Event event) {
        return "";
    }
}
```

同样有一段 "do not modify" 的 harness:读 stdin JSON → 构造 Event → 依次调用 `handleEvent` → 每次的返回值打印一行。你只填 `FraudDetector`。

**面经收录的 Python 原版 starter(出处对照用;注意原版方法名就叫 handleEvent,不是 handle_event):**

```python
@dataclass
class Event:
    event_type: str
    loan_amount: Optional[int]
    customer_details: Optional[Dict[str, str]]

class FraudDetector:
    def __init__(self):
        return

    def handleEvent(self, event: Event) -> str:
        return ""
```

---

## 二、中文题意精读(动笔前的 checklist)

**读题时要抓死的三句话 —— 一句对应一个 bug:**

1. Description 里 "present in all events **with type underwriting**" → 统计范围只有 underwriting,fraud_flag 完全不算 →(Bug ①)。
2. Schema 表里标 **(pii)** 的字段恰好四个:phone、email、address、**ssn**;credit_score 没有 (pii) 标记 →(Bug ②)。
3. Schema 表里 email/address/ssn 的 Required 是 **no** → 这些 key 可能整个不存在,取不到要跳过而不是给默认值 →(Bug ③)。

Part 1 规则:

- 只统计 `event_type == "underwriting"` 的事件,fraud_flag 完全跳过。
- PII 字段白名单 = **phone / email / address / ssn 四个**;`credit_score` 不是 PII,`loan_amount` 也不是。
- email / address / ssn 是可选字段,可能缺失 —— 缺失就跳过,不能产生空串。
- 求的是跨所有 underwriting 事件、跨字段的**全局去重值总数**(同一个 ssn 出现在两个事件里只算一次;address 和 phone 如果字符串相同也只算一次)。
- O(log n) 约束的真实含义:别在读取时全量重扫事件 —— 用成员 set 增量维护后,`handleEvent` 每次最多碰 4 个字段是 O(1),`getTotalUniquePiiValues` 是 O(1),优于要求,主动说出来。

Part 2 规则:

- `handleEvent` 返回 String:underwriting → `"1"` 或 `"0"`;fraud_flag → `""`。
- fraud_flag:把它的全部 PII 值加入 suspicious 集合,返回 `""`。
- underwriting:任一 PII 值命中 suspicious 集合 → 可疑 → 返回 `"1"`,**并把它自己的全部 PII 值也加入集合(传染)**;否则返回 `"0"`,并且**什么都不记录**(干净交易的 PII 不进任何集合)。

## 三、Part 1:三个 bug 逐个抓(讲解 + 修复全过程)

| # | Bug | 症状 | 修复 |
|---|---|---|---|
| 1 | 没过滤 `event_type` | fraud_flag 里的 PII 被计入,多算 | `handleEvent` 开头:非 underwriting 直接 return |
| 2 | 漏了 `ssn` 字段 | 少算(Test 0 返回 5 而不是 6) | 用字段白名单 `[address, phone, email, ssn]` 循环 |
| 3 | `getOrDefault(x, "")` 把空串塞进 set | 只要有任何事件缺字段,总数凭空多 1 | 取值后判 `null / isEmpty` 再 add |

**逐个细讲:**

- **Bug ①(没过滤 event_type):**`handleEvent` 从头到尾没看过 `event.eventType`,fraud_flag 事件的 PII 照收不误。狡猾之处:Test 0 里那条 fraud_flag 的四个值和第 1 条 underwriting 完全相同,set 去重后毫无痕迹 —— **两个官方测试都抓不到这个 bug**,只能靠重读题干发现,或自造用例暴露(见下面的 Case A)。
- **Bug ②(漏了 ssn):**代码只 add 了 address、phone、email 三行,ssn 从没被读过。这是 Test 0 返回 5 而不是 6 的直接原因 —— 差的那 1 个就是 `4568698929`。
- **Bug ③(缺失字段塞空串):**`getOrDefault(field, "")` 在 key 缺失时把 `""` 加进 set。多个事件缺字段也只共享同一个 `""`,所以症状是"总数凭空多 1"。原始代码上它暂时没发作(Test 1 三个 key 都在),但你修 Bug ② 时若照抄这个风格写 `getOrDefault("ssn", "")`,Test 1 立刻从 3 变 4。

**当场的调试方法论(边做边说出来,这正是评分项 Debugging):**

1. 先跑给定的 Test 0:期望 6,实际 5 → 说明有"少算"的 bug → 把 set 里的 5 个值和 explanation 里的 6 个值对一下,缺 `4568698929` → 对照 schema 表 → 漏了 ssn。
2. 修 ssn 时别照抄 `getOrDefault` 风格,否则 Test 1 变 4 → 顺势把整段改成"白名单循环 + 判空跳过",根治 Bug ③。
3. 两灯全绿后别停:重读需求第一句 "all events with type underwriting" → 发现没有 event_type 过滤 → 修,并**自己造最小用例验证**(官方测试对这个 bug 是瞎的)。
4. 每修一个 bug 重跑全部用例,修完后把自造用例留着 —— 面试官会注意到你补了测试。

**修复路径 × 测试矩阵(每个数字都是对应版本代码实际运行的结果):**

自造用例定义:

```json
Case A(期望 1):underwriting 的 phone 是唯一 PII;fraud_flag 带一个独有 phone,不应被计入
[
    {"event_type": "underwriting", "customer_details": {"phone": "111-111-1111"}, "loan_amount": 500},
    {"event_type": "fraud_flag",   "customer_details": {"phone": "999-999-9999"}}
]

Case B(期望 1):单条 underwriting,只有 phone,其余 3 个可选字段全缺
[
    {"event_type": "underwriting", "customer_details": {"phone": "111-111-1111"}, "loan_amount": 500}
]
```

| 代码状态 | Test 0(期望 6) | Test 1(期望 3) | Case A(期望 1) | Case B(期望 1) |
|---|---|---|---|---|
| V0 原始 buggy 版 | **5 ✗** | 3 ✓(碰巧) | **3 ✗**("" 和 999 都混入) | **2 ✗**("" 混入) |
| V1 = V0 + 一行 `getOrDefault("ssn","")` | 6 ✓ | **4 ✗**("" 混入) | **3 ✗** | **2 ✗** |
| V2 = 白名单循环 + 判空(修完②③) | 6 ✓ | 3 ✓ | **2 ✗**(999 仍混入) | 1 ✓ |
| V3 = V2 + event_type 过滤(最终版) | 6 ✓ | 3 ✓ | 1 ✓ | 1 ✓ |

看这张表能记住全题精髓:官方两条测试只能把你推到 V2;**V2 → V3 的最后一步只能靠读题 + 自造 Case A**,而这一步恰恰是面试官最想看到的。

**修复后的答案(面试交付版 —— Part 1 单独作答就写这个):**

```java
class DistinctPIIValuesCounter {

    /** PII field whitelist straight from the schema table. credit_score is NOT PII. */
    private static final List<String> PII_FIELDS = List.of("address", "phone", "email", "ssn");

    private final Set<String> piiSet = new HashSet<>();

    public void handleEvent(Event event) {
        // Fix 1: only events with type "underwriting" are counted.
        if (!"underwriting".equals(event.eventType)) {
            return;
        }
        Map<String, String> details = event.customerDetails;
        if (details == null) {
            return;
        }
        // Fix 2: iterate the full PII whitelist, including ssn.
        for (String field : PII_FIELDS) {
            String value = details.get(field);
            // Fix 3: a missing/blank field contributes nothing (never add "").
            if (value != null && !value.isEmpty()) {
                piiSet.add(value);
            }
        }
    }

    public int getTotalUniquePiiValues() {
        return piiSet.size();
    }
}
```

**进入 Part 2 后的重构版(把 PII 提取抽成共用工具 —— 当第二问出现时做这个重构,本身就是 Design 加分动作):**

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

## 四、Part 2:FraudDetector 实现

**动笔前先和面试官确认(每条都是加分项):**

1. "被判为可疑的 underwriting 事件,它自己的其余 PII 也要加入 suspicious 集合,对吗?" —— 对(explanation 第 3 条)。主动说出来,证明你读懂了最难的一条规则。
2. "干净的 underwriting 事件的 PII 需要存吗?" —— 不需要,只有可疑事件的 PII 会被记录。
3. "PII 匹配是全局值匹配(phone 的值撞上别人的 ssn 也算命中)还是按字段匹配?" —— 按期望输出,全局字符串匹配即可;可以补一句"按字段区分(如 key 存成 `phone:xxx`)能避免跨字段误碰撞,作为改进方向"。
4. "未知 event_type 怎么处理?" —— 建议防御式返回 `""` 并可记日志。

**规则的人话版(先建立直觉再写码):**

整个服务就是维护一个 **"PII 黑名单"集合**(`suspiciousPiiValues`),里面装的是"出过事"的具体值(某个地址、某个电话、某个 SSN……):

- **fraud_flag(人工实锤的欺诈)**:不用判定,把它带的所有 PII 值直接拉黑,返回 `""`。
- **underwriting(待审批的新交易)**:拿自己的 PII 挨个查黑名单 —— 一个都查不到 → 干净,返回 `"0"`,**什么都不记**;查到任何一个 → 可疑,返回 `"1"`,**并把自己带的全部 PII 也拉黑(传染)**。
- **为什么要传染**:欺诈者惯用"老 SSN 配新电话、新邮箱"换壳作案。事件 3 用了被实锤的 address+ssn,那它带来的新 phone/email 八成也是欺诈者的马甲 —— 一并拉黑,才能在事件 4 换壳出现时抓住它。

**Test Case 0 逐事件 trace(黑名单集合的变化)。先给值起代号:**

| 代号 | 值 |
|---|---|
| A1 / P1 / E1 / S1 | 123 Main St / 182-920-4124 / johndoe@gmail.com / 4568698929 |
| P2 / E2 | 947-213-9402 / janedoe@yahoo.com |
| A2 / E3 / S2 | 654 5th Ave / jamesdoe@hotmail.com / 938103583 |

| 事件 | 类型 | 携带 PII | 处理前黑名单 | 判定过程 | 返回 | 处理后黑名单 |
|---|---|---|---|---|---|---|
| 1 | underwriting | A1 P1 E1 S1 | ∅ | 4 个值都不在黑名单 → 干净 | `"0"` | ∅(**干净交易不记录!**) |
| 2 | fraud_flag | A1 P1 E1 S1 | ∅ | 不判定,直接拉黑 | `""`(空行) | {A1, P1, E1, S1} |
| 3 | underwriting | A1 **P2 E2** S1 | {A1, P1, E1, S1} | A1、S1 命中 → 可疑 | `"1"` | + **P2, E2**(传染!) |
| 4 | underwriting | A2 **P2** E3 S2 | {A1, P1, E1, S1, P2, E2} | **P2 命中**(事件 3 传染进来的) | `"1"` | + A2, E3, S2 |

三个关键观察:

- **事件 4 与 fraud_flag 拉黑的 {A1, P1, E1, S1} 零交集**,它唯一的联系是 P2 —— 而 P2 是从事件 3(被判可疑的 underwriting)传染进黑名单的。所以漏了传染规则,事件 4 必然错输出 `"0"`。这就是官方特意加第 4 个事件的原因。
- **事件 1 不回溯改判**:它的值后来(事件 2)被拉黑了,但它当时已经返回 `"0"` —— 决策按流的顺序做出,流式系统不改历史输出(production 讨论点:真实系统可以把它丢进人工复核队列)。
- **先查再加**:判定用的是"处理前"的黑名单;如果先把自己的值加进去再查,每个事件都会命中自己。

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

**错误实现 × 测试矩阵(每个输出都是对应版本代码实际运行的结果):**

自造 Case C(期望 `0`, `0`):两条干净的 underwriting 共享同一个 phone —— 专抓"干净交易的 PII 也被记录"这个官方测试抓不到的错误:

```json
[
    {"event_type": "underwriting", "customer_details": {"phone": "555-000-1234"}, "loan_amount": 100},
    {"event_type": "underwriting", "customer_details": {"phone": "555-000-1234"}, "loan_amount": 200}
]
```

| 实现版本 | Test 0(期望 0, 空, 1, 1) | Case C(期望 0, 0) |
|---|---|---|
| W1:漏传染(只记录 fraud_flag 的 PII) | 0, 空, 1, **0 ✗** | 0, 0 ✓ |
| W2:先 add 后 check | **1 ✗**, 空, 1, 1 | **1 ✗**, **1 ✗** |
| W3:干净交易的 PII 也记录(先 check 后无脑 add) | 0, 空, 1, 1 ✓(**官方测试抓不到!**) | 0, **1 ✗** |
| 最终正确版 | 0, 空, 1, 1 ✓ | 0, 0 ✓ |

结论:W1 死在官方第 4 个事件上;W2 第一个事件就露馅;**W3 能骗过官方测试**,所以 Case C 值得当场自造 —— 向面试官证明"我知道干净交易不该被记录"。

## 五、坑清单(按翻车概率排序)

1. **传染规则漏掉** —— 只把 fraud_flag 的 PII 入库,不记录被判可疑的 underwriting 的 PII → 第 4 个事件会错输出 "0"。这是全题最容易挂的点(#24 面经特别强调)。
2. **先 add 后 check** —— 把当前事件的值先加进集合再查询 → 每个事件都命中自己 → 全部输出 "1"。必须先判断、后加入。
3. **空串/缺失字段**(Part 1 的 bug 3 在 Part 2 同样会踩)—— 两个都缺 email 的事件会因为共享 `""` 而互相"传染"。
4. **credit_score 不是 PII** —— schema 表里它没有 (pii) 标记,别顺手加进白名单。
5. **返回类型是 String** —— `"1"` / `"0"` / `""`,不是 boolean、不是 int。fraud_flag 也必须"返回",不是不返回。
6. **Java 版 customer_details 可能是 `Map<String, Object>`**(因为 credit_score 是 int)—— 白名单只取 4 个字段可自然绕开;若必须转型,用 `value instanceof String s` 模式匹配,别盲目 cast。
7. 干净事件的 PII 被顺手存了 → 内存白涨,而且语义就错了(它们不可疑)。

## 六、必备 Follow-up 问答(详解版)

#19 面经原话:"面试官临时想了几个 follow up:**为什么要用这个数据结构;如果这是 production service,你要做什么改变**;等等。" HR 邮件也明说 "Be prepared to explain what changes you'd make before deploying code to production" —— 这部分不是加分题,是必考题。

回答框架:每条按 **现状缺陷 → 怎么改 → 权衡** 三段说。挑 3~4 条讲透,比 8 条念清单强得多。

### Q1:为什么用 HashSet?(数据结构选型)

30 秒话术:"需求只有两个操作——值级去重和'这个值出现过吗'。HashSet 的 add/contains 是均摊 O(1),正好是它最强的两项。不需要顺序所以不用 TreeSet(每次 O(log n));不需要给值挂附加信息所以不用 HashMap;List.contains 是 O(n),n 条事件总成本 O(n²),不可接受。"

被追问时的弹药:

- **均摊 O(1) 从哪来:** hashCode → 定位桶 → 桶内比对;哈希分布均匀时每桶平均 0~1 个元素。扩容(默认 load factor 0.75,容量翻倍 rehash)单次 O(n),但翻倍策略保证摊销后每次插入仍 O(1)——主动说出 "amortized" 这个词。
- **最坏情况:** 大量碰撞挤进同一桶。Java 8+ 单桶超 8 个且桶数组容量 ≥ 64 时,链表树化为红黑树,退化上限 O(log n)(Java 7 及以前是 O(n))。
- **字符串细节:** 严格说 add/contains 是 O(L)(L 为串长,算哈希要扫一遍字符),但 String 会缓存自己的 hashCode,且 PII 都是短串,视为常数。
- **hashCode/equals 契约:** equals 相等 ⇒ hashCode 必须相等;可变对象入 set 后再改字段,就再也 contains 不到它(经典事故)。String 不可变,天然安全。
- **小优化:** 预知规模时 `new HashSet<>((int)(expected / 0.75f) + 1)` 一次到位,避免反复 rehash。

### Q2:上 production 前你会改什么?(八个方向,每个都是"缺陷 → 改法")

**1. PII 安全(fintech 面试最好第一个说)**
缺陷:明文 SSN/电话放在堆内存、可能被打进日志——heap dump 或日志泄漏就是事故,合规上也过不去。
改法:集合里存 **HMAC-SHA256(带密钥的哈希)** 而不是明文——确定性哈希让相等匹配照常工作,但值不可逆推。追问点:为什么是 HMAC 不是普通 SHA256?因为电话/SSN 是**低熵值**(10 位数字可枚举),无密钥哈希能被字典暴破;密钥放 KMS。另外:日志只打事件 id 和掩码(`***-**-8929`),永不打原值。

**2. 状态外置(共享 + 持久)**
缺陷:进程内 HashSet 重启即丢;水平扩容后每个实例只见过自己那份流量,漏判。
改法:黑名单放 **Redis**(SADD / SISMEMBER 都是 O(1)),所有实例共享;或 DB 表 + 唯一索引;或 Kafka compacted topic,启动时重放重建本地缓存。
权衡:每次判定多一跳网络(毫秒级)。题干说 "produce a decision in seconds",预算足够;更苛刻时热点值加本地缓存 + 短 TTL。

**3. 输入校验 + 毒丸隔离**
缺陷:一条畸形事件(缺 customer_details、类型不对)抛异常,能卡死整个分区的消费。
改法:schema 校验(JSON Schema / protobuf);单条 try-catch,坏事件进**死信队列(DLQ)**留证 + 告警,流水不断。DLQ 速率本身是个监控指标。

**4. 并发与原子性**
缺陷:HashSet 非线程安全;更深一层,"先查后加"是两步操作,并发下两条相关欺诈可能同时查询、互相看不见、双双放行。
改法:简单档换 `ConcurrentHashMap.newKeySet()`;正确档是**保证判定的原子性**——按 key 分区串行(但注意:传染跨字段、黑名单是全局的,按单一字段分区行不通),或外部存储做原子"查+加"(Redis Lua 脚本 / SMISMEMBER+SADD 事务)。能指出"分区难在传染是跨 key 的",是这题并发讨论的最亮点。

**5. 可观测性**
改法:metrics——各类型事件 QPS、"1" 命中率、黑名单大小、处理延迟 p99、DLQ 计数;告警——命中率突变(可能是攻击,也可能是上游数据坏了,两种都要人看);日志结构化且不含 PII。

**6. 误报治理(风控产品思维,Affirm 爱听)**
缺陷:公寓楼/宿舍共享地址、家人共用电话 → 一条 fraud_flag 把整栋楼"传染"成可疑,而且级联放大。
改法:字段加权(ssn 命中 ≫ address 命中)+ 阈值,代替"任一命中一票否决";**共享值豁免**(一个 address 出现在超过 N 个不同用户名下,自动降权/白名单);传染深度上限;边界案例进人工复核队列,而不是直接拒贷。

**7. 幂等与顺序**
缺陷:消息系统至少一次投递(at-least-once),同一事件可能被处理两次;跨分区还可能乱序。
改法:set 的 add 天然幂等(主动说出这个好性质),但**对外副作用**(写决策库、发通知)要按事件 id 去重;顺序上,决策依赖处理顺序,重放时必须按原序,否则输出会变。

**8. 增长控制**
缺陷:黑名单只增不减,内存/存储无限涨。
改法:TTL(欺诈信号是否随时间衰减——这是要和风控团队定的**业务问题**,面试里点出"这不是纯技术决定"很加分)、冷热分层(热值内存、冷值 DB)、定期压缩。

### Q3:数据量大到单机放不下怎么办?

Redis Cluster 按值哈希分片,容量水平扩展;判定路径想省网络调用,本地放一个 **Bloom filter** 做一级过滤:它**没有假阴性**(说"不在"就一定不在,直接放行),少量假阳性(说"在"再去权威存储二次确认)。粗算:10 亿个值、1% 误报率约 1.2 GB,单机放得下。需要删除语义时换 counting / cuckoo filter。配合第 1 条:外置存储里放的本来就是 HMAC 值,分片 key 也用它。

### Q4(高频追问):人工发现标错了,要撤销(un-flag)怎么办?

`remove(value)` 本身一行,难的是**传染出来的衍生黑值**:它们"因它而黑",删掉源头不会自动回滚下游。两条路:(a) 记 provenance——每个值因哪个事件入黑,构成依赖图,撤销时沿图回收(实现重、易错);(b) **事件溯源(event sourcing)**——状态本来就是事件流的确定性函数,把那条 fraud_flag 标记为作废,然后**重放全流重建状态**。面试答 (b) 通常最漂亮,还自然引出"为什么流式系统偏爱可重放架构"。

## 七、变体详解(#25 店面的第二、三问)

#25 面经原文的三问结构:"Part 1 debug;Part 2 **实现功能用 Fraud Event match transaction**;Part 3 **记录 Fraud 的 Transaction 信息加入 Fraud Event 里,最后找出所有的 Fraud**。"

### 第一步永远是问清语义 —— 三个档位难度差很大

- **档位 A(最可能是这个):** 边处理边收集——凡是被判 `"1"` 的交易记下 id,最后 `getAllFraudulentTransactions()` 返回。纯增量,10 行代码。
- **档位 B:** fraud_flag 到来时要**回溯**——共享任一 PII 的**历史**交易也算 fraud。
- **档位 C:** 全量级联(fraud ring)——被回溯标记的历史交易,它们的其他 PII 也入黑、继续传染,直到闭包。

要问的澄清问题:交易身份是事件自带 id 还是我用自增序号?fraud_flag 匹配历史交易吗(A vs B/C)?被回溯标记的交易,它的其他 PII 还要继续传染吗(B vs C)?输出要什么顺序?

### 档位 A:在 FraudDetector 上加三行状态(先交付这个拿分)

```java
class FraudDetector {
    private final Set<String> suspiciousPiiValues = new HashSet<>();
    private final Set<Long> fraudulentTxnIds = new LinkedHashSet<>(); // insertion order kept
    private long nextTxnId = 0;

    public String handleEvent(Event event) {
        List<String> piiValues = extractPiiValues(event);
        if ("fraud_flag".equals(event.eventType)) {
            suspiciousPiiValues.addAll(piiValues);
            return "";
        }
        if ("underwriting".equals(event.eventType)) {
            long txnId = nextTxnId++;                       // NEW: identity per transaction
            boolean suspicious = piiValues.stream().anyMatch(suspiciousPiiValues::contains);
            if (suspicious) {
                fraudulentTxnIds.add(txnId);                // NEW: record it
                suspiciousPiiValues.addAll(piiValues);
                return "1";
            }
            return "0";
        }
        return "";
    }

    public List<Long> getAllFraudulentTransactions() {     // NEW: final answer
        return new ArrayList<>(fraudulentTxnIds);
    }
}
```

### 档位 B/C:倒排索引 + BFS(follow-up 弹药,以下代码已实测通过)

核心思路:除了黑名单,再维护两张表——`txnsByValue`(值 → 含它的交易,倒排索引)和 `valuesByTxn`(交易 → 它的值)。**不变量一句话:值入黑的那一刻,回溯扫一遍含它的历史交易(每个值只扫这一次);之后到来的交易在自己的 handleEvent 里前向查黑名单** —— 两个方向都盖住,不重不漏。

```java
class FraudLedger {
    private final Set<String> suspiciousValues = new HashSet<>();
    private final Set<Long> fraudulentTxnIds = new LinkedHashSet<>();
    private final Map<String, List<Long>> txnsByValue = new HashMap<>(); // inverted index
    private final Map<Long, List<String>> valuesByTxn = new HashMap<>();
    private long nextTxnId = 0;

    public String handleEvent(Event event) {
        List<String> values = extractPiiValues(event);
        if ("fraud_flag".equals(event.eventType)) {
            cascade(values);
            return "";
        }
        if ("underwriting".equals(event.eventType)) {
            long txnId = nextTxnId++;
            valuesByTxn.put(txnId, values);
            for (String v : values) {
                txnsByValue.computeIfAbsent(v, k -> new ArrayList<>()).add(txnId);
            }
            boolean suspicious = values.stream().anyMatch(suspiciousValues::contains);
            if (suspicious) {
                fraudulentTxnIds.add(txnId);
                cascade(values); // contagion + retro through newly blackened values
                return "1";
            }
            return "0";
        }
        return "";
    }

    /** BFS: new suspicious value -> historical txns containing it -> their other values -> ... */
    private void cascade(List<String> seedValues) {
        Deque<String> queue = new ArrayDeque<>();
        for (String v : seedValues) {
            if (suspiciousValues.add(v)) queue.add(v);   // enqueue only NEWLY blackened values
        }
        while (!queue.isEmpty()) {
            String value = queue.poll();
            for (long txnId : txnsByValue.getOrDefault(value, List.of())) {
                if (fraudulentTxnIds.add(txnId)) {       // each txn flagged at most once
                    for (String other : valuesByTxn.get(txnId)) {
                        if (suspiciousValues.add(other)) queue.add(other);
                    }
                }
            }
        }
    }

    public List<Long> getAllFraudulentTransactions() {
        return new ArrayList<>(fraudulentTxnIds);
    }
}
```

已验证的行为(9 个用例全过):t0{P1,S1} 干净 → 后来 `fraud_flag{S1}` 回溯拉黑 t0,并把 t0 的 P1 也传染入黑 → 新交易 t2{P1,E9} 命中 P1 判 "1" 且 E9 入黑 → t3{E9} 判 "1" → `fraud_flag{P2}` 回溯拉黑 t1{P2,S2} 并传染 S2 → t4{S2} 判 "1"。最终 `[0, 2, 3, 1, 4]`(按被标记的先后顺序)。

档位 B 就是把 cascade 里"继续入黑其他值"的两行去掉(只回溯一层,不再扩散);#25 Part 2 的 "match transaction" 若只是查询不改状态,一行搞定:`txnsByValue.getOrDefault(value, List.of())`。

**复杂度:** 每个值只入队一次、每笔交易只被标记一次 → 全程总成本 O(值-交易边数),摊到每个事件近似常数;空间 O(交易数 × 平均字段数)。

**边界:** 同一交易被多个 fraud_flag 匹配(Set 去重,天然幂等);fraud_flag 无任何匹配(空转,不报错);输出顺序用 LinkedHashSet 保插入序,要排序再加一行。

**面试策略:** #25 帖主提前 15 分钟做完、test case 全过,仍然被拒——大概率输在讨论深度。所以正确姿势是:先交付档位 A 并跑通,然后**主动**说"如果 fraud_flag 需要回溯匹配历史交易,我会加一个倒排索引,值入黑时 BFS 一次……",把富余时间花在这段讨论上,而不是提前结束。

## 八、60 分钟时间分配

| 时间 | 做什么 |
|---|---|
| 0–8 min | 读 Part 1 题干 + schema 表,手过 Test 0 |
| 8–20 min | 定位并修复 3 个 bug,每修一个跑一次,补 Case A / Case B 自造用例 |
| 20–25 min | 读 Part 2,复述规则(尤其传染规则)向面试官确认 |
| 25–45 min | 写 FraudDetector + 跑 Test 0 + 边界用例 |
| 45–55 min | Follow-up:数据结构选择、production 改造 |
| 最后 5 min | Buffer / 反问 |
