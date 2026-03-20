# MAS + ADK Session 深度集成设计方案

**日期：** 2026-03-16  
**状态：** 草稿  
**模块：** `hzapp-module-erplus-ai` → `mas/runtime`

> **实施策略：** 原 `WorkflowOrchestrator` 保持不变，新建 `WorkflowOrchestratorV1` 作为重构入口，待稳定后迁移调用方（`MasTaskJob`）并废弃原版。

---

## 背景与目标

### 现状问题

当前 `mas/runtime` 实现中，ADK 只被用作"LLM 通信层"：
- `AdkLlmClientAdapter` 每次调用都 `createSession()`，ADK Session 生命周期极短
- `GlobalSessionMemory`（自研 `ConcurrentHashMap`）与 ADK `session.state` 并行存在，形成双轨
- `MasCheckpointService` + `memoryManager.saveToDb()` 与 ADK `SessionService` 能力重叠
- 无统一 Event 机制，无中断/恢复能力，`A2AMessageBus` 已标记 `@Deprecated`

### 设计目标

1. **一个 WorkflowOrchestrator 对应一个 ADK Session**，Session 跨所有 Phase 持续存活
2. **DAG 执行图逻辑保留**，但由 ADK 原生的 `ParallelAgent`/`SequentialAgent` 承载
3. **最大化使用 ADK 原生能力**：`session.state`、`SessionService`、Event Stream、Callbacks
4. **自研核心缩减至一个**：`MasDagDispatcher`（运行时动态组合 ADK Agent 图）
5. **支持两种中断语义**：
   - **Phase 门控（A）**：Phase 执行后需人工审批，可中断/恢复
   - **分级用户注入（C3）**：运行时用户输入按紧急/补充两个优先级处理

---

## 架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                    WorkflowOrchestrator                         │
│                                                                 │
│  ADK Runner (1个，贯穿整个 Task 生命周期)                         │
│  ├── SessionService → 持久化 session.state (替代 saveToDb)       │
│  ├── Event Stream   → Flowable<Event> (替代 MasEventBus)        │
│  └── Callbacks      → 节点生命周期钩子 (替代 MasEventLogService)  │
│                                                                 │
│  InterruptManager                                               │
│  ├── URGENT 信号  → 终止当前 Flowable，重新规划                  │
│  └── SUPPLEMENT   → 写入 session.state["pending_supplements"]   │
│                                                                 │
│  ─── Phase Loop (1..N) ──────────────────────────────────────── │
│  │                                                              │
│  │  PlannerAgent (LlmAgent)                                     │
│  │  └── 输出 DagGenerationPlan (structured output)             │
│  │                                                              │
│  │  MasDagDispatcher（唯一的自研核心组件）                        │
│  │  └── 拓扑排序 DAG → 动态组合 ADK Agent 图                    │
│  │       ├── 同层无依赖节点 → ParallelAgent([...])              │
│  │       └── 层间串行     → SequentialAgent([tier1, tier2...]) │
│  │                                                              │
│  │  runner.runAsync(composedAgent) → event stream              │
│  │                                                              │
│  └──────────────────────────────────────────────────────────── │
└─────────────────────────────────────────────────────────────────┘
```

---

## 核心组件设计

### 1. WorkflowOrchestrator（重构）

**变更点：**
- 持有一个 `Runner` + 一个 `Session`（而不是每次调用都新建）
- `GlobalSessionMemory` 完全替换为 `session.state`
- 每个 Phase 调用 `MasDagDispatcher.compose(plan)` 得到 ADK Agent 图，交给 `runner.runAsync()`
- 订阅 Event Stream，驱动日志和 SSE 推流

```java
public class WorkflowOrchestrator {
    private final Runner runner;          // ADK Runner，全生命周期复用
    private final String sessionId;       // 与 MasTask 一一对应
    private final PlannerAgent plannerAgent;
    private final MasDagDispatcher dagDispatcher;
    private final InterruptManager interruptManager;

