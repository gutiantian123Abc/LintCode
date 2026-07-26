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

面试官的英文问法:"Why did you use a HashSet?" / "Why this data structure?"

30 秒话术:"需求只有两个操作——值级去重和'这个值出现过吗'。HashSet 的 add/contains 是均摊 O(1),正好是它最强的两项。不需要顺序所以不用 TreeSet(每次 O(log n));不需要给值挂附加信息所以不用 HashMap;List.contains 是 O(n),n 条事件总成本 O(n²),不可接受。"

**English 回答脚本(照着说):**
> "I only need two operations here: deduplication, and membership checks — 'have I seen this value before?' A HashSet gives me amortized O(1) add and contains, which is exactly what it's optimized for. I don't need ordering, so a TreeSet would just cost an extra log factor; I don't need to attach data to each value, so a HashMap is overkill; and a List would make contains O(n), which turns the whole stream into O(n squared). So a HashSet is the precise fit."

被追问时的弹药:

- **均摊 O(1) 从哪来:** hashCode → 定位桶 → 桶内比对;哈希分布均匀时每桶平均 0~1 个元素。扩容(默认 load factor 0.75,容量翻倍 rehash)单次 O(n),但翻倍策略保证摊销后每次插入仍 O(1)——主动说出 "amortized" 这个词。
- **最坏情况:** 大量碰撞挤进同一桶。Java 8+ 单桶超 8 个且桶数组容量 ≥ 64 时,链表树化为红黑树,退化上限 O(log n)(Java 7 及以前是 O(n))。
- **字符串细节:** 严格说 add/contains 是 O(L)(L 为串长,算哈希要扫一遍字符),但 String 会缓存自己的 hashCode,且 PII 都是短串,视为常数。
- **hashCode/equals 契约:** equals 相等 ⇒ hashCode 必须相等;可变对象入 set 后再改字段,就再也 contains 不到它(经典事故)。String 不可变,天然安全。
- **小优化:** 预知规模时 `new HashSet<>((int)(expected / 0.75f) + 1)` 一次到位,避免反复 rehash。

**被追问时的英文短句:**
> 均摊:"Resizing costs O(n), but capacity doubles each time, so the total copying work across n inserts is a geometric series under 2n — constant per insert, amortized."
> 最坏情况:"Under heavy collisions a bucket treeifies into a red-black tree since Java 8, so lookups degrade to O(log n) rather than O(n)."
> 字符串:"Strictly speaking, hashing a string is O(L) in its length, but String caches its hash code and these PII values are short."

### Q2:上 production 前你会改什么?(零基础详解版)

面试官的英文问法:"What changes would you make before deploying this to production?" / "What would it take to productionize this?"

**先建立总图景。**面试里你写的是:一个进程、内存里一个 HashSet,测试数据跑完程序就退出。生产上,这段代码会变成一个 7×24 小时运行的服务:部署在多台服务器上、每秒吃进成千上万条事件、会崩溃重启、会半夜发版、会遇到恶意数据、要接受审计。"上 production 前改什么"问的就是这段距离里的每一个坑。以下 8 条,每条按"哪里不行 → 需要的背景概念 → 怎么改"展开。面试不用全背:挑 PII、状态外置、误报治理(或并发)三条讲透,其余知道名词和一句话答法即可。

**英文开场白(先说这句再展开):**
> "It works as a coding exercise, but I'd change quite a few things before shipping it. The biggest ones are how we store the PII, where the state lives, and how we handle false positives — let me walk through them."

---

**1. PII 安全(fintech 面试建议第一个说)**

哪里不行:现在黑名单里存的是明文 SSN/电话。两个你可能没想过的泄漏通道:(a) 程序崩溃时,运维常把整个内存导出成文件排查问题(叫 **heap dump**),里面就有全部明文 SSN,这个文件会被下载、传阅;(b) 日志——只要有一行代码不小心把 event 打进日志,它就会被同步到日志平台、备份、第三方分析工具,扩散到你控制不了的地方。SSN 和密码不同:密码泄漏能改,SSN 改不了,泄漏是永久伤害,金融公司还要吃监管罚单。

