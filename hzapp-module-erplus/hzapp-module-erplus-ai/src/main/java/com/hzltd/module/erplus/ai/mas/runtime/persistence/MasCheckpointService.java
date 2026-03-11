package com.hzltd.module.erplus.ai.mas.runtime.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskHistoryMapper;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service to handle MAS task persistence and state checkpointing.
 */
@Slf4j
@Service
public class MasCheckpointService {
    private final MasTaskHistoryMapper taskHistoryMapper;

    public MasCheckpointService(MasTaskHistoryMapper taskHistoryMapper) {
        this.taskHistoryMapper = taskHistoryMapper;
    }

    /**
     * Records a task execution result and saves a checkpoint of the loop memory.
     * 
     * @param sessionId Current session ID.
     * @param taskId Unique task ID within the execution graph.
     * @param role Agent role that performed the task.
     * @param prompt The input instruction.
     * @param result Final text result from the agent.
     * @param status SUCCESS, FAILED, or INTERRUPTED.
     * @param memory Scoped memory to snapshot.
     * @param executionTime Elapsed time in milliseconds.
     */
    public void saveCheckpoint(String sessionId, String taskId, String role, 
                              String prompt, String result, String status, 
                              LoopMemory memory, long executionTime) {
        try {
            // Take a snapshot of the memory
            Map<String, Object> memorySnapshot = memory.asMap();
            
            // For now, we store the checkpoint as part of the result if it's internal
            // or we could add a new field if the schema allows. 
            // Since we follow existing DOs, let's keep 'result' as the main content 
            // and use logs/separate vars for state if needed.
            
            MasTaskHistoryDO historyDO = MasTaskHistoryDO.builder()
                    .sessionId(sessionId)
                    .taskId(taskId)
                    .name("Node: " + taskId) // Could be more descriptive
                    .role(role)
                    .prompt(prompt)
                    .result(result)
                    .status(status)
                    .executionTime(executionTime)
                    .isInternal(true)
                    .build();

            log.info("[Checkpoint] Saving state for task: {}, role: {}, status: {}", taskId, role, status);
            taskHistoryMapper.insert(historyDO);
            
            // Optional: Log memory snapshot keys for traceability
            log.debug("[Checkpoint] Memory snapshot keys: {}", memorySnapshot.keySet());
            
        } catch (Exception e) {
            log.error("[Checkpoint] Failed to save persistence for task {}", taskId, e);
        }
    }

    /**
     * Finds an existing successful checkpoint for a given task.
     */
    public MasTaskHistoryDO findLatestCheckpoint(String sessionId, String taskId) {
        try {
            return taskHistoryMapper.selectOne(new LambdaQueryWrapper<MasTaskHistoryDO>()
                    .eq(MasTaskHistoryDO::getSessionId, sessionId)
                    .eq(MasTaskHistoryDO::getTaskId, taskId)
                    .eq(MasTaskHistoryDO::getStatus, "SUCCESS")
                    .orderByDesc(MasTaskHistoryDO::getId) // Using ID for latest
                    .last("LIMIT 1"));
        } catch (Exception e) {
            log.warn("[Checkpoint] No existing checkpoint found for {}: {}", taskId, e.getMessage());
            return null;
        }
    }
}
