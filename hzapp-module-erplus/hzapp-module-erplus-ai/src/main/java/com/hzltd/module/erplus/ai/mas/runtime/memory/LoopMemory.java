package com.hzltd.module.erplus.ai.mas.runtime.memory;

import java.util.Map;
/**
 * Scoped local memory for a single AgentLoop execution.
 * Isolates scratchpads and intermediate results from parallel loops.
 */
public interface LoopMemory {

    /**
     * Get a value from the loop's local context.
     */
    Object get(String key);

    /**
     * Put a value into the loop's local context.
     */
    void put(String key, Object value);

    /**
     * Get the unique ID of this execution loop.
     */
    String getLoopId();

    /**
     * Get the unique ID of the MAS session.
     */
    String getSessionId();

    /**
     * Get all local variables as a map.
     */
    Map<String, Object> asMap();
}
