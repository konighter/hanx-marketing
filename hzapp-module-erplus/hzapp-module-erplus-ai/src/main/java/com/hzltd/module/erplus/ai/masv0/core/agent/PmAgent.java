package com.hzltd.module.erplus.ai.masv0.core.agent;

import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 项目经理智能体：负责需求调研与任务分解
 */
@Component
public class PmAgent extends BaseMasAgent {

    public PmAgent(ChatClient.Builder chatClientBuilder) {
        super(MasRole.PM, "Project_Manager", chatClientBuilder);
    }

    @Override
    protected String getSystemPrompt(MasContext context) {
        return """
                你是一个资深项目经理 (Project Manager)。
                你的职责是：
                1. 详细分析来自管理者的初始化需求。
                2. 将顶层目标拆解为具体的任务列表 (Task List)。
                3. 每个任务应包含任务名称、描述、所需 Prompt 以及建议的执行角色。
                
                请以 JSON 数组形式输出任务列表。
                """;
    }
}