    public MasOrchestrationResult executeMacroLoop(String goal) {
        // 1. 初始化 Session（首次）或恢复（SUSPEND 后重入）
        Session session = runner.sessionService()
            .getOrCreateSession(APP_NAME, userId, sessionId, Map.of("user_goal", goal))
            .blockingGet();

        int phaseCount = (int) session.state().getOrDefault("phase_count", 1);

        while (phaseCount <= MAX_PHASES) {
            // 2. 检查中断信号（Phase 边界检查点）
            InterruptSignal signal = interruptManager.poll();
            if (signal != null && signal.isUrgent()) {
                // 写入中断上下文，让 Planner 下轮感知
                appendStateUpdate(session, "urgent_interrupt", signal.getMessage());
            }

            // 3. 调用 Planner（本身是一个 ADK LlmAgent，也通过 runner 执行）
            DagGenerationPlan plan = invokePlanner(session, phaseCount);

            if (plan.getStatus() == DONE)    return buildFinishResult(session);
            if (plan.getStatus() == SUSPEND) return buildSuspendResult(session, plan);

            // 4. 动态构建 ADK Agent 图并执行
            BaseAgent composedPhaseAgent = dagDispatcher.compose(plan);
            Disposable execution = runner.runAsync(userId, sessionId, phaseInput(plan))
                .subscribe(
                    event -> handleEvent(event, session),
                    error -> handleError(error)
                );

            // 5. 等待 Phase 完成（支持 URGENT 中断）
            awaitPhaseWithInterrupt(execution);

            phaseCount++;
            appendStateUpdate(session, "phase_count", phaseCount);
        }
        // 超限安全退出：交 ReviewerAgent 处理
        return invokeReviewer(session, "Reached max phase limit");
    }
}
```

---

### 2. MasDagDispatcher（新增，唯一自研核心）

**职责：** 将 `DagGenerationPlan`（Planner 输出的 JSON）转换为 ADK 可执行的 Agent 图。

**算法：DAG 拓扑分层（Kahn 算法）**

```
输入 DAG：A → [B, C（并行）] → D

拓扑分层结果：
  Tier 1: [A]
  Tier 2: [B, C]   ← 同层，无依赖，并行
  Tier 3: [D]

构建结果：
  SequentialAgent(
    LlmAgent(A),
    ParallelAgent(LlmAgent(B), LlmAgent(C)),
    LlmAgent(D)
  )
```

**实现要点：**

```java
@Component
public class MasDagDispatcher {

    private final CustomAgentLoaderService agentLoaderService;

    public BaseAgent compose(DagGenerationPlan plan) {
        List<List<DagPlanNode>> tiers = topologicalTiers(plan.getNodes());

        List<BaseAgent> sequence = new ArrayList<>();
        for (List<DagPlanNode> tier : tiers) {
            List<BaseAgent> tierAgents = tier.stream()
                .map(node -> buildAdkAgent(node))
                .collect(toList());

            if (tierAgents.size() == 1) {
                sequence.add(tierAgents.get(0));
            } else {
                sequence.add(ParallelAgent.builder()
                    .name("Tier-" + sequence.size())
                    .subAgents(tierAgents)
                    .build());
            }
        }

        return sequence.size() == 1
            ? sequence.get(0)
            : SequentialAgent.builder().name("Phase-DAG").subAgents(sequence).build();
    }

    private LlmAgent buildAdkAgent(DagPlanNode node) {
        MasAgentConfigDO config = agentLoaderService.getConfigByRole(node.getAgentRole());
        return LlmAgent.builder()
            .name(node.getId())
            .model(config.getModelName())
            // instruction 使用 {key} 语法从 session.state 读取上游输出
            .instruction(buildInstruction(config, node))
            // outputKey 约定为 "nodeId_output"，写入 session.state
            .outputKey(node.getId() + "_output")
            .tools(resolveTools(config))
            .afterAgentCallback(this::onNodeComplete)   // 替代 MasEventLogService
            .build();
    }

    private String buildInstruction(MasAgentConfigDO config, DagPlanNode node) {
        StringBuilder instruction = new StringBuilder(config.getSystemPrompt());
        // 自动注入所有上游依赖节点的输出（ADK {key} 模板语法）
        if (node.getDependsOn() != null) {
            instruction.append("\n\n## Context from upstream agents:\n");
            for (String depId : node.getDependsOn()) {
                instruction.append("- ").append(depId).append(" result: {").append(depId).append("_output}\n");
            }
        }
        return instruction.toString();
    }
}
```

---

### 3. Session State 命名约定（替代 GlobalSessionMemory）

所有跨节点共享数据统一存放在 ADK `session.state`，按前缀分类：

| Key 模式 | 含义 | 生命周期 |
|---|---|---|
| `user_goal` | 用户的原始目标 | Session 全程 |
| `phase_count` | 当前 Phase 编号 | Session 全程 |
| `{nodeId}_output` | 某节点的执行输出 | Session 全程（供下游读取） |
| `pending_supplements` | 用户补充信息（非紧急） | 下一 Planner 轮消费后清除 |
| `urgent_interrupt` | 紧急中断消息 | 下一 Planner 轮消费后清除 |
| `temp:*` | Phase 内临时计算数据 | Phase 结束后自动丢弃 |
| `user:*` | 用户级偏好 | 跨 Session 持久 |

---

### 4. InterruptManager（新增）

**职责：** 接收外部用户输入，按优先级分流处理。

```java
@Component
public class InterruptManager {

