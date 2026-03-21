# Flowable + ADK Agent 集成设计

## 背景

MAS (Multi-Agent System) 需要在 Flowable 流程引擎上实现 Skill 的逻辑编排与调度。暂不依赖自研 MasAgent 框架（`WorkflowOrchestrator`, `PlannerAgent` 等），改用 Google ADK 作为 AI 推理引擎，通过将业务 Service 注册为 ADK Tools，由大模型驱动 Tool 组合来完成各阶段逻辑。

## 架构概览

```
Flowable (宏观编排, 租户隔离)
  └─ skill-seq-tasks: Phase 串行 + 监控/报警
      └─ skill-task-loop: collect → decide → [confirm?] → execute → wait → review → loop
          │
          ↓
SkillTaskDelegate (桥接层)
  └─ 从 execution 提取上下文 → SkillAgentFactory 路由 → ADK Runner 执行 → 回写结果
          │
          ↓
ADK Agent Tree (微观推理, 共享 Session & Memory)
  └─ SkillAgentFactory 按 skillCode 构建, 按 phase 路由
      ├── CollectAgent   → 查询类 Tools
      ├── DecideAgent    → 查询类 Tools (只输出建议, 不执行)
      ├── ExecuteAgent   → 操作类 + 校验类 Tools
      └── ReviewAgent    → 查询类 Tools
          │
          ↓
Tool 注册表 (MasToolRegistry)
  ├── ADV 查询: getAdCampaigns, getAdMetrics, getKeywordReport, getCompetitorData
  ├── BIZ 查询: getListingInfo, getInventoryStatus
  ├── ADV 操作: toggleCampaignStatus, adjustBid, adjustPlacementBid, addKeyword, removeKeyword, adjustKeywordBid
  └── BIZ 操作: adjustPrice, updateListing
```

## 核心组件设计

### 1. SkillAgentFactory

**职责**: Agent 工厂 + 路由器。按 `skillCode` 缓存 Agent Tree，按 `phase` 路由到子 Agent。

**位置**: `hzapp-module-erplus-ai` 模块

**核心接口**:
```java
@Service
public class SkillAgentFactory {
    // 根据 skillCode 和 phase 获取对应的 LlmAgent
    public LlmAgent getAgent(String skillCode, String phase);
    
    // 构建 Agent Tree (首次调用时创建并缓存)
    private AgentTree buildAgentTree(String skillCode, List<String> requiredTools);
}
```

**Agent Tree 结构**: 每个 skillCode 对应一棵 Agent Tree，包含 4 个子 Agent。Agent Tree 按 skillCode 缓存，避免重复构建。

**Tool 加载**: 从 `MasSkillDefDO.requiredTools` 读取该 Skill 声明的 Tool 列表，再根据阶段过滤：
- `collect` / `review`: 仅查询类 Tools
- `decide`: 查询类 Tools（允许补充查询，但不允许执行操作）
- `execute`: 操作类 Tools + 部分查询类 Tools（用于校验）

### 2. SkillTaskDelegate 改造

**改造要点**: 去掉 `WorkflowOrchestrator` 依赖，改用 `SkillAgentFactory` + ADK `Runner`。

每个阶段方法的统一流程：
1. 从 `DelegateExecution` 提取上下文（skillCode, sessionId, tenantId, phaseName 等）
2. 恢复租户上下文：`TenantContextHolder.setTenantId(tenantId)`
3. 通过 `SkillAgentFactory.getAgent(skillCode, phase)` 获取子 Agent
4. 构建用户消息（含阶段指引 `phaseInstruction` + 迭代信息）
5. 通过 ADK `Runner.runAsync(sessionId, userMessage)` 执行
6. 解析 Agent 输出，写回 Flowable 流程变量
7. 记录日志到 `mas_skill_instance_log`

### 3. 四阶段 Agent Instruction

#### CollectAgent
> 你是一个广告数据分析助手。请根据以下策略指引，调用可用的数据查询工具收集目标商品的相关数据。
> 目标 ASIN: {targetBizId}, 当前阶段: {phaseName}
> 策略指引: {phaseInstruction}
> 请收集必要的信息并整理成结构化分析报告。

#### DecideAgent
> 你是一个广告策略专家。基于已收集的数据，按照以下策略规则制定具体操作方案。
> 策略规则: {phaseInstruction}, 迭代轮次: {iteration}
> 请输出结构化的操作建议（JSON 数组），每条包含：actionType, target, params, expectedEffect。
> ⚠️ 你不能直接执行操作，只能输出建议。

#### ExecuteAgent
> 你是一个广告操作执行器。请严格按照以下操作方案逐条执行，不要自行发挥。
> 操作方案: {decisionOutput}
> 执行后记录每条操作的结果（成功/失败/原因）。

#### ReviewAgent
> 你是一个广告策略复盘分析师。请对比策略执行前后的数据变化，评估效果。
> 关注指标：ROI, ACOS, 转化率, 点击率, 曝光量等。
> 产出优化建议供下一轮迭代参考。

### 4. Tool 注册表 (MasToolRegistry)

**位置**: `hzapp-module-erplus-ai` 模块

所有 Tools 通过 Spring Bean 注册，初期全部为 Mock 实现。

