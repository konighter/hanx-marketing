package com.hzltd.module.erplus.ai.mas.runtime.execution;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Abstraction for LLM interaction within the ReAct loop.
 * <p>
 * This decouples the ReActNodeRunner from any specific LLM SDK (ADK, OpenAI, etc.).
 * Implementations translate between the generic message format and the specific SDK.
 */
public interface ReActLlmClient {

    /**
     * Generate a response given a conversation history.
     *
     * @param messages The conversation messages (system, user, assistant).
     * @return The LLM's raw text response.
     */
    String generate(List<Message> messages);

    /**
     * A simple message representation for the conversation.
     */
    @Data
    @AllArgsConstructor
    class Message {
        private String role;  // "system", "user", "assistant"
        private String content;

        public static Message system(String content) {
            return new Message("system", content);
        }

        public static Message user(String content) {
            return new Message("user", content);
        }

        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }
}
