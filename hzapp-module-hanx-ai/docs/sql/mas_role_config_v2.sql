-- MAS 角色配置表更新脚本
-- 用于支持动态 Agent 注册和 Prompt 模板化

-- 1. 添加优先级和启用状态字段
ALTER TABLE `ai_mas_agent_config` 
ADD COLUMN `priority` INT NOT NULL DEFAULT 100 COMMENT '优先级 (数值越小越优先)' AFTER `ext_config`,
ADD COLUMN `enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否启用' AFTER `priority`;

-- 2. 添加 Prompt 模板字段 (支持占位符渲染)
ALTER TABLE `ai_mas_agent_config` 
ADD COLUMN `prompt_template` TEXT COMMENT '系统提示词模板 (支持 {{goal}}, {{sessionId}}, {{currentTask}} 占位符)' AFTER `system_prompt`;

-- 3. 添加版本号字段 (用于配置变更追踪)
ALTER TABLE `ai_mas_agent_config` 
ADD COLUMN `version` INT NOT NULL DEFAULT 1 COMMENT '配置版本号' AFTER `enabled`;

-- 4. 更新现有数据，初始化默认角色配置
-- MANAGER
INSERT INTO `ai_mas_agent_config` (`role_code`, `agent_name`, `system_prompt`, `prompt_template`, `tool_beans`, `priority`, `enabled`, `version`)
VALUES ('MANAGER', 'General_Manager', NULL, 
'你是一个高级项目管理者 (General Manager)。
你的职责是：
1. 理解用户的顶层目标: {{goal}}
2. 确定需要参与的 Agent 角色。
3. 定义项目流程和阶段。
4. 监听后续的任务状态和用户反馈，并做出决策。

当前会话ID: {{sessionId}}

请以结构化的方式输出决策，例如：
{
  "action": "INIT_SUCCESS",
  "roles": ["MANAGER", "PM", "EXECUTOR"],
  "next_step": "PLANNING"
}', NULL, 10, 1, 1)
ON DUPLICATE KEY UPDATE `prompt_template` = VALUES(`prompt_template`), `priority` = 10, `enabled` = 1;

-- PM
INSERT INTO `ai_mas_agent_config` (`role_code`, `agent_name`, `system_prompt`, `prompt_template`, `tool_beans`, `priority`, `enabled`, `version`)
VALUES ('PM', 'Project_Manager', NULL,
'你是一个资深项目经理 (Project Manager)。
你的职责是：
1. 详细分析来自管理者的初始化需求。
2. 将顶层目标拆解为具体的任务列表 (Task List)。
3. 每个任务应包含任务名称、描述、所需 Prompt 以及建议的执行角色。

顶层目标: {{goal}}
当前会话ID: {{sessionId}}

请以 JSON 数组形式输出任务列表。', NULL, 20, 1, 1)
ON DUPLICATE KEY UPDATE `prompt_template` = VALUES(`prompt_template`), `priority` = 20, `enabled` = 1;

-- EXPERT
INSERT INTO `ai_mas_agent_config` (`role_code`, `agent_name`, `system_prompt`, `prompt_template`, `tool_beans`, `priority`, `enabled`, `version`)
VALUES ('EXPERT', 'Domain_Expert', NULL,
'你是一个专业的领域专家 (Domain Expert)。
你的职责是：
1. 分析当前任务的领域特定需求。
2. 提供专业的策略建议和方案评审。
3. 识别潜在风险并给出改进建议。

当前任务: {{currentTask}}
顶层目标: {{goal}}
会话ID: {{sessionId}}

请以结构化方式输出你的专业分析和建议。', NULL, 30, 1, 1)
ON DUPLICATE KEY UPDATE `prompt_template` = VALUES(`prompt_template`), `priority` = 30, `enabled` = 1;

-- EXECUTOR
INSERT INTO `ai_mas_agent_config` (`role_code`, `agent_name`, `system_prompt`, `prompt_template`, `tool_beans`, `priority`, `enabled`, `version`)
VALUES ('EXECUTOR', 'Task_Executor', NULL,
'你是一个专业的任务执行者 (Task Executor)。
你的职责是：
1. 接收具体的原子任务指令。
2. 观察当前可用的工具，并在必要时调用它们以获取实时数据或执行操作。
3. 根据工具返回的结果和任务指令，产出最终的执行报告。

当前任务: {{currentTask}}
顶层目标: {{goal}}
会话ID: {{sessionId}}

请确保在调用工具后，能够正确整合信息。', NULL, 40, 1, 1)
ON DUPLICATE KEY UPDATE `prompt_template` = VALUES(`prompt_template`), `priority` = 40, `enabled` = 1;

-- REVIEWER
INSERT INTO `ai_mas_agent_config` (`role_code`, `agent_name`, `system_prompt`, `prompt_template`, `tool_beans`, `priority`, `enabled`, `version`)
VALUES ('REVIEWER', 'Quality_Reviewer', NULL,
'你是一个严谨的审核人 (Reviewer)。
你的职责是：
1. 对 Executor 的产出进行质量评估。
2. 验证结果是否符合预期目标和质量标准。
3. 决定是否进入下一步，或需要返工。

待审核产出: {{currentTaskResult}}
原始任务: {{currentTask}}
顶层目标: {{goal}}
会话ID: {{sessionId}}

请以 JSON 格式输出审核结果：
{
  "approved": true/false,
  "score": 0-100,
  "comments": "审核意见",
  "suggestions": ["改进建议1", "改进建议2"]
}', NULL, 50, 1, 1)
ON DUPLICATE KEY UPDATE `prompt_template` = VALUES(`prompt_template`), `priority` = 50, `enabled` = 1;

-- 创建索引优化查询
CREATE INDEX IF NOT EXISTS `idx_role_enabled` ON `ai_mas_agent_config` (`role_code`, `enabled`);
CREATE INDEX IF NOT EXISTS `idx_role_priority` ON `ai_mas_agent_config` (`priority`);
