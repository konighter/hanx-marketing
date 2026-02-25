package com.hzltd.module.erplus.ai.core.service;

import com.hzltd.module.erplus.ai.core.agent.AiAgent;
import com.hzltd.module.erplus.ai.core.model.AiAgentRequest;
import com.hzltd.module.erplus.ai.core.model.AiAgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Agent 编排服务
 */
@Slf4j
@Service
public class AiAgentService {

    private final Map<String, AiAgent> agents = new ConcurrentHashMap<>();

    public AiAgentService(List<AiAgent> agentList) {
        agentList.forEach(agent -> {
            log.info("[AiAgentService] 注册 Agent: {} - {}", agent.getName(), agent.getDescription());
            agents.put(agent.getName(), agent);
        });
    }

    /**
     * 获取所有可用的 Agent
     */
    public Map<String, AiAgent> getAgents() {
        return agents;
    }

    /**
     * 执行指定名称的 Agent
     *
     * @param agentName Agent 名称
     * @param request   请求数据
     * @return 响应数据
     */
    public AiAgentResponse execute(String agentName, AiAgentRequest request) {
        return Optional.ofNullable(agents.get(agentName))
                .map(agent -> {
                    log.info("[AiAgentService] 开始执行 Agent: {}", agentName);
                    return agent.execute(request);
                })
                .orElseGet(() -> {
                    log.error("[AiAgentService] 未找到名称为 {} 的 Agent", agentName);
                    return AiAgentResponse.builder()
                            .success(false)
                            .message("Agent not found: " + agentName)
                            .build();
                });
    }

}
