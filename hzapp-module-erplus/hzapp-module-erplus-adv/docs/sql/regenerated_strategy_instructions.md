# Regenerated Strategy Instructions

Here are the structured JSON blocks for the `strategy_instruction` field of the existing skills, formatted according to `StrategyInstructionVO`.

## 1. 「火箭飙升」冷启动 (NEW_PRODUCT_LAUNCH_V1)

```json
{
  "summary": {
    "description": "专为新品打造的 30 天自动出价与拓词策略，前3天自动广告收集数据，后14天开启否定及长尾词提取，最终稳定ACOS。",
    "phases": [
      {
        "name": "Phase 1: 数据积累",
        "description": "通过自动广告快速获取充足的基础数据（建议执行 3 天）。"
      },
      {
        "name": "Phase 2: 词汇清洗与挖掘",
        "description": "每日巡检搜索词，剔除低效词，提取高效词（执行 11 天）。"
      },
      {
        "name": "Phase 3: ACOS 稳定控制",
        "description": "动态微调竞价，逐步贴近目标 ACOS，优化预算分配（执行 16 天）。"
      }
    ]
  },
  "strategy": [
    {
      "name": "Phase 1: 数据积累",
      "description": "通过自动广告快速获取充足的基础数据。",
      "order": 1,
      "cycleType": "ONCE",
      "interval": 259200,
      "tools": ["AD_SP_API"],
      "instruction": "开启自动广告，使用广泛匹配组合。若无历史数据，禁止人工否定关键词。目标：快速获取充足的基础曝光和点击。"
    },
    {
      "name": "Phase 2: 词汇清洗与挖掘",
      "description": "每日巡检搜索词，剔除低效词，提取高效词。",
      "order": 2,
      "cycleType": "PERIODIC",
      "interval": 86400,
      "tools": ["AD_SP_API", "KEYWORD_SUGGESTION"],
      "instruction": "每日巡检 Search Term 数据报告。当花费偏高且0转化的词，进入精确否定名单。出单转化高的词，剥离并建立手动精准广告活动。"
    },
    {
      "name": "Phase 3: ACOS 稳定控制",
      "description": "动态微调竞价，逐步贴近目标 ACOS，优化预算分配。",
      "order": 3,
      "cycleType": "PERIODIC",
      "interval": 14400,
      "tools": ["AD_SP_API"],
      "instruction": "每 4 小时动态微调竞价，逐步贴近设定目标。自动关闭表现最差的 10% 广告组，释放预算。若累计花费即将触及总预算阀值，提前进入收网模式。"
    }
  ]
}
```

## 2. 「旺季抢位」防守反击 (SEASONAL_RUSH_V1)

```json
{
  "summary": {
    "description": "针对旺季的高频波动，每两小时监控由于缺货或竞争加剧导致的排名下降，自动进行防守调价。",
    "phases": [
      {
        "name": "Phase 1: 高频排名监控",
        "description": "周期性调用排名接口，实时掌握核心词位置。"
      },
      {
        "name": "Phase 2: 竞价防守反击",
        "description": "当排名跌出理想区间时，梯度提价并增加 Top 位置权重。"
      },
      {
        "name": "Phase 3: 熔断与告警",
        "description": "当达到报价上限仍无法回升排名时，停止自动操作并告警。"
      }
    ]
  },
  "strategy": [
    {
      "name": "Phase 1: 高频排名监控",
      "description": "周期性调用排名接口，实时掌握核心词位置。",
      "order": 1,
      "cycleType": "PERIODIC",
      "interval": 7200,
      "tools": ["AD_RANK_API"],
      "instruction": "周期性调用 AD_RANK_API 获取商品在其核心词的综合卡位。判断流量分布情况，如果主核心词掉出搜索结果首页：立刻激活 Phase 2。"
    },
    {
      "name": "Phase 2: 竞价防守反击",
      "description": "当排名跌出理想区间时，梯度提价并增加 Top 位置权重。",
      "order": 2,
      "cycleType": "PERIODIC",
      "interval": 14400,
      "tools": ["AD_SP_API"],
      "instruction": "在设定上限之内，梯度提振广告 Bid（每次上升约20%）。优先部署 Top of Search 溢价系数。在后续 4 小时内对比排名上升趋势，如果已恢复至理想排位，暂停提价。"
    },
    {
      "name": "Phase 3: 熔断与告警",
      "description": "当达到报价上限仍无法回升排名时，停止自动操作并告警。",
      "order": 3,
      "cycleType": "ONCE",
      "interval": 0,
      "tools": [],
      "instruction": "如果当前竞价已达到或溢出设定上限，且排名依然无法重夺前列，抛出系统异常告警。暂停自动操作状态，交由人类运营专家介入。"
    }
  ]
}
```

## 3. 「智能清仓」库存消化 (CLEARANCE_V1)

```json
{
  "summary": {
    "description": "识别冗余库存商品，联动降价机制与高投入比广告，在最短周期内释放库存压力，挽回现金流。",
    "phases": [
      {
        "name": "Phase 1: 健康度体检",
        "description": "定位严重冗余 SKU，设定安全退出阈值。"
      },
      {
        "name": "Phase 2: 组合促销放量",
        "description": "通过 Prime 折扣与展示型广告开启首周探测。"
      },
      {
        "name": "Phase 3: 终极倾销模式",
        "description": "如果清理进度仍不达标，触发底线价探底策略。"
      }
    ]
  },
  "strategy": [
    {
      "name": "Phase 1: 健康度体检",
      "description": "定位严重冗余 SKU，设定安全退出阈值。",
      "order": 1,
      "cycleType": "ONCE",
      "interval": 0,
      "tools": ["INVENTORY_API"],
      "instruction": "使用 INVENTORY_API 定位库龄远超健康周期的 SKU，划定严重冗余等级。计算剩余日均销售（Velocity），设立安全退出阈值。"
    },
    {
      "name": "Phase 2: 组合促销放量",
      "description": "通过 Prime 折扣与展示型广告开启首周探测。",
      "order": 2,
      "cycleType": "PERIODIC",
      "interval": 259200,
      "tools": ["AD_SP_API"],
      "instruction": "首周：启动 Prime 专享折扣，配搭展示型推流广告。观测三日销量环比变化。"
    },
    {
      "name": "Phase 3: 终极倾销模式",
      "description": "如果清理进度仍不达标，触发底线价探底策略。",
      "order": 3,
      "cycleType": "ONCE",
      "interval": 0,
      "tools": ["AD_SP_API"],
      "instruction": "如果 Phase 2 下推流效率仍无法清理掉积压的 50% 库存，则触发探底模式。自动将产品修改至底线清仓价，不再关注利润，并在多渠道申报站外 Deal。"
    }
  ]
}
```
