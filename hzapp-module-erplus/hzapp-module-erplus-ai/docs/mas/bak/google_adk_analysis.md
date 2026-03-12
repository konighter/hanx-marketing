# Google ADK 集成可行性分析

## 1. Google ADK 概述

Google Agent Development Kit (ADK) 是 Google 推出的开源 AI Agent 框架，于 2025 年 Google I/O 大会发布 Java 版本。

### 核心组件

| 组件 | 说明 |
|------|------|
| `LlmAgent` | 基于 LLM 的智能体 |
| `FunctionTool` | 自定义 Java 工具 |
| `AgentTool` | 将 Agent 作为工具 |
| `SequentialAgent` | 顺序执行工作流 |
| `ParallelAgent` | 并行执行工作流 |
| `LoopAgent` | 循环迭代工作流 |
| `GoogleSearchTool` | 内置搜索工具 |

### 优势对比

| 特性 | 当前实现 | Google ADK |
|------|----------|------------|
| Agent 定义 | 自定义接口 | `LlmAgent` |
| Tool 定义 | Spring AI FunctionCalling | `@Schema` 注解 |
| 工作流 | 手写状态机 | `Sequential/Parallel/LoopAgent` |
| 多 Agent 编排 | Manager 手动调度 | 声明式 `subAgents()` |
| 内置工具 | 无 | Google Search, 代码执行器 |
| 状态共享 | `MasContext` | `outputKey` + `{key}` 占位符 |

---

## 2. 改动范围分析

### 2.1 依赖变更 (pom.xml)

```xml
<!-- 需要添加 -->
<dependency>
    <groupId>com.google</groupId>
    <artifactId>google-adk</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2.2 需要新增的适配器

| 适配器 | 职责 |
|--------|------|
| `AdkMasAgentAdapter` | 将 `MasAgent` 适配为 ADK `LlmAgent` |
| `AdkToolAdapter` | 将 Spring AI Tool 适配为 ADK `FunctionTool` |
| `AdkOrchestratorAdapter` | 将 `MasOrchestrator` 适配为 ADK 工作流 |

### 2.3 接口变更

| 接口 | 变更说明 |
|------|----------|
| `MasAgent` | 可选择实现 `AdkCompatible` 标记接口 |
| `MasTask` | 添加 `outputKey` 字段支持 |

---

## 3. 集成架构设计

```
┌─────────────────────────────────────────────────────────┐
│                    Google ADK Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  LlmAgent    │  │ Sequential   │  │   Parallel   │ │
│  │              │  │   Agent      │  │    Agent     │ │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘ │
│         │                 │                   │          │
│         └────────────┬────┴─────────────────┘          │
│                      ▼                                 │
│              AgentTool (Adapter)                       │
└──────────────────────┬────────────────────────────────┘
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   Current MAS Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  MasAgent    │  │    PM        │  │   Executor   │ │
│  │  (Interface) │  │    Agent     │  │    Agent     │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 4. 迁移策略

### 渐进式迁移 (推荐)

1. **Phase 1**: 保留现有 `MasAgent`，新增 ADK 适配器
2. **Phase 2**: 新功能使用 ADK 工作流，旧功能逐步迁移
3. **Phase 3**: 统一使用 ADK，移除适配器层

### 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 依赖冲突 | ADK 与 Spring AI 版本兼容 | 隔离模块加载 |
| 功能缺失 | ADK 缺少某些特性 | 保持双轨运行 |
| 学习成本 | 团队需要学习 ADK | 文档 + 培训 |

---

## 5. 推荐行动

1. **小规模试点**: 选择一个简单场景用 ADK 实现
2. **对比评估**: 性能、可维护性、功能完整性
3. **决定**: 是否全面迁移或保持双轨
