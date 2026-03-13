package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskMapper;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        // Fetch tasks that are "READY" for processing:
        // 1. ROOT tasks in PENDING status whose nextExecuteTime is reached (to be moved to RUNNING and activate children)
        // 2. LEAF tasks in PENDING/RESUME status whose nextExecuteTime is reached (to be executed by orchestrator)
        return taskMapper.selectList(new LambdaQueryWrapperX<MasTaskDO>()
                        .in(MasTaskDO::getStatus, List.of(MasTaskStatusEnum.PENDING.getStatus(), MasTaskStatusEnum.SUSPEND.getStatus()))
                .le(MasTaskDO::getNextExecuteTime, LocalDateTime.now())

        );
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
        return taskMapper.selectList(new com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX<MasTaskDO>()
                .eq(MasTaskDO::getParentId, parentId));
    }

    @Override
    public void suspendTask(Long taskId, LocalDateTime nextTime, String outputData) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        task.setStatus(MasTaskStatusEnum.SUSPEND.getStatus());
        task.setNextExecuteTime(nextTime);
        if (outputData != null) {
            task.setOutputData(outputData);
        }
        taskMapper.updateById(task);
        log.info("[MasTask] Task {} moved to SUSPEND state, next execution: {}", taskId, nextTime);
    }

    @Override
    public void approveReview(Long taskId) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[MasTask] approveReview: Task {} not found", taskId);
            return;
        }
        if (!MasTaskStatusEnum.REVIEW_REQUIRED.getStatus().equals(task.getStatus())) {
            log.warn("[MasTask] approveReview: Task {} is not in REVIEW_REQUIRED state (current: {})", taskId, task.getStatus());
            return;
        }
        task.setStatus(MasTaskStatusEnum.SUCCESS.getStatus());
        taskMapper.updateById(task);
        log.info("[MasTask] Task {} approved: REVIEW_REQUIRED → SUCCESS", taskId);

        // Propagate to parent if applicable
        checkAndUpdateParentStatus(task.getParentId());
    }

    @Override
    public void rejectReview(Long taskId, String reason) {
        MasTaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[MasTask] rejectReview: Task {} not found", taskId);
            return;
        }
        if (!MasTaskStatusEnum.REVIEW_REQUIRED.getStatus().equals(task.getStatus())) {
            log.warn("[MasTask] rejectReview: Task {} is not in REVIEW_REQUIRED state (current: {})", taskId, task.getStatus());
            return;
        }
        task.setStatus(MasTaskStatusEnum.FAILED.getStatus());
        task.setOutputData("Review rejected: " + (reason != null ? reason : "No reason provided"));
        taskMapper.updateById(task);
        log.info("[MasTask] Task {} rejected: REVIEW_REQUIRED → FAILED, reason: {}", taskId, reason);

        // Propagate to parent if applicable
        checkAndUpdateParentStatus(task.getParentId());
    }

    @Override
    public void activateChildren(Long parentId) {
        if (parentId == null) return;
        MasTaskDO parent = taskMapper.selectById(parentId);
        if (parent == null) return;

        List<MasTaskDO> children = getChildTasks(parentId);
        if (children.isEmpty()) return;

        if (MasTaskTypeEnum.PARALLEL.getType().equals(parent.getTaskType())) {
            // Parallel: activate all WAITING children
            children.stream()
                .filter(c -> MasTaskStatusEnum.WAITING.getStatus().equals(c.getStatus()))
                .forEach(c -> {
                    c.setStatus(MasTaskStatusEnum.PENDING.getStatus());
                    c.setNextExecuteTime(LocalDateTime.now()); // Immediate activation
                    taskMapper.updateById(c);
                    log.info("[MasTask] Parallel parent {}: Moving child {} to PENDING", parentId, c.getId());
                });
        } else {
            // Sequential: activate the FIRST WAITING child (based on execution_order)
            children.stream()
                .filter(c -> MasTaskStatusEnum.WAITING.getStatus().equals(c.getStatus()))
                .min(java.util.Comparator.comparingInt(MasTaskDO::getExecutionOrder))
                .ifPresent(first -> {
                    first.setStatus(MasTaskStatusEnum.PENDING.getStatus());
                    first.setNextExecuteTime(LocalDateTime.now()); // Immediate activation
                    taskMapper.updateById(first);
                    log.info("[MasTask] Sequential parent {}: Moving first child {} to PENDING", parentId, first.getId());
                });
        }
    }

    private void checkAndUpdateParentStatus(Long parentId) {
        if (parentId == null) return;
        
        MasTaskDO parent = taskMapper.selectById(parentId);
        if (parent == null) return;
        
        List<MasTaskDO> children = getChildTasks(parentId);
        if (children.isEmpty()) return;
        
        // --- FAILED propagation ---
        boolean anyFailed = children.stream().anyMatch(c -> MasTaskStatusEnum.FAILED.getStatus().equals(c.getStatus()));
        if (anyFailed) {
            parent.setStatus(MasTaskStatusEnum.FAILED.getStatus());
            parent.setOutputData("Child task(s) failed.");
            taskMapper.updateById(parent);
            checkAndUpdateParentStatus(parent.getParentId());
            return;
        }
        
        // --- All SUCCESS → REVIEW_REQUIRED ---
        boolean allSuccess = children.stream().allMatch(c -> MasTaskStatusEnum.SUCCESS.getStatus().equals(c.getStatus()));
        if (allSuccess) {
            parent.setStatus(MasTaskStatusEnum.REVIEW_REQUIRED.getStatus());
            taskMapper.updateById(parent);
        } else if (MasTaskTypeEnum.SEQUENTIAL.getType().equals(parent.getTaskType())) {
            // Trigger next waiting child
            children.stream()
                .filter(c -> MasTaskStatusEnum.WAITING.getStatus().equals(c.getStatus()))
                .min(java.util.Comparator.comparingInt(MasTaskDO::getExecutionOrder))
                .ifPresent(next -> {
                    next.setStatus(MasTaskStatusEnum.PENDING.getStatus());
                    next.setNextExecuteTime(LocalDateTime.now());
                    taskMapper.updateById(next);
                });
        }
    }
}
