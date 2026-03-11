# MAS Runtime 逻辑梳理报告

基于当前 `hzapp-module-erplus-ai` 模块的实现代码，以下是对 MAS（多智能体系统）运行时逻辑的详细梳理：

## 1. 执行流程与支持的操作

核心执行流程基于**有向无环图 (DAG)** 控制线，由 `LoopGraphManager` 和 `AgentLoopRunner` 驱动。

*   **图管理与调度 (`LoopGraphManager`)**:
    *   管理所有的 `GraphNode`（节点代表一个 AgentLoop）。
    *   通过 `executeGraph()` 持续循环评估节点依赖项（`requires`）。
    *   如果某节点的所有依赖都已成功 (`SUCCESS`)，则会将其派发到线程池（大小为10-50）中并发执行。
    *   如果发现任何依赖节点状态为 `FAILED` 或 `SKIPPED`，则当前节点以及其下游将被短路并标记为 `SKIPPED`。
*   **单节点生命周期执行 (`AgentLoopRunner`)**:
    *   实现了 `Callable<String>`，在独立线程中执行单节点的 Think -> Plan -> Execute 流程。
    *   **上下文初始化**: 为该节点创建专用的本地存储 `LocalLoopMemory`，并校验是否有历史断点 (`Checkpoint`) 可供无缝恢复。
    *   **重试机制**: 读取节点的 `RetryPolicy`（包含最大重试次数、退避基数和最大退避时间），发生异常时进行退避重试，状态在 `RUNNING` 和 `RETRYING` 之间流转。
    *   **Agent 执行**: 调用分配的 `BaseAgent.execute(localMemory)` 执行具体的底层大模型请求逻辑。
    *   **状态与上下文回收**: 执行结束后，将节点标记为 `SUCCESS`，把包含 Agent 最终输出结果的本地 Memory 数据合并 (merge) 到全局内存 (`GlobalSessionMemory`) 当中。同时调用持久化服务并记录日志。

## 2. eventLog 的记录时机与预估数据量

基于 `MasEventLogService` 实现实时执行事件记录，用于可视化回放。

*   **记录时机 (在 `AgentLoopRunner` 中体现)**:
    1.  **START**: 任务节点启动时。
    2.  **AGENT_EXECUTION**: Agent 开始调用执行具体指令前。
    3.  **RETRYING**: (如有失败) 每次尝试失败等待重试时。
    4.  **DONE**: Agent 成功执行完毕时。
    5.  **ERROR**: 达到最大重试次数引发最终失败时。
    6.  **STATE_SNAPSHOT**: 节点成功执行完的最后步骤，记录当前 `loopMemory` (LocalContext) 的全量数据快照（JSON 化）。
*   **预估数据量 (高容量警告)**:
    *   每个 Graph Node 理想情况下至少产生 4 条日志 (START, AGENT_EXECUTION, DONE, STATE_SNAPSHOT)。
    *   `START` / `DONE` 类的Payload数据极小（基础文本描述）。
    *   **瓶颈在于 `STATE_SNAPSHOT`**: 它通过 `objectMapper.writeValueAsString(state)` 将 `LocalLoopMemory` 进行 JSON 序列化并存入 `payload` 大字段中。由于内存中会容纳 Agent的输出结果长文本（包含诸如 `roleName_output`, 乃至之前节点传递过来的上游文本），**每个节点成功的快照体积可能在 5KB 到 100KB+ 不等。**
    *   如果一个 Session 有 10 个 Node，单次会话就能产生高达 40 条记录。在高并发请求场景下，`mas_event_log` 库表将非常容易发生空间膨胀，建议加上定期清理策略或者对 Payload 做压缩转储对象存储处理。

## 3. Memory（内存）的存储机制

采用**全局与局部隔离 (Global/Local Isolation)** 的层次化设计结构：

*   **全局会话内存 (`GlobalSessionMemory`)**:
    *   整个图执行周期的底层通用上下文，底层是一个 `ConcurrentHashMap`，所有并发执行的环域能够安全访问。
    *   **冲突策略 (`CollisionStrategy`)**: 具备对 Key 冲突解决的管理，支持 `STRICT` (抛异常), `MERGE` (对于Map进行合并), `OVERWRITE` (覆盖，默认行为)。
