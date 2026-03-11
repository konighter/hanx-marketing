package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.CustomAgentLoaderService;
import com.hzltd.module.erplus.ai.mas.runtime.loop.GraphNode;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagPlanNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DagParserUtil {

    private final CustomAgentLoaderService agentLoaderService;

    @Autowired
    public DagParserUtil(CustomAgentLoaderService agentLoaderService) {
        this.agentLoaderService = agentLoaderService;
    }

    /**
     * Converts a JSON schema formulation (DagGenerationPlan) into engine-executable GraphNodes.
     */
    public List<GraphNode> parse(DagGenerationPlan plan) {
        List<GraphNode> graphNodes = new ArrayList<>();
        if (plan == null || plan.getNodes() == null || plan.getNodes().isEmpty()) {
            return graphNodes;
        }

        // 1. Create nodes dynamically based on generated plan
        for (DagPlanNode planNode : plan.getNodes()) {
            String role = planNode.getAgentRole();
            BaseAgent agent = agentLoaderService.getAgentByRole(role);
            if (agent == null) {
                log.warn("[DagParserUtil] Requested agent role '{}' doesn't exist. Skipping node {}", role, planNode.getId());
                continue;
            }

            GraphNode node = new GraphNode(planNode.getId(), agent);
            
            // Setup dependencies pointing to loopIds from other nodes
            if (planNode.getDependsOn() != null) {
                for (String dependencyId : planNode.getDependsOn()) {
                     node.addDependency(dependencyId);
                }
            }

            graphNodes.add(node);
            log.debug("[DagParserUtil] Created GraphNode {} with Agent {} requires {}", node.getLoopId(), role, node.getRequires());
        }

        return graphNodes;
    }
}
