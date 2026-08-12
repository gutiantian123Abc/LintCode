# IXL — Game Credit System(System Design)真题还原(保险级备弹)

> **情报卡**:至此**第三次出现**——#20/#21(26 NG 管线)+ 本条新面经(细节最全:三个 API、要 schema、要 body/response、追问 failure 与 consistency)。面经原话:**"总体来说不算太复杂"**——NG 级 scope,单服务 + 单库就是正确答案,别过度设计。
> **你的校准**:HR 明说你的 VO **没有 system design**,Michaelina 确认 2 technical + Kevin——所以这是**保险级**:30 分钟读懂即可,不背细节;真被聊到,能讲 15 分钟就赢。
> **验证状态**:schema 与全部防护机制(原子拒绝透支 / 幂等键拦截 / CHECK 兜底 / 二次退款拒绝 / 对账恒等式)已在 sqlite 实测全绿。
> **最重要的一句**:这题**不是新知识**——它是你这两天学的并发课换成数据库词汇(见第 8 节映射表)。

---

## 1. 原题还原(英文,面试官视角)

> *Design a game credit system for video games. Players can **add** credits, **spend** credits on in-game purchases, and **refund** transactions. Design the three APIs — add, spend, refund — including request body and response format, and the **database schema**. Be ready to discuss: which parts could fail, and how do you ensure consistency?*

## 2. 开场澄清(2 分钟,附建议默认)

| 要问的 | 建议默认 | 为什么 |
|---|---|---|
| credit 和真钱的关系? | 充值购得的虚拟币;**支付渠道不在 scope 内**,我们只管账 | 砍掉支付集成,聚焦核心 |
| refund 退什么? | 退**某笔具体交易**(带 txn_id),不是退任意金额 | 决定 API 形态和防重复退款设计 |
| 允许透支吗? | 不允许,余额 ≥ 0 | 核心约束 |
| 部分退款? | 默认整笔退;部分退是 follow-up | 简化唯一性设计 |
| 规模? | 先按单库设计,聊扩展时再分片 | NG 级 scope 的正确姿势 |
| 网络重试怎么办? | 需要幂等——这是本题一半的分 | 铺垫 idempotency key |

## 3. 核心设计一句话

**账本为事实,余额为缓存。** 两张表:`transactions` 是**只追加**的账本(事实来源:审计、退款、恢复、对账全靠它);`players.balance` 是加速读的余额快照。不变量:**balance = SUM(账本带符号金额)**——这就是对账 job 每晚验的等式(实测:退款后余额 500 = 账本求和 500 ✓)。

## 4. Database Schema(实测版)

```sql
CREATE TABLE players (
  player_id INTEGER PRIMARY KEY,
  balance   INTEGER NOT NULL DEFAULT 0 CHECK (balance >= 0)   -- 兜底防线:透支在库层被拒
);

CREATE TABLE transactions (                                    -- 账本:只 INSERT,永不 UPDATE/DELETE
  txn_id          INTEGER PRIMARY KEY AUTOINCREMENT,
  player_id       INTEGER NOT NULL REFERENCES players(player_id),
  type            TEXT    NOT NULL CHECK (type IN ('ADD','SPEND','REFUND')),
  amount          INTEGER NOT NULL CHECK (amount > 0),         -- 整数!钱永远不用 float
  ref_txn_id      INTEGER UNIQUE REFERENCES transactions(txn_id),  -- REFUND 指向原交易;UNIQUE = 一笔只能退一次
  idempotency_key TEXT    NOT NULL UNIQUE,                     -- 幂等键:重试不重复入账
  created_at      TEXT    NOT NULL DEFAULT (datetime('now'))
);
CREATE INDEX idx_txn_player ON transactions(player_id, created_at);
```

