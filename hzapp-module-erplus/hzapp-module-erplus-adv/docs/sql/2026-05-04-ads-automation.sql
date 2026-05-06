CREATE TABLE IF NOT EXISTS `ads_automation_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '模版名称',
  `type` varchar(50) NOT NULL COMMENT '模版类型',
  `config` json NOT NULL COMMENT '配置蓝图',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 (1: 启用, 0: 禁用)',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告自动化模版';

CREATE TABLE IF NOT EXISTS `ads_automation_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '计划名称',
  `template_id` bigint(20) NOT NULL COMMENT '关联模版 ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺 ID',
  `sku` varchar(100) DEFAULT NULL COMMENT '商品 SKU/ASIN',
  `platform` varchar(32) NOT NULL COMMENT '平台 (AMAZON/META)',
  `context` json NOT NULL COMMENT '运行时参数',
  `status` varchar(32) NOT NULL DEFAULT 'RUNNING' COMMENT '计划状态',
  `last_run_at` datetime DEFAULT NULL COMMENT '最近执行时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告自动化计划';

CREATE TABLE IF NOT EXISTS `ads_automation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint(20) NOT NULL COMMENT '计划 ID',
  `rule_name` varchar(255) DEFAULT NULL COMMENT '触发规则名称',
  `trigger_data` json DEFAULT NULL COMMENT '触发指标',
  `action_taken` json DEFAULT NULL COMMENT '执行动作',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告自动化日志';
-- 初始模版数据
INSERT INTO `ads_automation_template` (`name`, `type`, `config`, `status`, `tenant_id`) VALUES 
('搜索词流转模版', 'KEYWORD_FLOW', '{"defaultContext": {"targetCpa": 10.0, "minOrders": 2}, "description": "将高转化搜索词自动添加为手动广告关键词"}', 1, 0),
('高 ACOS 暂停模版', 'ACOS_OPTIMIZATION', '{"defaultContext": {"targetAcos": 0.35, "minSpend": 20.0}, "description": "自动暂停高 ACOS 且无转化的关键词"}', 1, 0),
('低转化出价调优', 'BID_OPTIMIZATION', '{"defaultContext": {"targetRoas": 3.0, "maxBid": 2.0}, "description": "根据 ROAS 表现自动小幅调整关键词出价"}', 1, 0);
