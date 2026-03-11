package com.hzltd.module.erplus.ai.mas.runtime.memory;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared context across the entire MAS Session.
 * Accessible by all chained and parallel loops. Contains the original user goal, 
 * globally shared variables, and the finalized outputs of completed loops. 
 */
public class GlobalSessionMemory {

    private final String sessionId;
    // Thread-safe map to store session-wide variables
    private final Map<String, Object> globalContext = new ConcurrentHashMap<>();
    private final java.util.List<com.google.adk.events.Event> sessionEvents = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
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
    }

    public Object get(String key) {
        return key != null ? globalContext.get(key) : null;
    }

    public java.util.List<com.google.adk.events.Event> getSessionEvents() {
        return sessionEvents;
    }

    public boolean containsKey(String key) {
        return key != null && globalContext.containsKey(key);
    }

    public Map<String, Object> snapshot() {
        // Return a shallow copy of the current state
        return new ConcurrentHashMap<>(globalContext);
    }
}