背景概念:
- **哈希(hash)**:把任意字符串"搅拌"成一段固定长度乱码的单向函数。同样的输入永远得到同样的输出——所以拿哈希值照样能做"相等比较";但从乱码反推原文在计算上不可行。思路:黑名单不存 `4568698929`,存它的哈希;新交易的 ssn 也哈希一下再比对——匹配逻辑完全不变。
- **为什么普通哈希(SHA-256)不够**:电话号码只有 10 位数字,总共才 100 亿种可能。攻击者可以提前把所有号码全哈希一遍,做一张"乱码 → 原文"的反查字典;拿到你的黑名单一查全还原。这种"可能性少到穷举得完"的数据叫**低熵**数据。
- **HMAC**:带密钥的哈希——计算时混入一个只有你们公司知道的密钥。攻击者没有密钥,连哈希都算不出来,字典自然建不成。可以理解成"盖了公司印章的指纹"。
- **KMS(Key Management Service)**:云厂商提供的"密钥保险柜"。密钥不写进代码或配置文件(那样代码泄漏 = 密钥泄漏),程序运行时向 KMS 申请使用。

怎么改:黑名单里存 HMAC-SHA256(值);日志只打事件 id 和掩码(`***-**-8929`);密钥放 KMS。

**English 回答脚本**(可能的问法:"How would you handle the PII in this service?"):
> "Right now I'm holding raw SSNs and phone numbers in memory, which isn't acceptable in production — a heap dump or one careless log line would leak them. I'd store keyed hashes instead: HMAC-SHA256, with the key held in a KMS. Hashing is deterministic, so equality matching works exactly the same, but the values can't be recovered. And it has to be a keyed hash rather than plain SHA-256, because phone numbers and SSNs are low-entropy — with a plain hash an attacker could precompute every possible value. Logs would only ever show event IDs and masked values."

**2. 状态外置(黑名单不能只活在一个进程的内存里)**

哪里不行,两种死法:
- **重启即失忆**:发版、崩溃、机器维护,进程一停内存清零。重启后黑名单是空的——之前所有 fraud_flag 白记了。
- **多实例失联**:流量大了一台机器扛不住,标准做法是同一个程序跑 N 份分摊流量(**水平扩容**)。但每份程序各有各的内存:实例 A 处理了 fraud_flag、记住了骗子的电话;下一条交易被分给实例 B——B 的黑名单里根本没这个电话,放行,漏判。

背景概念:
- **Redis**:一个独立部署的"共享内存数据库",可以粗暴理解成"放在网络另一头、所有实例共用的一个巨大 HashSet"。常用数据结构做成了现成命令:`SADD`(往集合加元素)、`SISMEMBER`(查集合里有没有),都是 O(1),单机每秒扛十万级请求。所有实例读写同一个 Redis → 黑名单只有一份、谁都看得见;Redis 自己会落盘,重启不丢。
- **Kafka**(为"重放"铺垫):消息平台,事件像流水账一样按顺序持久保存。因为历史事件都在,新起的进程可以"从头读一遍账本"重建内存状态。

怎么改 + 权衡:黑名单放 Redis(或 DB 表)。代价:查内存是纳秒级,过一次网络是毫秒级,慢约三个数量级——但题干说 "produce a decision in seconds",毫秒级绰绰有余。**把这句权衡说出来,比方案本身更加分。**

**English 回答脚本**(可能的问法:"What happens when you run multiple instances, or the process restarts?"):
> "The blacklist lives in one process's memory, which fails in two ways: we lose it on every restart, and once we scale horizontally each instance only sees its own slice of the traffic, so the blacklists diverge and we miss fraud. I'd move the set into a shared store like Redis — SADD and SISMEMBER are both O(1) — or rebuild it from a Kafka compacted topic on startup. The trade-off is one network hop per check, which is milliseconds; since the requirement is a decision within seconds, that's well within budget."

