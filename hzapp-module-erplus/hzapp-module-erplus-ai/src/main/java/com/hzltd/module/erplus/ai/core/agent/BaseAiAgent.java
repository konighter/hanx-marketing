package com.hzltd.module.erplus.ai.core.agent;

import com.hzltd.module.erplus.ai.core.model.AiAgentRequest;
import com.hzltd.module.erplus.ai.core.model.AiAgentResponse;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;

/**
 * AI Agent 基础实现类
 */
public abstract class BaseAiAgent implements AiAgent {

    @Getter
    protected final String name;
    
    @Getter
    protected final String description;
    
    protected final ChatClient chatClient;

    protected BaseAiAgent(String name, String description, ChatClient.Builder chatClientBuilder, String... tools) {
        this.name = name;
        this.description = description;
        
        chatClientBuilder.defaultSystem(getSystemPrompt());
        
        if (tools != null && tools.length > 0) {
            chatClientBuilder.defaultFunctions(tools);
        }
        
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 获取系统提示词
     */
    protected abstract String getSystemPrompt();

    @Override
    public AiAgentResponse execute(AiAgentRequest request) {
        try {
            String content = chatClient.prompt()
                    .user(request.getPrompt())
                    .call()
                    .content();
            
            return AiAgentResponse.builder()
                    .content(content)
                    .build();
        } catch (Exception e) {
            return AiAgentResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }
}
