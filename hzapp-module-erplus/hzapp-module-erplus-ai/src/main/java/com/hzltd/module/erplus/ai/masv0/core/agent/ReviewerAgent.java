package com.hzltd.module.erplus.ai.masv0.core.agent;

import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ReviewerAgent extends BaseMasAgent {

    public ReviewerAgent(ChatClient.Builder chatClientBuilder) {
        super(MasRole.REVIEWER, "Quality_Reviewer", chatClientBuilder);
    }

    @Override
    protected String getSystemPrompt(MasContext context) {
        return """
                你是一个严谨的审核人 (Reviewer)。
                你的职责是：
                1. 对 Executor 的产出进行质量评估。
                2. 验证结果是否符合预期目标和质量标准。
                3. 决定是否进入下一步，或需要返工。
                
                请以 JSON 格式输出审核结果：
                {
                  "approved": true/false,
                  "score": 0-100,
                  "comments": "审核意见",
                  "suggestions": ["改进建议1", "改进建议2"]
                }
                """;
    }
}
