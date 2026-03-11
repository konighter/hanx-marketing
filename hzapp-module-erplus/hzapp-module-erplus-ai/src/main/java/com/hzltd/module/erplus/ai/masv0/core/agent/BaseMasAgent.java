package com.hzltd.module.erplus.ai.masv0.core.agent;

import com.hzltd.module.erplus.ai.masv0.framework.agent.MasAgent;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Mono;

/**
 * MAS Agent 抽象基类，集成 Spring AI ChatClient
 */
@Slf4j
public abstract class BaseMasAgent implements MasAgent {

    @Getter
    private final MasRole role;
    
    @Getter
    private final String name;
    
    protected final ChatClient chatClient;

    protected BaseMasAgent(MasRole role, String name, ChatClient.Builder chatClientBuilder) {
        this.role = role;
        this.name = name;
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Mono<String> handle(MasTask task, MasContext context) {
        return Mono.fromCallable(() -> {
            ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                    .system(getSystemPrompt(context))
                    .user(task.getPrompt());
            
            // 动态注入工具
            if (task.getTools() != null && !task.getTools().isEmpty()) {
                String[] toolArray = task.getTools().toArray(new String[0]);
                spec.toolNames(toolArray);
            }

            String response = spec.call().content();
            log.debug("MAS Agent {} handle task {}, response: {}", name, task, response);
            return response;
        });
    }

    /**
     * 获取系统提示词，子类可根据 context 动态调整
     */
    protected abstract String getSystemPrompt(MasContext context);
}
