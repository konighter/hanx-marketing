CREATE TABLE IF NOT EXISTS `mas_skill_def` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `skill_code` varchar(128) NOT NULL COMMENT '技能唯一编码',
  `name` varchar(128) NOT NULL COMMENT '技能名称',
  `description` varchar(512) DEFAULT NULL COMMENT '技能描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '技能图标 url',
  `use_case_tags` varchar(255) DEFAULT NULL COMMENT '适用场景标签',
  `strategy_instruction` text COMMENT '策略指导书',
  `required_tools` varchar(1024) DEFAULT NULL COMMENT '允许使用的工具集 json',
  `param_schema` text COMMENT '技能参数定义 schema json',
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_skill_code` (`skill_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MAS 技能定义表';

CREATE TABLE IF NOT EXISTS `mas_task_skill_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `task_id` bigint NOT NULL COMMENT '关联的 mas_task_job 的 ID',
  `skill_code` varchar(128) NOT NULL COMMENT '应用的技能编码',
  `target_biz_id` varchar(128) NOT NULL COMMENT '目标业务对象的唯一标识',
  `target_biz_name` varchar(255) DEFAULT NULL COMMENT '目标业务对象的名称',
  `config_params` text COMMENT '用户配置的运行参数 JSON',
  `current_stage` varchar(255) DEFAULT NULL COMMENT '当前进度或状态文本',
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MAS 任务与技能绑定关系表';

INSERT INTO mas_skill_def (skill_code, name, description, icon, use_case_tags, strategy_instruction, required_tools, param_schema) VALUES
('NEW_PRODUCT_LAUNCH_V1', '「火箭飙升」冷启动', '专为新品打造的 30 天自动出价与拓词策略，前3天自动广告收集数据，后14天开启否定及长尾词提取，最终稳定ACOS。', 'Lightning', '新品期, 拓流, 预算控制', '# NEW_PRODUCT_LAUNCH_V1
## 目标
执行新品30天冷启动策略，控制初期预算，积累数据并最终收敛 ACOS。

## Phrase 1 (Day 1-3): 数据积累
- 开启自动广告，使用广泛匹配组合。
- 若无历史数据，禁止人工否定关键词。
- 目标：快速获取充足的基础曝光和点击。

## Phrase 2 (Day 4-14): 词汇清洗与挖掘
- 每日巡检 Search Term 数据报告。
- 当花费偏高且0转化的词，进入精确否定名单。
- 出单转化高的词，剥离并建立手动精准广告活动。

## Phrase 3 (Day 15-30): ACOS 稳定控制
- 每 4 小时动态微调竞价，逐步贴近设定目标。
- 自动关闭表现最差的 10% 广告组，释放预算。
- 若累计花费即将触及总预算阀值，提前进入收网模式。', '["AD_SP_API", "KEYWORD_SUGGESTION"]', '{"targetRoas": { "type": "number", "label": "目标 ROAS", "required": true }, "totalBudget": { "type": "number", "label": "总预算 ($)", "required": true }}'),

('SEASONAL_RUSH_V1', '「旺季抢位」防守反击', '针对旺季的高频波动，每两小时监控由于缺货或竞争加剧导致的排名下降，自动进行防守调价。', 'DataLine', '旺季爆单, 排名卡位', '# SEASONAL_RUSH_V1
## 目标
旺季大促期间的高效抢位防守，坚守核心搜索词的市场份额。

## Phrase 1: 高频自然/广告排名监控
- 周期性调用 AD_RANK_API 获取商品在其核心词的综合卡位。
- 判断流量分布情况，如果主核心词掉出搜索结果首页：立刻激活 Phrase 2。

## Phrase 2: 竞价防守反击
- 在设定上限之内，梯度提振广告 Bid（每次上升约20%）。
- 优先部署 Top of Search 溢价系数。
- 在后续 4 小时内对比排名上升趋势，如果已恢复至理想排位，暂停提价。

## Phrase 3: 熔断与告警
- 如果当前竞价已达到或溢出设定上限，且排名依然无法重夺前列，抛出系统异常告警。
- 暂停自动操作状态，交由人类运营专家介入。', '["AD_RANK_API"]', '{"maxBid": { "type": "number", "label": "单次点击上限 ($)", "required": true }}'),

('CLEARANCE_V1', '「智能清仓」库存消化', '识别冗余库存商品，联动降价机制与高投入比广告，在最短周期内释放库存压力，挽回现金流。', 'ShoppingCart', '清仓抛售, 现金流恢复', '# CLEARANCE_V1
## 目标
通过智能联动调价与广告，最大限度抛售冗余积压商品，降低仓储成本并回笼现金流。

## Phrase 1: 库存与动销健康度体检
- 使用 INVENTORY_API 定位库龄远超健康周期的 SKU，划定严重冗余等级。
- 计算剩余日均销售（Velocity），设立安全退出阈值。

## Phrase 2: 阶梯式组合促销放量
- 首周：启动 Prime 专享折扣，配搭展示型推流广告。
- 观测三日销量环比变化。

## Phrase 3: 终极倾销模式
- 如果 Phrase 2 下推流效率仍无法清理掉积压的 50% 库存，则触发探底模式。
- 自动将产品修改至底线清仓价，不再关注利润，并在多渠道申报站外 Deal。', '["INVENTORY_API"]', '{"lowestPrice": { "type": "number", "label": "最低清仓底线价", "required": true }}');