*   **局部隔离内存 (`LocalLoopMemory`)**:
    *   为保证并发执行的节点不产生脏写污染，每个节点拥有独立的局部存储 `localContext`（非线程安全，仅单节点访问）。
    *   **读写机制**: 写操作仅记录本地；读操作优先查所在节点的局部缓存，未命中则“回源”读取 `GlobalSessionMemory`。
    *   **合并提交 (`mergeToGlobal`)**: 单节点在完全成功后，将其 `localContext` 整盘提交反刷回全局内存，供后续的下游节点读取（如上游 Agent将输出存为 `${role}_output`，下游就能从这里取出前置依赖项的数据）。
*   **持久化 (`MasMemoryService` & `MasCheckpointService`)**:
    *   执行完毕时触发 `MasMemoryService.saveToDb(sessionId)` 落盘会话最终状态。
    *   `MasCheckpointService` 负责在每次节点完成后，将结果及节点状态包装成 `MasTaskHistoryDO` 更新到数据库。具备故障恢复的能力（`findLatestCheckpoint`）。

## 4. 多 Agent 的协作机制

系统的多 Agent 协同目前建立在两种主控方式上：

*   **数据流/图依赖驱动协作 (被动流转)**:
    *   在 `LoopGraphManager` 的 DAG 拓扑机制下，多个 Agent 是以工序图的方式组织的。
    *   上一个 Agent (Node A) 收敛逻辑后，其结果自动暴露并在全局字典中沉淀，下游 Agent (Node B) 判断图依赖满足后被唤起处理自己的逻辑。这就是基于**有向无环图控制线索**与**全局共享内存**所实现的隐式协作。
*   **事件消息总线协作 (`A2AMessageBus` - 主动点对点)**:
    *   专门的 A2A（Agent-to-Agent）通信通道，基于 Reactor 的 `Sinks.Many<AgentMessage>`。
    *   实现了典型的 Pub/Sub 订阅者模式，所有的活跃的 Agent (如 `ProjectManagerAgent`) 可以通过 `register(this)` 注册至此。
    *   当需要主动交互（比如动态任务派发、质疑、或者纠偏）时，Agent 可以构建 `AgentMessage` 并 `publish()` 投递至总线，总线利用无阻塞调度线程 (`Schedulers.boundedElastic()`) 实时的定向推送给 `receiverRole` 的 `agent.onMessage(msg)` 事件队列，进而唤醒接受方触发下一轮 Think 处理。
*   **集中式协调角色 (`ProjectManagerAgent`)**:
    *   作为整个 MAS 系统的编排主管和大脑，它是唯一内置了预设指令的组件（默认 `ROLE_CODE = "PROJECT_MANAGER"`）。
    *   能够从全局上下文中读取 `ALL_REPORTS` 全局线索做汇总和决策调度。这意味着图里的某些复杂决策/分组分配是由主管节点通过 LLM 推理后建立新的意图指令来实现驱动其它子节点干活的。

总结来说，当前实现依靠 **DAG任务图** 保证执行的确定性和并发效率，通过局部/全局分级的 **Memory架构** 保障状态隔离与共享，同时提供了 **A2A Bus** 保保留了异步主动会话的能力，核心控制流稳定且易于故障回溯 (`Checkpoint`)。

---

## 5. 针对具体问题的补充解答

### Q1: DAG 是如何构建的？是要应用层来构建好DAG，然后交由 `LoopGraphManager` 来执行？
**结论：是的。当前 MAS Runtime 的代码中仅仅提供了 DAG 引擎，没有包含应用层的图构建代码。**
*   **分析**：全局搜索未发现在非测试代码中有任何 `new LoopGraphManager()` 或 `new GraphNode()` 的调用。唯一的实例化场景在 `MasPerformanceTest.java` (压力测试) 中：手动编写了 `for` 循环创建 `GraphNode` 并添加到列表中。
*   **设计意图**：这说明目前的架构设定是——**由上层应用（如处理前端请求的 Service 层或专门的 Workflow 构建编排层）负责解析业务意图**（可以将数据库配置里的工序组装起来，或者由 `ProjectManagerAgent` 动态输出 JSON 来映射成 Node），最终将组织好的 `GraphNode` 实例集合注入给 `LoopGraphManager` 进行 `executeGraph()`。目前的 runtime 模块充当纯粹的**执行引擎**角色。

