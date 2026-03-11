package com.hzltd.module.erplus.ai.masv0.framework.agent;

import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class ConfiguredMasAgent implements MasAgent {

    private final MasRole role;
    private final String name;
    private final MasRoleConfig config;
    private final ChatClient chatClient;
    private final List<String> defaultTools;
    private final Map<String, Object> runtimeContext = new ConcurrentHashMap<>();

    public ConfiguredMasAgent(MasRoleConfig config, ChatClient.Builder chatClientBuilder) {
        this.config = config;
        this.name = config.getName();
        
        MasRole assignedRole;
        try {
            assignedRole = MasRole.valueOf(config.getRoleId());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid roleId: {}, using null role", config.getRoleId());
            assignedRole = null;
        }
        this.role = assignedRole;
        
        this.defaultTools = config.getDefaultTools();
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Mono<String> handle(MasTask task, MasContext context) {
        return Mono.fromCallable(() -> {
            String sessionId = context.getSessionId();
            String systemPrompt = renderPrompt(
                    config.getSystemPromptTemplate(),
                    sessionId,
                    context.getVariable("TOP_GOAL"),
                    task.getPrompt()
            );

            var spec = chatClient.prompt()
                    .system(systemPrompt)
                    .user(task.getPrompt());

            List<String> tools = task.getTools();
            if (tools != null && !tools.isEmpty()) {
                spec.toolNames(tools.toArray(new String[0]));
            } else if (defaultTools != null && !defaultTools.isEmpty()) {
                spec.toolNames(defaultTools.toArray(new String[0]));
            }

            String response = spec.call().content();
            return response;
        });
    }

    private String renderPrompt(String template, String sessionId, String goal, String currentTask) {
        if (template == null) return "";
        String result = template;
        result = result.replace("{{sessionId}}", sessionId != null ? sessionId : "");
        result = result.replace("{{goal}}", goal != null ? goal : "");
        result = result.replace("{{currentTask}}", currentTask != null ? currentTask : "");
        result = result.replace("{{currentTaskResult}}", "");
        return result;
    }

    public void setRuntimeContext(String key, Object value) {
        runtimeContext.put(key, value);
    }

    public Object getRuntimeContext(String key) {
        return runtimeContext.get(key);
    }
}
