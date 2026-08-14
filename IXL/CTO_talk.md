# IXL CTO 轮终极攻略 — Joe Kent · 周一 8/17 · 6:30 PM ET · 30 分钟 · Google Meet

> **本轮画像(全部情报合成)**:不写码,但**内核是技术对话**——聊工程观念、代码品味、难题经历;形式像 BQ,评的是 technical excellence + culture fit。你的**提问区是重头戏**,Kevin 单独强调。Joe 人很好、**爱追问细节**。多听少说。
> **一句定位**:这 30 分钟他在回答一个问题——"这个人的工程品味和好奇心,值不值得放进我的团队?"

---

## 0. 情报卡(按证据等级)

| 级别 | 来源 | 内容 |
|---|---|---|
| A | Kevin(负责 HR)电话原话 | ① 必须准备**产品具体**的提问(方向、未来),不要 generic;② **禁忌:Don't give product recommendations**,除非 CTO 直接问;③ 多听少说,答对就收口 |
| A | 内推同学(在职) | Joe 人好不用紧张;会问:为什么开始写代码 / 平时喜欢做什么 / 希望将来的组什么样 / 什么是好的 engineering principle / 什么是好代码 / 遇到过什么难题;**Joe 比较会追问细节**;注重 **technical excellence**,然后 culture fit;工程文化对标 Google 风格 |
| B | Medium 面经(Joe 亲口) | 他的四条标准:**communication / quality of code / optimization / cooperation** |
| B | Glassdoor 多条 | IXL 头号 BQ:*"Why are you interested in working in EdTech at a company like IXL?"* |
| A | 内推人首封邮件(7/13) | 公司**非常看重 culture fit + 教育热情**;建议**表达比较确定会加入的意愿**;**少在面试阶段聊身份问题**(offer 后再沟通);产品要做 research(重点 IXL + 各收购品牌);**近期收购 MyTutor**(已验证公开:2025/5,£34.4M,IXL 首次收购海外公司);**内部有融合项目**(如整合 Vocabulary.com、ABCya 进 IXL);公司价值观:好奇心强、爱解决问题、**用技术带来实际影响/帮助他人的成就感**、技术+沟通+协作+一定领导力;氛围:不卷、家庭式、开学季前稍忙、results-driven、乐于助人 |
| B | 官方 JD | **San Mateo HQ onsite 岗**(你在 Atlanta,简历已印 "Open to relocation to the San Francisco Bay Area" → 话题若出现,答干脆的 yes);**Java 被点名**;3+ 年经验;*"Passion for improving education through technology"* 是**明文硬性要求**;职责原文:back-end wiring + application logic + **UI**、from scratch 与 existing code base 并重、design/coding/documenting/**testing**/debugging/**tuning**、跨多团队协作、估算与可行性评估;引用级数据:**1 in 4 美国学生用 IXL**、Rosetta Stone 25 种语言、Wyzant 全美最大 tutor 社区(300+ 科目);文化词:collaboration、kindness and respect、passion/tenacity/authenticity;薪资带 $116–150k(**offer 阶段的知识,周一绝不提**) |
| A | 你自己交出的信息 | 你已告诉 HR:**"我约 50% 的英语是用 Rosetta Stone 学的"** → 大概率进了面试官 notes,**Joe 主动聊 Rosetta Stone 的概率显著上调**(应答脚本见 Q10);最新简历已提交,每个加粗数字都是追问按钮(深挖预测见 6.5 节) |
| A | 内推同学(第二轮回复) | **做自己就行,想问什么都可以**;简历深挖有可能,但**大概率是很 general 的 BQ**;提问**多问 strategy**(他举的例子:Core Tech 在公司进程里怎么支持 IXL 的多个 brand;进组后什么工作适合你);**别问 tech stack**——跟 CTO 没什么好聊的;**Joe 技术很厉害** |

两个说法的调和:HR 说"非技术",同学说"技术"——**都对**:不考写码(非技术形式),考的是你**谈论技术的方式**(技术内核)。

---

## 1. 三条铁律(Kevin 红线,考场纪律)

### 铁律一:绝不主动给产品建议(头号禁忌)

你准备过的 Rosetta Stone 改进想法(hint ladder、对话陪练)**全部封存**。话题再接近也只说正面体验。为什么这是禁忌:一个用了 30 分钟的候选人向掌管产品多年的 CTO 提改进意见,无论内容对错,姿态就已经输了。

**唯一例外**:Joe **直接**问 "Is there anything you'd improve?" 才启用应急版,三层防护:

> *"As a user I genuinely enjoy it — the immersion method is what makes it different, and TruAccent surprised me with how well it handles pronunciation. One small moment I noticed as a learner: when I couldn't infer a word from the pictures, I sometimes wished for one more contrasting example before moving on. But I'd trust the team's data over my single experience."*

结构:真诚赞(具体到 feature)→ 以"一个学习者的瞬间"降格(不是 recommendation,是 observation)→ 主动把权威交还("你们的数据比我的个例可信")。**绝不出现 "you should / I would change"**。

### 铁律二:多听少说

- 答案基准长度 **30–60 秒**,答完核心**就停**——不追加 "and also..."。
- 展开留给他的追问——**追问细节是 Joe 的风格**,这正是你要的互动形态:他问一层你答一层,好过你一口气倒五层。
- 他说话时绝不打断;他讲公司/产品时,认真听 + 简短回应("that makes sense" / 一个跟进小问题)= 最好的表现。

### 铁律三:提问必须"产品具体"

Kevin 的原话画像:想象一个**对一切产品了如指掌、干了很多年**的 CTO 想听到什么。generic 问题(公司文化怎么样/成长机会)= 告诉他你只是来找工作的。第 3 节的问题库就是按这个标准造的。

---

## 2. 预测题库(同学情报逐条展开,每题带答案框架 + 停止线)

> **权重更新(同学二轮回复)**:开局大概率是 **general BQ**(Q1/Q2/Q6/Q7 这个档),简历深挖(Q8)是"可能但非主线"。但 Q5 难题故事照备不误——Joe 的追问不挑话题,**你说到哪他就挖到哪**,所以你主动说出口的每一句都要挖得动。

### Q1. "Why did you start coding?"(起源故事)

框架(60 秒):**具体的第一次 → 当时的感受 → 为什么留下来**。要真实、具体到某个时刻,不要"我从小热爱计算机"。
填入你自己的真实经历,结构参考:

> *"The first time it clicked for me was ____(具体场景:某门课/某个小工具/解决的某个真实问题). What hooked me wasn't the code itself — it was ____(看到它真的跑起来/有人真的用它). I've stayed because backend work keeps giving me that: you build something correct and reliable, and real things depend on it."*

**停止线**:讲完"为什么留下来"就收,不展开职业规划。

### Q2. "What do you like working on?"

答案主线(你的真实优势):可靠性导向的后端工程。

> *"I like the kind of backend work where correctness is non-negotiable. At Capital One that means services where money can't be wrong — so I've gotten deep into concurrency, defensive design, and testing. My favorite moments are finding the invariant that makes a hard problem simple."*

"invariant" 这个词值得说——这两周你真的在用它思考(295 的两条不变量、Score Server 的 seq、账本恒等式)。如果他追问,你有无穷弹药。

### Q3. "What does good code look like?"(他的招牌领域)

三点式,每点一句:

> *"Three things. It's **readable** — code is communication with the next engineer, who might be me in six months. It's **tested** — not just happy paths; the tests document what the code promises. And it's **simple** — the least clever solution that's fully correct. If I have to choose, I optimize for the person who has to change it safely later."*

追问弹药:他若问举例——LC 295 的经验直接化用:"分支多的写法每个分支都是雷,零分支的写法把正确性交给结构而不是交给小心"(不点题目名,说思想)。

### Q4. "What are good engineering principles?"

准备三条,每条 = 原则 + 一句自己的实践:

1. **Correctness first, then speed** — *"State the invariant before writing the code; test against an oracle when one exists."*
2. **Design for failure** — *"Assume retries, assume concurrency, assume bad input — idempotency and defensive checks aren't extras."*(你的 Score Server/Game Credit 功底)
3. **Communicate while you build** — *"Narrate design decisions, write code reviews that teach, leave the reasoning in the commit."*(呼应他的 communication 标准)

### Q5. "Tell me about a hard problem you solved."(必深挖,准备 1 主 1 备)

**Joe 会追问细节**——选你**每一层都真懂**的 Capital One 项目。周末作业:按下面三层写出来、背熟:

- 表层(60 秒 STAR):背景 → 难在哪 → 我做了什么 → 结果(有数字最好)
- 第二层(每个技术决定的 why):为什么选这个方案?排除了什么?tradeoff 是什么?
- 第三层(细节):具体怎么实现/怎么测/出过什么岔子/**学到了什么**
- **诚实红线**:被追问到不懂的层,直接说 *"I haven't gone that deep on that part — my understanding stops at ____."* 装懂在爱追细节的面试官面前必死,诚实 + 好奇是加分姿态。

### Q6. "What do you want your future team to look like?"

对标 Google 风格工程文化(同学原话),平衡"学"与"给":

> *"A team that takes code review seriously — review as teaching, not gatekeeping. Strong testing culture. People who explain their reasoning and want to hear yours. I want to learn from engineers better than me, and I want my own bar to pull my teammates up too."*

### Q7. "Why IXL / why EdTech?"(头号 BQ,大概率出现)

产品叙事 + 工程叙事,**只表达被吸引,零评价**:

> *"For me it's personal. A meaningful part of my English actually came from Rosetta Stone — I used it seriously when I was preparing to come to the U.S. for Georgia Tech. So the mission isn't abstract to me; I'm evidence that these products change trajectories. And the scale is real — one in four U.S. students uses IXL. On the engineering side: at Capital One I build services where correctness is non-negotiable, and Core Technology is that same discipline applied to learning, across the whole family. That combination is why IXL is my first choice."*

叙事弧线:**个人故事(我是产品的成果)→ 规模(1 in 4,JD 官方数据)→ 手艺(可靠性纪律)→ 承诺(first choice)**。这 40 秒是全场权重最高的一段,背到出口成章。

注意:提到 Rosetta Stone 只说 "I've used it as a learner"——正面兴趣信号,**不接任何评价**。

### Q8. 简历走读(等同学回复确认深度;先按"会挖"准备)

90 秒版 self-intro:学校 → Capital One 在做什么(一句话系统画像)→ 拿手领域(并发/可靠性/测试)→ 为什么坐在这里。每段简历项目至少准备两层细节。

### Q9. 杂项可能:working style / 怎么处理分歧 / 怎么学新东西 / location

一律短答 + 具体一例。分歧类答案的安全模板:*"Data and respect — I state my case with evidence, listen for what I'm missing, and commit fully once we decide."*

**location/relocation 若被提及**(JD 是 San Mateo onsite 岗,你在 Atlanta):干脆无保留——*"Absolutely — I'm ready to relocate to San Mateo."* 一秒都不犹豫,不加条件;犹豫是这轮唯一真正致命的信号。

**跨栈若被提及**(JD 职责含 UI):你有真实弹药,不用泛泛表态——*"I've owned features end-to-end before — Spring Boot APIs plus the Angular UI at Seek Now. Backend is where I'm deepest, but I'd rather own the feature than own a layer."*

### Q10. "你用过 Rosetta Stone?讲讲。"(你交给 HR 的信息大概率在他 notes 里——**出现概率上调,必须出声排练**)

> *"Yes — it's genuinely part of my story. When I was preparing to come to the U.S. for Georgia Tech, I used Rosetta Stone seriously — I'd estimate a meaningful share of my English came from it. What worked on me was exactly the design: no translations anywhere, so I had to start thinking in English instead of converting from Chinese, and the speech feedback gave me confidence to actually speak. I'm the kind of user these products are built for — that's a big part of why this role matters to me."*

**守则不变**:全程正面、零建议、不聊任何"要是能……就好了"。只有他**明确**问 "anything you'd improve?" 才启用第 1 节的三层应急版。这一题答好 = 全场最难忘的 45 秒:你不是"对教育感兴趣的候选人",你是**产品亲手教出来的候选人**。

### Q11. "为什么 Capital One 一年半就想走?"(时间线必然可见,必备)

> *"I'm not leaving something — I'm going toward something. Capital One has been great: production rigor at bank scale, systems where correctness is non-negotiable, and I'm grateful for it. But the pull toward education is real and specific: I'm a Rosetta Stone learner myself, my favorite project at Capital One was literally a learning tool — the knowledge assistant — and IXL is where all of that points. This is a deliberate move, not an escape."*

红线:**不说 Capital One 一个坏字**;"toward, not away" 是整段的骨架。

---

## 3. 你问 Joe 的问题库(重头戏,Kevin 单独点名)

**设计标准**:**strategy 优先**(同学原话"多问些 strategy")+ 产品具体(Kevin 标准)+ 让"什么都知道的人"有的聊 + 零批评暗示。
**主用 3 个,按这个顺序问——叙事弧线:公司战略 → 产品未来 → 我在其中的位置(收官即承诺信号)。**

### 主用

**P1(Core Tech × 多品牌战略——双重认证:Kevin 的"产品具体" + 同学几乎原话推荐)**:
> *"With the family growing — IXL, Rosetta Stone, TPT, and now MyTutor as the first international acquisition — how does Core Technology think about shared platform versus per-product infrastructure? What's the direction there?"*
为什么好:同学的例子和这题几乎一字不差;点出 MyTutor 是"**第一次海外收购**"= 你连最新动态都做了功课;这是 Joe 每天在想的问题。
**背景知识使用规则**:内推人提到的内部融合项目(Vocabulary.com/ABCya 整合进 IXL)**只做你理解答案的背景,不要当成自己的发现说出口**——如果 Joe 聊到融合方向,你自然接住即可;若被问"你怎么了解这么多",大方说 *"I did my research, and a friend at IXL has told me great things"*(你是正式内推,提朋友完全合法)。

**P2(Rosetta Stone 方向——Kevin 的"某个具体产品的方向、未来"点名形态)**:
> *"I've been a Rosetta Stone user, so I'm genuinely curious: with conversational AI moving so fast, how do you think about evolving the immersion method? What's the long-term vision for language learning at IXL?"*
为什么好:具体产品 + 未来方向 + "user" 身份表达真实兴趣,零评价。
**禁忌变体(长这样的一律不问)**:~~"What would you improve about Rosetta Stone?"~~ ——邀请自己发表产品意见的自杀问法。

**P3(进组做什么——同学直接建议的"fit"问题,收官用)**:
> *"If I joined Core Technology, what kinds of projects would an engineer with my background likely take on in the first year — and which of them matter most to where the platform is heading?"*
为什么好:同学原话"问问你之后进去组有什么事比较适合你的";隐含承诺信号(已经在想象入职),又把话题交回他手里。

### 备用(时间富余或气氛引导时用,按序)

**P4(战略挑战——题库 #25 适配,CTO 最有的聊的类型)**:
> *"What's the biggest technical challenge Core Technology is working through right now?"*
语气要点:"挑战 = 有意思的问题",纯好奇,不是"你们哪里不行"。

**P5(技术演进)**:*"IXL's Real-Time Diagnostic already personalizes practice — how are you thinking about what AI changes next in personalized learning?"*

**P6(半年期待——题库 #22,与 P3 同方向,二选一别都问)**:
> *"What would you expect from an engineer at my level in the first six months?"*

**P7(暖场收尾——题库 #20,气氛好时的最后一问)**:
> *"What advice would you give an engineer at the start of their career at IXL?"*
Joe 人好——这题把"当前辈"的机会递给他,junior 问出来真诚不掉价。

**P8(个人视角——题库 #3/#23 合体)**:*"You've been building this for a long time — what's kept it interesting for you?"*

### 不问清单(提问区专属红线)

- **tech stack 类**(用什么语言/框架/数据库)——同学原话:跟 CTO 没什么好聊的。这是入职后跟同事聊的话题,问 CTO 显得层次不够。
- 薪资/福利/流程类(Kevin 的领域)。
- 任何"你们为什么不做 X"式的隐性批评。

### 同学给的 1p3a 27 题库:CTO 轮逐题筛选

原帖自标"1–6 问 IC、7–13 问 manager"——它是通用武器,你的战场有特殊规则(Kevin:不 generic;同学:多 strategy)。筛选结果:

| 题库编号 | 判定 | 说明 |
|---|---|---|
| #9、#22、#25、#20、#3/#23 | ✅ 已适配收编 | 分别成为 P3、P6、P4、P7、P8 |
| #16(深专 vs 广博) | ✅ 可临场用 | 工程观念类,CTO 合适:*"Do you look for depth in one area or breadth across many — how does that play out here?"* |
| #6/#7/#8/#10/#11/#13 | 🔒 留给以后 | 都是好问题,但属于 **hiring manager / team matching** 轮——组规模、PM 关系、项目成功标准,到那轮再掏 |
| #1/#5/#21/#26/#27 | ❌ 这轮别问 | daily workflow、team bonding、hackathon——generic,踩 Kevin 红线 |
| #14/#15(转组/转方向支持) | ❌❌ 严禁 | 还没进门先问怎么离开这个组 = red flag |
| #19(钓结果:"我哪里可以提高") | ❌❌ 严禁 | 在 CTO 面前显不自信;这轮结果不靠钓 |
| #24(你最不 enjoy 公司哪里) | ❌❌ 严禁 | 邀请 CTO 说公司坏话——产品禁忌的近亲 |
| #12(你评价工程师最看重什么) | 💡 不必问 | **答案你已经有了**(四条标准:communication / quality / optimization / cooperation)——把它织进你的回答里,比问出来值钱十倍 |
| #2/#4/#17/#18 | — 不适用 | 场景不符(前雇主对比/彩票/城市/"没体验过的独特之处"对老 CTO 不成立) |

**红线**:这轮**不问** salary / benefits / WFH / 假期;不问任何能被读成质疑产品决策的问题;不问 Kevin 能答的流程性问题(下一步/时间线——那是 Kevin 的)。

---

## 4. 30 分钟推演(听说比 ≈ 6:4)

| 分钟 | 内容 | 你的姿态 |
|---|---|---|
| 0–3 | 寒暄 + self-intro | 90 秒版,微笑,能量适中 |
| 3–20 | 他问你(Q1–Q9 池子) | **短答 → 等追问 → 再展开一层**;他讲话时认真听 |
| 20–27 | "Any questions for me?" | P1 → P2(→P3),每个答案认真听 + 自然跟进半句 |
| 27–30 | 收尾 | 一句:*"This conversation made me even more excited about the role — thank you for the time."* 不加码 |

---

## 5. 红线清单(考前 10 分钟最后一眼)

| 禁 | 原因 |
|---|---|
| **主动给产品建议**(头号) | Kevin 原话禁忌;姿态问题,内容对也输 |
| 长篇大论/抢话/打断 | 铁律二;言多必失 + 位置感 |
| generic 提问 | Kevin 原话:显得只是来找工作 |
| 问薪资/福利/WLB/流程 | 不是这轮的话题 |
| 提其他公司/流程 | 被问到就是那句:*"IXL is my first choice — if the offer comes, I'm accepting it."* |
| 批评前东家 | 永恒红线 |
| 装懂 | Joe 追问细节;说 "my understanding stops at X" 反而加分 |
| 过度恭维 | 他见得多了 |
| SmartScore 争议等任何产品负面话题 | 铁律一的延伸 |
| 问 tech stack | 同学原话"没什么好聊的,毕竟 CTO"——层次不匹配 |
| **主动聊身份/visa** | 内推人首封邮件原话:少在面试阶段聊,offer 之后再沟通;被直接问就一句事实带过,不展开 |

---

## 6. 他的四条标准 × 你的表现方式

| Joe 的标准(他亲口说过) | 这 30 分钟里怎么体现 |
|---|---|
| communication | 短答有结构;听得认真;追问答得清晰 |
| quality of code | Q3/Q4 的答案 + 例子里的测试/不变量思维 |
| optimization | 难题故事里带一个性能/效率的具体改进(有数字最好) |
| cooperation | Q6 团队答案 + 分歧处理模板 |

内推人补充的公司价值观,对应落点:**好奇心**→ 你的提问区 + 听他讲时的真实兴趣;**用技术带来实际影响/帮助他人**→ why IXL 的核心句(real kids actually learning);**乐于助人**→ Q6 团队答案里加一句 *"I want to be the kind of teammate people feel safe asking for help — and I ask, too"*(呼应"大家庭"氛围);**解决问题的兴趣**→ 难题故事里的享受感,不是抱怨感。

**JD 关键词回声表**(答案里自然用 JD 的词,他听着就"对味"):

| JD 原文 | 回声位置 |
|---|---|
| *testing, debugging, and tuning* | Q4/Q5——你的测试意识和性能改进故事,JD 明文要的就是这个 |
| *starting from scratch and working within the existing code base* | Q2 可加一句:两种都喜欢——旧代码是"someone's tested wisdom",从零建是"设计不变量的机会" |
| *collaborate across multiple development teams* | Q6 团队答案;cooperation 标准同款 |
| *accurate estimates, evaluate feasibility* | 难题故事里若有估算/取舍决策,点一下这层 |
| *passion for improving education through technology* | why IXL——注意这句在 JD 里是**硬性要求**,不是装饰 |

---

## 6.5 简历深挖题库(Joe 拿着你的简历追问细节时)

**总原则**:简历上每个**加粗数字**都是一个"请追问我"的按钮。每个数字必须能答三件事:**口径**(这个数怎么定义)、**测法**(怎么得出的)、**份额**(多少是你的贡献、多少是团队/业务)。第三件答不干脆的数字,主动降格("it was a team effort — my part was ___")远比被挖穿体面。

### A. Capital One · document-sync service(**推荐的 Q5 主故事**)

选它当主故事的理由:多存储一致性 + 重试/幂等 + 监控与调优——和 Core Technology 的平台工作同类,也和你最熟的领域(一致性、防重、防线思维)完全同域。

| 预测追问 | 你要备好的 |
|---|---|
| "misalignment 降 36% ——怎么定义、怎么测?" | 什么算一次 misalignment、baseline 是多少、36% 是怎么统计出来的、**剩下 64% 为什么还在**(答得出剩余部分 = 真做过) |
| "S3 / Cassandra / DocumentDB / OneLake 四个存储,谁是 source of truth?冲突怎么解?" | 写入顺序、对账思路——你的"账本为事实"知识直接迁移 |
| "retry 怎么保证不重复处理?" | 幂等键/去重——你是这题的专业户,答出 "retries are safe because ___" |
| "on-call 最糟的一次 incident?" | 备一个 45 秒事故故事,落点在"之后加了什么防线",不在"多惨" |

### B. Capital One · knowledge assistant(25,000 queries/day)

**教育桥(你的专属叙事资产)**:这条 bullet 的结尾是 "accelerating new-hire onboarding and **learning**"——你在银行里已经造过一个**学习工具**。一句桥,可织进 Q2 或 why-IXL 的追问:*"My favorite project at Capital One is literally a learning product — that's part of how I realized I want to build for learners full-time."*

| 预测追问 | 你要备好的 |
|---|---|
| "RAG 具体怎么搭的?" | chunking → embedding → Pinecone → LangGraph 编排,讲到架构层即可,别背参数 |
| "怎么评估答案质量/控制幻觉?" | 评估方式、引用溯源、人工反馈闭环——做了什么说什么,没做的诚实说 "that's a known gap" |
| "索引了 Slack/GitHub,权限怎么管?" | 敏感追问,备一句真实的访问控制做法 |
| "25,000/day 什么口径?延迟/成本?" | 数的统计方式 + 量级感 |

### C. Capital One · code-review agent(review 20→8 分钟)

这条是 Joe **"quality of code"** 价值观的天然投名状:你不只写好代码,还造了让全组代码变好的工具。预测追问:怎么对照 Jira 验收标准检查、误报率多少、团队怎么从怀疑到接受、20→8 怎么测的。备好"一次它抓住真问题的具体例子"。

### D. Seek Now · route-optimization engine(33,000 → 480,000)

⚠️ **全简历最容易被挑战的数字**(14.5 倍)。必须主动拆解,别等他皱眉:

> *"To be clear about that number: the engine didn't do that alone. Before, scheduling was manual — that capped how many cases the company could take. Automation removed the cap, the business scaled the field team, and throughput compounded. My engine was the unlock, not the whole multiplier."*

诚实拆解 = 全简历可信度上升;含糊 = 整份简历打折。其他追问:OptaPlanner 的约束和目标函数是什么;实时数据源(天气/交通)挂了怎么降级。

### E. Seek Now · portal 与 developer-productivity CLI

portal = 你 "end-to-end + Angular UI" 的实证(JD 的 UI 职责你有真货);CLI(2 小时→3 分钟)= 工具思维的第二证据,备好"之前的手工流程长什么样、错误怎么发生"。

### F. 时间线预案(一句话级)

- M.S. 2018–2023 与工作重叠 → 按事实一句带过(如是边工作边读:*"I finished my Master's part-time while working full-time."*)
- "为什么 Capital One 一年半就走?" → **Q11,必背**。

### G. AI 话题 = 你的双向资产

Joe 2026 年必然在想 AI。你有**生产级 LLM 经验**(RAG、agent、25k/day)——他聊 AI 时你不是听众,是同行:讲具体(评估难、幻觉治理、权限),不吹愿景。经验的具体性,比任何 AI 热情宣言都值钱。

## 7. 周末准备清单

**周六(填内容)**:① 写出 Q1 起源故事、Q5 难题故事(建议用 doc-sync,三层!)的真实版本;② why IXL 终稿(含 Rosetta Stone 个人故事)+ Q10 + Q11 背熟;③ P1–P3 问题背熟;④ **过一遍 6.5 节数字自检**——简历上 8 个加粗数字(36% / 25,000 / 20→8 / 30→11 / 33k→480k / 3→2 周 / 2h→3min / 1,000+),每个写下口径、测法、份额三行;⑤ 扫 IXL 官方 blog + MyTutor 收购新闻(30 分钟封顶)。
**周日(练交付)**:① 出声排练全部短答,**计时 45 秒,练"停"**;② 模拟追问(可以来找我,我扮 Joe 按他的风格追问细节);③ 同学回复后更新第 0/5 节。
**周一**:① 白天正常上班,晚饭吃好;② 6:00 设备检查(摄像头/麦克/网络/安静房间);③ 6:15 扫第 5 节红线清单;④ 6:25 进 Meet 等待。

**一句话心法**:VO 证明了你会做;这 30 分钟证明你**懂分寸、有品味、真心想来**。少说,听好,问准——收官。
**同学的定心丸**:Joe 人很好,"做自己就行"——纪律都写在上面了,人到了场上就放松,像跟一个技术很厉害的前辈聊天(他确实是)。