**3. 输入校验 + 死信队列(一条坏数据不能瘫痪整条流水线)**

哪里不行(一个具体故事):上游团队发版带了 bug,发出一条 customer_details 是数字而不是对象的事件。你的代码解析时抛异常。消息队列的默认行为是"处理失败就重试"——于是你的服务反复读这条坏消息、反复崩,后面排队的几万条好消息全被堵死。这种消息有个名字:**毒丸(poison pill)**。

背景概念:
- **schema 校验**:进业务逻辑之前,先按"字段清单"(哪些必填、什么类型)检查数据长相,不合格不放行。
- **DLQ(Dead Letter Queue,死信队列)**:专门的"问题件货架"。处理失败的消息不无限重试,而是挪进 DLQ 存证,主流水继续跑;工程师之后来翻问题件、修上游。

怎么改:schema 校验 + 单条 try/catch + 失败进 DLQ + "DLQ 进件突然变多"报警(那意味着上游坏了)。

**English 回答脚本**(可能的问法:"What if an event is malformed?"):
> "I'd make sure one bad event can never take down the consumer. Today a malformed payload would throw, and with retries it becomes a poison pill blocking everything behind it. So: schema validation up front, a try-catch around each event, and failures go to a dead-letter queue with an alert on the DLQ rate — the stream keeps flowing and we debug the bad data on the side."

**4. 并发与原子性(HashSet 不是线程安全的,而且"先查后加"有竞态)**

背景概念:
- **线程**:一个进程里多个执行流同时跑你的代码。生产服务几乎必然多线程,否则多核白买。
- **线程安全**:数据结构被多个线程同时读写仍保持正确。HashSet **不是**——两个线程同时 add 会互相踩(丢元素,甚至结构损坏)。

哪里不行,两层:
- 第一层就是上面这个,修法一行:`ConcurrentHashMap.newKeySet()`(JDK 自带的线程安全 Set)。
- 第二层更隐蔽:我们的逻辑是"先查黑名单,再决定"——两步之间世界可能变了(术语:**check-then-act 竞态**)。场景:线程 A 正在处理 fraud_flag、往黑名单写值**还没写完**,同一骗子的 underwriting 已被线程 B 查询——B 没查到,放行,漏判。换线程安全的 Set 也救不了,因为问题出在"两步不是一个整体"。

背景概念:**原子性** = 把"查 + 改"打包成不可分割的一步,中间不许别人插进来。

怎么改:(a) 上锁,简单但吞吐低;(b) 用 Redis 时有个优雅性质——Redis 执行命令是**单线程排队**的,把"查+加"写成一个 Lua 脚本发过去,整个脚本天然原子;(c) **分区串行**——同一个 key 的所有事件固定路由给同一个线程,单线程内天然无竞态(Kafka 按 key 分区就是干这个)。

**本题的最高分观察**:分区这条常规路在这题走不通——一条事件有 4 个不同的 PII 值,按 phone 分区,ssn 相同的两条事件会落进不同分区、互相看不见;传染跨字段,黑名单天生全局。所以只能单写者,或共享存储上做原子"查+加"。

**English 回答脚本**(可能的问法:"Is this thread-safe?"):
> "HashSet isn't thread-safe, so at minimum I'd switch to ConcurrentHashMap.newKeySet(). But the subtler issue is that check-then-add is two separate steps: a fraud_flag could be mid-write while an underwriting event for the same person is being checked on another thread, and we'd miss it. So the check-and-add needs to be atomic — for example a Redis Lua script, since Redis executes commands on a single thread. The interesting wrinkle is that the usual fix, partitioning by key, doesn't work here: an event carries four different PII values and contagion crosses fields, so the blacklist is inherently global. That pushes you toward a single writer, or atomic operations on a shared store."

