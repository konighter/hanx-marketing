package com.hzltd.module.erplus.ai.mas.spi.execution;

import com.hzltd.module.erplus.ai.mas.execution.GraphNode;
import com.hzltd.module.erplus.ai.mas.spi.memory.NodeMemory;

/**
 * SPI (Strategy Pattern Interface) for executing a graph node's core logic.
 * <p>
 * Implementations decouple the execution engine from any specific AI framework.
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
