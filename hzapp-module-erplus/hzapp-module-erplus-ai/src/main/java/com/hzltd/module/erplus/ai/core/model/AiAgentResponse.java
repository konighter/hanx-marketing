package com.hzltd.module.erplus.ai.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * AI Agent 响应模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentResponse {

    /**
     * AI 生成的内容
     */
    private String content;

    /**
     * 响应元数据
     */
    private Map<String, Object> metadata;

    /**
     * 是否成功
     */
    @Builder.Default
    private boolean success = true;

    /**
     * 错误消息
     */
    private String message;

}
