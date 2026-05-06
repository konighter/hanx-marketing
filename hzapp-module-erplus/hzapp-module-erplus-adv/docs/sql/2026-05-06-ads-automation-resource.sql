-- 广告自动化计划资源关联表
CREATE TABLE IF NOT EXISTS `ads_automation_plan_resource` (
  `id` bigint(20) NOT NULL AUTO_VALUE_GENERATED,
  `plan_id` bigint(20) NOT NULL COMMENT '运营计划ID',
  `campaign_id` bigint(20) NOT NULL COMMENT '广告活动ID',
  `platform_campaign_id` varchar(64) DEFAULT NULL COMMENT '平台广告活动原始ID',
  `resource_role` varchar(32) NOT NULL COMMENT '资源角色: SOURCE, SINK_SHARED, SINK_ISOLATED',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_campaign_plan` (`campaign_id`), -- 一个活动只关联一个计划
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告自动化计划资源关联表';