---

#### 并发这条的实证演示 + 逐行讲解(ConcurrencyDemo.java,代码已实际编译运行)

**三个 Demo 的真实运行结果:**

```text
Demo1(裸 HashSet,两个线程同时各加 20 万个不同元素):
  trial 1: 期望 400000,实际 393794   <- 6206 个元素无声消失
  trial 2: 期望 400000,实际 400000   (侥幸没触发 —— 并发 bug 时隐时现,这正是它难查的原因)
  trial 3: 期望 400000,实际 396492   <- 3508 个元素无声消失

Demo2(线程安全的 Set,但"查"和"写"没绑在一起):underwriting 判定 = "0"   <- 欺诈漏判
Demo3(同一把锁把"整体写入"和"查询"焊死):        underwriting 判定 = "1"   <- 抓住了
```

**Demo 1:裸 HashSet 在并发写入下丢元素**

```java
static void demo1() throws Exception {
    for (int trial = 1; trial <= 3; trial++) {
        Set<Integer> set = new HashSet<>();                  // 普通 HashSet,无任何保护
        int perThread = 200_000;

        // 两个线程,各自添加互不重叠的 20 万个数(0~199999 和 200000~399999)
        Thread t1 = new Thread(() -> { for (int i = 0; i < perThread; i++) set.add(i); });
        Thread t2 = new Thread(() -> { for (int i = perThread; i < 2 * perThread; i++) set.add(i); });

        t1.start(); t2.start();           // 同时开跑
        t1.join(10_000); t2.join(10_000); // 等两个线程都干完

        System.out.println("expected 400000, actual " + set.size());
    }
}
```

讲解:两个线程加的数**完全不重叠**,理论上最后必然是 40 万个,实测丢了几千个。因为 `add` 内部是"定位桶 → 读链表 → 写回"好几步,两个线程同时写同一个桶(或同时触发扩容)时,后写的把先写的覆盖掉了——像两个厨师同时拿出冰箱同一个盒子各加一样菜再放回,后放回的盒子里没有对方加的菜。注意 trial 2 一个没丢:**并发 bug 看运气,测试环境跑一百遍都可能是好的**。修法一行:`ConcurrentHashMap.newKeySet()`。

**Demo 2 / 3 共用的场景设定:**

```java
// 线程 A 正在处理:fraud_flag  {phone-111, ssn-999, email-x, addr-123}
// 线程 B 正在处理:underwriting{phone-222, ssn-999}   -> 两者只共享 ssn
static final List<String> FRAUD_VALUES        = List.of("phone-111", "ssn-999", "email-x", "addr-123");
static final List<String> UNDERWRITING_VALUES = List.of("phone-222", "ssn-999");
// 正确结果:fraud_flag 把 4 个值拉黑后,underwriting 因 ssn-999 命中而判 "1"
```

**Demo 2:容器已经线程安全,但"查"和"写"没绑在一起 → 照样漏判**

```java
static void demo2() throws Exception {
    Set<String> blacklist = ConcurrentHashMap.newKeySet();   // 注意!这已经是线程安全的 Set
    CountDownLatch aWrotePhoneOnly = new CountDownLatch(1);
    CountDownLatch bFinishedCheck  = new CountDownLatch(1);
    StringBuilder decision = new StringBuilder();

    Thread a = new Thread(() -> {
        blacklist.add(FRAUD_VALUES.get(0));      // A 只写完了 phone……
        aWrotePhoneOnly.countDown();             // ……此刻正处于"写到一半"
        try { bFinishedCheck.await(); } catch (InterruptedException ignored) {}
        for (int i = 1; i < FRAUD_VALUES.size(); i++)
            blacklist.add(FRAUD_VALUES.get(i));  // 这才写 ssn/email/addr —— 太晚了
    });

    Thread b = new Thread(() -> {
        try { aWrotePhoneOnly.await(); } catch (InterruptedException ignored) {}
        boolean hit = UNDERWRITING_VALUES.stream().anyMatch(blacklist::contains); // 就在此刻查
        decision.append(hit ? "1" : "0");
        bFinishedCheck.countDown();
    });

    a.start(); b.start(); a.join(); b.join();
    System.out.println("decision = " + decision);   // 输出 "0":漏判
}
```

