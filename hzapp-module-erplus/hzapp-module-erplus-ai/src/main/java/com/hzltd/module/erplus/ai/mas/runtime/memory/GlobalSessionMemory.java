package com.hzltd.module.erplus.ai.mas.runtime.memory;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared context across the entire MAS Session.
 * Accessible by all chained and parallel nodes. Contains the original user goal,
 * globally shared variables, and the finalized outputs of completed nodes.
 *
 * <h3>Phase 3 changes</h3>
 * <ul>
 *   <li>Removed {@code sessionEvents} (ADK Event list) — events are now stored
 *       as text summaries under {@code _adk_history_{nodeId}} keys in the global context.</li>
 *   <li>Added {@link #getDirtyKeys()} for incremental DB persistence.</li>
 * </ul>
 */
public class GlobalSessionMemory {

    private final String sessionId;
    /** Thread-safe map to store session-wide variables. */
    private final Map<String, Object> globalContext = new ConcurrentHashMap<>();

    /** Keys that have been written to since last flush. Used for incremental persistence. */
    private final Set<String> dirtyKeys = ConcurrentHashMap.newKeySet();

    private CollisionStrategy defaultStrategy = CollisionStrategy.OVERWRITE;

    public GlobalSessionMemory(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setDefaultStrategy(CollisionStrategy strategy) {
        this.defaultStrategy = strategy;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void put(String key, Object value) {
        put(key, value, defaultStrategy);
    }

    public void put(String key, Object value, CollisionStrategy strategy) {
        if (key == null || value == null) return;

        globalContext.compute(key, (k, existing) -> {
            if (existing == null) {
                return value;
            }

            switch (strategy) {
                case STRICT:
                    throw new IllegalStateException("Duplicate key in GlobalSessionMemory (STRICT): " + key);
                case MERGE:
                    if (existing instanceof Map && value instanceof Map) {
                        Map<Object, Object> merged = new HashMap<>((Map<?, ?>) existing);
                        merged.putAll((Map<?, ?>) value);
                        return merged;
                    }
                    return value; // fall back to overwrite if not maps
                case OVERWRITE:
                default:
                    return value;
            }
        });
        dirtyKeys.add(key);
    }

    public Object get(String key) {
        return key != null ? globalContext.get(key) : null;
    }

    public boolean containsKey(String key) {
        return key != null && globalContext.containsKey(key);
    }

    /**
     * Return a shallow copy of the current state.
     */
    public Map<String, Object> snapshot() {
        return new ConcurrentHashMap<>(globalContext);
    }

    // ---- Dirty-key tracking for incremental persistence ----

    /**
     * Returns the set of keys that have been modified since the last
     * {@link #clearDirtyKeys()} call.
     */
    public Set<String> getDirtyKeys() {
        return Collections.unmodifiableSet(dirtyKeys);
    }

    /**
     * Resets the dirty-key set. Should be called after a successful persistence flush.
     */
    public void clearDirtyKeys() {
        dirtyKeys.clear();
    }
}
