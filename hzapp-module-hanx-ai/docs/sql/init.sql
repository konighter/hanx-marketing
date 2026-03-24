-- MAS 智能体角色配置表
CREATE TABLE IF NOT EXISTS `ai_mas_agent_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色代码 (MANAGER/PM/EXECUTOR/EXPERT/REVIEWER)',
  `agent_name` VARCHAR(100) NOT NULL COMMENT '智能体名称',
  `system_prompt` TEXT NOT NULL COMMENT '系统提示词',
  `tool_beans` VARCHAR(500) DEFAULT NULL COMMENT '绑定的工具 Bean 名称列表 (JSON 格式)',
  `ext_config` TEXT DEFAULT NULL COMMENT '扩展配置 (JSON 格式)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '最后修改人ID',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role` (`role_code`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAS 智能体角色配置表';

-- MAS 会话主表
CREATE TABLE IF NOT EXISTS `ai_mas_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` VARCHAR(64) NOT NULL COMMENT '会话唯一标识 (UUID)',
  `goal` TEXT NOT NULL COMMENT '顶层任务目标',
  `status` VARCHAR(50) NOT NULL COMMENT '当前状态',
  `user_id` BIGINT DEFAULT NULL COMMENT '发起用户 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '最后修改人ID',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAS 会话主表';

-- MAS 任务执行历史日志表
CREATE TABLE IF NOT EXISTS `ai_mas_task_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` VARCHAR(64) NOT NULL COMMENT '所属会话 ID',
  `task_id` VARCHAR(100) NOT NULL COMMENT '任务唯一标识',
  `name` VARCHAR(200) NOT NULL COMMENT '任务名称',
  `role` VARCHAR(50) NOT NULL COMMENT '执行角色',
  `prompt` TEXT NOT NULL COMMENT '执行提示词',
  `result` LONGTEXT DEFAULT NULL COMMENT '执行结果内容',
  `status` VARCHAR(20) NOT NULL COMMENT '执行状态 (SUCCESS/FAILED/INTERRUPTED)',
  `is_internal` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否为内部编排任务',
  `execution_time` BIGINT DEFAULT NULL COMMENT '执行耗时 (ms)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '最后修改人ID',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAS 任务执行历史日志表';

-- MAS 会话上下文变量表
CREATE TABLE IF NOT EXISTS `ai_mas_session_variable` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` VARCHAR(64) NOT NULL COMMENT '会话 ID',
  `var_key` VARCHAR(100) NOT NULL COMMENT '变量键',
  `var_value` TEXT DEFAULT NULL COMMENT '变量值',
  `var_type` VARCHAR(50) DEFAULT NULL COMMENT '变量类型',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
  `updater` VARCHAR(64) DEFAULT NULL COMMENT '最后修改人ID',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_key` (`session_id`, `var_key`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAS 会话上下文变量表';