讲解:先认识 **CountDownLatch(倒计时门闩)**——把它想成一扇闸门,`await()` 是"站在闸门前等",`countDown()` 是"开闸"。生产环境里这种倒霉时序是负载一高**随机**发生的;demo 用两道闸门把那个倒霉瞬间**冻结**下来让它 100% 复现:强迫 B 恰好在"A 写完 phone、还没写 ssn"的瞬间去查。B 的每一次 `contains` 单独看都是正确的(那一刻黑名单里确实没有 ssn-999),但业务结果是 `"0"`、欺诈放行。**结论:线程安全的容器只保证"单次操作"正确,救不了"两步之间被插队"。**这就像夫妻同时在两台 ATM 查余额都看到 1000、都决定取 800——每次查询和扣款单独都对,连起来账户被取走 1600。

**Demo 3:把"整体写入"和"查询"用同一把锁焊死 → 修复(逐行讲解)**

```java
static void demo3() throws Exception {
    Set<String> blacklist = new HashSet<>();      // ① 故意用回普通 HashSet
    Object lock = new Object();                   // ② 一把锁(任何对象都能当锁用)
    CountDownLatch aInsideLock = new CountDownLatch(1);
    StringBuilder decision = new StringBuilder();

    Thread a = new Thread(() -> {
        synchronized (lock) {                     // ③ A 在写第一个值之前就锁门
            blacklist.add(FRAUD_VALUES.get(0));   //    写入 phone
            aInsideLock.countDown();              // ④ 叫醒 B(但 B 会被锁挡住)
            sleep(200);                           // ⑤ 故意磨蹭 200ms,模拟"写到一半"
            for (int i = 1; i < FRAUD_VALUES.size(); i++)
                blacklist.add(FRAUD_VALUES.get(i)); //  写完剩下的 ssn/email/addr
        }                                         // ⑥ 走出大括号,锁自动释放
    });

    Thread b = new Thread(() -> {
        try { aInsideLock.await(); } catch (InterruptedException ignored) {}
        synchronized (lock) {                     // ⑦ B 想进,但锁在 A 手里 -> 原地阻塞等待
            boolean hit = UNDERWRITING_VALUES.stream().anyMatch(blacklist::contains);
            decision.append(hit ? "1" : "0");     // ⑧ 等到锁时,黑名单必然是完整的
        }
    });

    a.start(); b.start(); a.join(); b.join();
    System.out.println("decision = " + decision);   // 输出 "1":抓住
}
```

逐行讲解:

- **② 锁是什么。**Java 里任何对象都可以当"门锁"。`synchronized (lock) { ... }` 的含义:进入大括号前必须拿到 `lock` 上的锁;**同一时刻全世界只有一个线程能持有它**;别的线程走到自己的 `synchronized (lock)` 时发现锁被占,就在门口**原地睡觉**,直到持有者走出大括号(⑥)自动交还。
- **③ 锁的位置是全部精髓。**A 在**写第一个值之前**上锁、**写完全部 4 个值之后**才放锁——"写 4 个值"从外界看变成了**不可分割的一整块**:外人要么看到"一个都没写",要么看到"4 个全写完",永远看不到写了一半的中间状态。这就是原子性的字面实现。
- **⑦ 双方必须用同一把锁(初学者最容易漏的点)。**锁的本质是"君子协定",只拦得住同样来拿这把锁的人。如果 B 不上锁直接查,照样能读到写了一半的黑名单,Demo 2 惨案原样重演。规则:**所有会碰这份数据的代码路径,全部走同一把锁。**

