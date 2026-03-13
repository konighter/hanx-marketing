package com.hzltd.module.erplus.ai.mas.runtime.spi.memory;

import java.util.Map;

/**
 * Scoped local memory for a single graph node execution.
 * Isolates scratchpads and intermediate results from parallel nodes.
 */
public interface NodeMemory {

    /**
     * Get a value from the node's local context.
     */
    Object get(String key);

    /**
     * Put a value into the node's local context.
     */
    void put(String key, Object value);

    /**
     * Get the unique ID of this execution node.
     */
    String getNodeId();

    /**
     * Get the unique ID of the MAS session.
     */
    String getSessionId();

    /**
     * Get all local variables as a map.
     */
    Map<String, Object> asMap();
}
