# 03 · Dispute Status(争议状态 Debug + O(1) StatusManager)

**优先级:★★★★☆ —— 被称为"店面新题",近期出现频率上升**

| 项目 | 说明 |
|---|---|
| 出现记录 | 面经 #7(InterviewDB 原题,附 debug 提示)、#20(近期 coding 面)、#22(店面,发帖人确认与 InterviewDB dispute-status 同题) |
| 题型 | Part 1:Debug + 补全批量计算版 · Part 2:实现事件消费者(读 O(1)) |
| 核心考点 | "最新事件决定状态"语义、优先级判定、**增量计数(先减旧再加新)**、map key 存在性 |
| 前车之鉴 | #20 帖主:"题干太长……主要还是 data processing,我的经验是多沟通多交流" |

> 本文所有 Java 代码已实际编译运行、测试全部通过(含"reopen 抹掉 closed_fraud"的关键用例)。

---

## 一、英文原版题干

### 业务规则(Part 1 / Part 2 共用,务必逐句读)

> At Affirm, we take seriously every dispute for a loan and want to accurately communicate the status of the loan based on its disputes.
>
> A dispute goes through a series of events, **always starting with an `opened` event**. A dispute can be closed with a `closed_fraud` or `closed_not_fraud` event, **but can be later reopened** by the customer. **The dispute's state is based on the most recent event** that occurred for that dispute.
>
> A loan can have **multiple disputes**, due to concurrent fraud investigations.
> - If a loan has a dispute closed with fraud, no matter what the other disputes say, that loan is **fraudulent**.
> - Otherwise, if a loan has any open disputes, the loan is **under_investigation**.
> - Finally, otherwise, the loan is **not_fraudulent**.

**数据 schema:**

| Field | Type |
|---|---|
| created_at | unix timestamp |
| dispute_id | int(标识一个 dispute,同一 dispute 的多条事件共享它) |
| loan_id | int(一个 loan 可有多个 dispute) |
| event_type | string enum: `"opened"` / `"closed_fraud"` / `"closed_not_fraud"` |

### Part 1 — Debug(原始 Python 代码,部分省略)

```python
class StatusCalculator:
    # (some code omitted...)

    def _get_dispute_status(self, dispute_id: int) -> DisputeEventType:
        events = self._get_all_events_for_dispute(dispute_id)
        events = sorted(events, key=lambda x: x.created_at)
        return events[-1].event_type

    def _get_loan_status(self, loan_id: int) -> LoanStatus:
        all_dispute_statuses = self._get_all_dispute_statuses_for_loan(loan_id)
        if DisputeEventType.closed_fraud in all_dispute_statuses:
            return LoanStatus.fraudulent
        if DisputeEventType.opened in all_dispute_statuses:
            return LoanStatus.under_investigation
        return LoanStatus.not_fraudulent

    """ !!! DO NOT modify the code below !!! """

    def get_status_per_loan(self) -> dict[int, LoanStatus]:
        all_loan_ids = [entry.loan_id for entry in self.db]
        return {
            loan_id: self._get_loan_status(loan_id)
            for loan_id in all_loan_ids
        }
```

