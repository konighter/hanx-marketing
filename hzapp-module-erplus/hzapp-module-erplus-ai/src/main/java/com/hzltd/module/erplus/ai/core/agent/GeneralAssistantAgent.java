package com.hzltd.module.erplus.ai.core.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 通用助手 Agent
 */
@Component
public class GeneralAssistantAgent extends BaseAiAgent {

    public GeneralAssistantAgent(ChatClient.Builder chatClientBuilder) {
        super("GeneralAssistant", 
              "一个通用的 AI 助手，可以回答问题并使用工具。", 
              chatClientBuilder, 
              "getCurrentDateTime");
    }

    @Override
    protected String getSystemPrompt() {
        return "你是一个专业的 AI 助手。你可以回答用户的问题，并且在需要时可以调用工具获取实时信息。如果是获取当前时间，请调用 getCurrentDateTime 工具。";
    }
}
