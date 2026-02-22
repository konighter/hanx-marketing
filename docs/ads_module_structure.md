# 广告系统模块拆分设计

## 模块总览

`hzapp-module-erplus-adv` 内部按功能域划分为 4 个子模块，采用**统一平台适配器** (`adapter/`) 集中管理各平台的认证与业务 API 实现，方便后续引入新平台。

```
com.hzltd.module.erplus.adv
├── adapter/         ← 统一平台适配层 (认证 + 业务API)
├── auth/            ← 模块1: 广告主认证授权 (OAuth2)
├── metadata/        ← 模块2: 广告元数据同步和操作
├── report/          ← 模块3: 广告效果数据同步和展示
├── strategy/        ← 模块4: 广告运营策略
├── dal/             ← 持久化层 (DO + Mapper, 共享)
│   ├── dataobject/
│   └── mysql/
├── enums/           ← 枚举常量
└── framework/       ← 模块内框架配置
```

---

## 统一平台适配层 (adapter)

**核心职责**: 集中封装各平台的 OAuth2 认证和业务 API 调用，一个平台一个 Adapter 实现类，引入新平台只需新增一个 Adapter。

### 包结构

```
adapter/
├── AdsPlatformAdapter.java             ← 统一适配器接口 (认证 + 业务)
├── AdsPlatformAdapterFactory.java      ← 适配器工厂 (按平台枚举获取)
├── model/
│   └── AdsTokenResult.java             ← Token 交换/刷新结果模型
├── amazon/
│   └── AmazonAdsAdapter.java           ← Amazon 实现 (LWA OAuth + Ads API)
├── meta/
│   └── MetaAdsAdapter.java             ← Meta 实现 (Facebook OAuth + Marketing API)
├── google/
│   └── GoogleAdsAdapter.java           ← Google 实现 (Google OAuth + Ads API)
└── tiktok/
    └── TikTokAdsAdapter.java           ← TikTok 实现 (TikTok OAuth + Ads API)
```

### 接口定义

```java
public interface AdsPlatformAdapter {
    AdsPlatformEnum getPlatform();

    // === 认证相关 ===
    String getAuthorizeUrl(String state);
    AdsTokenResult exchangeToken(String code);
    AdsTokenResult refreshToken(AdsAccountCredentialDO credential);

    // === 元数据同步 ===
    void fetchCampaigns(Long accountId, AdsAccountCredentialDO credential);

    // === 远程操作 ===
    Boolean updateStatus(String externalId, String status, AdsAccountCredentialDO credential);
}
```

---

## 模块1: 广告主认证授权 (auth)

**核心职责**: 管理各平台的 OAuth2 授权流程，Token 的获取、刷新和安全存储。通过 `AdsPlatformAdapterFactory` 获取对应平台的 Adapter 执行实际授权操作。

### 包结构

```
auth/
├── controller/
│   └── AdsAuthController.java          ← 授权回调、Token 管理 API
├── service/
│   ├── AdsAuthService.java             ← 授权服务接口
│   └── AdsAuthServiceImpl.java         ← 授权服务实现
└── vo/
    ├── AdsAuthCallbackReqVO.java       ← 授权回调请求
    ├── AdsAuthUrlReqVO.java            ← 生成授权链接请求
    └── AdsAccountRespVO.java           ← 账户信息响应
```

### 涉及的表

- `ads_account` — 账户注册与状态管理
- `ads_account_credential` — Token 安全存储与刷新
- `ads_sync_task` (task_type=TOKEN_REFRESH) — Token 刷新任务

### 核心 API

| HTTP 方法 | 路径                      | 描述                                          |
| :-------- | :------------------------ | :-------------------------------------------- |
| GET       | `/adv/auth/authorize-url` | 调用 Adapter 生成 OAuth2 授权链接             |
| GET       | `/adv/auth/callback`      | OAuth2 授权回调处理 (调用 Adapter 换取 Token) |
| POST      | `/adv/auth/refresh-token` | 手动刷新 Token (调用 Adapter 刷新)            |
| GET       | `/adv/account/list`       | 获取已授权账户列表                            |
| DELETE    | `/adv/account/{id}`       | 取消授权/删除账户                             |

