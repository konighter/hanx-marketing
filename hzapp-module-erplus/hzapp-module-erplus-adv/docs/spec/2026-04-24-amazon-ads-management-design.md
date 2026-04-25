# 亚马逊广告管理详细设计方案 (2026-04-24)

## 1. 目标
实现亚马逊广告活动（Campaign）、广告组（AdGroup）、关键词（Keyword）的创建、修改、调价及关键词管理功能。重点解决前端配置页面与后端 `ads_campaign_attribute` 存储结构不匹配的问题。

## 2. 核心规约

### 2.1 数据隔离与命名称规范
*   **字段隔离**: 明确区分实体主表的 `extData` 字段与 `ads_campaign_attribute` 存储的 `attributes` 数据。
    *   `extData`: 存储在主表（如 `ads_campaign`）的 JSON 列中，用于存放实体自身的扩展元数据。
    *   `attributes`: 存储在属性表（如 `ads_campaign_attribute`）中，用于存放平台特有的业务配置。
*   **键名规范**: 统一使用 `amz_` 前缀，前后端及数据库不进行 Key 的自动转换。
    *   示例: `amz_dynamic_bidding`, `amz_negative_keywords`。

### 2.2 数据流转路径
*   **读取 (Read)**: 
    1. 调用 `AdsCampaignService.getCampaign(id)` 获取实体。
    2. 查询 `ads_campaign_attribute` 获取该实体的所有属性。
    3. 将属性合并到 VO 的 `attributes` 字段（Map 结构）返回。
*   **写入 (Write)**:
    1. 后端接收 `attributes` 字段。
    2. 将 `attributes` 中以 `amz_` 开头的项持久化到对应的 `_attribute` 表。
    3. 将 `attributes` 传递给平台 API（`AdsAmazonManageApi`）进行平台同步。

### 2.3 Amazon 属性 Key 定义清单 (必须严格一致)
为确保前后端通信不出现偏差，统一使用后端 `AdsCampaignMangerApi` 中定义的常量键名：

| 实体 | 属性 Key (Database & Frontend) | 说明 |
| :--- | :--- | :--- |
| **Campaign** | `amz_dynamic_bidding` | 竞价策略（含位置调整百分比） |
| **Campaign** | `amz_negative_keywords` | 广告活动级否定关键词列表 |
| **Campaign** | `amz_negative_target` | 广告活动级否定定向（ASIN/类目） |
| **Campaign** | `amz_optimization_rules` | 亚马逊优化规则配置 |
| **AdGroup** | `amz_targeting_type` | 投放定向方式（KEYWORD/PRODUCT） |
| **AdGroup** | `amz_keyword_targetings` | 关键词定向列表 |
| **AdGroup** | `amz_product_targetings` | 商品/品类定向列表 |
| **AdGroup** | `amz_ad_group_negative_keywords` | 广告组级否定关键词 |
| **AdGroup** | `amz_ad_group_negative_target` | 广告组级否定定向 |
| **AdGroup** | `amz_ad_group_config` | 广告组其他扩展配置 |

## 3. 详细设计 (第一阶段：查询链路对齐)

### 3.1 后端修改 (erplus-adv & erplus-api)
*   **VO 更新**:
    *   在广告计划详情返回 VO（如 `AdsCampaignRespVO`）中增加 `Map<String, Object> attributes` 字段。
*   **Service 逻辑 (Read)**:
    *   `AdsCampaignServiceImpl.getCampaign(id)`: 
        1. 调用现有的 `getCampaignAttributes(id)` 获取该计划的所有平台属性。
        2. 将返回的 `amz_` 开头的属性 Map 注入到 VO 的 `attributes` 字段中返回。

### 3.2 亚马逊数据映射 (erplus-amz)
*   **同步 Hook**: 确保 `AdsCampaignMangerApi` 在同步数据时，正确将 SP-API 返回的 `dynamicBidding` 等字段以 `amz_` 前缀存入属性表（此部分现有逻辑已基本覆盖）。

### 3.3 前端修改 (hzapp-ui)
*   **数据接入**:
    *   `AdCampaignDetailDrawer.vue`: 
        1. 仅将后端 `attributes` 字段赋值给子组件，**禁止** 手动合并 `shopId`、`accountId` 等无关字段。
        2. 若子组件需要这些上下文信息，通过独立的 Props 显式传递。
*   **组件展示与清理**:
    *   `AmazonCampaignConfig.vue`: 
        1. 移除内部对 `shopId`、`accountId` 的混合逻辑。
        2. 全面更新模板绑定，直接使用 `amz_dynamic_bidding` 等标准键名展示数据。
        3. **清理保存逻辑**: 移除 `handleSave` 中对 `shopId`/`accountId`/`adGroups` 的手动解构清洗代码，保持 `attributes` 的纯净。

## 4. 详细设计 (第二阶段：修改逻辑 - 待讨论)
*注：此部分待查询链路验证完成后，再进一步讨论增量更新与 SP-API 实时同步的细节。*

## 4. 验证计划
*   **同步测试**: 手动触发同步，检查 `ads_campaign_attribute` 表中是否正确存入 `amz_` 前缀的键。
*   **更新测试**: 修改竞价策略或否定关键词，观察数据库属性表是否更新，并验证 SP-API 请求日志是否包含正确参数。
*   **调价测试**: 在列表页快速修改预算/出价，确保持久化到属性表的同时实时同步至亚马逊。
