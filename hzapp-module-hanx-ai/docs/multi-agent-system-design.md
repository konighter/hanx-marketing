# 多智能体系统 (Multi-Agent System, MAS) 技术设计文档

## 1. 设计愿景
构建一个具备高度自治、灵活协调、持久记忆和人机协作能力的 AI 智能体集群。该系统能够将复杂的顶层目标拆解为可执行的任务链，并由不同角色的 Agent 通力合作完成，同时支持实时的状态追踪和人工介入。

## 2. 系统架构
系统采用 **“总线驱动 + 层级协调”** 的架构模式。

### 2.1 逻辑分层
- **应用/交互层 (App/Interaction)**: 负责 WebSocket 实时通信、Chat 界面和任务看板。
- **编排/层级层 (Orchestration/Hierarchy)**: 负责任务分解、角色指派、并行调度和状态流转。
- **执行/原子层 (Execution/Atomic)**: 各个角色的 Agent 实现，调用 LLM 和 Tool 执行具体原子操作。
- **能力层 (Capabilities)**: 存储 (Memory)、状态机 (State Machine)、提示词管理 (Prompt Registry)。

### 2.2 核心组件
- **MAS Orchestrator (编排器)**: 系统大脑，负责初始化场景、启动状态机、管理消息总线。
- **Agent Roles (角色智能体)**:
    - **Manager (协调者)**: 决策全局路径，处理异常和冲突。
    - **Expert (领域专家)**: 提供特定领域的逻辑支持和方案评审。
    - **PM (项目经理)**: 负责需求调研，将目标拆解为具体的 Task 列表。
    - **Executor (执行者)**: 执行 Task，利用 Tool 完成实际操作。
    - **Reviewer (审核人)**: 对 Executor 的产出进行质量评估，决定是否进入下一步。
- **Event Bus (消息总线)**: 基于 Reactor/EventPublisher 的异步消息机制，实现 Agent 间的解耦通信。

## 3. 详细设计

### 3.1 场景初始化与状态机 (Initialization & State Machine)
系统的启动完全由 **管理者 (Manager)** 主导。

**初始阶段逻辑 (Project Bootstrapping):**
1. **目标分析**: 用户输入目标后，Manager 首先解析该目标的复杂度、所属领域及紧急程度。
2. **角色选定**: Manager 根据目标确定需要参与的 Agent 角色（如：是否需要特定领域的 Expert，分配多少名 Executor）。
3. **流程指派**: Manager 定义项目的核心流程（如：传统的“规划-执行-审核”流，或针对紧急情况的“快速执行”流）。
4. **启动规划**: 在确定角色和流程后，Manager 将任务移交给 PM Agent 进入规划环节。

**状态定义:**
- `INIT`: 管理者初始化 (确定角色与流程)
- `PLANNING`: 需求分解中 (PM 介入)
- `EXECUTING`: 运行中
- `REVIEWING`: 审核中
- `WAITING_FOR_USER`: 等待人工介入 (Human-in-the-loop)
- `COMPLETED`: 已完成
- `FAILED`: 失败

### 3.2 存储系统 (Memory)
- **短期记忆 (Short-term context)**: 基于 Redis 或数据库的会话存储，记录当前任务链的所有交互上下文（Message History）。
- **长期记忆 (Long-term experience)**: 基于向量数据库 (如 Milvus/VectorStore)，存储历史任务的成功经验、复杂问题的解决方案，供 Agent 进行检索增强 (RAG)。

### 3.3 动态调整与响应反馈 (Dynamic Adjustment)
系统中的“中断”并非强制停止任务，而是指在任务执行期间，管理者 (Manager) 能够实时根据各种信号调整策略。

**核心决策闭环:**
1. **信号接入**:
   - **用户反馈**: 用户通过 UI (Chat) 发送指令或修正建议，触发 `USER_FEEDBACK` 事件。
   - **任务状态变更**: 任何任务的 **完成 (Completed)**、**中断 (Interrupted)** 或 **失败 (Failed)** 都会作为事件实时反馈给 Manager。
2. **管理者评估 (Manager Evaluation)**:
   `MasOrchestrator` 将上述信号汇总给 **Manager (协调者)**。Manager 基于全量上下文（短期记忆）评估当前进度与最终目标的偏差。
3. **决策与重排 (Decision & Re-planning)**:
   - **推进任务**: 任务成功，Manager 决定启动计划中的下一个子任务。
   - **异常处理**: 任务失败或中断，Manager 决定是重试、跳过还是调整后续计划。
   - **实时重排**: 若反馈涉及目标变化，Manager 触发 `RE_PLANNING` 指挥 PM 重新拆分任务。
4. **状态同步**: 所有调整通过看板实时展示。

### 3.4 框架层详细设计 (Framework Detailed Design)