    // 外部调用入口（由 HTTP Controller 或 MQ Consumer 调用）
    public void submitUserInput(String sessionId, String message, InterruptPriority priority) {
        switch (priority) {
            case URGENT:
                // 写入紧急信号队列，WorkflowOrchestrator 会在下一个检查点响应
                urgentQueue.offer(new InterruptSignal(sessionId, message, true));
                break;
            case SUPPLEMENT:
                // 直接写入 session.state，不打断当前执行
                appendToSession(sessionId, "pending_supplements", message);
                break;
        }
    }

    // WorkflowOrchestrator 在 Phase 边界调用，非阻塞
    public InterruptSignal poll() {
        return urgentQueue.poll();
    }
}
```

**URGENT 响应流程：**
```
用户发来 URGENT 消息
    ↓
InterruptManager.submitUserInput(URGENT)
    ↓ 写入 urgentQueue
WorkflowOrchestrator 在当前 Phase runner.runAsync() 完成后
    ↓ phase 边界检查 interruptManager.poll()
发现 URGENT 信号
    ↓ appendStateUpdate(session, "urgent_interrupt", message)
下一轮 PlannerAgent 读取 {urgent_interrupt}，重新规划
    ↓
生成针对新输入的新 DAG
```

> **注意：** 当前设计 URGENT 信号不打断正在运行的 `runner.runAsync()`，而是等待当前 Phase 的 DAG 完成后在 Phase 边界响应。这避免了强制取消 LLM 调用的复杂性，且 Planner 会在下一轮基于新输入做完整的计划调整。如果业务要求更激进的中断（立即取消），可通过 `Disposable.dispose()` 实现，但需要应对未完成节点的清理问题。

---

### 5. Event 机制（基于 ADK Callbacks）

用 ADK 原生 Callbacks 替代自研 `MasEventBus` + `MasEventLogService`：

```java
// 在 MasDagDispatcher.buildAdkAgent() 中统一注册
.afterAgentCallback((ctx, response) -> {
    String nodeId = ctx.agentName();
    String output = response.map(Event::stringifyContent).orElse("");

    // 1. 写入 MAS 日志（原 MasEventLogService 职责）
    eventLogService.logEvent(sessionId, nodeId, "COMPLETED", output);

    // 2. 推送 SSE 事件给前端（实时进度）
    sseEmitter.send(MasSseEvent.nodeCompleted(nodeId, output));

    return Optional.empty(); // 不修改 response
})
.beforeAgentCallback((ctx) -> {
    eventLogService.logEvent(sessionId, ctx.agentName(), "STARTED", "");
    sseEmitter.send(MasSseEvent.nodeStarted(ctx.agentName()));
    return Optional.empty();
})
```

---

### 6. Phase 门控（场景 A）的实现

Phase 门控由 Planner 通过 `DagGenerationPlan.status = SUSPEND` + `approvalRequired = true` 触发：

```
Planner 输出：
{
  "status": "SUSPEND",
  "reasoning": "需要用户确认以下营销方案后再继续执行",
  "approvalRequired": true,
  "pendingApprovalContent": "...(给用户看的摘要)..."
}

WorkflowOrchestrator 收到 SUSPEND：
  1. 返回 MasOrchestrationResult.SUSPEND
  2. MasTaskJob 将任务状态改为 AWAITING_APPROVAL
  3. 持久化当前 session.state（ADK SessionService 自动完成）

用户点击"确认"：
  1. 外部调用 WorkflowOrchestrator.resume(sessionId, userApproval)
  2. 写入 session.state["user_approval"] = userApproval
  3. 重新触发 executeMacroLoop（从当前 phaseCount 继续）
  4. Planner 读取 {user_approval}，生成下一步 DAG
