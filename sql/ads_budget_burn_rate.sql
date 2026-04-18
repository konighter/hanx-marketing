CREATE TABLE `ads_budget_burn_rate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `advertiser_id` varchar(255) DEFAULT NULL COMMENT '广告主ID',
  `marketplace_id` varchar(255) DEFAULT NULL COMMENT '站点ID',
  `dataset_id` varchar(255) DEFAULT NULL COMMENT '数据集ID',
  `budget_scope_id` varchar(255) DEFAULT NULL COMMENT '预算范围ID(CampaignID或ProfileID)',
  `budget_scope_type` varchar(255) DEFAULT NULL COMMENT '预算范围类型',
  `advertising_product_type` varchar(255) DEFAULT NULL COMMENT '广告产品类型',
  `budget` decimal(10,2) DEFAULT NULL COMMENT '预算金额',
  `budget_usage_percentage` decimal(10,2) DEFAULT NULL COMMENT '预算使用百分比',
  `usage_updated_timestamp` datetime DEFAULT NULL COMMENT '预算使用更新时间戳',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告预算消耗进度记录';
