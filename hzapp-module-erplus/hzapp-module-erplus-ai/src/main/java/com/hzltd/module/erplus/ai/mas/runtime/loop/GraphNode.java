package com.hzltd.module.erplus.ai.mas.runtime.loop;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in the execution graph.
 */
@Data
public class GraphNode {
    private final String loopId;
    private final BaseAgent agent;
    
    // Dependencies that must complete before this node can run
    private final Set<String> requires = new HashSet<>();
    
    /**
     * Optional: The role that must review this node's output.
     */
    private String reviewerRole;
    private long completionTimeoutMs = 300000; // Default 5 minutes
    private RetryPolicy retryPolicy;

    // --- Graph Structure & Hierarchy ---
    private NodeType type = NodeType.SINGLE;
    private String parentId; // For GroupNode support

    // --- Execution Tracking ---
    private NodeStatus status = NodeStatus.PENDING;
    private String result;
    private long startTime;
    private long endTime;

    public GraphNode(String loopId, BaseAgent agent) {
        this.loopId = loopId;
        this.agent = agent;
    }

    public void addDependency(String requiredLoopId) {
        this.requires.add(requiredLoopId);
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

    public enum NodeType {
        SINGLE,
        GROUP
    }
}
