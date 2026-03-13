package com.hzltd.module.erplus.ai.mas.runtime.spi.memory;

import java.util.List;

/**
 * System-level interface for memory management.
 * High-level components should depend on this interface instead of concrete implementations.
 */
public interface MasMemoryManager {

    /**
     * Retrieves the KV-based session memory for a given ID.
     */
    MasSessionMemory getSessionMemory(String sessionId);

    /**
     * Persists session memory to database.
     */
    void saveToDb(String sessionId);

    /**
     * Agnostic search method for memory retrieval.
     */
    List<MasMemoryEntry> searchMemory(String sessionId, String query);
}
