-- =============================================
-- 跨平台广告管理系统 - 数据库 DDL
-- 适配平台: Amazon Ads / Meta Ads / Google Ads / TikTok Ads
-- 设计规范: 第三范式 (3NF)
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 广告账户表
-- ----------------------------
DROP TABLE IF EXISTS `ads_account`;
CREATE TABLE `ads_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(32) NOT NULL COMMENT '广告平台: AMAZON / META / GOOGLE / TIKTOK',
  `external_account_id` varchar(128) NOT NULL COMMENT '平台原始账户ID',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '账户显示名称',
  `credential_id` bigint DEFAULT NULL COMMENT '关联凭证ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父级账号ID',
  `currency` varchar(8) NOT NULL DEFAULT 'USD' COMMENT '账户币种 (USD, CNY, EUR...)',
  `timezone` varchar(64) NOT NULL DEFAULT '' COMMENT '账户时区 (如 America/Los_Angeles)',
  `auth_status` tinyint NOT NULL DEFAULT 1 COMMENT '授权状态 (1: 有效, 0: 失效)',
  `last_synced_at` datetime DEFAULT NULL COMMENT '上次同步完成时间',
  `ext_config` json DEFAULT NULL COMMENT '平台专属配置 (Amazon ProfileId, Google ManagerAccountId 等)',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_external_id` (`platform`, `external_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告账户表';

-- ----------------------------
-- 2. 账户凭证表 (3NF: 凭证信息从账户表独立)
-- ----------------------------
DROP TABLE IF EXISTS `ads_account_credential`;
CREATE TABLE `ads_account_credential` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `credential_type` varchar(32) NOT NULL COMMENT '凭证类型: OAUTH2 / API_KEY / LWA',
  `access_token` text DEFAULT NULL COMMENT '加密后的 Access Token',
  `refresh_token` text DEFAULT NULL COMMENT '加密后的 Refresh Token',
  `token_expires_at` datetime DEFAULT NULL COMMENT 'Token 过期时间',
  `client_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'OAuth Client ID',
  `client_secret` text DEFAULT NULL COMMENT '加密后的 Client Secret',
  `ext_credential` json DEFAULT NULL COMMENT '其他凭据 (如 Amazon Developer Token, Google Developer Token)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告账户凭证表';

-- ----------------------------
-- 3. 广告计划表
-- ----------------------------
DROP TABLE IF EXISTS `ads_campaign`;
CREATE TABLE `ads_campaign` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `external_id` varchar(128) NOT NULL COMMENT '平台原始 Campaign ID',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '计划名称',
  `campaign_type` varchar(32) NOT NULL DEFAULT '' COMMENT '计划类型 (SP/SB/SD/SEARCH/DISPLAY/VIDEO)',
  `objective` varchar(64) NOT NULL DEFAULT '' COMMENT '广告目标 (CONVERSIONS / TRAFFIC / AWARENESS)',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '统一状态: ENABLED / PAUSED / ARCHIVED / REMOVED',
  `platform_status` varchar(64) NOT NULL DEFAULT '' COMMENT '原始平台状态原文',
  `budget_type` varchar(16) NOT NULL DEFAULT 'DAILY' COMMENT '预算类型: DAILY / LIFETIME / CAMPAIGN_TOTAL',
  `daily_budget` decimal(18,4) DEFAULT NULL COMMENT '日预算',
  `total_budget` decimal(18,4) DEFAULT NULL COMMENT '总预算',
  `start_date` date DEFAULT NULL COMMENT '投放开始日期',
  `end_date` date DEFAULT NULL COMMENT '投放结束日期',
  `delivery_schedule` text DEFAULT NULL COMMENT '分时投放配置 (7x24 布尔矩阵 JSON)',
  `bidding_strategy` varchar(32) NOT NULL DEFAULT '' COMMENT '出价策略 (MANUAL_CPC / AUTO_BID / TARGET_ROAS)',
  `ext_data` json DEFAULT NULL COMMENT '平台扩展字段',
  `synced_at` datetime DEFAULT NULL COMMENT '上次从平台同步的时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_external` (`account_id`, `external_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告计划表';

-- ----------------------------
-- 4. 广告组表
-- ----------------------------
DROP TABLE IF EXISTS `ads_ad_group`;
CREATE TABLE `ads_ad_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `campaign_id` bigint NOT NULL COMMENT '关联广告计划ID',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID (冗余, 优化跨层级查询)',
  `external_id` varchar(128) NOT NULL COMMENT '平台原始广告组ID',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '广告组名称',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '统一状态: ENABLED / PAUSED / ARCHIVED / REMOVED',
  `platform_status` varchar(64) NOT NULL DEFAULT '' COMMENT '原始平台状态',
  `default_bid` decimal(18,4) DEFAULT NULL COMMENT '默认出价',
  `bid_strategy` varchar(32) NOT NULL DEFAULT '' COMMENT '出价策略',
  `targeting_type` varchar(32) NOT NULL DEFAULT '' COMMENT '投放定向方式 (KEYWORD / AUTO / AUDIENCE)',
  `ext_data` json DEFAULT NULL COMMENT '定向参数等平台扩展 (年龄/地域/兴趣/设备)',
  `synced_at` datetime DEFAULT NULL COMMENT '上次从平台同步的时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_external` (`campaign_id`, `external_id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告组表';

-- ----------------------------
-- 5. 广告实体表
-- ----------------------------
DROP TABLE IF EXISTS `ads_ad`;
CREATE TABLE `ads_ad` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ad_group_id` bigint NOT NULL COMMENT '关联广告组ID',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID (冗余)',
  `external_id` varchar(128) NOT NULL COMMENT '平台原始广告ID',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '广告名称',
  `ad_format` varchar(32) NOT NULL DEFAULT '' COMMENT '广告格式 (IMAGE / VIDEO / CAROUSEL / RESPONSIVE)',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '统一状态: ENABLED / PAUSED / ARCHIVED / REMOVED',
  `platform_status` varchar(64) NOT NULL DEFAULT '' COMMENT '平台原始状态',
  `headline` varchar(512) NOT NULL DEFAULT '' COMMENT '标题文案',
  `description` text DEFAULT NULL COMMENT '描述文案',
  `landing_page_url` varchar(1024) NOT NULL DEFAULT '' COMMENT '落地页URL',
  `call_to_action` varchar(64) NOT NULL DEFAULT '' COMMENT '行动号召 (SHOP_NOW / LEARN_MORE)',
  `review_status` varchar(32) NOT NULL DEFAULT '' COMMENT '平台审核状态 (APPROVED / PENDING / REJECTED)',
  `ext_data` json DEFAULT NULL COMMENT '平台扩展字段',
  `synced_at` datetime DEFAULT NULL COMMENT '上次从平台同步的时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_adgroup_external` (`ad_group_id`, `external_id`),
  KEY `idx_ad_group_id` (`ad_group_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告实体表';

-- ----------------------------
-- 6. 创意素材表 (3NF: 素材从广告表的JSON数组独立)
-- ----------------------------
DROP TABLE IF EXISTS `ads_creative_asset`;
CREATE TABLE `ads_creative_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ad_id` bigint NOT NULL COMMENT '关联广告ID',
  `asset_type` varchar(16) NOT NULL COMMENT '素材类型: IMAGE / VIDEO / HTML',
  `asset_url` varchar(1024) NOT NULL COMMENT '素材文件URL',
  `asset_hash` varchar(64) NOT NULL DEFAULT '' COMMENT '素材指纹 (MD5/SHA256), 用于变更检测',
  `width` int DEFAULT NULL COMMENT '像素宽度',
  `height` int DEFAULT NULL COMMENT '像素高度',
  `duration_seconds` int DEFAULT NULL COMMENT '视频时长 (秒)',
  `file_size_bytes` bigint DEFAULT NULL COMMENT '文件大小 (字节)',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '轮播/展示顺序',
  `status` varchar(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '素材状态: ACTIVE / REPLACED / DELETED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_ad_id` (`ad_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告创意素材表';

-- ----------------------------
-- 7. 关键词/投放目标表 (3NF: 独立于广告组)
-- ----------------------------
DROP TABLE IF EXISTS `ads_keyword`;
CREATE TABLE `ads_keyword` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ad_group_id` bigint NOT NULL COMMENT '关联广告组ID',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID (冗余)',
  `external_id` varchar(128) NOT NULL DEFAULT '' COMMENT '平台关键词ID',
  `keyword_text` varchar(512) NOT NULL COMMENT '关键词文本',
  `match_type` varchar(16) NOT NULL COMMENT '匹配类型: EXACT / PHRASE / BROAD',
  `bid` decimal(18,4) DEFAULT NULL COMMENT '自定义出价 (NULL时继承广告组默认出价)',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '统一状态',
  `platform_status` varchar(64) NOT NULL DEFAULT '' COMMENT '原始平台状态',
  `is_negative` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为否定关键词 (0: 否, 1: 是)',
  `ext_data` json DEFAULT NULL COMMENT '平台扩展字段',
  `synced_at` datetime DEFAULT NULL COMMENT '上次同步时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_ad_group_id` (`ad_group_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_keyword_text` (`keyword_text`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关键词/投放目标表';

-- ----------------------------
-- 8. 每日绩效报表
-- ----------------------------
DROP TABLE IF EXISTS `ads_report_daily`;
CREATE TABLE `ads_report_daily` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `entity_type` varchar(16) NOT NULL COMMENT '实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD',
  `entity_id` bigint NOT NULL COMMENT '内部实体ID (对应各表的id)',
  `external_entity_id` varchar(128) NOT NULL DEFAULT '' COMMENT '平台原始实体ID (辅助同步比对)',
  `report_date` date NOT NULL COMMENT '报表日期',
  `impressions` bigint NOT NULL DEFAULT 0 COMMENT '展现量',
  `clicks` bigint NOT NULL DEFAULT 0 COMMENT '点击量',
  `spend` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '花费',
  `conversions` int NOT NULL DEFAULT 0 COMMENT '转化数量',
  `conversion_value` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '转化总金额/销售额',
  `video_views` bigint NOT NULL DEFAULT 0 COMMENT '视频播放量',
  `reach` bigint NOT NULL DEFAULT 0 COMMENT '触达人数 (Meta/TikTok)',
  `placement` varchar(64) DEFAULT NULL COMMENT '广告位: Top of Search, Detail Page, etc.',
  `synced_at` datetime DEFAULT NULL COMMENT '数据写入/覆盖时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_entity_date` (`account_id`, `entity_type`, `entity_id`, `report_date`, `placement`),
  KEY `idx_report_date` (`report_date`),
  KEY `idx_entity` (`entity_type`, `entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告每日绩效报表';

-- ----------------------------
-- 9. 实体变更日志 (审计表)
-- ----------------------------
DROP TABLE IF EXISTS `ads_entity_change_log`;
CREATE TABLE `ads_entity_change_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `entity_type` varchar(16) NOT NULL COMMENT '实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD / CREATIVE',
  `entity_id` bigint NOT NULL COMMENT '内部实体ID',
  `action` varchar(32) NOT NULL COMMENT '操作类型: STATUS_CHANGE / BUDGET_CHANGE / BID_CHANGE / CREATIVE_REPLACE / TARGETING_CHANGE / METADATA_SYNC / ENTITY_CREATE / ENTITY_DELETE',
  `field_name` varchar(64) NOT NULL DEFAULT '' COMMENT '变更字段名 (如 status, daily_budget, bid)',
  `old_value` text DEFAULT NULL COMMENT '变更前的值',
  `new_value` text DEFAULT NULL COMMENT '变更后的值',
  `source` varchar(16) NOT NULL COMMENT '变更来源: PLATFORM_SYNC / USER / AI_AGENT',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人 (用户名或 AI Agent ID)',
  `request_id` varchar(128) NOT NULL DEFAULT '' COMMENT '关联的平台API请求ID (用于追溯)',
  `remark` varchar(512) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_entity` (`entity_type`, `entity_id`),
  KEY `idx_action` (`action`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告实体变更日志 (审计表)';

-- ----------------------------
-- 10. 同步任务表
-- ----------------------------
DROP TABLE IF EXISTS `ads_sync_task`;
CREATE TABLE `ads_sync_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `parent_task_id` bigint DEFAULT NULL COMMENT '父任务ID（子任务才有值，父任务为 NULL）',
  `platform` varchar(32) DEFAULT NULL COMMENT '广告平台: AMAZON / META / GOOGLE / TIKTOK',
  `task_type` varchar(32) NOT NULL COMMENT '任务类型: METADATA_FULL / METADATA_INCR / REPORT_DAILY / REPORT_DIMENSION / TOKEN_REFRESH',
  `status` varchar(16) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态: PENDING / RUNNING / SUCCESS / FAILED / PARTIAL',
  `platform_job_id` varchar(128) NOT NULL DEFAULT '' COMMENT '平台报表 Job ID (异步报表场景)',
  `date_range_start` date DEFAULT NULL COMMENT '报表起始日期',
  `date_range_end` date DEFAULT NULL COMMENT '报表结束日期',
  `context` json DEFAULT NULL COMMENT '平台特定上下文 (JSON), 如 profileId / reportType / groupBy / baseUrl / region',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '已重试次数',
  `max_retries` int NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  `error_message` text DEFAULT NULL COMMENT '失败原因',
  `scheduled_at` bigint DEFAULT NULL COMMENT '预计执行时间 (epoch millis 时间戳，避免时区转换)',
  `started_at` datetime DEFAULT NULL COMMENT '开始执行时间',
  `finished_at` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_parent_task_id` (`parent_task_id`),
  KEY `idx_status` (`status`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_scheduled_at` (`scheduled_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告数据同步任务表';

-- ----------------------------
-- 11. 亚马逊广告 Profile 表
-- ----------------------------
DROP TABLE IF EXISTS `ads_amazon_profile`;
CREATE TABLE `ads_amazon_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `profile_id` varchar(128) NOT NULL COMMENT '亚马逊原始 Profile ID',
  `country_code` varchar(16) NOT NULL COMMENT '国家代码 (US, UK, JP...)',
  `region` varchar(16) NOT NULL COMMENT '区域 (NA, EU, FE)',
  `currency_code` varchar(16) NOT NULL COMMENT '币种',
  `timezone` varchar(64) NOT NULL COMMENT '时区',
  `entity_id` varchar(128) NOT NULL COMMENT '亚马逊原始 Entity ID (广告主账号 ID)',
  `entity_name` varchar(255) NOT NULL DEFAULT '' COMMENT '广告主账号名称',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `config` json DEFAULT NULL COMMENT '配置信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_profile_id` (`profile_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_entity_id` (`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='亚马逊广告 Profile 表';

-- ----------------------------
-- 12. 广告计划分时调度跟踪表
-- ----------------------------
DROP TABLE IF EXISTS `ads_campaign_schedule`;
CREATE TABLE `ads_campaign_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `campaign_id` bigint NOT NULL COMMENT '广告计划 ID',
  `account_id` bigint NOT NULL COMMENT '广告账户 ID',
  `current_status` varchar(32) NOT NULL COMMENT '当前该调度逻辑认为的状态: ENABLED / PAUSED',
  `next_transition_time` datetime NOT NULL COMMENT '下一次状态变迁时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_id` (`campaign_id`),
  KEY `idx_next_transition_time` (`next_transition_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告计划分时调度跟踪表';


-- ----------------------------
-- 13. 广告小时绩效报表 (原始明细层)
-- ----------------------------
DROP TABLE IF EXISTS `ads_report_hourly`;
CREATE TABLE `ads_report_hourly` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `entity_type` varchar(16) NOT NULL COMMENT '实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD',
  `entity_id` bigint NOT NULL COMMENT '内部实体ID',
  `report_hour` datetime NOT NULL COMMENT '报表小时点',
  `metrics` json NOT NULL COMMENT '数值指标池 (spend, clicks, impressions, conversions, etc.)',
  -- 常用指标虚拟列，用于性能加速和排序
  `spend` decimal(18,4) GENERATED ALWAYS AS (COALESCE(metrics->'$.spend', 0)) VIRTUAL,
  `clicks` bigint GENERATED ALWAYS AS (COALESCE(metrics->'$.clicks', 0)) VIRTUAL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hour_entity` (`account_id`, `entity_type`, `entity_id`, `report_hour`),
  KEY `idx_report_hour` (`report_hour`),
  KEY `idx_spend` (`spend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告小时绩效报表';

-- ----------------------------
-- 14. 广告性能预计算汇总表 (汇总层/分析层)
-- ----------------------------
DROP TABLE IF EXISTS `ads_report_summary`;
CREATE TABLE `ads_report_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint NOT NULL COMMENT '关联广告账户ID',
  `entity_type` varchar(16) NOT NULL COMMENT '实体类型: ACCOUNT / CAMPAIGN / ADGROUP',
  `entity_id` bigint NOT NULL COMMENT '内部实体ID',
  `period_type` varchar(10) NOT NULL COMMENT '周期类型: DAY / WEEK / MONTH',
  `period_value` varchar(20) NOT NULL COMMENT '周期值 (YYYY-MM-DD / YYYY-WW / YYYY-MM)',
  `metrics` json NOT NULL COMMENT '聚合后的指标池',
  -- 核心排序/分析指标虚拟化
  `spend` decimal(18,4) GENERATED ALWAYS AS (COALESCE(metrics->'$.spend', 0)) VIRTUAL,
  `conversions` int GENERATED ALWAYS AS (COALESCE(metrics->'$.conversions', 0)) VIRTUAL,
  `sales` decimal(18,4) GENERATED ALWAYS AS (COALESCE(metrics->'$.sales', 0)) VIRTUAL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人ID',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最后修改人ID',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_period_entity` (`account_id`, `entity_type`, `entity_id`, `period_type`, `period_value`),
  KEY `idx_period_value` (`period_value`),
  KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告性能预计算汇总表';

SET FOREIGN_KEY_CHECKS = 1;
