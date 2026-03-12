package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.memory.NodeMemory;
import lombok.extern.slf4j.Slf4j;

/**
 * Default NodeRunner implementation that delegates execution to a {@link BaseAgent}.
 * <p>
 * This runner bridges the MAS graph execution engine with the Google ADK-based
 * agent execution layer. It simply invokes {@code agent.execute(memory)}.
 */
@Slf4j
public class AdkNodeRunner implements NodeRunner {

    @Override
    public String run(GraphNode node, NodeMemory memory) throws Exception {
        BaseAgent agent = node.getAgent();
        if (agent == null) {
            throw new IllegalStateException("GraphNode " + node.getNodeId() + " has no assigned agent");
        }
        log.info("[AdkNodeRunner] Delegating execution to agent: {} for node: {}", 
                agent.getRoleName(), node.getNodeId());
        return agent.execute(memory);
    }
}