### Q2: AgentLoopRunner 的 Think -> Plan -> Execute，具体逻辑是？代码中没有分析出来。
**结论：这在当前框架代码层面是“概念性”的，主要依赖于底层大模型自身的推理执行闭环 (如 ReAct 模式)。**
*   **分析**：在 `AgentLoopRunner.java` 中，类注释写了 `Executes a single graph node through its lifecycle (Think -> Plan -> Execute -> Review)`，但在真实的 `call()` 取值里，只有一行核心逻辑：`result = assignedAgent.execute(loopMemory)`。
*   **进一步追踪**：进入到实际执行的实体 `DynamicAdkAgent.java` 的 `execute()` 方法，可以看到：**框架仅仅是把 `Instruction`（指令），`Global Goal Context`（全局上线文）拼接成文本 Prompt，最后丢给 Google ADK 的 `LlmAgent.runAsync()`。**
*   **实质**：系统框架本身并没有硬编码诸如“第一步先调用模型思考返回Plan，第二步解析Plan，第三步再循环执行”的显式状态机。如果模型能够实现 "Think -> Plan -> Execute"，是因为注入的 ADK `LlmAgent` 绑定了 Tool，使用了底层的 Tool-Calling（ReAct机制），所以这个思考和调用的循环被委托给了 Google ADK 的原生客户端来自动完成。

### Q3: 与 Google ADK 的结合验证（关于 WorkFlowAgent, Tools, Memory 和 A2A 的体现）
**结论：目前与 ADK 的结合属于“浅层代理模式”，仅重度使用了 `LlmAgent` 和 `Tools`，完全剔除了 ADK 自带的 Memory、A2A和 WorkFlow 概念。**
*   **`LLMAgent` (已体现)**: 在 `AdkAgentFactory` 与 `DynamicAdkAgent` 中均通过 `LlmAgent.builder()` 创建并调用，这是核心引擎。
*   **`Tools` (已体现)**: 有深度的结合。`UnifiedToolRegistry` 通过 Spring Context 扫描带有 `@MasTool` 注解的 Bean，并将其转换包装成 ADK 的原生 `FunctionTool`，然后挂载到 `LlmAgent` 上。
*   **`WorkFlowAgent` (未体现)**: 完全没有使用 ADK 原生的编排或 WorkFlowAgent。因为 HZApp 拥有自己的一套 `LoopGraphManager` + `GraphNode` DAG 控制体系来管理多节点工作流，不需要再包裹 ADK 的 Workflow 层。
*   **`Memory` (未体现)**: 完全抛弃了 ADK 的 Memory 对象（如历史对话上下文管理）。ADK Agent 在每次被这套 MAS 调度时，都是无状态创建了一个极简的只包含系统 Prompt 和拼接变量的 `InvocationContext`。上下文状态管理完全由 HZApp 自研的 `GlobalSessionMemory` 和 `LocalLoopMemory` 接管，通过手写 Prompt 的形式灌给大模型。
---

### Q4: 为什么抛弃了 ADK 的 Memory 和 A2A 机制？基于什么考虑？
这主要基于**生产环境的企业级管控诉求、确定性执行以及分布式持久化**的考量。Google ADK (尤其是当时的实验性版本) 提供的 Memory 和 A2A 虽然开箱即用，但在复杂商业系统中存在难以克服的局限：

**1. 抛弃 ADK 原生 Memory 的原因 (转为自研 `GlobalSessionMemory` / `LocalLoopMemory`)**
*   **状态隔离与并发污染问题**：ADK 默认的 Memory 往往是为线性对话 (Chat) 或单线 Agent 执行设计的历史记录。但在 MAS (多智能体) 的 DAG 执行图中，会有多个节点**并发**运行。如果在底层共用同一个大模型客户端层面的 Memory 栈上下文，由于竞态条件，数据极其容易发生串扰或相互污染。
*   **数据结构的定制诉求**：真实的业务执行需要在每一步后抽出特定的强类型业务对象（例如某步产出的 `roleName_output` 甚至提取出来的 JSON）。自研双层 Memory（全局与局部）能精细控制哪部分数据写回全局池共享，哪部分只在单步存留，且配合 `CollisionStrategy` 可以决定数据冲突是抛异常还是 Merge。
*   **持久化与 Checkpoint 强绑定**：生产系统追求在发生报错时可以从断点直接无缝恢复（如 `MasCheckpointService.findLatestCheckpoint`）。自研内存使得在单步完成时可以精准序列化当前局部的 `HashMap` 快照直接入库，这使得恢复和后续可视化 `eventLog` 重放变得极为容易。依赖外购 SDK 内部封装的状态是无法做到这种微观快照抓取的。