---

## 模块2: 广告元数据同步和操作 (metadata)

**核心职责**: 从各平台同步广告结构 (Campaign→AdGroup→Ad→Keyword)，并提供本地 CRUD 操作和反向推送到平台。通过 `AdsPlatformAdapterFactory` 获取 Adapter 执行平台通信。

### 包结构

```
metadata/
├── controller/
│   ├── AdsCampaignController.java      ← Campaign CRUD API
│   ├── AdsAdGroupController.java       ← AdGroup CRUD API
│   ├── AdsAdController.java            ← Ad CRUD API
│   └── AdsKeywordController.java       ← Keyword CRUD API
├── service/
│   ├── AdsCampaignService.java         ← 计划服务接口
│   ├── AdsCampaignServiceImpl.java
│   ├── AdsAdGroupService.java          ← 广告组服务接口
│   ├── AdsAdGroupServiceImpl.java
│   ├── AdsAdService.java               ← 广告服务接口
│   ├── AdsAdServiceImpl.java
│   ├── AdsKeywordService.java          ← 关键词服务接口
│   └── AdsKeywordServiceImpl.java
├── sync/
│   ├── AdsMetadataSyncService.java     ← 元数据同步调度接口
│   └── AdsMetadataSyncServiceImpl.java ← 同步实现 (调用 Adapter)
└── vo/
    ├── campaign/                       ← Campaign 相关 VO
    ├── adgroup/                        ← AdGroup 相关 VO
    ├── ad/                             ← Ad 相关 VO
    └── keyword/                        ← Keyword 相关 VO
```

### 涉及的表

- `ads_campaign` / `ads_ad_group` / `ads_ad` / `ads_keyword` — 四级元数据
- `ads_creative_asset` — 创意素材
- `ads_entity_change_log` — 变更审计日志
- `ads_sync_task` (task_type=METADATA_FULL/INCR) — 同步任务状态

### 核心 API

| HTTP 方法 | 路径                   | 描述                     |
| :-------- | :--------------------- | :----------------------- |
| GET       | `/adv/campaign/page`   | 分页查询广告计划         |
| PUT       | `/adv/campaign/status` | 修改计划状态 (启用/暂停) |
| PUT       | `/adv/campaign/budget` | 修改计划预算             |
| GET       | `/adv/adgroup/list`    | 查询广告组列表           |
| PUT       | `/adv/adgroup/bid`     | 修改广告组出价           |
| POST      | `/adv/sync/metadata`   | 触发元数据同步           |
| GET       | `/adv/changelog/list`  | 查询变更日志             |

---

## 模块3: 广告效果数据同步和展示 (report)

**核心职责**: 定时拉取各平台的绩效报表数据，提供聚合查询和趋势分析。

### 包结构

```
report/
├── controller/
│   └── AdsReportController.java        ← 报表查询 API
├── service/
│   ├── AdsReportService.java           ← 报表服务接口
│   └── AdsReportServiceImpl.java
├── sync/
│   ├── AdsReportSyncService.java       ← 报表同步服务
│   └── AdsReportSyncServiceImpl.java
└── vo/
    ├── AdsReportQueryReqVO.java        ← 报表查询请求
    └── AdsReportSummaryRespVO.java     ← 汇总响应
```

### 涉及的表

- `ads_report_daily` — 每日绩效报表
- `ads_sync_task` (task_type=REPORT_DAILY) — 报表同步任务

### 核心 API

| HTTP 方法 | 路径                  | 描述                 |
| :-------- | :-------------------- | :------------------- |
| GET       | `/adv/report/summary` | 获取指定实体绩效汇总 |
| GET       | `/adv/report/trend`   | 获取时间趋势数据     |
| POST      | `/adv/report/sync`    | 手动触发报表同步     |

---

## 模块4: 广告运营策略 (strategy)

**核心职责**: 基于绩效数据，提供自动化运营建议和批量操作能力（AI Agent / MCP 集成点）。