执行时间线(对照代码序号):

```text
线程 A:  ③拿到锁 → 写phone → ④开闸叫醒B → ⑤磨蹭200ms → 写ssn/email/addr → ⑥交还锁
线程 B:  ……等闸门…… → 醒了 → ⑦到门口,锁在A手里 → 睡觉等待 ────────→ 拿到锁 → ⑧查:ssn命中 → "1" ✓
```

三个值得咀嚼的细节:

1. **⑤ 的 sleep 纯粹是为了证明** B 真的在门口等了 200ms,删掉它结论不变。
2. **① 故意用回普通 HashSet 也安全**——所有读写都被同一把锁保护,同一时刻只有一个线程碰它。说明"锁"一个方案同时解决了第一层(容器安全)和第二层(原子性)两个问题。
3. **对回生产:**`synchronized` 只能锁住**同一个 JVM 里**的线程;黑名单挪到 Redis、多台机器共享后,这把锁够不着了——**Redis Lua 脚本就是"分布式世界里的同一把锁"**:Redis 单线程执行,整个脚本一口气跑完,效果和 Demo 3 的大括号一模一样。

面试手写时,Demo 3 的结构就是标准答案骨架:`synchronized` 包住 fraud_flag 的整段写入、包住 underwriting 的"查+传染写入",共用一把锁。可以再补一句英文:"On a single box I'd use one lock around the whole check-and-taint; across instances, the same idea becomes a Redis Lua script — Redis's single-threaded execution gives me the atomicity for free."

---

**5. 可观测性(让服务"能被看见")**

背景概念:**可观测性**三件套——metrics(持续上报的数字,画成仪表盘:每秒处理多少条、耗时多少、错多少)、日志(逐条事件的文字记录)、告警(数字越线自动叫人)。没有这三样,服务坏了你只能等客户投诉才知道。
- **p99**:把一段时间内所有请求的耗时从小到大排,取第 99% 位置的值。"p99 = 80ms" = 99% 的请求快于 80ms。不用平均值的原因:1% 的超慢请求会被 99% 的快请求平均掉,而用户记住的恰恰是慢的那次。

本服务的仪表盘:各类型事件 QPS(每秒条数)、"1" 命中率、黑名单大小、处理延迟 p99、DLQ 进件数。最值钱的一条告警:**命中率突变**——突然飙高只有两种解释:真有攻击,或上游数据坏了(比如某字段全变成同一个默认值,引发疯狂传染),哪种都得叫人来看。

**English 回答脚本**(可能的问法:"How would you monitor this in production?"):
> "I'd add metrics first: events per second by type, the flag rate, the size of the suspicious set, p99 processing latency, and the dead-letter rate, with alerts on anomalies. The flag rate is the one I'd watch most closely — a sudden spike either means a real attack or broken upstream data, and both need a human immediately."

**6. 误报治理(风控产品思维,Affirm 最爱听)**

背景概念:**误报(false positive)** = 把好人当骗子。对 Affirm 的代价:好客户被拒贷 = 直接丢收入 + 口碑,误报率过高本身就是事故。

为什么这个算法天生爱误报,推演一遍:PII 值不是人人唯一——整栋公寓楼共用一个 address,一家人共用一个座机。一条 fraud_flag 拉黑了 "123 Main St":这栋楼里每个诚实住户的下一笔交易都命中 address → 全部判 "1";传染规则再火上浇油——每个被误判住户的电话、邮箱也跟着入黑,黑名单像雪球滚过整栋楼。

四个手段:
- **字段加权**:字段"含金量"不同——ssn 几乎人手唯一,命中给 90 分;address 天然共享,命中只给 20 分;总分过阈值才判可疑,代替"任一命中一票否决"。
- **共享值豁免**:某个 address 出现在 50 个不同用户名下 → 系统自动认定是公寓楼/公司地址 → 降权或白名单。
- **传染深度上限**:传染只许走一层,不许链式滚雪球。
- **人工复核队列**:分数在边界的交易不直接拒,转人工。