**面经给的两条 debug 提示(#7 原话):**

1. **use `event_type` for filtering**
2. **check if the map key exists**

省略的部分(`_get_all_events_for_dispute`、`_get_all_dispute_statuses_for_loan`、构造索引的代码)正是藏 bug 的地方,不同场次细节可能不同,但 bug 家族固定,见下文第三节。

### Part 2 — Coding: StatusManager

> Our current solution is inefficient as it requires us to reprocess all events for a loan's dispute every time we need to evaluate its status. Event consumers allow us to process events on the fly and give a **read-optimized view** of this data.
>
> Your task: Design an event consumer **StatusManager** which processes a stream of DisputeEvent events and supports a function for **retrieving the status of a loan with time complexity O(1)**.

```python
class StatusManager:
    def add_event(self, event: DisputeEvent) -> None: ...
    def get_loan_status(self, loan_id: int) -> LoanStatus | None: ...
```

判题 harness:逐条 `add_event`,然后对每个出现过的 loan_id 打印 `loan_id status.value`(按 loan_id 排序)。

---

## 二、中文题意精读(动笔前 checklist)

- **Dispute 状态 = 该 dispute 最新一条事件的 event_type**(按 created_at 排,不是按输入顺序)。
- **Loan 状态优先级:任一 dispute 当前是 closed_fraud → fraudulent;否则任一 opened → under_investigation;否则 not_fraudulent。**
- 全题最关键的语义推论:**closed_fraud 的 dispute 被 reopen 后,它就不再是 closed_fraud** —— loan 状态可能从 fraudulent 退回 under_investigation。任何"见过 fraud 就永久定罪"的实现都是错的。
- 未知 loan:Part 1 harness 只查存在的 loan;Part 2 签名返回 `LoanStatus | None` → Java 里返回 `null`。
- `get_status_per_loan` 用**含重复**的 loan_id 列表反复调用 `_get_loan_status` → 你的辅助方法必须幂等、可重入,还要能接受同一 key 查多次。

## 三、Part 1:Debug 攻略

省略代码每场不完全一样,**准备 bug 家族 + 方法论**比背某一版代码更稳:

| Bug 家族 | 具体形态 | 对应提示 |
|---|---|---|
| map key 不存在 | `eventsByDispute.get(id)` 直接 `.sort()`/遍历 → Java NPE / Python KeyError;或构造索引时没用 computeIfAbsent/setdefault | 提示 2 |
| event_type 混淆 | 拿字符串和 enum 比较(`"opened" in statuses` vs enum 成员);或收集的是"所有事件的 type"而不是"每个 dispute 最新事件的 type" —— 后者会让已 reopen 的 dispute 仍被算作 closed_fraud | 提示 1 |
| 索引重复 | disputesByLoan 用 List 收集,同一 dispute_id 被加入 N 次(每条事件加一次)→ 结果虽可能对,但重复计算;应该用 Set | — |
| 排序稳定性 | 相同 created_at 的两条事件顺序未定义 → 主动问面试官平局规则 | — |

**现场流程:** 先跑给定测试 → 记下 expected vs actual → 从 harness(不可修改区)往上读调用链 → 在省略/可疑处加最小复现用例 → 修一个跑一次。口头 narrate 每一步。

**修好后的批量版参考(Java,已验证):**

```java
    // ===================== Part 1: debugged batch version =====================
    static class StatusCalculator {
        private final Map<Integer, List<DisputeEvent>> eventsByDispute = new HashMap<>();
        // Set, not List: the same dispute_id appears in many rows -> avoid duplicates.
        private final Map<Integer, Set<Integer>> disputesByLoan = new HashMap<>();

        StatusCalculator(List<DisputeEvent> db) {
            for (DisputeEvent e : db) {
                eventsByDispute.computeIfAbsent(e.disputeId, k -> new ArrayList<>()).add(e);
                disputesByLoan.computeIfAbsent(e.loanId, k -> new HashSet<>()).add(e.disputeId);
            }
        }

        /** A dispute's status = event_type of its LATEST event (by created_at). */
        DisputeEventType getDisputeStatus(int disputeId) {
            List<DisputeEvent> events = eventsByDispute.get(disputeId);
            if (events == null || events.isEmpty()) {
                // "check if the map key exists" - never NPE on an unknown key
                throw new NoSuchElementException("Unknown dispute: " + disputeId);
            }
            DisputeEvent latest = events.get(0);
            for (DisputeEvent e : events) {
                if (e.createdAt >= latest.createdAt) { // ties: later element wins (confirm with interviewer)
                    latest = e;
                }
            }
            return latest.eventType;
        }

        /** Priority: any closed_fraud > any opened > not_fraudulent. */
        LoanStatus getLoanStatus(int loanId) {
            Set<Integer> disputeIds = disputesByLoan.get(loanId);
            if (disputeIds == null) {
                return null; // unknown loan
            }
            boolean anyOpen = false;
            for (int disputeId : disputeIds) {
                DisputeEventType status = getDisputeStatus(disputeId);
                if (status == DisputeEventType.CLOSED_FRAUD) {
                    return LoanStatus.FRAUDULENT;
                }
                if (status == DisputeEventType.OPENED) {
                    anyOpen = true;
                }
            }
            return anyOpen ? LoanStatus.UNDER_INVESTIGATION : LoanStatus.NOT_FRAUDULENT;
        }
    }
```

> 找 latest 用一次线性扫即可(O(k)),不必排序(O(k log k))—— 顺口说出这个优化,但如果原代码用 sorted 且正确,别浪费时间重写,debug 题以"修对"为先。

## 四、Part 2:O(1) StatusManager(本题核心)

**思路讲解(写码前先说):**

读 O(1) ⇒ 答案必须在写入时就预聚合好。为每个 loan 维护两个计数器:`openDisputes`、`closedFraudDisputes`。判定变成:

```text
closedFraudDisputes > 0 -> fraudulent
openDisputes > 0        -> under_investigation
否则                     -> not_fraudulent      (closed_not_fraud 根本不需要计数器)
```

**关键动作:一条新事件到来 = 该 dispute 的状态"迁移"。必须先把旧状态的贡献 -1,再把新状态 +1。**只加不减是本题最经典的错误 —— closed_fraud 被 reopen 后,fraud 计数必须掉回去。因此还需要第二张表:`dispute_id → 最新状态`。

**参考实现(已验证):**

```java
    // ===================== Part 2: O(1) event consumer =====================
    static class StatusManager {

        /** Latest known event per dispute. */
        private static class DisputeState {
            long latestCreatedAt;
            DisputeEventType latestType;
            int loanId; // a dispute belongs to exactly one loan
        }

        /** Aggregated counters per loan - the whole point of O(1) reads. */
        private static class LoanCounts {
            int openDisputes;
            int closedFraudDisputes;
            // closed_not_fraud needs no counter: it is the fallback status.
        }

        private final Map<Integer, DisputeState> stateByDispute = new HashMap<>();
        private final Map<Integer, LoanCounts> countsByLoan = new HashMap<>();

        /** O(1). The core move: DECREMENT the dispute's previous status, then increment the new one. */
        public void addEvent(DisputeEvent event) {
            DisputeState state = stateByDispute.get(event.disputeId);
            if (state == null) {
                state = new DisputeState();
                state.loanId = event.loanId;
                stateByDispute.put(event.disputeId, state);
                countsByLoan.computeIfAbsent(event.loanId, k -> new LoanCounts());
            } else {
                if (event.createdAt < state.latestCreatedAt) {
                    return; // stale out-of-order event: the latest event already defines the status
                }
                // undo the contribution of the previous latest event
                applyDelta(countsByLoan.get(state.loanId), state.latestType, -1);
            }
            state.latestCreatedAt = event.createdAt;
            state.latestType = event.eventType;
            applyDelta(countsByLoan.get(state.loanId), event.eventType, +1);
        }

        private static void applyDelta(LoanCounts counts, DisputeEventType type, int delta) {
            if (type == DisputeEventType.OPENED) {
                counts.openDisputes += delta;
            } else if (type == DisputeEventType.CLOSED_FRAUD) {
                counts.closedFraudDisputes += delta;
            }
        }

        /** O(1). Unknown loan -> null. */
        public LoanStatus getLoanStatus(int loanId) {
            LoanCounts counts = countsByLoan.get(loanId);
            if (counts == null) {
                return null;
            }
            if (counts.closedFraudDisputes > 0) {
                return LoanStatus.FRAUDULENT;
            }
            if (counts.openDisputes > 0) {
                return LoanStatus.UNDER_INVESTIGATION;
            }
            return LoanStatus.NOT_FRAUDULENT;
        }
    }
```

配套的类型定义:

```java
    enum DisputeEventType { OPENED("opened"), CLOSED_FRAUD("closed_fraud"), CLOSED_NOT_FRAUD("closed_not_fraud");
        final String value;
        DisputeEventType(String value) { this.value = value; }
    }

    enum LoanStatus { UNDER_INVESTIGATION("under_investigation"), FRAUDULENT("fraudulent"), NOT_FRAUDULENT("not_fraudulent");
        final String value;
        LoanStatus(String value) { this.value = value; }
    }

    static class DisputeEvent {
        final long createdAt; final int disputeId; final int loanId; final DisputeEventType eventType;
        DisputeEvent(long createdAt, int disputeId, int loanId, DisputeEventType eventType) {
            this.createdAt = createdAt; this.disputeId = disputeId; this.loanId = loanId; this.eventType = eventType;
        }
    }
```

## 五、测试清单(面试时按顺序过,第 6 步是灵魂)

```java
StatusManager m = new StatusManager();
m.addEvent(evt(1, d1, loan100, OPENED));           // loan100 -> UNDER_INVESTIGATION
m.addEvent(evt(2, d1, loan100, CLOSED_NOT_FRAUD)); // loan100 -> NOT_FRAUDULENT
m.addEvent(evt(3, d2, loan100, OPENED));           // loan100 -> UNDER_INVESTIGATION
m.addEvent(evt(4, d2, loan100, CLOSED_FRAUD));     // loan100 -> FRAUDULENT
m.addEvent(evt(5, d1, loan100, OPENED));           // loan100 -> FRAUDULENT   (fraud 优先于 open)
m.addEvent(evt(6, d2, loan100, OPENED));           // loan100 -> UNDER_INVESTIGATION  <- 灵魂用例:
                                                   //   reopen 撤销了 closed_fraud,只加不减的实现在这里死
m.getLoanStatus(999)                               // -> null(没见过的 loan)
// 乱序:先来 (t=7, d3, CLOSED_FRAUD) 再来 (t=0, d3, OPENED) -> 旧事件被忽略,仍 FRAUDULENT
```

## 六、坑清单(按翻车概率排序)

1. **只加不减** —— 不维护 dispute 当前状态、事件一来就 `count++` → reopen 后 fraud 计数永远 > 0。必须"先减旧、再加新"。
2. **把"见过 closed_fraud"当永久事实**(同 1 的语义版)—— 题干明确说 dispute 可以 reopen,状态以最新事件为准。
3. **map key 不存在就取值** —— Java 里 `get` 返回 null 后直接调方法 = NPE;全部用 `computeIfAbsent` / 判空。这正是官方提示 2。
4. **enum 与字符串混比** —— `"opened".equals(DisputeEventType.OPENED)` 永远 false;统一在解析边界转成 enum。这是官方提示 1 的常见形态。
5. **per-loan 的 dispute 索引用 List 存出重复**(每条事件 add 一次)→ 用 Set。
6. **动了 "DO NOT modify" 区域** —— harness 用重复 loan_id 反复调用,你只能保证自己的方法幂等,不能改它。
7. **乱序/同刻事件没问** —— 主动问:"事件保证按 created_at 递增吗?created_at 相同怎么算?" 然后按约定处理(参考实现:旧于当前最新的忽略;相同时间后到覆盖)。
8. 未知 loan 返回值 —— 签名是 `LoanStatus | None`,返回 `null`,别抛异常也别默认 not_fraudulent。

## 七、必备 Follow-up 问答

**Q:为什么 Part 2 比 Part 1 快?复杂度各是多少?**
Part 1 每次查询要拉全量事件重算:单 loan O(E_loan)(若排序则 ×log)。Part 2 把计算摊到写入:`addEvent` O(1),`getLoanStatus` O(1),空间 O(D + L)(D=dispute 数,L=loan 数)。这就是 read-optimized view / 物化视图的思想 —— 题干原话 "event consumers give a read-optimized view",把这句还给面试官。

**Q:closed_not_fraud 为什么不用计数?**
三态里它是兜底:fraud 计数=0 且 open 计数=0 时的唯一可能。少一个计数器 = 少一处出错。

**Q:上 production 前改什么?**
状态外置(Redis/DB)防重启丢失;消费幂等(事件带唯一 id,去重);乱序治理(按 dispute_id 分区保序,或事件带版本号);快照 + 事件重放做恢复与对账(定期与 DB 全量重算比对,防计数漂移);metrics(各状态 loan 数、消费 lag)+ 告警;并发下按 key 分区单线程,或 dispute 级锁。

**Q:如果要支持 `get_all_fraudulent_loans()` 也 O(1)/O(k)?**
再维护一个 `Set<Integer> fraudulentLoans`:fraud 计数 0→1 时加入,1→0 时移除 —— 展示同一模式的推广。

## 八、60 分钟时间分配

| 时间 | 做什么 |
|---|---|
| 0–8 min | 通读规则,自己复述三条优先级 + "最新事件定状态",确认平局/乱序约定 |
| 8–20 min | Part 1:跑测试 → 定位 bug(对照两条提示)→ 修复 + 复跑 |
| 20–25 min | Part 2 思路讲解:两张表 + 先减后加,拿到面试官认可再动手 |
| 25–45 min | 写 StatusManager + 按第五节清单跑用例(重点秀"灵魂用例") |
| 45–55 min | Follow-up:复杂度对比、production 改造 |
| 最后 5 min | Buffer / 反问 |