### 包结构

```
strategy/
├── controller/
│   └── AdsStrategyController.java      ← 策略执行 API / MCP 接口
├── service/
│   ├── AdsStrategyService.java         ← 策略服务接口
│   └── AdsStrategyServiceImpl.java
├── rule/
│   ├── AdsAutoRule.java                ← 自动化规则定义
│   └── AdsAutoRuleEngine.java          ← 规则引擎
└── vo/
    ├── AdsStrategyReqVO.java
    └── AdsStrategyResultVO.java
```

### MCP Tool 定义

| Tool 名称                    | 描述               |
| :--------------------------- | :----------------- |
| `get_ad_performance_summary` | 获取绩效概览       |
| `find_underperforming_ads`   | 查找表现不佳的广告 |
| `adjust_campaign_budget`     | 调整预算           |
| `update_ad_status`           | 修改状态           |
| `analyze_platform_trends`    | 趋势分析           |

---

## 开发优先级

| 阶段    | 模块                      | 说明                                                 |
| :------ | :------------------------ | :--------------------------------------------------- |
| Phase 1 | adapter + auth + metadata | 先完成统一适配层、认证授权和元数据同步，打通核心链路 |
| Phase 2 | report                    | 接入绩效数据，丰富数据展示                           |
| Phase 3 | strategy                  | 引入 AI 策略优化和 MCP 接口                          |

## 前端模块设计

前端代码位于 `hzapp-ui-admin-vue3/src/app/erplus/views/adv`，采用 Vue 3 + Element Plus 构建。

### 目录结构与功能说明

```text
adv/
├── auth/                       # 模块 1: 广告授权管理
│   └── index.vue               # 授权账号列表页 (支持授权跳转、Token 状态监控)
├── components/                 # 公用业务组件
│   ├── AdCampaignList.vue      # 广告计划列表 (支持级联筛选)
│   ├── AdGroupList.vue         # 广告组列表
│   ├── AdList.vue              # 广告列表
│   ├── AdKeywordList.vue       # [NEW] 关键词列表
│   ├── AdDataChart.vue         # 数据可视化组件 (ECharts)
│   └── ColumnCustomizer.vue    # 列定制动态配置组件
├── composables/                # 组合式函数
│   └── useAdsColumns.ts        # 统一管理各层级的动态列定义
├── types/                      # 类型定义
│   └── ads.d.ts                # 广告系统专属 TS 类型定义
└── index.vue                   # 模块 2: 广告管理入口 (Tab 页签组织跨级联查)
```

### 核心页面功能清单

| 页面/组件            | 核心功能                                                              | 对应后端接口       |
| :------------------- | :-------------------------------------------------------------------- | :----------------- |
| **Ads Auth 页**      | 展示已授权平台账号；点击“新增授权”跳转 OAuth；解绑账号。              | `/adv/auth/**`     |
| **Ads Manager 主页** | 全局店铺筛选；触发全量/增量同步；Tab 切换 Campaign/Group/Ad/Keyword。 | `/adv/sync/**`     |
| **Campaign 列表**    | 展示计划状态、预算；支持开启/暂停操作；导出 Excel。                   | `/adv/campaign/**` |
| **AdGroup 列表**     | 根据选中的 Campaign 过滤展示；展示出价信息。                          | `/adv/ad-group/**` |
| **Keyword 列表**     | 展示匹配类型、实时出价；支持批量关键词状态更新。                      | `/adv/keyword/**`  |

### 接口适配层 (API)

前端通过统一的 `src/app/erplus/api/adv/ads.ts` 与后端通信，封装 `AdsPlatformAdapter` 暴露的通用能力。

---

## 开发计划 (Phase 2: Frontend)

1. **API 定义**: 创建 `ads.ts` 替换原有的 `AmzAdvCampaignApi`。
2. **授权页面**: 实现 `auth/index.vue`。
3. **管理页面迁移**: 修改 `index.vue` 适配通用分页接口。
4. **Keyword 组件**: 新建 `AdKeywordList.vue`。

```

```
