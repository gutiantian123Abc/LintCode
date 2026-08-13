# IXL VO 考前复习计划(周三晚 + 周四上午)

> 面试:**周四 8/13 下午 1:00 PM ET**,Google Meet + HackerRank CodePair,2.5–3 小时 = 2 轮技术 + Kevin 收尾。**无 system design**(HR 确认)。通过标准 = 互动质量。
> 本计划假设:今晚 23:30 前睡,明早 7:00 起、8:00 开始、12:15 收工。每块相互独立,起晚了按第 3 节裁剪规则砍。

## 0. 三条总原则

1. **不学新东西**。今晚和明早的一切都是巩固已会的;新题、新面经、新 follow-up 一律不碰。
2. **出声 > 默读**。所有标注"述"的内容必须念出声——这场 VO 考的就是说。
3. **睡眠优先级最高**。任何复习与 7 小时睡眠冲突时,选睡眠。

## 1. 今晚(现在 → 23:30 强制熄灯)

| 时间 | 做什么 | 具体 |
|---|---|---|
| 22:45–23:05 | **限时手写 LC 295**(今晚唯一必做) | 合上所有资料,空白编辑器写全:双堆声明(lambda 比较器)+ addNum 三步舞 + findMedian(双边 long + /2.0 + 空防御) |
| 23:05–23:15 | 对照核对 | 打开 MedianFinder295.java 逐行对照;错哪补哪,只重看错的那一节 |
| 23:15–23:25 | 收尾两件 | 295 坑清单(§11)扫一遍;30 秒 pitch(§13)出声念一遍 |
| 23:30 | **睡觉** | 手机放远,定 7:00 闹钟 |

手写一次全对 → 提前收工直接睡。三步舞写错 → 明早 8:00 的 295 块加一次重写。

## 2. 明早(8:00 → 12:15)

代号:**写** = 合卷手写;**述** = 出声口述;**扫** = 快速扫读。

