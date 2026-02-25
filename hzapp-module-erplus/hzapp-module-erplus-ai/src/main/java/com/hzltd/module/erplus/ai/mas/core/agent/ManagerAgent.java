package com.hzltd.module.erplus.ai.mas.core.agent;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 管理者智能体：负责场景初始化、角色指派与决策调整
 */
@Component
public class ManagerAgent extends BaseMasAgent {

    public ManagerAgent(ChatClient.Builder chatClientBuilder) {
        super(MasRole.MANAGER, "General_Manager", chatClientBuilder);
    }

    @Override
    protected String getSystemPrompt(MasContext context) {
        return """
                你是一个高级项目管理者 (General Manager)。
                你的职责是：
                1. 理解用户的顶层目标。
                2. 确定需要参与的 Agent 角色 (MANAGER, PM, EXPERT, EXECUTOR, REVIEWER)。
                3. 定义项目流程和阶段。
                4. 监听后续的任务状态和用户反馈，并做出决策。
                
                请以结构化的方式输出决策，例如：
                {
                  "action": "INIT_SUCCESS",
                  "roles": ["MANAGER", "PM", "EXECUTOR"],
                  "next_step": "PLANNING"
                }
                """;
    }
}
