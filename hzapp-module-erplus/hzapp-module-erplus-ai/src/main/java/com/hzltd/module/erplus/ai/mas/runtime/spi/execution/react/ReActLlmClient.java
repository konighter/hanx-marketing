package com.hzltd.module.erplus.ai.mas.runtime.spi.execution.react;

import java.util.List;

/**
 * Generic interface for an LLM that can participate in a ReAct loop.
 */
public interface ReActLlmClient {

    /**
     * Generate a response based on the conversation history.
     */
    String generate(List<Message> messages);

    /**
     * Internal message structure.
     */
    class Message {
        public String role;
        public String content;

        public static Message system(String content) {
            Message m = new Message();
            m.role = "system";
            m.content = content;
            return m;
        }

        public static Message user(String content) {
            Message m = new Message();
            m.role = "user";
            m.content = content;
            return m;
        }

        public static Message assistant(String content) {
            Message m = new Message();
            m.role = "assistant";
            m.content = content;
            return m;
        }
    }
}
