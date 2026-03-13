# MAS 多 Phase 场景模拟验证 (12 Phases)

## 场景: 为亚马逊店铺构建自动化广告投放方案

> **用户目标**: "分析亚马逊店铺近 3 个月的广告数据，找出 ACOS 过高的 Campaign，优化出价策略，生成执行报告并自动调整出价"
>
> **已注册工具**: `database_query`, `amazon_sp_api`, `report_generator`, `bid_adjuster`
>
> **已注册 Agent**: ANALYST, STRATEGIST, EXECUTOR, REVIEWER, WRITER

---

## 状态追踪

```
sessionId = "S-ADV-001"
GlobalSessionMemory = {} (初始为空)
```

---

## Phase 1: 数据采集

### PlannerAgent 输入

```
userGoal     = "分析亚马逊店铺近3个月广告数据，找出ACOS过高Campaign，优化出价，生成报告并自动调整"
globalContext = ""  ← 首次执行，无历史
```

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "第一步：从 Amazon SP-API 拉取广告数据，并从内部 DB 拉取订单数据，两者可并行",
  "nodes": [
    {"id": "p1-n1", "agentRole": "ANALYST", "instruction": "通过 SP-API 拉取近3个月广告报表",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api"]},
    {"id": "p1-n2", "agentRole": "ANALYST", "instruction": "从内部数据库查询近3个月订单数据以计算真实ACOS",
     "nodeType": "REACT", "toolSet": ["database_query"]}
  ]
}
```

### DAG 调度

```
p1-n1: requires=[], → 立即提交 ✅
p1-n2: requires=[], → 立即提交 ✅  ← 两个节点并行执行
```

### p1-n1 ReAct 循环 (ANALYST + amazon_sp_api)

```
Step 1: Think "需要调用 SP-API 获取 Sponsored Products 报表"
        Act   amazon_sp_api({action: "getReport", reportType: "SP_CAMPAIGNS", period: "90d"})
        Obs   ToolResult.success("{campaigns: [{id: C1, spend: 5000, sales: 8000, acos: 62.5%}, ...]}")

Step 2: Think "数据已获取，共 15 个 Campaign"
        final_answer "SP广告数据: 15个Campaign, 总花费¥45000, 总销售¥72000, 平均ACOS=62.5%"
```

### p1-n2 ReAct 循环 (ANALYST + database_query) — 并行执行

```
Step 1: Think "查询订单表获取真实销售数据"
        Act   database_query({sql: "SELECT campaign_id, SUM(order_amount) FROM orders WHERE ..."})
        Obs   ToolResult.success("[{campaign_id: C1, real_sales: 7500}, ...]")

Step 2: final_answer "内部订单数据: 15个Campaign的真实销售总额¥68000"
```

### Phase 1 结果

```
DagExecutionResult: outcome=ALL_SUCCESS, success=2, failed=0, duration=3200ms
globalHistory = "
- Phase 1: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=3200ms"

GlobalSessionMemory:
  ANALYST_output (p1-n1) = "SP广告数据: 15个Campaign..."
  ANALYST_output (p1-n2) = "内部订单数据: 15个Campaign..."
```

✅ **验证**: 两个无依赖 REACT 节点并行执行，各自独立工具调用

---

## Phase 2: 数据交叉分析

### PlannerAgent 输入

```
globalContext = "Phase 1: 2 nodes, outcome=ALL_SUCCESS..."
+ sessionMemory 中有 ANALYST_output 数据
```

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "交叉比对 SP-API 和内部DB数据，计算真实 ACOS 并标记异常 Campaign",
  "nodes": [
    {"id": "p2-n1", "agentRole": "ANALYST", "instruction": "交叉比对两个数据源，计算每个Campaign的真实ACOS并标记ACOS>50%的异常Campaign",
     "nodeType": "REACT", "toolSet": ["database_query"]}
  ]
}
```

### p2-n1 ReAct 循环

```
Step 1: Think "需要把SP数据和DB数据做JOIN比对"
        Act   database_query({sql: "SELECT c.campaign_id, c.sp_sales, o.real_sales, (c.spend/o.real_sales*100) as real_acos FROM ..."})
        Obs   ToolResult.success("[{campaign_id:C1, real_acos:66.7%}, {campaign_id:C3, real_acos:85.2%}, ...]")

Step 2: Think "发现5个Campaign真实ACOS超过50%线"
        final_answer "异常Campaign: C1(66.7%), C3(85.2%), C7(71.3%), C9(58.9%), C12(92.1%)"
```

