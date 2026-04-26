# 亚马逊广告配置更新逻辑详细设计 (2026-04-26)

## 1. 目标
重构亚马逊广告配置（Campaign/AdGroup 详情页）的修改逻辑。从“全局保存”模式转变为“分区自治 + 原子操作”模式，以适应亚马逊 SP-API 独立接口的特性，提高操作效率和反馈速度。

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
    *   `基本信息` Tab：显示全局“保存”按钮（处理预算、名称、日期等核心字段）。
    *   `亚马逊配置` Tab：**不显示**底部“保存”按钮。
*   **一致性保证**：如果原子操作失败，前端数据需自动回滚为快照中的原始值，并弹出错误信息。

## 3. 详细设计 (前端 hzapp-ui)

### 3.1 列表批量操作实现
在 `AmazonCampaignConfig.vue` 及其子组件中：
*   **表格增强**：添加 `type="selection"` 列。
*   **操作条**：在表格上方实现 `BatchActionBar` 组件，仅在 `selectedRows.length > 0` 时显示。
*   **功能点**：
    *   `batchDelete`: 调用后端批量删除接口。
    *   `batchUpdateBid`: 弹出快捷修改框（支持：固定值、按金额增减、按比例增减）。

### 3.2 分区自治组件拆分
将 `AmazonCampaignConfig.vue` 拆分为更细粒度的子组件：
*   `CampaignBiddingSection.vue`: 处理竞价策略及位置调价，带局部“应用”按钮。
*   `NegativeTargetingManager.vue`: 统筹关键词和商品的否定投放，处理批量逻辑。

### 3.3 状态管理
*   **Snapshot**: 在进入详情页时保留一份 `attributes` 的原始快照。
*   **Dirty Checking**: 计算当前值与快照的差异，用于显示“未保存”标记或高亮“应用”按钮。

## 4. 后端 API 接口需求 (erplus-adv & erplus-api)

为支持前端的原子操作，后端需在 `AdsCampaignController` 和 `AdsAdGroupController` 中补充或优化以下接口：

### 4.1 广告活动级 (Campaign)
*   `PUT /campaign/update-bidding`: 更新竞价策略和位置调整。
*   `POST /campaign/negative-keyword/batch-create`: 批量新增否定词。
*   `DELETE /campaign/negative-keyword/batch-delete`: 批量删除。
*   `PUT /campaign/negative-keyword/update`: 更新单条（状态/匹配类型）。

### 4.2 广告组级 (AdGroup)
*   `PUT /ad-group/update-targeting-type`: 修改定向类型。
*   `POST /ad-group/targeting/batch-create`: 批量新增定向（关键词/商品）。
*   `PUT /ad-group/targeting/batch-update-bid`: 批量修改定向出价。
*   `DELETE /ad-group/targeting/batch-delete`: 批量删除定向。

## 5. 同步逻辑 (erplus-amz)
*   **实时性**: 后端接收到原子更新请求后，应立即调用 `AdsAmazonManageApi` 进行平台同步。
*   **容错**: 如果亚马逊返回错误（如 400 Bad Request），应将 SP-API 的原始错误消息透传回前端，以便用户针对性修正。

## 6. 验证计划
1.  **原子操作验证**: 切换开关或修改 Bid，检查网络请求是否立即发出，且只包含必要字段。
2.  **批量操作验证**: 勾选多条数据进行删除和改价，检查 SP-API 的批量接口调用是否正确。
3.  **冲突验证**: 模拟网络失败，验证前端数据是否正确回滚。
4.  **UI 引导验证**: 切换 Tab，检查底部保存按钮的显示/隐藏状态是否符合设计。