**2. 抛弃 ADK 实验性 A2A (Agent-to-Agent) 的原因 (转为自研 `A2AMessageBus`)**
*   **黑盒黑产与不可控性**：目前许多开源 Agent 框架内置的 A2A（如两两互相争论、自行协商）在企业业务中很容易“发散”或者陷入“死循环”。在涉及真金白银处理（比如广告投放策略）时，企业需要的是**强确定性**的调度（DAG）为主，A2A 通信只能作为受控的辅助手段。使用自研总线能做到每一条流转的消息可监测、可拦截、可留痕。
*   **异步调度堆栈的控制**：底层如果使用黑盒的跨 Agent 聊天，系统很容易导致线程耗尽。HZApp 使用自研了基于 Reactor 反应式的 `Sinks.Many` 消息队列，在专门的 `Schedulers.boundedElastic()` 线程池中无阻塞分发，不仅具备高吞吐能力，且和公司的微服务/事件驱动基础架构（如可能后续接入 Kafka）适配度更高。 
*   **生命周期脱钩**：原生的实验性A2A要求 Agent 对象常驻且有彼此的底层引用握手。而 HZApp 把 Agent 视为了**无状态的算子**，他们不关心谁发消息，只关心自己的 `ROLE_CODE` 是否在总线（Bus）收到了指令，这在云原生架构横向伸缩环境（后续可以演进为跨服务器不同集群的 Agent 通信）下是更好的架构选择。

---

## 6. 进阶架构疑问解答

### Q5: `AgentLoopRunner` 的定位：重点到底是流程上下文控制，还是 Loop 循环？
**结论：您理解得很对。`AgentLoopRunner` 的重点是多Agent节点的生命周期管理、上下文隔离与重试控制，而不是所谓的 "Think循环" 控制。**
*   **命名误导**：叫做 `LoopRunner` 可能是前期设计时参考了 AutoGPT 等框架的 "while-true 循环思考" 模式。但在引入 Google ADK 之后，底层的 Tool-Calling 循环（即观察、思考、调用工具、再观察）被 ADK 内部的 `LlmAgent.runAsync()` 吞并了。
*   **实际职责**：目前它其实应该叫做 `GraphNodeExecutor`。它的真正价值在于：
    1.  初始化隔离局部的 `LocalLoopMemory`，防止脏写。
    2.  与 `MasCheckpointService` 结合做断点恢复（发现之前跑成功的，直接 return，不调大模型）。
    3.  提供基于业务规则的 `RetryPolicy` 退避重试（网络断了、或者大模型报错，框架层面再拉起）。
    4.  在执行前后记录业务级可视化日志 (`eventLog`)。

### Q6: 替换 MAS 的 Memory (实现应用级或 Agent 级的检索替换与大小控制)
**结论：当前的 `MasMemoryService` 和 `LoopMemory` 体系耦合度较高，尚未提供面向接口的插件化能力。如果需要支持诸如向量检索 (Vector DB) 或应用级别的 Memory 控制，需要进行重构。**
*   **当前代码分析**：`MasMemoryService` 内部硬编码集成了 MyBatis-Plus 的 `MasSessionVariableMapper`。所有的存储都是 KV 结构 (通过 `ConcurrentHashMap` 缓存，`List<MasSessionVariableDO>` 存库)。代码中甚至有硬编码的 `pruneContext` (超过 10000 字符就通过 FIFO 删除旧数据)。
*   **如何实现插件化替换 (重构建议)**：
    1.  **抽取 Interface**：把 `MasMemoryService` 改为 `MasMemoryManager` 接口。
    2.  **分层 Memory Provider**：根据粒度实现不同的 Provider：
        *   `JdbcSessionMemoryProvider` (现有的，面向全体 KV)。
        *   `Milvus/PineconeVectorMemoryProvider` (配合 ADK 检索，专门负责存取带有业务 Embeddings 的长文本对话)。
    3.  **大模型上下文注入点修改**：目前传给大模型的 Prompt 是在 `DynamicAdkAgent.java` 的 `execute` 方法中强行拼接（如 `contextualPrompt.append(memory.get(xxx))`）。如果要智能截断，这一步必须改造为：调用**“Memory 检索插件”**，根据当前用户的 query 自动 Retrieve 最近似或最关键的 Top-K 历史片段，再送给 ADK Agent。

