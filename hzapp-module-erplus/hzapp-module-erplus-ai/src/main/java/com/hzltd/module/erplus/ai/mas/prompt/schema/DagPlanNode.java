package com.hzltd.module.erplus.ai.mas.prompt.schema;

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

    /**
     * Execution type: "SIMPLE" (default) or "REACT" (tool-use loop).
     * If null, defaults to SIMPLE.
     */
    private String nodeType;

    /**
     * Optional set of tool names available for REACT nodes.
     * If null or empty, all registered tools are available.
     */
    private List<String> toolSet;
}
