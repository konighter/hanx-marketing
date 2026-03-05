# 亚马逊广告活动与广告组 API 接口能力及字段对齐调研表

本调研报告主要梳理了亚马逊基于 Sponsored Products (SP) v3 API 中「广告活动 (Campaign)」和「广告组 (Ad Group)」维度的核心配置能力，并详细拆解了这些配置与当前本地业务表 (`ads_campaign`、`ads_ad_group` 等) 以及扩展 JSON 数据结构 (`ext_data.platformConfig`) 的映射与对齐方案。

---

## 1. 广告活动 (Campaign) 维度配置能力

广告活动维度的主要配置包含：**基本设置**、**预算控制**、**竞价策略**、**位置出价调整** 和 **广告活动级别的否定关键词**。

### 1.1 核心配置与 API 映射

| 配置项大类 | 具体配置内容 | Amazon SP API v3 端点及核心对象 | 本地数据库结构对齐方案 | 备注 |
|---|---|---|---|---|
| **基本设置** | 计划名称、起止时间 | `POST/PUT /sp/campaigns` <br> `name`, `startDate`, `endDate` | `ads_campaign` 表：`name`, `start_time`, `end_time` | 完全对齐 |
| **状态控制** | 计划状态 (开启/暂停/归档) | `POST/PUT /sp/campaigns` <br> `state` (ENABLED, PAUSED, ARCHIVED) | `ads_campaign` 表：`status` <br> (`0`:PAUSED, `1`:ENABLED, 等) | 在 Adapter 层做 Enum 转换 |
| **定向类型** | 手动定向/自动定向 | `POST/PUT /sp/campaigns` <br> `targetingType` (MANUAL, AUTO) | `ads_campaign` 表：`targeting_type` | 强关联字段 |
| **预算控制** | 每日预算金额与类型 | `POST/PUT /sp/campaigns` <br> `budget` (budgetType, budget) | `ads_campaign` 表：`daily_budget` | V3 目前主推 DAILY 预算类型 |
| **竞价策略** | 动态竞价策略 (只降、升降)、固定竞价、基于规则竞价 | `POST/PUT /sp/campaigns` <br> `dynamicBidding.strategy` | `ads_campaign` 表：`ext_data.platformConfig.dynamicBidding.strategy` | SP v3 提供 4 种策略选项 |
| **位置出价** | 顶部首行 (Top of Search)、商品详情页出价调整百分比 | `POST/PUT /sp/campaigns` <br> `dynamicBidding.placementBidding` | `ads_campaign` 表：`ext_data.platformConfig.dynamicBidding.adjustments` 列表 | 最高可增加 900% |
| **优化规则 (通用)** | Optimization Rules: 结构更通用，包含完整的 condition、action (如 ADOPT) 引擎管理逻辑 | `POST/PUT /sp/optimizationRules` (或相关 v1 endpoints) | `ads_campaign` 表：`ext_data.platformConfig.optimizationRules` 列表 | 通用规则管理API (**本次优先实现**) |
| **优化规则 (SP专有)** | Campaign Optimization Rules: 基于 Sponsored Products 专属能力的微调 | `POST/PUT /sp/campaignOptimizationRules` | `ads_campaign` 表：`ext_data.platformConfig.campaignOptimizationRules` 列表 | SP 独有 (扩展字段待定) |
| **否定词** | 广告活动级 - 否定关键词 (Exact / Phrase) | `POST/PUT /sp/campaignNegativeKeywords` <br> `keywordText`, `matchType` | `ads_campaign` 表：`ext_data.platformConfig.negativeKeywords` 列表 | 需调用独立 API 管理 |

> **知识补充：Campaign Optimization Rules vs Optimization Rules**
> 1. **Campaign Optimization Rules (SP)*: 专指基于 Sponsored Products (SP) 的特定能力。支持在指定计划上设定 Target ROAS 等直接出价微调与预算分配规则，由 SP 引擎进行自动竞价与选词辅助。
> 2. **Optimization Rules (通用/SB Beta)*: 这是一个更泛化的性能目标框架，主要在 Sponsored Brands (SB) 提供 Beta API，允许预设“Cost per Click”目标等成本限制，模型会自动增减投放。本质上“Campaign Optimization Rules”是 SP 链路中对这一泛化理念的专属、细化实现。

> **对齐总结 (Campaign)**：基本信息以结构化列的形式存放在 `ads_campaign`；涉及亚马逊特有的竞价调整与否定对象集合，被统一封装入 `AmazonCampaignConfigVO`，并序列化存至表级的 `ext_data` 下的 `platformConfig` 键名中，方便查询回显与 JSON 全量覆盖更新。

---

## 2. 广告组 (Ad Group) 维度配置能力

广告组维度的配置更加细化，除了包含基础的**默认出价**等基本属性，主要能力集中在具体的**定向设置**（关键词、商品、品类）以及**广告组级别的否定操作**。

### 2.1 核心配置与 API 映射