#### 3.4.1 消息驱动框架 (Message Framework)
- **核心组件**: `MasEventBus`, `MasEvent`, `MasSubscriber`。
- **设计要点**:
    - **非阻塞异步**: 基于 Project Reactor (Flux/Mono) 实现，确保 Agent 间的通信不会阻塞主线程。
    - **主题/类型过滤**: 事件总线支持根据事件类型 (`topic` 或 `class`) 进行订阅，实现消息的精准投递。
    - **解耦设计**: Agent 仅依赖事件总线，不直接持有其他 Agent 的引用。

#### 3.4.2 状态机框架 (State Machine Framework)
- **核心组件**: `MasStateMachine`, `StateTransition`, `StateStore`。
- **设计要点**:
    - **领域模型驱动**: 状态流转逻辑与业务逻辑分离。每个状态机实例绑定一个具体的场景/任务会话。
    - **持久化支持**: 支持将状态机当前状态存储至数据库/Redis，确保分布式环境下或服务重启后的状态一致性。
    - **自动触发器**: 关联 `MasEvent`，当特定事件到达时自动触发状态转换检查。

#### 3.4.3 执行框架 (Execution Framework)
- **核心组件**: `MasTaskExecutor`, `MasTask`, `ExecutionStrategy`。
- **设计要点**:
    - **任务抽象**: `MasTask` 封装了可执行的逻辑单元（Prompt、Tool、Expected Output）。
    - **执行策略可插拔**: 
        - **本地模式**: 使用虚拟线程 (Project Loom) 或线程池并行执行。
        - **分布式模式**: 可扩展支持通过 MQ (如 RocketMQ) 分发到不同的节点执行。
    - **上下文注入**: 执行器在运行前会自动将 `MasContext` (会话上下文) 注入到 Executor 中，确保 Agent 拥有必要的背景知识。

#### 3.4.4 上下文与记忆保护 (Context & Memory)
- **设计要点**:
    - **线程安全**: `MasContext` 采用并发容器，确保多个 Agent 并行访问/修改上下文时的安全性。
    - **分级存储**: 
        - **会话级 (Session)**: 生命周期仅限当前任务。
        - **持久级 (Archive)**: 任务完成后转存至向量数据库作为长期记忆。

### 3.5 循环执行流程图 (MAS Execution Flowchart)
以下流程图展示了系统从初次规划到循环执行、及响应用户动态反馈的完整生命周期。

```mermaid
graph TD
    %% 初始阶段: 管理者主导
    Start([用户输入目标]) --> ManagerInit[Manager: 分析目标]
    ManagerInit --> SelectRoles[Manager: 确定介入角色]
    SelectRoles --> DefineWorkflow[Manager: 确定项目流程]
    
    %% 规划阶段
    DefineWorkflow --> StatePlanning{状态: PLANNING}
    StatePlanning --> PM[PM Agent: 需求调研与任务拆解]
    PM --> TaskList[生成 Task List / 计划]

    %% 执行循环 (Manager 决策中心)
    TaskList --> ManagerDecision[Manager Agent: 执行决策与调度]
    ManagerDecision -->|指派/启动| StateExec{状态: EXECUTING}
    StateExec --> Exec[Executor: 执行子任务]
    Exec --> Review{Reviewer: 审核产出}

    %% 反馈闭环 (回到 Manager)
    Review -- 审核完毕 --> Feedback[任务状态反馈: 完成/失败/中断]
    Feedback --> ManagerDecision

    %% 用户介入 (回到 Manager)
    UserFeedback((用户反馈/介入)) -.-> ManagerDecision

    %% Manager 调整决策分支
    ManagerDecision -. -->|重规划| StatePlanning
    ManagerDecision -. -->|继续下一任务| TaskList
    ManagerDecision -. -->|修正执行| StateExec

    %% 结束
    ManagerDecision -- 目标达成 --> End([完成])
```
- **实时看板**: 采用甘特图或流程图形式展示任务链及每个节点的实时状态。
- **协作对话框**: 用户可以在任何阶段与正在运行的 Agent 群体对话，补充信息或纠正偏差。
- **调试视图**: 展示 Agent 间的内部对话和 Tool 调用详情。

## 5. 技术栈建议
- **核心框架**: Spring Boot 3 + Spring AI Alibaba
- **异步处理**: Reactor (Project Reactor) / Virtual Threads (Java 21)
- **状态机**: Spring Statemachine 或轻量级自定义实现
- **实时通信**: Spring WebSocket + STOMP
- **向量存储**: DashVector (Spring AI 原生支持)
- **数据库**: MySQL (任务持久化) + Redis (实时上下文)

## 6. 实施路线图
1. **Phase 1**: 确定角色定义及基础 `BaseMasAgent` 抽象。
2. **Phase 2**: 实现核心 `MasOrchestrator` 和基于消息的状态机原型。
3. **Phase 3**: 集成长短期记忆系统。
4. **Phase 4**: 开发用户前端交互界面及介入功能。
