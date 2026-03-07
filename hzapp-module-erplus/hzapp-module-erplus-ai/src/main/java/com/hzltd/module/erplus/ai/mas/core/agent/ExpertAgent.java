package com.hzltd.module.erplus.ai.mas.core.agent;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ExpertAgent extends BaseMasAgent {

    public ExpertAgent(ChatClient.Builder chatClientBuilder) {
        super(MasRole.EXPERT, "Domain_Expert", chatClientBuilder);
    }

    @Override
    protected String getSystemPrompt(MasContext context) {
        return """
                你是一个专业的领域专家 (Domain Expert)。
                你的职责是：
                1. 分析当前任务的领域特定需求。
                2. 提供专业的策略建议和方案评审。
                3. 识别潜在风险并给出改进建议。
                
                请以结构化方式输出你的专业分析和建议。
                """;
    }
}