### Phase 2 结果

```
globalHistory += "
- Phase 2: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=1800ms"
```

✅ **验证**: PlannerAgent 读取 Phase 1 历史 → 规划下一步

---

## Phase 3: 策略制定 (SIMPLE 节点)

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "基于异常Campaign分析结果，制定降低ACOS的出价调整策略",
  "nodes": [
    {"id": "p3-n1", "agentRole": "STRATEGIST", "instruction": "根据5个异常Campaign数据制定出价调整策略",
     "nodeType": "SIMPLE"}
  ]
}
```

### p3-n1 执行 (AdkNodeRunner)

```
NodeExecutor:
  node.getNodeType() == SIMPLE → selectedRunner = nodeRunner (AdkNodeRunner)
  AdkNodeRunner → agent.execute(nodeMemory)
  STRATEGIST 从 sessionMemory 读取: ANALYST_output = "异常Campaign: C1(66.7%)..."
  返回: "出价策略: C1降20%, C3降40%, C7降25%, C9降10%, C12暂停投放"
```

### Phase 3 结果

```
globalHistory += "
- Phase 3: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=2100ms"
```

✅ **验证**: SIMPLE 节点正确使用 AdkNodeRunner，从 Memory 读取前序结果

---

## Phase 4: 执行出价调整 — ⚠️ PARTIAL_FAILURE

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "按策略逐个调整Campaign出价",
  "nodes": [
    {"id": "p4-n1", "agentRole": "EXECUTOR", "instruction": "调整C1出价降低20%",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api", "bid_adjuster"]},
    {"id": "p4-n2", "agentRole": "EXECUTOR", "instruction": "调整C3出价降低40%",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api", "bid_adjuster"]},
    {"id": "p4-n3", "agentRole": "EXECUTOR", "instruction": "调整C7出价降低25%",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api", "bid_adjuster"]},
    {"id": "p4-n4", "agentRole": "EXECUTOR", "instruction": "暂停C12投放",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api"]}
  ]
}
```

### DAG 调度 — 4 个节点全部并行

```
p4-n1: ✅ ReAct: bid_adjuster → 成功降低C1出价20%
p4-n2: ✅ ReAct: bid_adjuster → 成功降低C3出价40%
p4-n3: ❌ ReAct Step 1: amazon_sp_api({action: "updateBid", campaignId: "C7"})
                → ToolResult.failure("API rate limit exceeded. Retry after 60 seconds")
           Step 2: Think "API 限流了，等一会再试"
                   Act: amazon_sp_api({action: "updateBid"...})
                → ToolResult.failure("API rate limit exceeded")
           ...
           Step 25: 达到 maxSteps → throw RuntimeException
           → NodeExecutor: retryPolicy.maxAttempts=1 → FAILED
           → NodeCompletionEvent.failure("p4-n3", "EXECUTOR", "exceeded maximum steps")

p4-n4: ✅ ReAct: amazon_sp_api → 成功暂停C12
```

### Phase 4 结果

```
DagExecutionResult:
  outcome = PARTIAL_FAILURE
  successCount = 3, failedCount = 1, skippedCount = 0
  failures = [{nodeId: "p4-n3", agentRole: "EXECUTOR", errorMessage: "exceeded maximum steps"}]

WorkflowOrchestrator (L179-192):
  dagResult.isSuccess() = false
  dagResult.getOutcome() == PARTIAL_FAILURE  → 不是 DEADLOCK/TIMEOUT → 不直接返回 FAIL
  → 继续 ← "PARTIAL_FAILURE: continue to next phase so Planner can adapt"  ✅

globalHistory += "
- Phase 4: 4 nodes, outcome=PARTIAL_FAILURE, success=3, failed=1, skipped=0, duration=58000ms
  Failures:
    - [p4-n3/EXECUTOR]: exceeded maximum steps"
```

✅ **验证**: PARTIAL_FAILURE 不中断流程，失败详情记入 globalHistory，Planner 下一轮可读取