### Q7: DAG 运行中动态添加节点 (ManagerAgent 开启新任务组合)
**结论：当前的 `LoopGraphManager` 引擎不支持在执行 Graph 的途中动态插入/修改节点树。**
*   **代码死板处**：`LoopGraphManager.executeGraph()` 是一个 `while(true)` 死循环，每轮迭代遍历现有的 `nodes.values()` 来发车。这个图在 `executor.submit` 之前由外部完成组装。因为内部高度依赖 `dependencies (requires)` 列表来做并发前置判断，运行时热添加（Hot-Add）新的 `GraphNode` 会直接破坏图的完整性判断。
*   **如果非要支持该业务场景，架构应如何演进？**
    *   **方案A (主从图嵌套 Sub-Graph)**：父级图执行到一个特定的 `GraphNode` 时，该节点的本质不是调用 LLM Agent，而是唤起一个新的子线程去 new 出一个次级的 `LoopGraphManager`，跑完子任务图后，把结果收敛，汇总给父节点的 `LoopMemory`，父节点才算完成。这种方式最稳妥。
    *   **方案B (动态任务黑板模式)**：ManagerAgent 不要直接修改 DAG 物理结构，而是往 `GlobalSessionMemory` 的某个特定 Key (如 `PENDING_TASKS_QUEUE`) 追加 JSON 任务指令。专门设一个处于 while-true 监听这个队列的 Worker Agent。

### Q8: Node的概念映射（App / Task / 最小可执行单元）如何支持？
**结论：当前框架只有一维扁平的 `GraphNode`，缺乏层级和领域边界的概念。**
*   **当前痛点**：对于框架，所有的节点都是平等的 `Callable<String>`，不论它是一个 30秒的复杂市场调研 App，还是一个 100毫秒的字符拼接单元。但对业务来说，他们产生的日志噪音、计费模式、重试容忍度完全不同。
*   **支持方案 (结构化重构)**：
    1.  **增加分类层级 `NodeType` / `ExecutionLevel`**：为 `GraphNode` 对象增加明确的载体标记定义 (App 级，Flow 级，Action/Unit 级)。
    2.  **差异化执行引擎**：修改 `AgentLoopRunner`。如果检查到节点是 App 级别，可能根本不调 `LlmAgent`，而是发起对其他微服务的 RPC 调用或者阻塞等待人为审批 (Human-in-the-loop)。
### Q8: Node的概念映射（App / Task / 最小可执行单元）如何支持？
**结论：当前框架只有一维扁平的 `GraphNode`，缺乏层级和领域边界的概念。**
*   **当前痛点**：对于框架，所有的节点都是平等的 `Callable<String>`，不论它是一个 30秒的复杂市场调研 App，还是一个 100毫秒的字符拼接单元。但对业务来说，他们产生的日志噪音、计费模式、重试容忍度完全不同。
*   **支持方案 (结构化重构)**：
    1.  **增加分类层级 `NodeType` / `ExecutionLevel`**：为 `GraphNode` 对象增加明确的载体标记定义 (App 级，Flow 级，Action/Unit 级)。
    2.  **差异化执行引擎**：修改 `AgentLoopRunner`。如果检查到节点是 App 级别，可能根本不调 `LlmAgent`，而是发起对其他微服务的 RPC 调用或者阻塞等待人为审批 (Human-in-the-loop)。
    3.  **Memory 的可见性隔离**：应用(App)级的内存应该是持久态的，而算子级的内存应该是即开即用的。在 `GlobalSessionMemory` 设计上要引入 Named Space（命名空间映射），App 节点只能读写 `memory.getSpace("app-id")`。

---

## 7. 长周期、动态反馈循环的支持分析

### Q9: 对于无法一开始就确定完整流程，需要“分析 -> 决策 -> 执行 -> 反馈复盘 -> 再分析”的动态长周期任务，现有执行层如何支撑？
**结论：现有的执行层（DAG图引擎）是静态的且不具备“环”状逻辑。想要支撑这种动态、长周期的循环任务，完全靠底层引擎硬编码是不现实的，必须依靠【宏观调度（大脑） + 微观算子（手脚）】的两层交互架构。**