每个决策的理由(边画边说):**金额用整数**(credit 数或分,float 会丢精度——经典送命题);**账本 append-only**(改历史 = 毁审计;错账用红冲——再记一笔反向交易,不改旧行);**idempotency_key UNIQUE**(实测:重复插入被库直接拒);**ref_txn_id UNIQUE**(实测:同一笔交易第二次退款被库直接拒——防重复退款不靠应用层自觉);**CHECK (balance >= 0)**(实测:越过应用层直接扣 999 被库拒——多层防线的最后一层)。

## 5. 三个 API(body / response / 错误分类)

**共同约定**:全部 POST(有副作用);body 必带 `idempotency_key`(客户端生成 UUID);response 必带新余额(客户端免二次查询);**幂等重放返回原结果**(同 key 再来一次 → 返回第一次的 response,HTTP 200,不是报错——这是幂等的正确语义);错误分类:业务失败用 4xx + 结构化错误码,**余额不足是业务结果不是系统故障,绝不返回 500**。

```text
POST /players/{id}/credits/add
body:     { "amount": 500, "idempotency_key": "uuid-a1", "source": "order_123" }
200:      { "txn_id": 789, "balance": 1500 }
400:      { "error": "INVALID_AMOUNT" }              // amount <= 0

POST /players/{id}/credits/spend
body:     { "amount": 300, "idempotency_key": "uuid-b2", "item_id": "sword_42" }
200:      { "txn_id": 790, "balance": 1200 }
409:      { "error": "INSUFFICIENT_BALANCE", "balance": 200 }   // 业务失败,附当前余额

POST /transactions/{txn_id}/refund
body:     { "idempotency_key": "uuid-c3" }           // 整笔退;部分退加 amount 字段(先问)
200:      { "txn_id": 791, "refunded_txn_id": 790, "balance": 1500 }
404:      { "error": "TXN_NOT_FOUND" }
409:      { "error": "ALREADY_REFUNDED" }
```

一句主动声明收 refund 的口子:退 SPEND = 把 credit 还给玩家(纯内部,总是可行);退 ADD = 真钱出金,要走支付渠道 + 校验余额够扣——**默认 scope 只做退 SPEND**,说出这个边界。

## 6. 一致性核心(被问概率最高的部分)

**病根:check-then-act 竞态。** 两个并发 spend 都读到 balance=500、都判"够"、都扣 400 → 透支。这就是你 Score Server 里 `count++` 三步病的数据库版。三重防护,由主到次:

```sql
-- 防线一(推荐主答案):条件更新 —— "检查+扣款"合成一条原子语句
UPDATE players SET balance = balance - 400
WHERE player_id = ? AND balance >= 400;
-- 影响行数 1 = 成功;0 = 余额不足(实测:第二笔 400 在余额 100 时恰好返回 0 行)

-- 防线二(另一种写法):行锁
BEGIN;
SELECT balance FROM players WHERE player_id = ? FOR UPDATE;   -- 锁住这一行,别人等
-- 应用层检查,再 UPDATE
COMMIT;

-- 防线三(兜底):CHECK (balance >= 0) —— 前两道全漏了,库层最后一道拒绝
```

**两表一致性**:余额更新 + 账本插入必须**同一个数据库事务**(ACID 原子性)——要么都成,要么都不成;应用在两步之间崩溃,DB 自动回滚,不会出现"扣了钱没记账"。**跨时间的一致性**靠对账 job:每晚验 `balance == SUM(ledger)`,发现漂移报警——不变量思想的生产形态。

## 7. "哪里会挂?"问答银行(面经实锤追问)

| 会挂的地方 | 怎么办 |
|---|---|
| 客户端超时,不知道成没成 | 拿**同一个 idempotency_key 重试**——成过则返回原结果,没成则正常执行。超时 + 幂等 = 安全重试 |
| 应用在"扣余额"和"记账本"之间崩溃 | 同一事务,DB 自动回滚——这正是用事务的理由 |
| 同一玩家并发双花 | 第 6 节三重防护 |
| 重复退款 | `ref_txn_id UNIQUE`,库层拒绝(实测) |
| DB 挂了 | 返回 503,客户端带幂等键重试;高可用聊主从切换,点到即止 |
| 规模上来了 | 读走只读副本;写**按 player_id 分片**(每个玩家的数据独立,天然可分);热点玩家(鲸鱼)→ 该玩家的请求排队串行化 |

