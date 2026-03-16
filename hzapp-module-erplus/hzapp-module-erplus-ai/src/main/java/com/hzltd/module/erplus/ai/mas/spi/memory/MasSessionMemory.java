package com.hzltd.module.erplus.ai.mas.spi.memory;

import java.util.Map;

/**
 * Agnostic interface for KV-based session memory.
 */
public interface MasSessionMemory {
    
    void put(String key, Object value);

    void put(String key, Object value, CollisionStrategy strategy);
    
    Object get(String key);

    boolean containsKey(String key);
    
    void remove(String key);
    
    Map<String, Object> snapshot();
    
    String getSessionId();
}