结合当前代码，系统能够通过以下**三种机制或演进方向**来支撑这种诉求：

#### 方案一：依赖 Agent 自身的 ReAct 闭环（目前最直接的支持）
*   由于底层跑的是带有 Tool Calling 的 Google ADK `LlmAgent`，对于那些能够在单次 LLM 上下文（Context Window）和单次连接超时（例如几分钟内）完成的循环，**系统其实已经原生支持了**。
*   大模型在接到 `ProjectManagerAgent` 丢过去的宏观指令后，它在内部使用 ReAct 模式（Reasoning and Acting），自动经历了：`思考该怎么做 -> 调用搜索工具 -> 获取反馈 -> 分析失败 -> 换个词再搜索 -> 执行处理 -> 返回结果`。
*   **局限**：这只适合“中短周期、计算密集型”内部流转，并且高度依赖模型的推理能力。一旦等待时间跨天、或工具调用极多，单次 LLM 会话会崩溃。

#### 方案二：事件驱动的“黑板模式”与 A2A 通信（目前部分支撑）
这种方法**摒弃 DAG**，依靠基于 Reactor 的 `A2AMessageBus` 和 `GlobalSessionMemory` 黑板：
1.  **动态决策核心**：将 `ProjectManagerAgent` 作为驻留核心大脑，它不再挂载于死板的 DAG 节点上，而是监听 MessageBus 上的“状态机（State）流转事件”。
2.  **拆解执行体**：专门设各种执行干活的 WorkerAgent。
3.  **流转过程**：Manager 先在 `GlobalSessionMemory` 下发“第一步分析单”，通过 A2A 指派给 AnalysisAgent；AnalysisAgent 跑完把结果写回共享内存，并发一个 A2A 回给 Manager；Manager 判断后，再下发“第二步决策指令”；如果执行出错，Error反馈回来，Manager 再下发纠偏指令。
*   **优势**：解开了固定有向无环图的限制，想要几次循环都可以，属于真正的长周期动态交互。
*   **局限**：状态维护成本极高（State Machine），如果进程重启，依靠纯内存的 `Sinks.Many` 队列会丢失当前的流转状态，需要配以强大的数据库状态机机制作底座。

#### 方案三：动态重构生成 DAG 的“多次派发模式”（最佳云原生演进路线）
这是当前架构（`LoopGraphManager` + 持久化 `Checkpoint`）顺势演进的最优解：
*   不要试图把“长周期的循环和未知步骤”画进一张 DAG 图里。DAG 引擎只负责执行**确定性的微小批次**。
*   **具体步骤**：
    1.  **宏观启动**：一个外部的 `Workflow Orchestrator`（这是 Java 业务层应用代码，而非 MAS 底层引擎）启动，唤醒主脑 Agent，传入全局 Session 和用户的长周期目标。
    2.  **动态出图**：主脑 Agent（LLM 模型）首先进行“分析与决策”，然后它并非直接输出文本，而是**输出一组具有结构化的 JSON，即下一阶段的小型确定的 GraphNode 定义集合**。
    3.  **生成并执行引擎**：应用层将这个由 LLM 生成的第一阶段 GraphNode 转化为真正的 DAG 图，丢给 `LoopGraphManager`。这组节点开始稳稳地并发执行。
    4.  **复盘再分析**：这组迷你 DAG 执行完毕并成功写入 `Checkpoint` 和 `GlobalSessionMemory` 后，触发应用层回调。应用层再次唤醒主脑 Agent 说：“你看，第一阶段结果都在这段 Memory 里了，请决定下一步干嘛”。主脑通过分析，再次下发第二阶段的小型 DAG JSON，进入下一次组装运行。
    ... 依此类推。
*   **怎么停止？** 当主脑 Agent 决定任务已经达成，它生成的 JSON 返回为特定的 `{"status": "DONE", "nodes": []}`。
*   **这个方案为什么好？** 
    1.  完美利用了现有系统底层确定性强的 `LoopGraphManager`。
    2.  利用了现成的断点恢复，哪怕断电重启，只要看最后一次落在哪就行。
    3.  将“不确定的循环流转控制”推给了一个专门作为 Orchestrator 的外层调度器和主脑大模型，做到了框架执行与应用意图分离。
