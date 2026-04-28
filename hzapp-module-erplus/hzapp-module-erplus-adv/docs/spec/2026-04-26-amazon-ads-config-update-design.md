# 亚马逊广告配置更新逻辑详细设计 (2026-04-26)

## 1. 目标
重构亚马逊广告配置（Campaign/AdGroup 详情页）的修改逻辑。从“全局保存”模式转变为“分区自治 + 原子操作”模式，以适应亚马逊 SP-API 独立接口的特性，提高操作效率和反馈速度。同时解决列表类属性的局部更新与 ID 回填问题，确保本地数据与平台数据的一致性。

## 2. 交互设计规约

### 2.1 修改模式分类
我们将配置项分为三类，采用不同的更新策略：

| 类别 | 包含项 | 更新触发时机 | 交互反馈 |
| :--- | :--- | :--- | :--- |
| **原子字段** | 状态开关 (Status)、单行出价 (Bid) | 操作即生效 (Switch 点击 / Input 失焦) | Loading 动画 + 成功/失败 Toast |
| **复合配置区** | 竞价策略 (Dynamic Bidding)、亚马逊优化规则 | 点击区域内的“应用 (Apply)”按钮 | 变更感知（未保存提示）+ 区域局部 Loading |
| **列表管理** | 否定关键词、否定定向、商品定向 | 弹窗确认 (新增) / 按钮点击 (删除/批量) | 进度条或全局 Loading |

### 2.2 抽屉容器逻辑
*   **底部按钮动态显示**：
    *   `基本信息` Tab：显示全局“保存”按钮。
    *   `亚马逊配置` Tab：**不显示**底部“保存”按钮。
*   **一致性保证**：如果操作失败，前端数据需自动回滚为快照中的原始值，并弹出错误信息。

## 3. 详细设计 (前端 hzapp-ui)

### 3.1 列表批量操作实现
在 `AmazonCampaignConfig.vue` 及其子组件中：
*   **表格增强**：添加 `type="selection"` 列。
*   **操作条**：在表格上方实现 `BatchActionBar` 组件，仅在 `selectedRows.length > 0` 时显示。
*   **属性刷新机制 (New)**：由于后端需要时间与亚马逊同步，当收到批量操作成功的响应后，前端主动调用 `adv` 元数据的属性查询接口（见 4.2），刷新最新的属性配置数据。

### 3.2 分区自治组件拆分
*   `CampaignBiddingSection.vue`: 处理竞价策略及位置调价。
*   `NegativeTargetingManager.vue`: 处理否定投放和批量逻辑。

## 4. 后端 API 接口需求

### 4.1 亚马逊平台特有管理接口 (erplus-amz)
为支持原子操作，新增亚马逊特有接口。
> **⚠️ 关键避坑指南**：新增的 Controller 必须注入并使用基于 SP-API v3 的 `AdsAmazonManageApi`，**切勿**误用同目录下的 v2 服务（`AmzAdvOperationApiAdsService`），建议接口路径加上 `v3` 标识以示区分。

目录：`com.hzltd.module.amz.adv.controller.admin.manager`
*   **Campaign Manager**
    *   `PUT /erplus/amz/adv/v3/campaign/manager/update-bidding`
    *   `POST /erplus/amz/adv/v3/campaign/manager/negative-keyword/batch-create`
    *   `DELETE /erplus/amz/adv/v3/campaign/manager/negative-keyword/batch-delete`
    *   `PUT /erplus/amz/adv/v3/campaign/manager/negative-keyword/update`
*   **AdGroup Manager**
    *   `PUT /erplus/amz/adv/v3/ad-group/manager/update-targeting-type`
    *   `POST /erplus/amz/adv/v3/ad-group/manager/targeting/batch-create`
    *   `PUT /erplus/amz/adv/v3/ad-group/manager/targeting/batch-update-bid`
    *   `DELETE /erplus/amz/adv/v3/ad-group/manager/targeting/batch-delete`

### 4.2 本地元数据属性查询接口 (erplus-adv)
为了支持前端在局部修改完成后能够单独刷新实体属性（获取包含最新平台 ID 的数据），在 `adv` 模块新增属性专用查询接口：
*   **Campaign Attributes**
    *   `GET /erplus/adv/campaign/get-attributes` (参数: `id`)：返回该广告活动对应的 attributes Map。
*   **AdGroup Attributes**
    *   `GET /erplus/adv/ad-group/get-attributes` (参数: `id`)：返回该广告组对应的 attributes Map。

## 5. 属性修改核心流程 (后端逻辑 - 闭环设计)

为了解决列表类属性的**增量覆盖问题**和**新实体 ID 回填问题**，后端在处理亚马逊特有属性更新时，必须遵循以下“三步走”的闭环流程：

1.  **平台指令下发 (Delta Update)**
    *   接收前端的“增量”修改请求（如仅新增 2 个否定词）。
    *   调用 `AdsAmazonManageApi` 中对应的接口，向 Amazon 发起修改请求。
2.  **本地属性覆写与数据补全 ([TODO])**
    *   **[TODO]** 目前对于数组类属性的修改，直接增量保存会引发覆盖和缺少 `keywordId` 的问题。为了解决 ID 回填和全量覆写问题，规划了以下两种解决路径：
    *   **方案 1（首选 - 解析返回）**：亚马逊的修改接口支持返回全量数据。后续将在调用成功后，直接解析亚马逊接口返回的全量数据（包含最新生成的平台 ID）。
    *   **方案 2（备选 - 局部反查）**：若平台接口未返回完整数据，后端主动触发一次平台查询 API。**注意：查询范围必须严格控制在“单个被修改的属性”**（例如仅拉取 `negativeKeywords`），避免拉取整个 Campaign 或 AdGroup 的全量属性，以优化性能。
    *   **执行本地覆盖**：无论采用哪种方案，拿到最新的完整列表数据后，调用 `adv` 模块中对应 Meta 实体的修改方法（如 `AdsAdGroupService.saveOrUpdateAdGroupAttributes`），使用局部更新（`replace=false`）将完整的列表覆写到本地 DB。
    *   *当前阶段*：前端通过调用属性刷新接口（4.2 节）或依赖定时同步任务来确保数据的最终一致性。

> **容错说明 (Eventual Consistency)**：如果第 1 步成功但后续步骤失败，导致本地 DB 属性未及时更新，系统将依赖后端的 `AdsMetadataSyncService` 定时全量/增量同步任务来进行数据兜底修复，确保最终一致性。

## 6. 验证计划
1.  **增量操作与 ID 回填验证**: 批量新增否定词后，触发前端刷新，验证其是否已包含正确的平台 `keywordId`。
2.  **覆写保护验证**: 修改否定词后，检查该实体的其他属性（如竞价策略）是否完好无损未被清空。
3.  **接口隔离验证**: 确保新开发的功能接口路由到了 v3 SP-API，而未影响旧有的 v2 业务。
4.  **UI 状态一致性验证**: 修改某项属性保存并自动触发刷新接口后，前端页面展示的数据应为最新的平台返回结果。
