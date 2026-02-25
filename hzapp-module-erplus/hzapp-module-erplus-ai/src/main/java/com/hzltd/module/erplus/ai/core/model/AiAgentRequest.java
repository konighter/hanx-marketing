package com.hzltd.module.erplus.ai.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * AI Agent 请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentRequest {

    /**
     * 用户输入的提示词
     */
    private String prompt;

    /**
     * 上下文数据
     */
    private Map<String, Object> context;

    /**
     * 会话ID，用于追踪多轮对话
     */
    private String sessionId;

}