```

---

## 组件变更对照表

| 原组件 | 新替代 | 状态 |
|---|---|---|
| `GlobalSessionMemory` | ADK `session.state` | 废弃 |
| `LocalNodeMemory` | ADK `temp:` 前缀 state / Callback Context | 废弃 |
| `MasCheckpointService` | ADK `SessionService`（原生持久化） | 废弃 |
| `memoryManager.saveToDb()` | ADK `appendEvent()` 自动触发 | 废弃 |
| `MasEventLogService` | ADK Before/After Agent Callbacks | 轻量保留接口，实现改为 Callback |
| `A2AMessageBus` | 已 `@Deprecated`，正式移除 | 删除 |
| `DagExecutionEngine` | ADK `ParallelAgent` / `SequentialAgent` | 废弃 |
| `NodeExecutor` | ADK 原生 Agent 执行（内置 retry、状态管理） | 废弃 |
| `AdkNodeRunner` / `ReActNodeRunner` | ADK `LlmAgent`（带 Tool 的即为 ReAct 等价） | 废弃 |
| `AdkLlmClientAdapter` | 直接使用 ADK `Runner` | 简化 |
| **新增：`MasDagDispatcher`** | 运行时 DAG → ADK Agent 图动态组合 | **新增** |
| **新增：`InterruptManager`** | 分级中断信号管理 | **新增** |

---

## 关键技术约束

### ADK ParallelAgent 的并发 state 安全性

- ADK 官方文档明确：`ParallelAgent` 的并发子 Agent 之间**无自动 state 共享**，需开发者通过不同 `output_key` 隔离写入
- `SessionService.appendEvent()` 保证单次 state 写入的线程安全
- **约定：每个节点只写自己的 `{nodeId}_output`，不读/写其他并发节点的 key**，因此无并发冲突

### session.state 不能存复杂对象

- ADK `session.state` 要求值必须可序列化（基本类型、List、Map）
- 节点输出统一存为 `String`（JSON 格式）；需要时由下游 Agent instruction 解析

### 动态 Agent 组合的限制

- `ParallelAgent`/`SequentialAgent` 的 `subAgents` 在构建时确定，但每个 Phase 都是全新构建，因此"动态"体现在每个 Phase 重新构建不同拓扑的 ADK Agent 图
- 图的 `name` 包含 Phase 编号以区分日志，例如 `Phase-3-DAG`

---

## sessionId / appName 参数线程 & MasTaskJob 衔接

### 核心映射

| 参数 | 含义 | 来源 |
|---|---|---|
| `appName` | 父任务 → ADK App（Trace 整棵任务树 + Resume） | `"mas-task-" + parentTask.getId()` |
| `sessionId` | 叶子任务 → ADK Session（具体执行单元） | `task.getSessionId() + "_Task#" + task.getId()` |
| `userId` | 发起该 Task 的用户/租户 ID | `task.getCreatorId()` |
| `goal` | 格式化后的任务目标 | `TaskContextFormatter.format(task)` |

### 签名设计

```java
// V1 入口签名 — appName 是运行时参数，不是 @Value 配置
public MasOrchestrationResult executeMacroLoop(String appName, String sessionId,
                                                String userId, String goal)

// 用户审批恢复
public MasOrchestrationResult resume(String appName, String sessionId, String userId,
                                      String goal, String userApproval)
```

### MasTaskJob 调用方式

```java
// MasTaskJob.java
// 父任务 → 一个 ADK App（覆盖整棵任务树的所有 Session）
String appName   = "mas-task-" + getParentTaskId(task);
// 叶子任务 → 一个 ADK Session
String sessionId = task.getSessionId() + "_Task#" + task.getId();
String userId    = task.getCreatorId();

MasOrchestrationResult result = orchestratorV1.executeMacroLoop(
    appName, sessionId, userId, formattedGoal);
```

### 为什么 appName = 父任务？

MasTask 存在**父子任务**关系。将父任务映射为 ADK App：
- **Trace 完整性**：同一 App 下的所有 Session（即所有子任务）可统一追溯
- **Resume 支持**：App 级别的 Session 列表可枚举，支持整体任务恢复
- **配置隔离**：不同父任务可拥有独立的 Agent 配置/模型选择

V1 内部用 `appName` + `sessionId` 从 ADK `SessionService` 获取或创建 Session：

```java
Session session = runner.sessionService()
    .getOrCreate(appName, userId, sessionId, initialState)
    .blockingGet();