| 时间 | 题目 | 怎么复习 |
|---|---|---|
| 8:00–8:30 | **LC 295**(押题 #1,实锤挂人题) | 写:三步舞 + findMedian 再默一遍(5 分钟)。述:两条不变量;§6 手走 5,2,8,1;FU1 计数桶;FU2 三段定位;FU3 四关键词(lazy deletion / 记账 / 浮顶才清 / 逻辑大小)。扫:§6.5 三状态表、§11 坑清单 |
| 8:30–9:10 | **Task Scheduler**(押题 #2) | 述:findCycle 三步(走到"任意 visited 节点"停,至多一圈)。扫:FU3 ρ 形图四精确点;FU6 三档并发(CoarseLock 20.7s → Snapshot 4ms → BrokenSnapshot CME → Actor)。述:6.5 "History is recorded; the future is recomputed" |
| 9:10–9:45 | **Italic Parser**(押题 #3) | 扫:主循环四出口对照表、tryReadTag、Segment(text + Set&lt;String&gt; styles,nested 的原因)。述:不平衡 tag 怎么兜底。扫:ArrayDeque 平替表(Stack 用 push/pop/peek;Queue 用 add/poll/peek;null 禁令) |
| 9:45–9:55 | 休息 | 站起来活动,别刷手机 |
| 9:55–10:20 | **ABC Log IDs**(押题 #4) | 述:ArrayList 主解流程一遍。扫:TreeSet 版 FU2 对照读法;FU3(k 条 B);FU4 流式(两窗口重叠才是真下界;唯一候选即定案) |
| 10:20–10:50 | **Search Bar 读 Doc 题**(#5,考读文档能力) | 述:六步读题流程——这是真正被打分的技能。扫:R1–R6 + 词汇钉死表;管线三段(枚举 k → 配额 → 渲染)。述:三个易错点——strict > 保住更小的 k;extras = bestShown − bestChosen.size();quota 循环走 results 而不是 bestChosen(相关性信息只在 results 里) |
| 10:50–11:20 | **Score Server**(并发主战场) | 述:心脏九行逐行(seq 去重 / pending 缓冲 / nextSeq 前缀推进)+ 一句分工"锁管同时,seq 管乱序"。扫:complete = 放答案 + 响铃,join = 睡等 + 拿到;FU1 简单英语版(global lock 430ms → per-client 105ms);FU3 幂等一行 `if (m.seq < nextSeq) return;` |
| 11:20–11:45 | **Random Rectangles**(随机 + 测试) | 述:AABB 四条件 + `nextInt(W - w + 1)` 的 +1。扫:placeWithRestarts(33.3% → 100%);dice 7.6% vs 名单 18%;golden vs property 测试。FU 全部口述级 |
| 11:45–12:00 | **Kevin 轮台词**(必做,出声) | *"IXL is my first choice — if the offer comes, I'm accepting it."* 全程不提其他公司;准备好要问他的 2–3 个问题(团队日常、新人 ramp-up、下一步时间线) |
| 12:00–12:15 | 机动 | 哪块超时了补哪块;都没超:10 分钟手写 Kth Largest 热身(PriorityQueue 手感),或扫 Game Credit §11 的 30 秒总结 |

## 3. 裁剪规则(起晚了 / 某块超时)

**永不砍**(按顺序死保):① 295 全块 ② Kevin 台词 ③ Search Bar 六步口述 ④ Scheduler findCycle 口述。

**先砍**(从下往上):Game Credit(保险级,HR 说无 SD)→ Rectangles 的 FU 细节(保 AABB + restarts)→ ABC 的 FU3/FU4 → Score Server 的 FU2/FU5。

每块红线 = 计划时间 +10 分钟,到线强制翻页——考场不会补时,复习也不要。

## 4. 考前 45 分钟(12:15–12:55)

- 12:15–12:35 午饭(熟悉的食物,七分饱),咖啡因按平时习惯,别加量
- 12:35–12:50 设备:Chrome 登录 Google Meet 和 HackerRank 各测一次;摄像头 / 麦克风 / 耳机;充电器插上;手机静音放手边(备用热点);水杯;关掉所有通知和无关窗口
- 12:50 洗手间;12:55 进会议室等待
- 最后一句:两轮技术考的是**和你一起工作是什么感觉**——先澄清、先声明不变量、写码持续 narrate、主动报边界、主动说测试。卡住就把思路说出声,沉默才是唯一真正丢分的方式。

## 5. 八题速记表(考场前最后一眼)

| 题 | 一句话核心 | 最容易翻的坑 |
|---|---|---|
| LC 295 | 劈两半、顶对顶;三步舞零分支 | `b - a` 溢出;(a+b) 用 int 相加;默认 min-heap 方向反 |
| Task Scheduler | 建图 + findCycle;历史已记录、未来重算 | walk 停在"任意 visited 节点",不是出发点 |
| Italic Parser | 扫描 + 栈配平 tag;主循环四出口 | 不平衡 tag 的兜底出口 |
| ABC Log IDs | ArrayList 三指针扫描 | 重复 ID / 边界;TreeSet 版留作 FU 弹药 |
| Search Bar | 读懂 R1–R6 → 枚举 k → 配额 → 渲染 | quota 循环走 results,不是 bestChosen |
| Score Server | seq 去重 + pending 缓冲 + nextSeq 推进;锁管同时、seq 管乱序 | `complete()` 是放答案 + 响铃,不是赋值 |
| Random Rectangles | 随机放 + AABB 拒绝 + restarts | `nextInt` 的 +1;place 卡死要 restarts |
| Game Credit(保险) | 账本为事实、余额为缓存;条件 UPDATE 一句顶三行 | 只扫 §11 三十秒总结,不再投入 |

## 6. 全局武器(每题通用,挣的就是互动分)

开场澄清 2 分钟 → 说方案要 buy-in → **先声明不变量再写代码** → 写码持续 narrate → 写完主动手走验证 → 主动报边界 → 说测试思路(golden / property / fuzz)→ 每个 FU 先给 baseline 再给升级路径。

## 7. 文件清单

| 文件 | 用途 |
|---|---|
| IXL_VO_LC295_终极版.md + MedianFinder295.java | 押题 #1,FU1/2 完整讲解已折入 |
| IXL_VO_TaskScheduler_终极版.md | 押题 #2 |
| IXL_VO_ItalicParser_终极版.md | 押题 #3 |
| IXL_VO_ABCLogIds_终极版.md | 押题 #4 |
| IXL_VO_SearchBar_终极版.md | #5,六步读 doc 流程 |
| IXL_VO_ScoreServer_终极版.md | 并发 + CompletableFuture(含两个零基础速成附录) |
| IXL_VO_RandomRectangles_终极版.md | 随机、AABB、测试(含 Random/shuffle 速成附录) |
| IXL_VO_GameCredit_SystemDesign_还原.md | 保险级,只看 §11 |
