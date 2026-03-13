package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionVariableDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionVariableMapper;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryEntry;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Orchestrates session-wide memory, including persistence to the database and pruning.
 * Now implements MasMemoryManager to provide framework-agnostic memory services.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasMemoryService implements MasMemoryManager {

    private final MasSessionVariableMapper sessionVariableMapper;
    
    // In-memory cache for active sessions: sessionId -> GlobalSessionMemory
    private final Map<String, GlobalSessionMemory> activeSessions = new ConcurrentHashMap<>();

    /**
     * Extended word pattern supporting:
     * - Latin letters (A-Za-z)
     * - CJK Unified Ideographs (\\u4e00-\\u9fff)
     * - Digits (0-9)
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fff\\w]+");

    public java.util.Set<String> getActiveSessionIds() {
        return activeSessions.keySet();
    }

    /**
     * Retrieves or initializes the global memory for a session.
     */
    @Override
    public MasSessionMemory getSessionMemory(String sessionId) {
        return activeSessions.computeIfAbsent(sessionId, id -> {
            log.info("[Memory] Initializing session memory from DB for: {}", id);
            GlobalSessionMemory memory = new GlobalSessionMemory(id);
            loadFromDb(id, memory);
            return memory;
        });
    }

    /**
     * Persists only the modified (dirty) keys to the database, then clears the dirty set.
     */
    @Override
    public void saveToDb(String sessionId) {
        GlobalSessionMemory memory = activeSessions.get(sessionId);
        if (memory == null) return;

        Set<String> dirty = memory.getDirtyKeys();
        if (dirty.isEmpty()) {
            log.debug("[Memory] No dirty keys for session {}. Skipping DB flush.", sessionId);
            return;
        }

        log.info("[Memory] Flushing {} dirty keys for session {}", dirty.size(), sessionId);
        for (String key : dirty) {
            Object value = memory.get(key);
            if (value != null) {
                saveVariable(sessionId, key, value);
            }
        }
        memory.clearDirtyKeys();

        // Context Pruning
        pruneContext(sessionId);
    }

    /**
     * Core search logic moved from GraphMemoryService to be framework-agnostic.
     */
    @Override
    public List<MasMemoryEntry> searchMemory(String sessionId, String query) {
        log.debug("[Memory] Searching memory for query: '{}' in session: {}", query, sessionId);

        Set<String> wordsInQuery = extractWords(query);
        List<MasMemoryEntry> matchingMemories = new ArrayList<>();

        // Get snapshot of session memory
        MasSessionMemory sessionMemory = getSessionMemory(sessionId);
        Map<String, Object> variables = sessionMemory.snapshot();

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            Set<String> wordsInVar = extractWords(key + " " + value);
            if (wordsInVar.isEmpty()) continue;

            if (!Collections.disjoint(wordsInQuery, wordsInVar)) {
                matchingMemories.add(MasMemoryEntry.builder()
                        .content("Variable [" + key + "]: " + value)
                        .author("MAS-Graph-State")
                        .timestamp(Instant.now().toString())
                        .build());
            }
        }

        log.info("[Memory] Search in session {} found {} matching memory entries", sessionId, matchingMemories.size());
        return matchingMemories;
    }

    private void pruneContext(String sessionId) {
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
            
            List<MasSessionVariableDO> sorted = activeVars.stream()
                    .sorted(Comparator.comparing(MasSessionVariableDO::getUpdateTime))
                    .collect(Collectors.toList());
            
            while (totalSize > threshold && !sorted.isEmpty()) {
                MasSessionVariableDO victim = sorted.remove(0);
                victim.setVarType("ARCHIVED");
                sessionVariableMapper.updateById(victim);
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
                    .varType("STRING") 
                    .build();
            sessionVariableMapper.insert(newVar);
        }
    }
    
    private Set<String> extractWords(String text) {
        if (text == null) return Collections.emptySet();
        Set<String> words = new HashSet<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return words;
    }

    public void evictSession(String sessionId) {
        activeSessions.remove(sessionId);
        log.debug("[Memory] Evicted session {} from active memory", sessionId);
    }
}
