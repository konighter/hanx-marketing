package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionVariableDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionVariableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Orchestrates session-wide memory, including persistence to the database and pruning.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasMemoryService {

    private final MasSessionVariableMapper sessionVariableMapper;
    
    // In-memory cache for active sessions: sessionId -> GlobalSessionMemory
    private final Map<String, GlobalSessionMemory> activeSessions = new ConcurrentHashMap<>();

    public java.util.Set<String> getActiveSessionIds() {
        return activeSessions.keySet();
    }

    /**
     * Retrieves or initializes the global memory for a session.
     */
    public GlobalSessionMemory getSessionMemory(String sessionId) {
        return activeSessions.computeIfAbsent(sessionId, id -> {
            log.info("[Memory] Initializing session memory from DB for: {}", id);
            GlobalSessionMemory memory = new GlobalSessionMemory(id);
            loadFromDb(id, memory);
            return memory;
        });
    }

    /**
     * Persists all current memory state to the database and checks for pruning.
     */
    public void saveToDb(String sessionId) {
        GlobalSessionMemory memory = activeSessions.get(sessionId);
        if (memory == null) return;

        Map<String, Object> snapshot = memory.snapshot();
        for (Map.Entry<String, Object> entry : snapshot.entrySet()) {
            saveVariable(sessionId, entry.getKey(), entry.getValue());
        }
        
        // Phase 7: Context Pruning
        pruneContext(sessionId);
    }

    private void pruneContext(String sessionId) {
        // threshold: 10,000 characters for demo. In reality, would be token-based.
        int threshold = 10000;
        
        List<MasSessionVariableDO> activeVars = sessionVariableMapper.selectList(
                new LambdaQueryWrapper<MasSessionVariableDO>()
                        .eq(MasSessionVariableDO::getSessionId, sessionId)
                        .ne(MasSessionVariableDO::getVarType, "ARCHIVED")
        );
        
        long totalSize = activeVars.stream()
                .mapToLong(v -> v.getVarValue() != null ? v.getVarValue().length() : 0)
                .sum();
        
        if (totalSize > threshold) {
            log.warn("[Memory] Session {} exceeds context threshold ({} > {}). Archiving oldest variables.", 
                    sessionId, totalSize, threshold);
            
            // Sort by update time (oldest first). BaseDO has getUpdateTime()
            List<MasSessionVariableDO> sorted = activeVars.stream()
                    .sorted(Comparator.comparing(MasSessionVariableDO::getUpdateTime))
                    .collect(Collectors.toList());
            
            while (totalSize > threshold && !sorted.isEmpty()) {
                MasSessionVariableDO victim = sorted.remove(0);
                victim.setVarType("ARCHIVED");
                sessionVariableMapper.updateById(victim);
                
                // Also remove from in-memory cache if present
                GlobalSessionMemory mem = activeSessions.get(sessionId);
                if (mem != null) {
                    // Logic to remove from memory could be added here
                    // However, we might want to keep it in memory but just mark it "Long-term"
                }

                totalSize -= victim.getVarValue().length();
                log.info("[Memory] Archived variable: {}", victim.getVarKey());
            }
        }
    }

    private void loadFromDb(String sessionId, GlobalSessionMemory memory) {
        List<MasSessionVariableDO> variables = sessionVariableMapper.selectList(
                new LambdaQueryWrapper<MasSessionVariableDO>()
                        .eq(MasSessionVariableDO::getSessionId, sessionId)
                        .ne(MasSessionVariableDO::getVarType, "ARCHIVED")
        );
        for (MasSessionVariableDO var : variables) {
            memory.put(var.getVarKey(), var.getVarValue());
        }
    }

    private void saveVariable(String sessionId, String key, Object value) {
        String valStr = String.valueOf(value);
        
        MasSessionVariableDO existing = sessionVariableMapper.selectOne(
                new LambdaQueryWrapper<MasSessionVariableDO>()
                        .eq(MasSessionVariableDO::getSessionId, sessionId)
                        .eq(MasSessionVariableDO::getVarKey, key)
        );

        if (existing != null) {
            existing.setVarValue(valStr);
            sessionVariableMapper.updateById(existing);
        } else {
            MasSessionVariableDO newVar = MasSessionVariableDO.builder()
                    .sessionId(sessionId)
                    .varKey(key)
                    .varValue(valStr)
                    .varType("STRING") // Default
                    .build();
            sessionVariableMapper.insert(newVar);
        }
    }
    
    /**
     * Clear session from memory (e.g., on cleanup or completion)
     */
    public void evictSession(String sessionId) {
        activeSessions.remove(sessionId);
        log.debug("[Memory] Evicted session {} from active memory", sessionId);
    }
}