```

**优点：**
- `sessionId` = DAG 执行的隔离单元，多个 Task 并发不冲突
- `appName` = 父任务级别的 Trace/Resume 边界
- `session.state` 持久化后，服务重启仍可从正确的 Phase 恢复
- `MasTaskJob` 的状态机逻辑（SUSPEND/FINISH/FAIL）不需要改动

---

## MAS 事件机制设计

### 设计原则

- **事件是执行轨迹** — 每个事件记录「谁」在「什么阶段」做了「什么事」
- **双轨派发** — 事件同时写入持久化日志（审计）和 WebSocket 推流（实时展示）
- **基于 ADK Callbacks 桥接** — 不引入独立的事件总线，由 ADK 的 Before/After Agent Callback 触发

### 事件类型定义

```
MasEventType
├── PHASE_STARTED           Phase N 开始
├── PHASE_COMPLETED         Phase N 完成
├── PHASE_SUSPENDED         Phase 挂起（等待用户审批）
│
├── PLANNER_STARTED         Planner 开始推理
├── PLANNER_COMPLETED       Planner 完成，输出 DagGenerationPlan
├── PLANNER_FAILED          Planner 推理失败（含重试次数）
│
├── NODE_STARTED            DAG 节点开始执行（含节点ID/AgentRole）
├── NODE_COMPLETED          DAG 节点完成（含输出摘要）
├── NODE_FAILED             DAG 节点失败（含错误信息）
│
├── USER_INPUT_RECEIVED     收到用户输入（URGENT/SUPPLEMENT）
├── INTERRUPT_TRIGGERED     URGENT 中断被响应
│
└── TASK_FINISHED           整个 Task 完成（FINISH/FAIL）
```

### 事件数据结构

```java
@Value
@Builder
public class MasEvent {
    String      sessionId;     // Task 级别的隔离 ID
    String      eventId;       // UUID
    MasEventType type;
    String      nodeId;        // 为空时表示 Orchestrator 层事件
    String      agentRole;     // 执行节点的 Agent 角色（NODE_* 事件）
    String      payload;       // 事件携带的数据（JSON 字符串）
    String      summary;       // 简短摘要，用于展示
    LocalDateTime timestamp;
    int         phaseIndex;    // 当前 Phase 编号
}
```

### 事件流转路径

```
ADK Callback 触发
       │
       ▼
  MasEventPublisher.publish(event)
       │
       ├──► MasEventLogService.logEvent()    ← 写入 mas_event_log 表（审计、可回放）
       │
       └──► MasReporter.push(event)          ← WebSocket 推流（实时展示）
```

### 在 MasDagDispatcher 中注册 Callbacks

```java
private LlmAgent buildAdkAgent(DagPlanNode node, MasEventPublisher publisher, 
                                String sessionId, int phaseIndex) {
    return LlmAgent.builder()
        .name(node.getId())
        .outputKey(node.getId() + "_output")
        // ... model, instruction, tools ...
        .beforeAgentCallback((ctx) -> {
            publisher.publish(MasEvent.builder()
                .type(NODE_STARTED)
                .sessionId(sessionId)
                .nodeId(node.getId())
                .agentRole(node.getAgentRole())
                .phaseIndex(phaseIndex)
                .summary("Node " + node.getId() + " started")
                .build());
            return Optional.empty();
        })
        .afterAgentCallback((ctx, response) -> {
            String output = response.map(Event::stringifyContent).orElse("");
            publisher.publish(MasEvent.builder()
                .type(NODE_COMPLETED)
                .sessionId(sessionId)
                .nodeId(node.getId())
                .agentRole(node.getAgentRole())
                .phaseIndex(phaseIndex)
                .payload(output)
                .summary("Node " + node.getId() + " completed")
                .build());
            return Optional.empty();
        })
        .build();
}
```

---

## Report 组件设计

### 设计目标

将 Agent 执行过程通过 **WebSocket** 实时推送给用户，让用户看到：
- 当前执行到哪个 Phase / 哪个节点
- 每个节点在做什么（STARTED → THINKING → COMPLETED）
- Planner 输出的规划摘要
- 用户输入是否被接收/响应

### 组件结构

```
mas/runtime/report/
├── MasReporter.java              接口：定义 push(MasEvent) 方法
├── WebSocketMasReporter.java     实现：通过 WebSocket 推流（主实现）
├── CompositeReporter.java        组合多个 Reporter（如 WS + 钉钉机器人）
└── MasEventPublisher.java        聚合：统一触发 log + report 的入口
```

### MasReporter 接口

```java
public interface MasReporter {
    /**
     * 将执行事件推送给关注该 session 的客户端.
     * 实现必须是非阻塞的，推送失败只记日志，不影响执行流程
     */
    void push(MasEvent event);
}
```

### WebSocketMasReporter 实现

```java
@Component
@RequiredArgsConstructor
public class WebSocketMasReporter implements MasReporter {

