package com.hzltd.module.erplus.ai.masv0.framework.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MAS 角色配置类 - 替代硬编码枚举，支持运行时动态配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasRoleConfig {

    /**
     * 角色唯一标识
     */
    private String roleId;

    /**
     * 角色显示名称
     */
    private String name;

    /**
     * 角色职责描述
     */
    private String description;

    /**
     * 默认绑定的工具 Bean 名称列表
     */
    private List<String> defaultTools;

    /**
     * 系统提示词模板 (支持占位符)
     * 可用占位符: {{sessionId}}, {{goal}}, {{currentTask}}
     */
    private String systemPromptTemplate;

    /**
     * 扩展配置 (JSON 格式)
     */
    private String extConfig;

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * 优先级 (数值越小越优先)
     */
    @Builder.Default
    private int priority = 100;

    /**
     * 预定义角色的默认配置
     * 提供向后兼容的默认值
     */
    public static class Defaults {
        
        public static MasRoleConfig manager() {
            return MasRoleConfig.builder()
                    .roleId("MANAGER")
                    .name("管理者")
                    .description("全局决策与任务分配")
                    .systemPromptTemplate("""
                            你是一个高级项目管理者 (General Manager)。
                            你的职责是：
                            1. 理解用户的顶层目标: {{goal}}
                            2. 确定需要参与的 Agent 角色 (MANAGER, PM, EXPERT, EXECUTOR, REVIEWER)。
                            3. 定义项目流程和阶段。
                            4. 监听后续的任务状态和用户反馈，并做出决策。
                            
                            当前会话ID: {{sessionId}}
                            
                            请以结构化的方式输出决策，例如：
                            {
                              "action": "INIT_SUCCESS",
                              "roles": ["MANAGER", "PM", "EXECUTOR"],
                              "next_step": "PLANNING"
                            }
                            """)
                    .priority(10)
                    .enabled(true)
                    .build();
        }

        public static MasRoleConfig pm() {
            return MasRoleConfig.builder()
                    .roleId("PM")
                    .name("项目经理")
                    .description("需求拆解与规划")
                    .systemPromptTemplate("""
                            你是一个资深项目经理 (Project Manager)。
                            你的职责是：
                            1. 详细分析来自管理者的初始化需求。
                            2. 将顶层目标拆解为具体的任务列表 (Task List)。
                            3. 每个任务应包含任务名称、描述、所需 Prompt 以及建议的执行角色。
                            
                            顶层目标: {{goal}}
                            当前会话ID: {{sessionId}}
                            
                            请以 JSON 数组形式输出任务列表。
                            """)
                    .priority(20)
                    .enabled(true)
                    .build();
        }

        public static MasRoleConfig expert() {
            return MasRoleConfig.builder()
                    .roleId("EXPERT")
                    .name("领域专家")
                    .description("策略提供与质量把控")
                    .systemPromptTemplate("""
                            你是一个专业的领域专家 (Domain Expert)。
                            你的职责是：
                            1. 分析当前任务的领域特定需求。
                            2. 提供专业的策略建议和方案评审。
                            3. 识别潜在风险并给出改进建议。
                            
                            当前任务: {{currentTask}}
                            顶层目标: {{goal}}
                            会话ID: {{sessionId}}
                            
                            请以结构化方式输出你的专业分析和建议。
                            """)
                    .priority(30)
                    .enabled(true)
                    .build();
        }

        public static MasRoleConfig executor() {
            return MasRoleConfig.builder()
                    .roleId("EXECUTOR")
                    .name("执行者")
                    .description("具体子任务实施")
                    .systemPromptTemplate("""
                            你是一个专业的任务执行者 (Task Executor)。
                            你的职责是：
                            1. 接收具体的原子任务指令。
                            2. 观察当前可用的工具，并在必要时调用它们以获取实时数据或执行操作。
                            3. 根据工具返回的结果和任务指令，产出最终的执行报告。
                            
                            当前任务: {{currentTask}}
                            顶层目标: {{goal}}
                            会话ID: {{sessionId}}
                            
                            请确保在调用工具后，能够正确整合信息。
                            """)
                    .priority(40)
                    .enabled(true)
                    .build();
        }

        public static MasRoleConfig reviewer() {
            return MasRoleConfig.builder()
                    .roleId("REVIEWER")
                    .name("审核人")
                    .description("结果验证与验收")
                    .systemPromptTemplate("""
                            你是一个严谨的审核人 (Reviewer)。
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
                            }
                            """)
                    .priority(50)
                    .enabled(true)
                    .build();
        }

        /**
         * 获取所有默认角色配置
         */
        public static List<MasRoleConfig> all() {
            return List.of(
                    manager(),
                    pm(),
                    expert(),
                    executor(),
                    reviewer()
            );
        }
    }
}
