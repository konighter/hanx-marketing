package com.hzltd.module.erplus.ai.core.agent;

import com.hzltd.module.erplus.ai.core.model.AiAgentRequest;
import com.hzltd.module.erplus.ai.core.model.AiAgentResponse;
import reactor.core.publisher.Mono;

/**
 * AI Agent 接口定义
 */
public interface AiAgent {

    /**
     * 获取 Agent 名称
     */
    String getName();

    /**
     * 获取 Agent 描述
     */
    String getDescription();

    /**
     * 执行 Agent 逻辑
     *
     * @param request 请求数据
     * @return 响应数据
     */
    AiAgentResponse execute(AiAgentRequest request);

    /**
     * 异步执行 Agent 逻辑
     *
     * @param request 请求数据
     * @return 响应数据流程
     */
    default Mono<AiAgentResponse> executeAsync(AiAgentRequest request) {
        return Mono.fromCallable(() -> execute(request));
    }

}
