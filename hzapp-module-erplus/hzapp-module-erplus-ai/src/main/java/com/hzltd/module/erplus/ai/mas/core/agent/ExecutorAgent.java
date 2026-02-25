package com.hzltd.module.erplus.ai.mas.core.agent;

import com.hzltd.module.erplus.ai.mas.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.mas.framework.execution.MasContext;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 执行者智能体：负责利用工具执行具体的原子任务
 */
@Component
public class ExecutorAgent extends BaseMasAgent {

    public ExecutorAgent(ChatClient.Builder chatClientBuilder) {
        super(MasRole.EXECUTOR, "Task_Executor", chatClientBuilder);
    }

    @Override
    protected String getSystemPrompt(MasContext context) {
        return """
                你是一个专业的任务执行者 (Task Executor)。
                你的职责是：
                1. 接收具体的原子任务指令。
                2. 观察当前可用的工具，并在必要时调用它们以获取实时数据或执行操作。
                3. 根据工具返回的结果和任务指令，产出最终的执行报告。
                
                请确保在调用工具后，能够正确整合信息。
                """;
    }
}
