package com.hzltd.module.erplus.ai.mas.runtime.memory;

import java.util.HashMap;
import java.util.Map;

/**
 * Context tightly bounded to a single AgentLoop execution.
 * Isolates scratchpads and intermediate results from parallel loops.
 * Reads fallback to GlobalSessionMemory. Writes stay local until merged.
 */
public class LocalLoopMemory implements LoopMemory {

    private final String loopId;
    private final GlobalSessionMemory globalSessionMemory;
    
    // Not thread-safe as it should only be accessed by the single loop thread executing it
    private final Map<String, Object> localContext = new HashMap<>();

    public LocalLoopMemory(String loopId, GlobalSessionMemory globalSessionMemory) {
        this.loopId = loopId;
        this.globalSessionMemory = globalSessionMemory;
    }

    public GlobalSessionMemory getGlobalSessionMemory() {
        return globalSessionMemory;
    }

    @Override
    public String getLoopId() {
        return loopId;
    }

    @Override
    public String getSessionId() {
        return globalSessionMemory != null ? globalSessionMemory.getSessionId() : "unknown";
    }

    @Override
    public Object get(String key) {
        if (key == null) return null;
        // Check local first
        if (localContext.containsKey(key)) {
            return localContext.get(key);
        }
        // Fallback to global
        if (globalSessionMemory != null) {
            return globalSessionMemory.get(key);
        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        if (key != null && value != null) {
            localContext.put(key, value);
        }
    }

    /**
     * Commits all local bounded variables into the global session memory.
     * Typically called when the loop successfully completes.
     */
    public void mergeToGlobal() {
        if (globalSessionMemory != null) {
            localContext.forEach(globalSessionMemory::put);
        }
    }

    /**
     * Clears local scratchpad.
     */
    public void clearLocal() {
        localContext.clear();
    }
    
    public Map<String, Object> snapshotLocal() {
        return new HashMap<>(localContext);
    }

    @Override
    public Map<String, Object> asMap() {
        return snapshotLocal();
    }
}
