-- 1. 创建跨境商品刊登状态表 (erplus_product_listing)
CREATE TABLE IF NOT EXISTS `erplus_product_listing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '关联 erplus_cross_product.id (本地 SPU ID)',
  `platform_id` int(11) NOT NULL COMMENT '平台 ID',
  `shop_id` int(11) NOT NULL COMMENT '店铺 ID',
  `market_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '市场 ID',
  `seller_sku` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '卖家 SKU',
  `platform_product_code` varchar(128) COLLATE utf8mb4_unicode_ci COMMENT '平台商品Code',
  `sync_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '刊登状态: 0-待提交, 10-待发布, 90-发布中, 91-发布失败, 99-发布成功',
  `latest_task_id` bigint(20) DEFAULT NULL COMMENT '最新任务 ID',
  `publish_time` datetime DEFAULT NULL COMMENT '最近发布时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_market_sku` (`shop_id`, `market_id`, `seller_sku`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跨境商品刊登状态表';

-- 2. 创建刊登任务流水表 (erplus_product_listing_task)
CREATE TABLE IF NOT EXISTS `erplus_product_listing_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `listing_id` bigint(20) DEFAULT NULL COMMENT '关联 刊登状态表 ID',
  `listing_data` longtext COLLATE utf8mb4_unicode_ci COMMENT '刊登数据快照 (JSON)',
  `schedule_time` datetime DEFAULT NULL COMMENT '预约发布时间',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '任务状态: 0-待提交, 10-待发布, 90-发布中, 91-失败, 99-成功',
  `status_info` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态详细信息',
  `raw_feedback` longtext COLLATE utf8mb4_unicode_ci COMMENT '平台原始反馈 JSON',
  `brief` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误简述预览',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人 ID',
  `begin_time` datetime DEFAULT NULL COMMENT '发布开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '发布结束时间',
  `duration` int(11) DEFAULT NULL COMMENT '发布耗时(ms)',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '乐观锁版本',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户 ID',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_listing_id` (`listing_id`),
  KEY `idx_schedule_status` (`schedule_time`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跨境商品刊登任务流水表';


ALTER TABLE ruoyi_vue_pro_2.erplus_cross_product_attrs ADD version BIGINT NULL COMMENT '版本号' AFTER attr_value;