## 8. ★ 你已有知识的映射表(这题为什么对你是送分)

| 你这两天学的 | 这道题里的化身 |
|---|---|
| `synchronized`(对象门锁) | `SELECT ... FOR UPDATE`(数据库行锁) |
| 原子三步合一(`count++` 病的解药) | 条件 `UPDATE ... WHERE balance >= ?`(检查+扣款一条语句) |
| Score Server 的 seq 去重 / FU3 重传去重 | `idempotency_key UNIQUE`(重试不重复入账) |
| Task Scheduler FU9 / Score Server FU4 的 journal | append-only 账本(重放即恢复,审计即历史) |
| Score Server FU1 的 partition by key | shard by `player_id`(同一把钥匙,从锁开到集群) |
| Score Server FU2 的 actor / 单写线程 | 热点玩家请求排队串行化 |
| 属性测试 / 校验器的不变量思想 | 对账 job:`balance == SUM(ledger)` |

**面试里最值钱的话**:聊到任何一行,顺手点破映射——*"this is the database version of a row-level lock / an idempotency dedup / a journal"*——展示的是概念迁移能力,比背设计模板高一个段位。

## 9. 坑清单(考场速查)

| 坑 | 后果 | 解法 |
|---|---|---|
| **钱用 float/double** | 精度丢失,送命题 | 整数(credit 数或分) |
| 先 SELECT 检查再 UPDATE,无锁无条件 | check-then-act 竞态,双花透支 | 条件 UPDATE 或 FOR UPDATE |
| 幂等重放返回报错 | 客户端重试被误导 | 同 key 返回**原结果**,200 |
| 余额不足返回 500 | 把业务结果当系统故障 | 409 + 结构化错误码 + 当前余额 |
| refund 不关联原交易 | 无法防重复退款、无法审计 | `ref_txn_id` + UNIQUE |
| 账本允许 UPDATE/DELETE | 审计失效 | append-only;错账红冲 |
| 余额和账本分两次提交 | 崩溃后两表漂移 | 同一事务 + 夜间对账兜底 |
| 上来就画微服务/消息队列 | NG 级题过度设计 | 单服务单库起步,被问 scale 才扩展 |

## 10. 15 分钟作答节奏(聊天式 SD 的正确姿势)

| 时间 | 动作 |
|---|---|
| 0–2 min | 澄清(第 2 节,重点:refund 语义、不透支、支付出 scope) |
| 2–5 min | 画 schema,边画边讲决策理由(账本+余额、整数、三个 UNIQUE/CHECK) |
| 5–9 min | 三个 API 的 body/response,讲幂等语义和错误分类 |
| 9–13 min | 一致性:竞态病根 + 条件 UPDATE / 行锁 / 事务 / 对账 |
| 13–15 min | 接"哪里会挂"的追问(第 7 节银行);聊 scale 才提分片 |

## 11. 30 秒总结陈词(背诵版)

> *"Two tables: an append-only transactions ledger as the source of truth, and a balance column as a cached snapshot — reconciled nightly. All three APIs are POSTs carrying an idempotency key, so retries are safe and replay returns the original response. Spend does check-and-deduct in one atomic conditional UPDATE — zero rows affected means insufficient balance, a 409 not a 500. Refund references the original transaction with a unique constraint, so double refunds are rejected by the database itself. Balance update and ledger insert share one transaction — a crash between them rolls back cleanly."*

**记忆钩子**:账本为事实、余额为缓存;三个 UNIQUE/CHECK 各守一门(幂等键防重试、ref 防重退、CHECK 防透支);条件 UPDATE 一句顶三行;同事务防漂移,对账兜长期;**整套东西 = 你的并发课换成 SQL 词汇**。
