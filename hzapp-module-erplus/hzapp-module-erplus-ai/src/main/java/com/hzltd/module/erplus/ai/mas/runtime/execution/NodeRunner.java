package com.hzltd.module.erplus.ai.mas.runtime.execution;

import com.hzltd.module.erplus.ai.mas.runtime.memory.NodeMemory;

/**
 * SPI (Strategy Pattern Interface) for executing a graph node's core logic.
 * <p>
 * Implementations decouple the execution engine from any specific AI framework.
 * For example:
 * <ul>
 *   <li>{@code AdkNodeRunner} — delegates to Google ADK LlmAgent</li>
 *   <li>{@code AgentScopeNodeRunner} — delegates to other agent frameworks</li>
 * </ul>
 */
public interface NodeRunner {

    /**
     * Execute the core logic for a graph node.
     *
     * @param node   The graph node to execute.
     * @param memory The scoped memory for this execution.
     * @return The execution result as a string.
     * @throws Exception if execution fails.
     */
    String run(GraphNode node, NodeMemory memory) throws Exception;
}
