package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskMapper;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MasTaskService 实现类
 */
@Slf4j
@Service
public class MasTaskServiceImpl implements MasTaskService {

    private final MasTaskMapper taskMapper;

    public MasTaskServiceImpl(MasTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public void saveTask(MasTaskDO task) {
        if (task.getId() == null) {
            taskMapper.insert(task);
        } else {
            taskMapper.updateById(task);
        }
    }

    @Override
    public MasTaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<MasTaskDO> getPendingTasks() {
        // Fetch PENDING tasks OR RESUME tasks whose nextExecuteTime is reached
        return taskMapper.selectList(null).stream()
            .filter(t -> {
                String status = t.getStatus();
                if (MasTaskStatusEnum.PENDING.getStatus().equals(status)) {
                    return true;
                }
                if (MasTaskStatusEnum.RESUME.getStatus().equals(status)) {
                    return t.getNextExecuteTime() == null || t.getNextExecuteTime().isBefore(LocalDateTime.now());
                }
                return false;
            })
            .collect(Collectors.toList());
    }

    @Override
    public void updateTaskStatus(Long taskId, String status, String outputData) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        task.setStatus(status);
        if (outputData != null) {
            task.setOutputData(outputData);
        }
        taskMapper.updateById(task);

        log.info("[MasTask] Task {} status updated to {}", taskId, status);

        // If finished, check parent status
        if (MasTaskStatusEnum.SUCCESS.getStatus().equals(status) || 
            MasTaskStatusEnum.FAILED.getStatus().equals(status)) {
            checkAndUpdateParentStatus(task.getParentId());
        }
    }

    @Override
    public void resetTask(Long taskId) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        task.setStatus(MasTaskStatusEnum.PENDING.getStatus());
        task.setOutputData(null);
        task.setRetryData(null);
        task.setNextExecuteTime(null);
        taskMapper.updateById(task);
        log.info("[MasTask] Task {} has been reset to PENDING", taskId);
    }

    @Override
    public List<MasTaskDO> getChildTasks(Long parentId) {
        return taskMapper.selectList(null).stream()
            .filter(t -> parentId.equals(t.getParentId()))
            .collect(Collectors.toList());
    }

    @Override
    public void resumeTask(Long taskId, LocalDateTime nextTime, String outputData) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        task.setStatus(MasTaskStatusEnum.RESUME.getStatus());
        task.setNextExecuteTime(nextTime);
        if (outputData != null) {
            task.setOutputData(outputData);
        }
        taskMapper.updateById(task);
        log.info("[MasTask] Task {} moved to RESUME state, next execution: {}", taskId, nextTime);
    }

    private void checkAndUpdateParentStatus(Long parentId) {
        if (parentId == null) return;

        MasTaskDO parent = taskMapper.selectById(parentId);
        if (parent == null) return;

        List<MasTaskDO> children = getChildTasks(parentId);
        boolean allFinished = children.stream().allMatch(c -> 
            MasTaskStatusEnum.SUCCESS.getStatus().equals(c.getStatus()));
        
        if (allFinished) {
            // Aggregate child results into parent outputData
            String aggregateResult = children.stream()
                .map(c -> String.format("[%s]: %s", c.getName(), c.getOutputData()))
                .collect(Collectors.joining("\n"));
            parent.setOutputData(aggregateResult);
            
            // Parent moves to review stage
            parent.setStatus(MasTaskStatusEnum.REVIEW_REQUIRED.getStatus());
            taskMapper.updateById(parent);
            log.info("[MasTask] Parent Task {} moved to REVIEW_REQUIRED with aggregated results", parentId);
        } else if (MasTaskTypeEnum.SEQUENTIAL.getType().equals(parent.getTaskType())) {
            // Check if we should trigger the next child
            children.stream()
                .filter(c -> MasTaskStatusEnum.WAITING.getStatus().equals(c.getStatus()))
                .findFirst()
                .ifPresent(next -> {
                    next.setStatus(MasTaskStatusEnum.PENDING.getStatus());
                    taskMapper.updateById(next);
                    log.info("[MasTask] Sequential parent {}: Moving child {} to PENDING", parentId, next.getId());
                });
        }
    }
}