---

## Phase 5: 自修复 — PlannerAgent 看到失败历史

### PlannerAgent 输入

```
globalContext 包含:
  "Phase 4: PARTIAL_FAILURE... Failures: [p4-n3/EXECUTOR]: exceeded maximum steps"
```

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "C7的出价调整因API限流失败，需要单独重试。C9还没有处理。本轮只处理这两个。",
  "nodes": [
    {"id": "p5-n1", "agentRole": "EXECUTOR", "instruction": "重新调整C7出价降低25%（注意API限流，请间隔调用）",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api", "bid_adjuster"]},
    {"id": "p5-n2", "agentRole": "EXECUTOR", "instruction": "调整C9出价降低10%",
     "nodeType": "REACT", "toolSet": ["bid_adjuster"], "dependsOn": ["p5-n1"]}
  ]
}
```

### 执行 — 这次成功

```
p5-n1: ReAct Step 1: bid_adjuster → ToolResult.success("C7 bid reduced by 25%")
       Step 2: final_answer "C7 出价已降低25%"  ✅
p5-n2: 等待 p5-n1 完成 → bid_adjuster → "C9 出价已降低10%"  ✅
```

### Phase 5 结果

```
globalHistory += "
- Phase 5: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=4500ms"
```

✅ **验证**: PlannerAgent 读完失败历史 → 自修复策略 → 串行执行避免限流

---

## Phase 6: 验证调整效果 — 需要等待数据

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "出价调整已全部完成，需要验证调整是否生效",
  "nodes": [
    {"id": "p6-n1", "agentRole": "ANALYST", "instruction": "查询SP-API确认5个Campaign的当前出价是否已更新",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api"]}
  ]
}
```

### p6-n1 执行

```
Step 1: amazon_sp_api({action: "getCampaigns", ids: ["C1","C3","C7","C9","C12"]})
        Obs: "C1: bid=0.80(原1.00), C3: bid=0.60(原1.00), C7: bid=0.75(原1.00), C9: bid=0.90(原1.00), C12: PAUSED"
Step 2: final_answer "全部5个Campaign调整已生效"
```

```
globalHistory += "
- Phase 6: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=2000ms"
```

✅ **验证**: 验证阶段逻辑正确

---

## Phase 7: RESUME — 等待数据积累

### PlannerAgent 输出

```json
{
  "status": "RESUME",
  "reasoning": "出价调整已生效但需等待至少24小时收集新数据才能评估效果。建议明天同一时间再执行后续分析。",
  "nextExecuteAt": "2026-03-13T11:00:00"
}
```

### WorkflowOrchestrator (L140-154)

```
plan.getStatus() == RESUME
nextTime = LocalDateTime.parse("2026-03-13T11:00:00")

return MasOrchestrationResult.builder()
    .type(SUSPEND)
    .nextExecuteTime(2026-03-13T11:00:00)
    .history(globalHistory)
    .build();
```

✅ **验证**: RESUME 正确返回 SUSPEND + nextExecuteTime，流程暂停

---

## ⏸️ 暂停 24 小时 — MasTaskJob 恢复执行

```
MasTaskJob 定时扫描 → 发现 S-ADV-001 到达 nextExecuteTime
→ 重新调用 WorkflowOrchestrator.executeMacroLoop("S-ADV-001", 同一 userGoal)
→ sessionMemory 从 DB 恢复 (包含 globalHistory + 所有 Agent 输出)
→ phaseCount 从 1 重新开始
```

> **注意**: phaseCount 重新从 1 开始，但 globalHistory 保留了之前 6 个 Phase 的完整记录。PlannerAgent 通过 globalHistory 知道之前做了什么。

---

## Phase 8 (恢复后 Phase 1): 采集新数据比对效果

### PlannerAgent 输入

