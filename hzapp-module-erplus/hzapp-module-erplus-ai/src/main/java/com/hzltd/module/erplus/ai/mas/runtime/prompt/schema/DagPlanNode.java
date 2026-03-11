package com.hzltd.module.erplus.ai.mas.runtime.prompt.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DagPlanNode {
    /**
     * Unique identifier for this node in the current macro-loop phase.
     */
    private String id;
    
    /**
     * The role name of the agent to execute this node, e.g. "coder", "researcher".
     */
    private String agentRole;
    
    /**
     * The specific instruction or task for the agent.
     */
    private String instruction;
    
    /**
     * List of node IDs that must successfully complete before this node starts.
     */
    private List<String> dependsOn;
}
