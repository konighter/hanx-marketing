SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for erplus_schedule_task
-- ----------------------------
CREATE TABLE IF NOT EXISTS `erplus_schedule_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` int DEFAULT NULL COMMENT '店铺ID',
  `parent_task_id` bigint DEFAULT NULL COMMENT '父任务 ID',
  `platform` varchar(64) DEFAULT NULL COMMENT '平台标识 (如 AMAZON, ERP, SYSTEM)',
  `task_type` varchar(64) DEFAULT NULL COMMENT '任务类型',
  `task_unique_id` varchar(128) DEFAULT NULL COMMENT '任务唯一标识（用于幂等）',
  `status` varchar(32) DEFAULT NULL COMMENT '任务状态: PENDING / RUNNING / SUCCESS / FAILED / SUBMITTED / PROCESSING',
  `platform_job_id` varchar(128) DEFAULT NULL COMMENT '平台 Job ID (异步场景)',
  `date_range_start` date DEFAULT NULL COMMENT '起始日期',
  `date_range_end` date DEFAULT NULL COMMENT '结束日期',
  `context` text COMMENT '任务参数上下文 (JSON)',
  `retry_count` int DEFAULT '0' COMMENT '已重试次数',
  `max_retries` int DEFAULT '3' COMMENT '最大重试次数',
  `error_message` text COMMENT '失败原因',
  `scheduled_at` bigint DEFAULT NULL COMMENT '预计执行时间 (epoch millis)',
  `started_at` datetime DEFAULT NULL COMMENT '开始执行时间',
  `finished_at` datetime DEFAULT NULL COMMENT '完成时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户 ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_task_unique` (`task_unique_id`, `task_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_scheduled_at` (`scheduled_at`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通用任务调度表';

SET FOREIGN_KEY_CHECKS = 1;