```
globalContext = "
- Phase 1: 2 nodes, ALL_SUCCESS...
- Phase 2: 1 nodes, ALL_SUCCESS...  
- Phase 3: 1 nodes, ALL_SUCCESS...
- Phase 4: 4 nodes, PARTIAL_FAILURE... [p4-n3]: exceeded maximum steps
- Phase 5: 2 nodes, ALL_SUCCESS... (自修复)
- Phase 6: 1 nodes, ALL_SUCCESS... (验证生效)
"
```

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "恢复执行。之前已完成出价调整并等待24h，现在需要拉取新数据评估效果",
  "nodes": [
    {"id": "p8-n1", "agentRole": "ANALYST", "instruction": "拉取最近24小时广告表现数据",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api"]},
    {"id": "p8-n2", "agentRole": "ANALYST", "instruction": "拉取最近24小时订单数据",
     "nodeType": "REACT", "toolSet": ["database_query"]}
  ]
}
```

### 并行执行 → 全部成功

```
globalHistory += "
- Phase 7: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=2800ms"
```

✅ **验证**: 恢复执行后 PlannerAgent 通过 globalHistory 理解上下文

---

## Phase 9: 效果评估

### PlannerAgent 输出 — 1个 REACT + 1个 SIMPLE

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "比对调整前后数据",
  "nodes": [
    {"id": "p9-n1", "agentRole": "ANALYST", "instruction": "计算5个Campaign调整前vs调整后的ACOS变化",
     "nodeType": "REACT", "toolSet": ["database_query"]},
    {"id": "p9-n2", "agentRole": "STRATEGIST", "instruction": "评估调整效果并给出下一步建议",
     "nodeType": "SIMPLE", "dependsOn": ["p9-n1"]}
  ]
}
```

### 执行

```
p9-n1 (REACT): 
  Step 1: database_query → "{C1: 66.7%→52.1%↓, C3: 85.2%→61.0%↓, C7: 71.3%→55.8%↓, C9: 58.9%→51.2%↓, C12: PAUSED}"
  Step 2: final_answer "调整效果: 4个Campaign ACOS均下降，但C3仍>60%需进一步优化"

p9-n2 (SIMPLE): AdkNodeRunner 
  从 Memory 读取 ANALYST_output
  返回: "建议: C3需要进一步降低出价或调整关键词，其余暂维持。C12可评估是否重新启用。"

globalHistory += "
- Phase 8: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=3500ms"
```

✅ **验证**: REACT→SIMPLE 串行依赖正确

---

## Phase 10: 二次优化

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "C3仍需进一步优化，需要分析关键词级别数据",
  "nodes": [
    {"id": "p10-n1", "agentRole": "ANALYST", "instruction": "查询C3的关键词级别报表，找出ACOS最高的关键词",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api", "database_query"]},
    {"id": "p10-n2", "agentRole": "EXECUTOR", "instruction": "将C3中ACOS>80%的关键词设为Negative",
     "nodeType": "REACT", "toolSet": ["amazon_sp_api"], "dependsOn": ["p10-n1"]}
  ]
}
```

### p10-n1 ReAct (多工具组合)

```
Step 1: Think "先从SP-API拉关键词报表"
        Act: amazon_sp_api({action: "getKeywordReport", campaignId: "C3"})
        Obs: "[{keyword: 'cheap widget', acos: 120%}, {keyword: 'widget sale', acos: 35%}, ...]"

Step 2: Think "再从DB查对应的转化数据"
        Act: database_query({sql: "SELECT keyword, conversion_rate FROM ..."})
        Obs: "[{keyword: 'cheap widget', cvr: 0.2%}, ...]"

Step 3: final_answer "C3关键词分析: 'cheap widget'(ACOS:120%, CVR:0.2%) 和 'discount widget'(ACOS:95%, CVR:0.5%) 应设为Negative"
```

### p10-n2 ReAct 执行 (等 p10-n1 完成)

```
Step 1: amazon_sp_api({action: "addNegativeKeywords", campaignId: "C3", keywords: ["cheap widget", "discount widget"]})
        Obs: "成功添加2个Negative关键词"
Step 2: final_answer "已将2个高ACOS关键词设为Negative"
```

```
globalHistory += "
- Phase 9: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=6200ms"
```

✅ **验证**: 单个 REACT 节点使用多个工具，依赖串行正确

---

## Phase 11: 生成汇总报告

### PlannerAgent 输出

```json
{
  "status": "IN_PROGRESS",
  "reasoning": "所有优化已完成，生成最终报告",
  "nodes": [
    {"id": "p11-n1", "agentRole": "WRITER", "instruction": "生成包含全部优化过程和效果的Markdown报告",
     "nodeType": "REACT", "toolSet": ["report_generator"]}
  ]
}
```

### p11-n1 ReAct

```
Step 1: Think "汇总所有Phase的数据生成报告"
        Act: report_generator({title: "亚马逊广告优化报告", format: "markdown", content: ...})
        Obs: ToolResult.success("report saved to /reports/adv-opt-20260312.md")