**English 回答脚本**(可能的问法:"Do you see any problem with the matching rule itself?"):
> "The any-match rule will over-flag in the real world. Apartment buildings share an address, families share a phone number — one fraud_flag on a shared address would flag every honest neighbor, and the contagion rule then snowballs it. I'd move to weighted scoring — an SSN match is worth far more than an address match — with a threshold, auto-whitelist values that appear across many distinct users, cap the contagion depth, and send borderline scores to a manual review queue instead of auto-declining."

**7. 幂等与顺序(同一条消息可能送来两次)**

背景概念:
- **at-least-once(至少一次投递)**:消息系统的常见承诺。为什么会重复?消费者处理完消息、正要向队列发"处理完了"的确认回执时崩溃了——队列没收到回执,以为没处理,恢复后同一条再发一遍。"精确一次"极难,行业默认"至少一次,可能重复"。
- **幂等**:同一操作做 1 次和做 N 次结果一样。类比电梯按钮:按一次和狂按十次,都只来一台电梯。

对到本题:好消息——`set.add` 天然幂等,同一条 fraud_flag 消费两次,黑名单一模一样(**主动说出这个好性质**)。坏消息——副作用不幂等:判 "1" 时若还要写决策记录、发通知,重复消费 = 写两条、发两次。修法:每个事件带全局唯一 id,执行副作用前先查"这个 id 干过没有"(去重表)。
顺序:本题输出依赖处理顺序(fraud_flag 在前在后结果完全不同),重放和多分区消费必须保序。

**English 回答脚本**(可能的问法:"What if the same event is delivered twice?"):
> "Message systems are typically at-least-once, so duplicates will happen. The nice property here is that set-add is naturally idempotent — reprocessing a fraud_flag leaves the state unchanged, so the core logic is safe for free. Side effects aren't, though: if flagging writes a decision record or sends a notification, I'd dedupe those by event ID. And since the output depends on event order, replays and partitioned consumption have to preserve ordering."

**8. 增长控制(只进不出的黑名单会吃光内存)**

哪里不行:黑名单只加不删,服务跑三年涨到几十亿条,内存装不下、Redis 账单爆炸。

背景概念:**TTL(Time To Live)** = 给数据设"保质期",到期自动删除,Redis 原生支持。

但先别急着设 TTL——这里有个比技术更重要的问题:**欺诈信号会过期吗?**五年前被盗用过的电话,今天还该拉黑吗?号码可能早换了主人。这是业务决策,要和风控团队一起定;面试里点出"这不是纯技术决定"非常加分。工程配套:冷热分层——最近命中过的值留内存(热),长期没动静的沉到便宜的 DB(冷),查询先热后冷。

**English 回答脚本**(可能的问法:"The set grows forever — is that a problem?"):
> "Yes — the suspicious set only ever grows, so I'd put a lifecycle on it: TTLs in Redis, hot values in memory, cold ones tiered out to a database. Whether fraud signals should expire at all is really a business question for the risk team rather than a pure engineering call — a phone number flagged five years ago may belong to someone else today."

### Q3:数据量大到单机放不下怎么办?

面试官的英文问法:"What if the dataset no longer fits on a single machine?" / "How does this scale?"

问题设定:值多到一台机器(甚至一台 Redis)都放不下,比如百亿级。

