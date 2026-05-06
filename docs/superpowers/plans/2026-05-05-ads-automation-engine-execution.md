# 广告自动化引擎执行侧实现计划 (Ads Automation Engine Execution)

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建广告自动化计划的后台执行引擎，实现基于利润模型和 ROAS 阈值的自动化出价、调价及广告活动管理。

**Architecture:**
- **任务驱动**: 接入 `ErpTaskEngine` 系统，每个运营计划对应一个独立的调度任务。
- **原子操作**: 通过 `AdsActionDispatcher` 统一封装与 Amazon/Meta API 的交互及日志审计。
- **策略隔离**: 实现同类词聚合与高 ROAS 词独立广告活动逻辑。

**Tech Stack:** Java 17, Spring Boot, MyBatis Plus, Vue 3, Element Plus

---

## Chunk 1: 后端基础设施与接口定义 (Infrastructure)

### Task 1: 创建出价范围与利润模型接口
**Files:**
- Create: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/automation/dto/BidRangeDTO.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/automation/service/AdsProfitModelService.java`
- Create: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/automation/service/AdsCampaignSimilarityService.java`

- [ ] **Step 1: 定义 BidRangeDTO**
```java
package com.hzltd.module.erplus.adv.automation.dto;
import lombok.Data;
@Data
public class BidRangeDTO {
    private Double minBid;
    private Double maxBid;
    private Double targetCpc; // 推荐出价
}
```
- [ ] **Step 2: 定义 AdsProfitModelService (基于 ACOS 计算出价范围)**
```java
package com.hzltd.module.erplus.adv.automation.service;
import com.hzltd.module.erplus.adv.automation.dto.BidRangeDTO;
public interface AdsProfitModelService {
    /**
     * 根据 SKU 和目标 ACOS 计算利润模型约束下的出价范围
     */
    BidRangeDTO calculateBidRange(String sku, String platform, Double targetAcos);
}
```
- [ ] **Step 3: 定义 AdsCampaignSimilarityService (词根匹配)**
```java
package com.hzltd.module.erplus.adv.automation.service;
import java.util.List;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
public interface AdsCampaignSimilarityService {
    /**
     * 寻找相似词根的广告活动
     */
    List<AdsCampaignDO> findSimilarCampaigns(List<Long> associatedCampaignIds, String keyword);
}
```

---

## Chunk 2: 任务处理器与执行引擎 (Execution Engine)

### Task 2: 实现自动化计划任务处理器
**Files:**
- Create: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/automation/engine/AdsAutomationPlanTaskHandler.java`
- Modify: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/auto_plan/service/AdsAutomationPlanServiceImpl.java`

- [ ] **Step 1: 实现 AdsAutomationPlanTaskHandler**
  - 实现 `ErpTaskHandler` 接口。
  - `getTaskType()` 返回 `ADV_AUTO_PLAN_EXEC`。
  - `onStart()` 逻辑：初始化广告结构 -> 抓取报表 -> 规则评估 -> 原子操作分发 -> 重新排期下一次执行。
- [ ] **Step 2: 在计划创建时关联任务**
  - 在 `AdsAutomationPlanServiceImpl.createAdsAutomationPlan` 成功后，调用 `ErpTaskEngine.submitTask`。

---

## Chunk 3: 原子操作分发器 (Action Dispatcher)

### Task 3: 实现 AdsActionDispatcher
**Files:**
- Create: `hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/automation/engine/AdsActionDispatcher.java`

- [ ] **Step 1: 定义原子操作方法**
  - `promoteKeyword(term, targetCampaign)`: 关键词转移。
  - `adjustBudget(campaignId, amount)`: 预算调整。
  - `adjustBid(keywordId, newBid)`: 出价调整。
  - `setNegativeExact(campaignId, keyword)`: 设置否定词。
- [ ] **Step 2: 记录执行日志**
  - 在每个操作完成后，持久化记录到 `erplus_ads_automation_log`。

---

## Chunk 4: 前端策略配置增强 (UI Integration)

### Task 4: 更新创建向导配置项
**Files:**
- Modify: `hzapp-ui/hzapp-ui-admin-vue3-tiny/src/app/erplus/views/adv/automation/CreatePlanByTemplate.vue`

- [ ] **Step 1: 增加预算策略表单项**
  - 在“策略配置”部分增加 `roasIsolationThreshold`（独立预算 ROAS 阈值）。
- [ ] **Step 2: 增加出价约束预览**
  - 当选择商品和目标 ACOS 后，调用后端接口（待创建）实时预览预估的出价范围，指导用户配置。