#### 查询类 Tools

| Tool 名 | 模块 | 入参 | 说明 |
|---------|------|------|------|
| `getAdCampaigns` | ADV | asin, days | 获取广告活动列表及核心指标 |
| `getAdMetrics` | ADV | asin, days | 获取近 N 天广告汇总指标 |
| `getKeywordReport` | ADV | asin, campaignId, days | 获取关键词表现报告 |
| `getCompetitorData` | ADV | asin | 获取竞品广告/价格数据 |
| `getListingInfo` | BIZ | asin | 获取 Listing 详情 |
| `getInventoryStatus` | BIZ | asin | 获取库存状态 |

#### 操作类 Tools

| Tool 名 | 模块 | 入参 | 说明 |
|---------|------|------|------|
| `toggleCampaignStatus` | ADV | campaignId, enabled | 启用/暂停广告活动 |
| `adjustBid` | ADV | campaignId, adGroupId, targetId, newBid | 调整 BID |
| `adjustPlacementBid` | ADV | campaignId, placement, percentage | 调整位置 BID 规则 |
| `addKeyword` | ADV | campaignId, adGroupId, keyword, matchType, bid | 新增关键词 |
| `removeKeyword` | ADV | keywordId | 删除关键词 |
| `adjustKeywordBid` | ADV | keywordId, newBid | 调整关键词 BID |
| `adjustPrice` | BIZ | asin, newPrice | 调整商品价格 |
| `updateListing` | BIZ | asin, field, value | 修改 Listing |

### 5. 租户隔离

| 层面 | 实现方式 |
|------|---------|
| Flowable 流程启动 | `startProcessInstanceByKeyAndTenantId(key, bk, params, tenantId)` |
| Delegate 方法入口 | 从流程变量恢复 `TenantContextHolder.setTenantId(tenantId)` |
| ADK Session ID | 格式: `{tenantId}:{skillCode}:{targetBizId}` |
| Tool 数据隔离 | Tool 内部通过 `TenantContextHolder` 获取租户 |

### 6. Session & Memory 共享

- ADK 的 `InMemorySessionService` 或后续的 `DatabaseSessionService` 管理 Session
- Session ID 在 Flowable 流程启动时生成，贯穿所有阶段
- 4 个子 Agent 共享同一个 Session，CollectAgent 写入的数据可被 DecideAgent 直接引用
- Session 中的 `events`（对话历史）作为天然的 Memory

## 文件变更清单

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `ai/mas/adk/SkillAgentFactory.java` | Agent 工厂，按 skillCode 构建 Agent Tree，按 phase 路由 |
| `ai/mas/adk/MasToolRegistry.java` | Tool 注册表，管理所有 Tool 的注册和按 Skill/阶段加载 |
| `ai/mas/adk/tools/query/GetAdCampaignsTool.java` | 获取广告活动列表 (Mock) |
| `ai/mas/adk/tools/query/GetAdMetricsTool.java` | 获取广告汇总指标 (Mock) |
| `ai/mas/adk/tools/query/GetKeywordReportTool.java` | 获取关键词报告 (Mock) |
| `ai/mas/adk/tools/query/GetCompetitorDataTool.java` | 获取竞品数据 (Mock) |
| `ai/mas/adk/tools/query/GetListingInfoTool.java` | 获取 Listing 详情 (Mock) |
| `ai/mas/adk/tools/query/GetInventoryStatusTool.java` | 获取库存状态 (Mock) |
| `ai/mas/adk/tools/action/ToggleCampaignStatusTool.java` | 启用/暂停广告 (Mock) |
| `ai/mas/adk/tools/action/AdjustBidTool.java` | 调整 BID (Mock) |
| `ai/mas/adk/tools/action/AdjustPlacementBidTool.java` | 调整位置 BID (Mock) |
| `ai/mas/adk/tools/action/AddKeywordTool.java` | 新增关键词 (Mock) |
| `ai/mas/adk/tools/action/RemoveKeywordTool.java` | 删除关键词 (Mock) |
| `ai/mas/adk/tools/action/AdjustKeywordBidTool.java` | 调整关键词 BID (Mock) |
| `ai/mas/adk/tools/action/AdjustPriceTool.java` | 调整价格 (Mock) |
| `ai/mas/adk/tools/action/UpdateListingTool.java` | 修改 Listing (Mock) |

### 改造文件

| 文件路径 | 说明 |
|---------|------|
| `adv/service/mas/SkillTaskDelegate.java` | 去掉 WorkflowOrchestrator，改用 SkillAgentFactory + ADK Runner |
| `adv/service/mas/SkillInitDelegate.java` | 确保 tenantId 写入流程变量 |

## 验证计划

### 单元测试
- `SkillAgentFactory`: 验证 Agent Tree 构建、phase 路由、Tool 过滤
- `MasToolRegistry`: 验证 Tool 注册、按 Skill/阶段加载
- 各 Mock Tool: 验证入参解析和 Mock 返回值

### 集成测试
- 启动一个完整的 Flowable 流程，验证 4 个阶段依次执行
- 验证 Session 共享（CollectAgent 的数据能被 DecideAgent 引用）
- 验证租户隔离（不同 tenantId 的流程互不干扰）
