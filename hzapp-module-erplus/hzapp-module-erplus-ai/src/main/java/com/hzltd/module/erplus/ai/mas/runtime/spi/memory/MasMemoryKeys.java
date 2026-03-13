package com.hzltd.module.erplus.ai.mas.runtime.spi.memory;

/**
 * Standardized constants for MAS Memory keys to prevent hardcoding strings 
 * and ensure consistency across Orchestrator, Agents, and Execution Engine.
 */
public final class MasMemoryKeys {

    private MasMemoryKeys() {}

    /**
     * The macro user goal for the entire session. 
     * Replacing "goal" and "userGoal".
     */
    public static final String USER_GOAL = "userGoal";

    /**
     * The current phase/iteration number in the Workflow Orchestrator loop.
     * Replacing "phaseCount" and "_phaseCount".
     */
    public static final String PHASE_COUNT = "phaseCount";

    /**
     * Aggregated execution history across all phases, formatted for LLM consumption.
     */
    public static final String GLOBAL_HISTORY = "globalHistory";

    /**
     * The specific task instruction/prompt for a sub-agent execution.
     */
    public static final String TASK_INSTRUCTION = "taskInstruction";

    /**
     * Strategic instructions or constraints derived from MAS Skills.
     */
    public static final String STRATEGY_INSTRUCTION = "strategy_instruction";

    /**
     * Helper to generate a key for a node's execution result.
     * @param nodeId Unique node ID
     * @return Generated key: {nodeId}_result
     */
    public static String nodeResultKey(String nodeId) {
        return nodeId + "_result";
    }

    /**
     * Helper to generate a key for a specific agent's output for a node.
     * @param nodeId Unique node ID
     * @param role Agent role code
     * @return Generated key: {nodeId}_{role}_output
     */
    public static String agentOutputKey(String nodeId, String role) {
        return nodeId + "_" + role + "_output";
    }
}
