-- MAS 业务任务表 (支持并行/串行/人工审核)
CREATE TABLE IF NOT EXISTS `ai_mas_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` VARCHAR(64) NOT NULL COMMENT '所属会话 ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父任务 ID (支持嵌套)',
  `name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型 (PARALLEL/SEQUENTIAL/LEAF)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态 (PENDING/RUNNING/REVIEW_REQUIRED/SUCCESS/FAILED)',
  `input_data` LONGTEXT DEFAULT NULL COMMENT '输入参数 (JSON)',
  `output_data` LONGTEXT DEFAULT NULL COMMENT '输出结果 (JSON)',
  `execution_order` INT DEFAULT '0' COMMENT '执行顺序 (串行任务用)',
  `retry_data` TEXT DEFAULT NULL,
  `strategy_instruction` LONGTEXT DEFAULT NULL COMMENT '策略指导书结构化 JSON',
  `next_execute_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '最后修改人ID',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_session_status` (`session_id`, `status`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAS 业务任务中心表';