- **分片(sharding)**:把数据按规则切成 N 份放 N 台机器——像图书馆按书名首字母分馆,找书先看首字母决定去哪个馆。**Redis Cluster** 自动做这件事:对值做哈希决定归哪台管,查询按同样规则路由。等于把一个大 HashSet 拆成 N 台机器上的 N 个小 HashSet,单次操作还是 O(1)。
- **Bloom filter(从零讲)**:一个极省内存的"预筛器",只会回答两种话——"**绝对没见过**"或"**可能见过**"。结构:一长条 bit 数组(初始全 0)+ k 个不同的哈希函数。加入值:算出 k 个位置,全部置 1。查询值:看同样 k 个位置——**只要有一个是 0,绝对没加过**(加过的话必然全是 1);全是 1 则大概率加过,但可能是别的值凑巧占了这些位(**假阳性**)。
- 关键性质背下来:**没有假阴性**(说"没有"绝对可靠),**有少量假阳性**(说"有"需要复核)。
- 用法:放在每台应用服务器本地内存当第一道门。绝大多数干净交易在这里得到"绝对没见过"→ 直接放行,零网络开销;少数"可能见过"的,再去 Redis 权威确认。
- 为什么省:不存值本身只存 bit。10 亿个值、1% 误报率 ≈ 1.2 GB,单机内存轻松放。
- 局限一句话:标准 Bloom filter 不支持删除(多个值共享 bit,删除会误伤别人);要删除就换 counting Bloom filter / cuckoo filter。

**English 回答脚本:**
> "Two layers. For capacity, Redis Cluster shards the set across nodes by hashing the value, so each lookup is still O(1). For latency, I'd keep a Bloom filter in each instance's local memory as a first-pass check. A Bloom filter never gives false negatives — when it says 'not present', we skip the network call entirely, which covers the vast majority of clean transactions. It does give occasional false positives, so on a hit we confirm against Redis. And it's tiny: a billion values at a one-percent false-positive rate is only about 1.2 gigabytes. If we needed deletions I'd use a cuckoo filter instead."

### Q4(高频追问):人工发现标错了,要撤销(un-flag)怎么办?

面试官的英文问法:"An operator flagged a customer by mistake — how would you undo that?"

场景:运营发现某条 fraud_flag 标错了,客户是好人,要求撤销。

为什么不是一行 `remove` 的事:删那条 flag 自己的 4 个值容易;但传染规则已经以它为起点污染了别的值——被它命中的交易的其他 PII 入了黑名单,可能又继续传染。这些"衍生黑值"散落在集合里,和正当来源的黑值混在一起,分不出哪些该跟着删。更麻烦的:一个值可能同时被两条不同的 fraud_flag 拉黑,撤销其中一条时它**不该**被删。

两个方案:
- **(a) 记账(provenance)**:每个值入黑时记下"因哪个事件而黑",形成依赖关系,撤销时沿依赖回收。听着直观,实现上是引用计数 + 依赖图,边界情况多、容易错。
- **(b) 事件溯源(event sourcing)**——从零解释:换一种世界观,**不把"当前状态"当真相,把"发生过的事件流水"当真相**;状态永远是"把流水从头算一遍"的结果。类比银行:银行不直接改你的余额,只追加交易流水,余额是流水加总出来的;冲正一笔错账 = 追加一条反向记录、重新加总。我们的 FraudDetector 恰好就是"输入事件流 → 输出状态"的确定性函数,所以撤销可以做成:**把那条 fraud_flag 标记作废,然后重放整个事件流,重建一份从未受它影响的黑名单**,替换旧的。绝对正确,不会漏删错删。代价:全量重放慢 → 定期存快照(snapshot),从最近快照开始放。

**English 回答脚本(推荐答 b):**
> "Removing the flag's own values is trivial — the hard part is the contagion. Values that turned suspicious because of that flag went on to taint others, and after the fact you can't tell them apart from legitimately suspicious values. You could track provenance — record which event caused each value to be blacklisted — but that becomes a reference-counted dependency graph and it's easy to get wrong. The cleaner answer is event sourcing: the state is just a deterministic function of the event stream, so I'd void the bad fraud_flag and replay the stream to rebuild the state without its influence — guaranteed correct. To keep replay fast, snapshot periodically and replay from the last snapshot."

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
