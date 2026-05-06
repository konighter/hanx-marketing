一、核心思路（先把逻辑钉死）
你不是在“调广告”，而是在做一套约束优化：
- 目标：CPA ≤ 目标值
- 可控变量：CPC / 预算 / 关键词集合 / 匹配方式
- 反馈信号：点击、订单、CVR、花费
  关键换算（全流程都围绕它）：
  目标CPC = 目标CPA × CVR
  例：目标CPA $6，CVR 10% → 目标CPC ≈ $0.6
  这就是你所有出价的“天花板”

---
二、整体系统架构（3层自动化）
Layer 1：采集层（Data Ingestion）
定时（每天/每12小时）拉取：
- Search Term Report（搜索词级）
- Campaign / AdGroup / Keyword报表
- 维度：
    - Impressions
    - Clicks
    - Spend
    - Orders
    - Sales

---
Layer 2：决策层（Decision Engine）
对每个“搜索词/关键词”打标签：
1）计算指标
- CVR = Orders / Clicks
- CPA = Spend / Orders
- CPC = Spend / Clicks

---
2）分类（核心规则）
A. 优质词（可放量）
- Orders ≥ 2
- CPA ≤ 目标CPA
  👉 标记：WIN

---
B. 潜力词（观察）
- Clicks ≥ 10
- Orders = 0 或 1
  👉 标记：TEST

---
C. 亏损词（要处理）
- Clicks ≥ 阈值（如15）
- Orders = 0
  或 CPA > 目标CPA × 1.3
  👉 标记：KILL

---
Layer 3：执行层（Action Engine）
这是自动化的核心👇

---
三、自动创建广告（从Auto到Manual）
触发条件（只有WIN才转）
当一个词：
- Orders ≥ 2
- CPA ≤ 目标CPA

---
自动创建：
Campaign结构：
- Campaign：Manual
- AdGroup：按词或按主题分组

---
创建3类关键词：
1）Exact（核心）
- 出价 = 目标CPC × 1.0
  2）Phrase（扩量）
- 出价 = 目标CPC × 0.8
  3）（可选）Broad
- 出价 = 目标CPC × 0.5

---
同步动作（非常关键）
👉 在自动广告中：
- 否定该词（Exact 或 Phrase）
  目的：
  把流量控制权从Auto迁移到Manual

---
四、自动调价逻辑（每天执行）
对所有关键词执行：

---
1）WIN词（赚钱）
- CPA ≤ 目标CPA
  → 出价 +10%
  → 或预算 +20%
  （放量但有节奏）

---
2）TEST词（不确定）
- 点击 ≥10 无单
  → 出价 -15%
- 有1单但不稳定
  → 保持不动 or 小幅 -10%

---
3）KILL词（亏损）
- 点击 ≥15 无单
  → 直接暂停 或 否定
- CPA过高
  → 出价 -30%

---
五、自动广告控制（防止你说的“狂推”）
Auto Campaign策略：
出价约束：
- 不允许超过：目标CPC × 0.7

---
动态竞价：
- 必须：Down Only

---
预算控制：
- 每个Auto Campaign：
    - 固定上限（如 $5–10）

---
流量再分配机制：
当某词转入Manual后：
- Auto中否定
- 预算自然释放给新词

---
六、预算分配系统（Portfolio级）
你可以做一个简单分配：
- 50% → Manual（赚钱）
- 30% → Auto（挖词）
- 20% → 测试Campaign

---
七、节奏控制（避免系统震荡）
不要每天大调，设定：
- 调价频率：24–48小时
- 单次变动：
    - 出价：≤20%
    - 预算：≤30%

---
八、伪代码（可以直接给开发）
for term in search_terms:
cvr = term.orders / term.clicks if term.clicks > 0 else 0
cpa = term.spend / term.orders if term.orders > 0 else None

    if term.orders >= 2 and cpa <= target_cpa:
        label = "WIN"
    elif term.clicks >= 10:
        label = "TEST"
    else:
        continue

    if label == "WIN":
        create_manual_keyword(term, bid=target_cpc)
        add_negative_to_auto(term)

    elif label == "TEST":
        adjust_bid(term, -0.15)

    elif label == "KILL":
        pause_or_negative(term)

---

# 九、 广告自动化运营计划执行规范 (V2 - 讨论补充)

## 1. 运营计划配置结构 (Configuration)

### 1.1 广告层次 (Hierarchy)
- **投放模式**：
    - `AUTO_TO_MANUAL`: 自动广告挖词 -> 手动精准。
    - `BROAD_TO_EXACT`: 手动广泛 -> 手动精准。
- **组织结构**：
    - **同根关联**：使用 `findSimilarCampaigns(List<Long> campaignIds, String keyword)` 匹配同类词广告活动。
    - **隔离机制**：若 `findSimilarCampaigns` 返回为空，或触发“预算独立规则”，则新建独立的 Campaign。

### 1.2 预算规则 (Budget Rules)
- **基础预算**：初始广告活动/广告组的每日预算。
- **独立预算触发 (ROAS Threshold)**：
    - 在策略配置中指定 `roasIsolationThreshold`。
    - 当关键词的 ROAS 持续超过该阈值时，将其从当前广告组移出，建立 **独立 Campaign** 并赋予专项预算。

### 1.3 监控指标 (Monitoring Metrics)
- **数据周期**：7天/14天滚动数据。
- **关键指标**：Orders, CPA, ACOS, CVR, Clicks。

### 1.4 执行规则 (Execution Rules)
- **WIN (优质词)**: Orders ≥ 2 且 CPA ≤ 目标CPA。
- **TEST (观察词)**: 点击 ≥ 10 且 Orders < 2。
- **KILL (亏损词)**: 点击 ≥ 15 且 Orders = 0，或 CPA > 目标CPA * 1.3。

## 2. 执行引擎原子操作 (Atomic Operations)

为保证执行的一致性与可追踪性，引擎必须封装以下原子操作：

1. **关键词转移 (Keyword Promotion)**：`Source(Negate) -> Destination(Add Keyword)`。
2. **调整预算 (Budget Adjustment)**：调整 Campaign 每日预算（幅度 ≤ 30%）。
3. **调整出价 (Bid Adjustment)**：参考 `AdsProfitModelService` 计算的 ACOS 下的 BID 范围。
4. **状态控制 (State Management)**：Campaign / AdGroup / 关键词的 `Pause` 与 `Start`。
5. **关键词管理 (Keyword Lifecycle)**：新增 (Create) 与 删除 (Archive) 关键词。
6. **否定词管理 (Negative Management)**：新增/删除 **Negative Exact**。

## 3. 关键接口定义 (Interface Spec)

### 3.1 相似性查找
```java
/**
 * 根据给定词和活动列表，寻找具有相同词根/主题的广告活动
 */
List<AdsCampaignDO> findSimilarCampaigns(List<Long> associatedCampaignIds, String keyword);
```

### 3.2 利润出价模型
```java
/**
 * 根据产品利润模型计算在特定ACOS目标下的合理出价范围
 */
BidRangeDTO calculateBidRange(String sku, String platform, Double targetAcos);
```