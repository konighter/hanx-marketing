# 跨平台广告管理系统架构与业务模型设计文档

## 1. 背景与目标

为了统一管理 Amazon Ads, Meta Ads (Facebook/Instagram), Google Ads, 和 TikTok Ads 的广告业务，我们需要设计一个兼容四大主流平台的广告系统。
该系统需要具备：

- 稳健的广告元数据和投放数据同步机制
- 整体对外暴露标准 HTTP API
- 提供对 AI 友好 (Tool / MCP 接口) 的能力，以支持大模型进行广告分析和自动化操作

---

## 2. 统一业务模型设计 (Unified Business Model)

尽管各大平台的广告层级和术语存在差异，但整体均可映射为以下四层标准模型：

### 2.1 核心层级映射 (Core Hierarchy)

1. **Ad Account (广告账户)**: 平台的授权和计费实体。所有操作的基础。
2. **Campaign (广告计划)**: 设定整体预算 (Budget)、广告目标 (Objective) 和时间范围。
3. **Ad Group / Ad Set (广告组)**: 设定受众定向 (Targeting)、出价策略 (Bidding策略)。
   - _搜索广告特有_: 对于 Amazon Ads 和 Google Ads，在此层级或其下挂载 Keywords (关键词) 和 Targets (投放目标)。
4. **Ad / Creative (广告实体/创意)**: 具体的图片、视频、文案和落地页 URL。

### 2.2 统一数据实体定义 (Data Entities)

- **AccountInfo**:
  `platform`, `account_id`, `account_name`, `currency`, `timezone`, `auth_status`
- **Campaign**:
  `campaign_id`, `account_id`, `platform_campaign_id`, `name`, `status`, `daily_budget`, `objective`
- **AdGroup**:
  `ad_group_id`, `campaign_id`, `platform_adgroup_id`, `name`, `status`, `bid_strategy`, `default_bid`
- **Ad**:
  `ad_id`, `ad_group_id`, `platform_ad_id`, `name`, `status`, `creative_assets`

### 2.3 绩效数据指标 (Performance Metrics)

整合跨平台的标准绩效指标：

- **基础指标**: `Impressions` (展现量), `Clicks` (点击量), `Cost/Spend` (花费)
- **转化指标**: `Conversions` (转化数), `Conversion_Value/Sales` (转化总额/销售额)
- **衍生指标**: `CTR` (点击率 = Clicks/Impressions), `CPC` (单次点击成本 = Cost/Clicks), `CPA` (单次转化成本 = Cost/Conversions), `ROAS` (广告支出回报率 = Sales/Cost)

---

## 3. 数据同步机制 (Sync Mechanism)

考虑到不同平台的 API 限制 (Rate Limits) 和报告生成机制的区别（如 Amazon 和 Google 的异步报表），需设计稳健的同步架构。

### 3.1 广告元数据同步 (Metadata Sync)

- **全量同步 (Full Sync)**: 每日或每周执行一次，拉取并更新所有的 Campaign, AdGroup, Ad 结构，确保本地数据库与平台完全一致。
- **增量同步 (Incremental Sync)**:
  - 优先使用 Webhook (若平台如 Meta 支持) 订阅状态和结构变更。
  - 对于不支持 Webhook 的平台，每隔数小时轮询最近更新的实体。

### 3.2 投放数据同步 (Performance Data Sync)

- **异步报表拉取架构**:
  1. **触发**: 定时任务 (Cron Task) 触发报表生成请求 (Request Report)。
  2. **排队**: 将 `ReportId` 或 `JobId` 压入消息队列 (Message Queue, 如 RabbitMQ 或 Redis Streams)。
  3. **轮询**: 消费者监听队列，定时轮询报表状态 (Status Check)。
  4. **下载与解析**: 状态变更为成功后，下载 CSV/JSON 文件并流式解析。
- **数据入库清洗**:
  - 数据解析后按照统一指标转化为标准化格式。
  - 数据按 `天 (Daily)` 和 `小时 (Hourly)` 粒度写入数据库。
  - 推荐后端采用关系型数据库存储核心结构，配合 OLAP 数据库 (如 ClickHouse) 存储海量报表数据以支持多维分析。

### 3.3 异常容错机制

- **重试机制 (Retry)**: 针对 HTTP 429 Too Many Requests 等临时性错误采用指数退避算法 (Exponential Backoff) 重试。
- **死信队列 (DLQ)**: 处理多次重试仍失败的任务，触发告警以便人工排查。

---

## 4. 接口层设计 (Interface Layer)

### 4.1 统一 HTTP API (RESTful)

供前端控制台、ERP 系统或内部其他微服务调用：

- `GET /api/v1/ads/campaigns`: 获取广告计划列表 (支持跨平台过滤查询)
- `POST /api/v1/ads/campaigns/status`: 开启/暂停广告计划
- `PUT /api/v1/ads/campaigns/budget`: 修改广告计划预算
- `GET /api/v1/ads/reports/daily`: 获取每日广告绩效汇总报表

### 4.2 AI 友好的 Tool / MCP 接口

为大语言模型 (Agents) 提供调用的接口，遵循简单的单职责原则，并返回丰富的上下文，便于 AI 进行分析和决策。

**规划的 Action/Tool 定义:**

1. `get_ad_performance_summary`
   - **参数**: `time_range` (如 "last_7_days"), `platform` (可选, 如 "amazon"), `account_id` (可选)
   - **描述**: 获取指定时间段内的总体花费、ROI 等关键表现数据。
2. `find_underperforming_ads`
   - **参数**: `roas_threshold` (如 1.5, ROAS 低于此时判定为表现不佳), `min_spend` (最低花费门槛)
   - **描述**: 查询花费较高但 ROAS 低于设定阈值的广告计划/组/实体。
3. `adjust_campaign_budget`
   - **参数**: `campaign_id`, `new_budget`
   - **描述**: 调整特定广告计划的预算金额。
4. `update_ad_status`
   - **参数**: `entity_id`, `entity_type` (campaign | adgroup | ad), `status` (PAUSED | ENABLED)
   - **描述**: 开启或暂停特定广告实体。
5. `analyze_platform_trends`
   - **描述**: 返回最近 30 天各平台的趋势数据（花费 vs 转化），便于 AI 生成市场分析图表或总结建议。

---

## 5. 后续细化与 Review 计划

完成本基础架构与模型设计后，后续将按模块进行细化设计和 Review：

1. 各平台 Adapter 的 SDK 选型与限流处理细节。
2. 数据库表结构 (Schema) 与 DDL 设计。
3. 同步任务调度引擎的具体技术栈选型 (如 Quartz, XXL-Job 或 Celery)。
4. MCP 接口的认证鉴权机制及操作安全性设计。