    private final SimpMessagingTemplate messagingTemplate;  // Spring WebSocket

    /**
     * 推送到客户端订阅的 Topic，前端用 sessionId 区分不同 Task 的进度
     * Topic 格式：/topic/mas/{sessionId}
     */
    @Override
    public void push(MasEvent event) {
        try {
            String destination = "/topic/mas/" + event.getSessionId();
            MasEventDTO dto = toDTO(event);
            messagingTemplate.convertAndSend(destination, dto);
        } catch (Exception e) {
            log.warn("[MasReporter] Failed to push event to WebSocket: {}", e.getMessage());
            // 不抛出异常，不影响执行流程
        }
    }

    private MasEventDTO toDTO(MasEvent event) {
        return MasEventDTO.builder()
            .eventId(event.getEventId())
            .type(event.getType().name())
            .nodeId(event.getNodeId())
            .agentRole(event.getAgentRole())
            .summary(event.getSummary())
            .phaseIndex(event.getPhaseIndex())
            .timestamp(event.getTimestamp())
            .build();
        // payload（完整输出）不推 WS，避免消息过大；前端需要时主动查 REST API
    }
}
```

### MasEventPublisher（双轨派发聚合入口）

```java
@Component
@RequiredArgsConstructor
public class MasEventPublisher {

    private final MasEventLogService eventLogService;  // 持久化（DB）
    private final MasReporter reporter;               // 实时推送（WS）

    public void publish(MasEvent event) {
        // 1. 持久化日志
        eventLogService.logEvent(
            event.getSessionId(),
            event.getNodeId() != null ? event.getNodeId() : "ORCHESTRATOR",
            event.getType().name(),
            event.getSummary(),
            event.getPayload()
        );
        // 2. 实时推流
        reporter.push(event);
    }
}
```

### 前端集成约定

```
WebSocket 连接地址：ws://{host}/ws/mas
STOMP 订阅 Topic：/topic/mas/{sessionId}

消息格式（JSON）：
{
  "eventId":   "uuid",
  "type":      "NODE_COMPLETED",
  "nodeId":    "trend-analysis-node",
  "agentRole": "TREND_ANALYZER",
  "summary":   "Node trend-analysis-node completed",
  "phaseIndex": 2,
  "timestamp": "2026-03-16T09:58:27"
}

完整输出（payload）通过 REST 接口获取：
GET /api/mas/tasks/{taskId}/events/{eventId}
```

### 容错与降级

- Report 失败（WebSocket 断连）**不影响**主流程执行
- 客户端重连后可通过 `GET /api/mas/tasks/{taskId}/events` 拉取历史事件（从 `mas_event_log` 表）
- 支持 `CompositeReporter` 扩展：可同时推 WS + 钉钉 + 企业微信

---

## 验证计划

### 单元测试

- `MasDagDispatcher.compose()` — 输入各种拓扑结构的 `DagGenerationPlan`，验证生成正确的 ADK Agent 嵌套结构
- `InterruptManager` — 验证 URGENT/SUPPLEMENT 两种信号的路由行为
- `session.state` 命名约定 — 验证上下游 key 引用一致性

### 集成测试

- 模拟一个 3-Phase Task，Phase 2 包含 2 个并行节点，验证 `session.state` 正确传递
- 模拟 Phase 1 执行中发来 SUPPLEMENT 信号，验证不影响当前 Phase，Planner 在 Phase 2 感知
- 模拟 Phase 1 发来 URGENT 信号，验证 Phase 1 完成后触发重新规划
- 模拟 Planner 返回 SUSPEND，验证任务状态变为 AWAITING_APPROVAL，resume 后正确恢复

### 手工验证

- 通过 SSE 端点观察节点执行的实时事件推流
- 验证 ADK `DatabaseSessionService` 持久化后，服务重启仍能从正确的 Phase 恢复
