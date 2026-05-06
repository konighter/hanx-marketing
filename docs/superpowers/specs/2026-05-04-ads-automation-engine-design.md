# 广告自动化执行引擎设计文档 (Ads Automation & Strategy Engine)

## 1. 背景与目标 (Background & Goals)
为了提升广告运营效率，减少人工盯盘成本，系统需要一套通用的广告自动化引擎。该引擎能够基于预设模版（SOP）自动创建广告活动，并根据实时表现数据执行关键词流转、自动调价、自动止损等操作。

### 核心目标：
- **平台无关性 (Platform Agnostic)**: 上层策略逻辑、指标定义与执行动作均采用抽象定义，不与特定平台（Amazon, Meta 等）耦合。
- **模版驱动 (Template Driven)**: 支持多种预设策略模版，用户通过实例化模版快速创建“自动化计划”。
- **通用调度与执行**: 引擎能够处理复杂的逻辑判定，并通过平台适配层（Adapter）执行具体的 API 调用。
- **高扩展性**: 能够轻松增加新的判定指标（Metrics）和动作指令（Actions）。

---

## 2. 系统架构 (Architecture)

系统分为三层，确保业务逻辑与平台实现彻底解耦。

### 2.1 抽象领域层 (Agnostic Domain Layer)
定义通用的商业语言：
- **Standard Metrics**: `IMPRESSIONS`, `CLICKS`, `SPEND`, `ORDERS`, `SALES`, `CPA`, `ROAS`, `CVR`, `CTR`。
- **Standard Actions**: 
    - `CREATE_KEYWORD`: 在目标组创建关键词。
    - `NEGATE_TERM`: 否定/排除特定词。
    - `ADJUST_BID`: 按比例或固定值调整竞价。
    - `UPDATE_STATUS`: 暂停或启用实体。

### 2.2 策略引擎层 (Strategy Engine Layer)
核心逻辑分发器：
- **Evaluator**: 匹配计划（Plan）中的规则。
- **Context Manager**: 管理计划执行时的变量（如 `targetCpa`）。
- **Log Engine**: 记录自动化执行的每一笔流水，提供可追溯性。

### 2.3 平台适配层 (Platform Implementation Layer)
具体的执行细节：
- **Platform Spec**: 每个平台提供一份 Spec，定义如何将标准动作映射到平台 API。
- **Adapters**: 实现 `AmazonAdapter`, `MetaAdapter` 等，负责具体的网络请求与限流处理。

---

## 3. 数据模型 (Data Modeling)

### 3.1 自动化模版表 (`ads_automation_template`)
存储预设的策略逻辑。
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键 |
| `name` | varchar | 模版名称 (如：SP 自动转手动增长模版) |
| `type` | varchar | 模版类型 (如：KEYWORD_FLOW, BID_OPTIMIZE) |
| `blueprint` | json | 策略蓝图 (包含规则集规则：If Metric OP Value Then Action) |
| `status` | tinyint | 状态 (1: 启用, 0: 禁用) |

### 3.2 自动化计划表 (`ads_automation_plan`)
模版实例化的产物，绑定具体的账户和广告实体。
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键 |
| `template_id` | bigint | 关联模版 ID |
| `shop_id` | bigint | 关联店铺 ID |
| `platform` | varchar | 平台 (AMAZON/META) |
| `context` | json | 运行时参数 (如 `target_cpa`, `source_campaign_id`, `target_campaign_id`) |
| `status` | varchar | 计划状态 (RUNNING, PAUSED, COMPLETED) |
| `last_run_at` | datetime | 最近执行时间 |

### 3.3 自动化执行日志 (`ads_automation_log`)
| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键 |
| `plan_id` | bigint | 关联计划 ID |
| `rule_name` | varchar | 触发的规则名称 |
| `trigger_data` | json | 触发时的指标快照 |
| `action_taken` | json | 执行的具体动作与结果 |

---

## 4. 核心流程：关键词自动化流转 (Case Study: Amazon Keyword Flow)

当一个“自动转手动”计划被激活后，`KeywordFlowTaskHandler` 将按以下步骤执行：

1.  **数据采集**: 获取 `source_campaign_id` 下近 7 天的所有 Search Term 报表。
2.  **指标归一化**: 将 Amazon 的 `attributedOrders7d` 映射为标准的 `ORDERS` 指标。
3.  **规则匹配 (Evaluator)**:
    - `IF ORDERS >= 2 AND CPA <= context.targetCpa`:
        - 标记为 `ACTION_WIN`。
4.  **指令翻译 (Platform Spec)**:
    - `ACTION_WIN` -> 调用 Amazon Adapter 执行：
        1. `createKeywords` 在目标手动广告组增加词。
        2. `createNegativeKeywords` 在源自动广告组排除该词。
5.  **结果反馈**: 记录日志并更新 Plan 的状态。

---

## 5. 扩展性设计 (Extensibility)

### 5.1 指标扩展
新增指标只需在 `AdsMetricEnum` 中定义，并在数据提取层增加对应的映射逻辑。

### 5.2 动作扩展
1. 在 `AdsActionTypeEnum` 增加新类型。
2. 在各平台的 Adapter 中增加对应的逻辑处理函数。
3. 引擎会自动支持在 Template 中引用该 Action。

---

## 6. 验证计划 (Verification Plan)

### 6.1 单元测试
- 编写 `Evaluator` 测试，确保在不同阈值下能正确产生预期的 `Agnostic Actions`。
- 测试 `Context Manager` 对动态变量的替换逻辑。

### 6.2 集成测试
- 在 Mock 环境下模拟 Amazon Search Term 报表，验证从“判定”到“调用 Adapter”的全链路闭环。
- 检查 `ads_automation_log` 是否能正确记录操作详情。