| 配置项大类 | 具体配置内容 | Amazon SP API v3 端点及核心对象 | 本地数据库结构对齐方案 | 备注 |
|---|---|---|---|---|
| **基本设置** | 广告组名称、状态 | `POST/PUT /sp/adGroups` <br> `name`, `state` | `ads_ad_group` 表：`name`, `status` | 完全对齐 |
| **基础出价** | 广告组的默认竞价金额 | `POST/PUT /sp/adGroups` <br> `defaultBid` | `ads_ad_group` 表：`default_bid` (DECIMAL) | 当未设置具体关键词出价时的默认兜底 |
| **关键词定向** | 具体的投放关键词、匹配方式 (Exact / Phrase / Broad)、单独出价 | `POST/PUT /sp/keywords` <br> `keywordText`, `matchType`, `bid` | `ads_ad_group` 表：`ext_data.platformConfig.keywordTargetings` | 对于大规模词库可能拆分独立表，目前采用 JSON 拓展配置直观聚合展示 |
| **商品与品类定向** | 指定 ASIN 定向、特定商品类目定向 | `POST/PUT /sp/targets` <br> 提供不同 Target 表达式及 `bid` | `ads_ad_group` 表：`ext_data.platformConfig.productTargetings` / `categoryTargetings` | API 中两者均通过 targets 端点使用不同 expression 表示 |
| **否定关键词** | 广告组内 - 排除特定搜索词 | `POST/PUT /sp/negativeKeywords` <br> `keywordText`, `matchType` | `ads_ad_group` 表：`ext_data.platformConfig.negativeKeywords` | 广告组的否定优先级高于活动级 |
| **否定商品定向** | 排除特定的竞品 ASIN 或品牌 | `POST/PUT /sp/negativeTargets` <br> 否定表达式 `asin` / `brandId` | `ads_ad_group` 表：`ext_data.platformConfig.negativeTargetings` | 仅用于商品/类目定向类广告组，用于拉黑不转化商品或自身品牌保护 |

> **对齐总结 (Ad Group)**：受限于广告组内部可能存在大量灵活多变的目标 (Targets)，当前本地通过 `AmazonAdGroupConfigVO` 按功能列表拆分为 `keywordTargetings`、`productTargetings`、`categoryTargetings` 和 `negativeKeywords` 并聚合并落库在 `ext_data.platformConfig`。这种方式适合管理后台页面的“统一详情表单”编辑和覆盖式更新模式；待后续业务深水区，如果单个广告组内关键词数以千计产生 JSON 超长截断风险时，需将其平铺到对应的独立数据表进行存储。

---

## 3. 实现与对接注意点

1. **统一扩展层抽象 (`platformConfig`)**
   目前对所有实体（如 Campaign、AdGroup、ProductAd 等），凡涉及特定广告网络（如 Amazon）才能识别或专有的复杂策略字段，均统一包装至各实体表的 JSON 列 `ext_data.platformConfig` 内。

2. **状态更新钩子的粒度**
   鉴于管理端操作可能会高频次变更状态（比如只做启停：ENABLED <-> PAUSED），对于单一的状态变更应采用快速更新请求，而对于携带了完整 `platformConfig` 覆盖行为的操作，推荐在后置 Hook（如 `postCampaignUpdate` / `postAdGroupUpdate`）中通过组装全量/增量 SP API 模型结构完成下发执行。

---

## 附录：Optimization Rules 详解与数据示例

### 典型应用场景：分时竞价调整 (Schedule-based bidding)

在实际广告投放中，广告主经常遇到特定时段（如早晨 `05:00` 到 `07:00`）是目标客户购买高峰期的情况。为了不错过这些高转化流量，他们希望单独在这个时间段将竞价提高一定比例。

**Optimization Rules** 能够解决这个场景：  
你可以创建一个名为 “晨间自动提高出价 15%” (`increase_bids_by_15%_on_mornings`) 的**分段竞价 (SCHEDULE)** 规则绑定在这个 Campaign 上。该规则会指挥亚马逊系统在规定时段内自动追加 15% 的曝光出价权重，并且支持设置整体生效的生命周期。

### 模拟配置结构 (`optimizationRules`)
在我们的本地数据库中，这个通用规则结构将序列化保存在 `ads_campaign` 表的 `ext_data.platformConfig.optimizationRules` 数组内。
注：如果是特化版的 *Campaign* Optimization Rules，将放在 `campaignOptimizationRules` 数组内，结构待定。

以下是 Optimization Rules 完整配置的模拟 JSON 数据：

```json
[
  {
    "action": {
      "actionDetails": {
        "actionOperator": "INCREMENT",
        "actionUnit": "PERCENT",
        "value": "15"
      },
      "actionType": "ADOPT"
    },
    "recurrence": {
      "duration": {
        "startTime": "2022-11-01T00:00:00Z"
      },
      "timesOfDay": [
        {
          "endTime": "07:00",
          "startTime": "05:00"
        }
      ],
      "type": "DAILY"
    },
    "ruleCategory": "BID",
    "ruleName": "increase_bids_by_15%_on_mornings",
    "ruleSubCategory": "SCHEDULE",
    "status": "ENABLED"
  }
]
```

**字段解析 (基于官方及用户的最新 API Schema)：**
- **`ruleCategory` 与 `ruleSubCategory`**: 标明规则的主分类与子分类。在这个例子中是 `BID` 的 `SCHEDULE` (分时竞价) 规则。
- **`ruleName`**: 规则名称（如 `increase_bids_by_15%_on_mornings`）。
- **`status`**: 规则状态（如 `ENABLED`）。
- **`action`**: 执行动作定义。
  - `actionType`: `ADOPT` (采纳该调整)。
  - `actionDetails`: 定义如何调整，如操作符 `INCREMENT` (增加)，单位 `PERCENT` (百分比)，值 `15` (15%)。
- **`recurrence`**: 每周期的生效时间范围说明。
  - `type`: `DAILY`（代表该时段是按天重复）。
  - `duration.startTime`: 整个规则在哪个自然日期开始生效。
  - `timesOfDay`: 每天内具体生效的小时/分钟时段数组（如 `05:00` 到 `07:00`）。

> **对接注意**：
> 真实通过 `POST /sp/campaignOptimizationRules` 创建规则时，根节点还需要传入 `campaignIds`（最多 20 个）。由于我们本地数据是挂载在具体的某个 `ads_campaign` 数据行下，所以在通过 `AmazonAdsAdapter` 组装并发送请求时，应当直接将当前广告活动的 ID 放进 `campaignIds` 列表里发送。
