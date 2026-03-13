package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.NodeMemory;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of NodeMemory that keeps local state in an in-memory map
 * and can optionally merge changes back to the global MasSessionMemory.
 */
public class LocalNodeMemory implements NodeMemory {

    private final String nodeId;
    private final String sessionId;
    
    @Getter
    private final MasSessionMemory globalMemory;
    
    @Getter
    private final Map<String, Object> localMap = new HashMap<>();

    public LocalNodeMemory(String nodeId, MasSessionMemory globalMemory) {
        this.nodeId = nodeId;
        this.globalMemory = globalMemory;
        this.sessionId = globalMemory instanceof GlobalSessionMemory 
                ? ((GlobalSessionMemory) globalMemory).getSessionId() 
                : "unknown";
    }

    @Override
    public Object get(String key) {
        if (localMap.containsKey(key)) {
            return localMap.get(key);
        }
        return globalMemory.get(key);
    }

    @Override
    public void put(String key, Object value) {
        localMap.put(key, value);
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public Map<String, Object> asMap() {
        return localMap; 
    }

    public void mergeToGlobal() {
        localMap.forEach(globalMemory::put);
    }
}
