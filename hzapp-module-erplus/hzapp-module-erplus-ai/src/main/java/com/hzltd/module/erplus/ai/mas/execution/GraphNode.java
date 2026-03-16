package com.hzltd.module.erplus.ai.mas.execution;

import com.hzltd.module.erplus.ai.mas.agent.BaseAgent;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in the execution graph (DAG).
 */
@Data
public class GraphNode {
    /**
     * Node execution type.
     */
    public enum NodeType {
        /** Simple single-call execution (AdkNodeRunner). */
        SIMPLE,
        /** ReAct loop execution with tool use (ReActNodeRunner). */
        REACT
    }

    private final String nodeId;
    private final BaseAgent agent;
    
    // Dependencies that must complete before this node can run
    private final Set<String> requires = new HashSet<>();
    
    /**
     * Execution type: SIMPLE (default) or REACT (with tool-use loop).
     */
    private NodeType nodeType = NodeType.SIMPLE;

    /**
     * Set of tool names available to this node when nodeType=REACT.
     * If null or empty, all registered tools are available.
     */
    private Set<String> toolSet;

    /**
     * Optional: The role that must review this node's output.
     */
    private String reviewerRole;
    private long completionTimeoutMs = 300000; // Default 5 minutes
    private RetryPolicy retryPolicy;

    // --- Execution Tracking ---
    private NodeStatus status = NodeStatus.PENDING;
    private String result;
    private long startTime;
    private long endTime;

    public GraphNode(String nodeId, BaseAgent agent) {
        this.nodeId = nodeId;
        this.agent = agent;
    }

    public void addDependency(String requiredNodeId) {
        this.requires.add(requiredNodeId);
    }

    /**
     * Enum for status tracking
     */
    public enum NodeStatus {
        PENDING,
        RUNNING,
        RETRYING,
        SUCCESS,
        FAILED,
        SKIPPED
    }
}
