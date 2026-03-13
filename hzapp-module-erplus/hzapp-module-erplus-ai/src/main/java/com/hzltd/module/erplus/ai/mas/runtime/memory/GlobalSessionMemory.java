package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.CollisionStrategy;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global Memory for a single Session.
 * Currently uses ConcurrentHashMap for thread-safety.
 * Now implements MasSessionMemory interface.
 */
@Slf4j
public class GlobalSessionMemory implements MasSessionMemory {

    @Getter
    private final String sessionId;
    
    // Internal KV storage
    private final Map<String, Object> storage = new ConcurrentHashMap<>();

    // Tracks which keys were modified since the last DB flush
    private final Set<String> dirtyKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public GlobalSessionMemory(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, CollisionStrategy.OVERWRITE);
    }

    @Override
    public void put(String key, Object value, CollisionStrategy strategy) {
        if (key == null || value == null) return;

        if (strategy == CollisionStrategy.ERROR && storage.containsKey(key)) {
            throw new IllegalStateException("Collision detected for key: " + key);
        }

        if (strategy == CollisionStrategy.IGNORE && storage.containsKey(key)) {
            log.debug("[Memory] Ignoring write for existing key: {}", key);
            return;
        }

        storage.put(key, value);
        dirtyKeys.add(key);
    }

    @Override
    public Object get(String key) {
        return storage.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public void remove(String key) {
        if (storage.remove(key) != null) {
            dirtyKeys.add(key); // Mark as dirty so DB can potentially delete if logically handled
        }
    }

    @Override
    public Map<String, Object> snapshot() {
        return new HashMap<>(storage);
    }

    public Set<String> getDirtyKeys() {
        return Collections.unmodifiableSet(dirtyKeys);
    }

    public void clearDirtyKeys() {
        dirtyKeys.clear();
    }
}
