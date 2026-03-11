-- MAS 业务任务表
CREATE TABLE IF NOT EXISTS `ai_mas_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` VARCHAR(64) NOT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `name` VARCHAR(200) NOT NULL,
  `task_type` VARCHAR(20) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  `input_data` LONGTEXT DEFAULT NULL,
  `output_data` LONGTEXT DEFAULT NULL,
  `execution_order` INT DEFAULT '0',
  `retry_data` TEXT DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` VARCHAR(64) DEFAULT NULL,
  `updater` VARCHAR(64) DEFAULT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT 0,
  `tenant_id` BIGINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

-- Agent 配置表 (CustomAgentLoaderService 需要)
CREATE TABLE IF NOT EXISTS `ai_mas_agent_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL,
  `agent_name` VARCHAR(100) NOT NULL,
  `system_prompt` TEXT,
  `tool_beans` TEXT,
  `ext_config` TEXT,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` VARCHAR(64) DEFAULT NULL,
  `updater` VARCHAR(64) DEFAULT NULL,
  `deleted` BIT(1) NOT NULL DEFAULT 0,
  `tenant_id` BIGINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);