Step 2: final_answer "报告已生成: /reports/adv-opt-20260312.md"

globalHistory += "
- Phase 10: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=3000ms"
```

✅ **验证**: 报告生成工具调用正确

---

## Phase 12: 任务完成

### PlannerAgent 输入

```
globalContext = "
- Phase 1-6: 数据采集、分析、策略制定、出价调整(含失败重试)、验证
- Phase 7-8: 恢复后数据比对
- Phase 9: 效果评估
- Phase 10: 二次关键词优化
- Phase 11: 报告生成
"
```

### PlannerAgent 输出

```json
{
  "status": "DONE",
  "reasoning": "全部优化步骤已完成: 1) 数据采集和交叉分析 2) 5个Campaign出价调整 3) C3关键词级别优化 4) 效果验证(ACOS平均下降15%) 5) 报告已生成"
}
```

### WorkflowOrchestrator (L127-132)

```
plan.getStatus() == DONE
→ return MasOrchestrationResult.builder()
    .type(FINISH)
    .history(globalHistory)
    .build();
```

✅ **验证**: PlannerAgent 判断 DONE，流程正常结束

---

## 全流程 globalHistory 最终状态

```
- Phase 1: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=3200ms
- Phase 2: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=1800ms
- Phase 3: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=2100ms
- Phase 4: 4 nodes, outcome=PARTIAL_FAILURE, success=3, failed=1, skipped=0, duration=58000ms
  Failures:
    - [p4-n3/EXECUTOR]: exceeded maximum steps
- Phase 5: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=4500ms
- Phase 6: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=2000ms
⏸️ RESUME (等待24小时)
- Phase 7: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=2800ms
- Phase 8: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=3500ms
- Phase 9: 2 nodes, outcome=ALL_SUCCESS, success=2, failed=0, skipped=0, duration=6200ms
- Phase 10: 1 nodes, outcome=ALL_SUCCESS, success=1, failed=0, skipped=0, duration=3000ms
⏹️ DONE
```

---

## 验证矩阵

| # | 验证项 | Phase | 状态 |
|---|---|---|---|
| 1 | 无依赖节点并行执行 | P1 (n1∥n2) | ✅ |
| 2 | REACT 节点使用指定 toolSet | P1-n1 仅 amazon_sp_api | ✅ |
| 3 | SIMPLE 节点走 AdkNodeRunner | P3 | ✅ |
| 4 | PlannerAgent 读取 globalHistory 规划下一步 | P2 | ✅ |
| 5 | PARTIAL_FAILURE 不中断流程 | P4 (3 成功 1 失败) | ✅ |
| 6 | 失败详情写入 globalHistory | P4 Failures 记录 | ✅ |
| 7 | PlannerAgent 自修复失败任务 | P5 重试 C7 | ✅ |
| 8 | 串行依赖正确等待 | P5 (n2 依赖 n1) | ✅ |
| 9 | RESUME 暂停恢复 | P7 → P8 | ✅ |
| 10 | 恢复后 globalHistory 保持完整 | P8 PlannerAgent 读到完整历史 | ✅ |
| 11 | 单 REACT 节点多工具组合 | P10-n1 (sp_api + db) | ✅ |
| 12 | 流程最终 DONE 正常退出 | P12 | ✅ |
| 13 | globalHistory 结构化累积 (含 outcome/counts/failures) | 全部 10 个 Phase | ✅ |
| 14 | REACT→SIMPLE 混合依赖链 | P9 (n1:REACT → n2:SIMPLE) | ✅ |

> **发现的潜在问题**: RESUME 后 `phaseCount` 从 1 重新开始，但 globalHistory 中已有 "Phase 1-6" 的记录。这不会影响逻辑正确性（PlannerAgent 依据 globalHistory 内容决策，不依赖 phaseCount 数值），但日志中 Phase 编号会重复。如需修复可考虑将 phaseCount 持久化到 sessionMemory。
