package com.hzltd.module.erplus.sys.service.task;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.module.erplus.system.enums.ErpTaskStatus;
import com.hzltd.module.erplus.system.service.ErpTaskEngine;
import com.hzltd.module.erplus.system.service.ErpTaskHandler;
import com.hzltd.module.erplus.sys.convert.job.ErpTaskConvert;
import com.hzltd.module.erplus.sys.dal.dataobject.task.ErpScheduleTaskDO;
import com.hzltd.module.erplus.sys.dal.mysql.task.ErpScheduleTaskMapper;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通用任务引擎实现类
 *
 * @author antigravity
 */
@Slf4j
@Service
public class ErpTaskEngineImpl implements ErpTaskEngine {

    @Resource
    private ErpScheduleTaskMapper taskMapper;

    @Resource
    private List<ErpTaskHandler> handlers;

    private Map<String, ErpTaskHandler> handlerMap;

    private static final ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(100));

    @PostConstruct
    public void init() {
        handlerMap = handlers.stream().collect(Collectors.toMap(ErpTaskHandler::getTaskType, h -> h));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitTask(ErpTaskSubmitRequest request) {
        // 幂等检查
        ErpScheduleTaskDO existing = taskMapper.selectByUniqueId(request.getTaskUniqueId(), request.getTaskType());
        if (existing != null) {
            // 如果任务已存在且未结束 或者已经成功 ，返回现有ID, 如果失败了, 允许重新创建
            if (!ErpTaskStatus.isFailed(existing.getStatus())) {
                return existing.getId();
            } else {
                // 如果失败了, 删除历史任务，重新创建任务
                taskMapper.deleteById(existing.getId());
            }
        }

        ErpScheduleTaskDO taskDO = ErpTaskConvert.INSTANCE.convert(request);
        taskDO.setStatus(ErpTaskStatus.PENDING.getStatus());
        if (taskDO.getScheduledAt() == null) {
            taskDO.setScheduledAt(System.currentTimeMillis());
        }
        if (taskDO.getMaxRetries() == null) {
            taskDO.setMaxRetries(3);
        }
        taskMapper.insert(taskDO);
        return taskDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitChildTasks(Long parentId, List<ErpTaskSubmitRequest> requests) {
        if (CollUtil.isEmpty(requests)) {
            return;
        }
        for (ErpTaskSubmitRequest request : requests) {
            ErpScheduleTaskDO childTask = ErpTaskConvert.INSTANCE.convert(request);
            childTask.setParentTaskId(parentId);
            childTask.setStatus(ErpTaskStatus.PENDING.getStatus());
            if (childTask.getScheduledAt() == null) {
                childTask.setScheduledAt(System.currentTimeMillis());
            }
            if (childTask.getMaxRetries() == null) {
                childTask.setMaxRetries(3);
            }
            taskMapper.insert(childTask);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, ErpTaskResult result) {
        ErpScheduleTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        updateTaskState(task, result);
        taskMapper.updateById(task);

        // 级联处理
        if (ErpTaskStatus.SUCCESS.getStatus().equals(result.getStatus())) {
            if (task.getParentTaskId() != null) {
                checkAndCompleteParent(task.getParentTaskId());
            }
        }
    }

    @Override
    public ErpTaskDTO getTask(String taskUniqueId, String taskType) {
        ErpScheduleTaskDO taskDO = taskMapper.selectByUniqueId(taskUniqueId, taskType);
        return ErpTaskConvert.INSTANCE.convert(taskDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseTask(Long taskId) {
        ErpScheduleTaskDO task = taskMapper.selectById(taskId);
        if (task != null && !ErpTaskStatus.isTerminal(task.getStatus())) {
            task.setStatus(ErpTaskStatus.PAUSED.getStatus());
            taskMapper.updateById(task);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeTask(Long taskId) {
        ErpScheduleTaskDO task = taskMapper.selectById(taskId);
        if (task != null && ErpTaskStatus.PAUSED.getStatus().equals(task.getStatus())) {
            // 恢复为 PENDING 重新进入调度
            task.setStatus(ErpTaskStatus.PENDING.getStatus());
            task.setScheduledAt(System.currentTimeMillis()); // 立即触发
            taskMapper.updateById(task);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseTask(String taskUniqueId, String taskType) {
        ErpScheduleTaskDO task = taskMapper.selectByUniqueId(taskUniqueId, taskType);
        if (task != null && !ErpTaskStatus.isTerminal(task.getStatus())) {
            task.setStatus(ErpTaskStatus.PAUSED.getStatus());
            taskMapper.updateById(task);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeTask(String taskUniqueId, String taskType) {
        ErpScheduleTaskDO task = taskMapper.selectByUniqueId(taskUniqueId, taskType);
        if (task != null && ErpTaskStatus.PAUSED.getStatus().equals(task.getStatus())) {
            task.setStatus(ErpTaskStatus.PENDING.getStatus());
            task.setScheduledAt(System.currentTimeMillis());
            taskMapper.updateById(task);
        }
    }

    /**
     * 引擎轮询入口 (由定时任务调用)
     */
    public void schedule() {
        // 扫描 PENDING 和 SUBMITTED 任务
        List<ErpScheduleTaskDO> tasks = taskMapper.selectListByStatuses(
                Arrays.asList(ErpTaskStatus.PENDING.getStatus(), ErpTaskStatus.SUBMITTED.getStatus()),
                100
        );

        for (ErpScheduleTaskDO taskDO : tasks) {
            taskExecutor.submit(() -> {
                try {
                    processTask(taskDO);
                } catch (Exception e) {
                    log.error("Process task {} failed", taskDO.getId(), e);
                    handleTaskError(taskDO, e.getMessage());
                }
            });

        }
    }

    private void processTask(ErpScheduleTaskDO taskDO) {
        ErpTaskHandler handler = handlerMap.get(taskDO.getTaskType());
        if (handler == null) {
            log.error("No handler found for task type: {}", taskDO.getTaskType());
            handleTaskError(taskDO, "Handler not found: " + taskDO.getTaskType());
            return;
        }

        ErpTaskDTO model = ErpTaskConvert.INSTANCE.convert(taskDO);
        ErpTaskResult result;

        if (ErpTaskStatus.PENDING.getStatus().equals(taskDO.getStatus())) {
            taskDO.setStartedAt(LocalDateTime.now());
            result = handler.onStart(model);
        } else {
            result = handler.onPoll(model);
        }

        if (result != null) {
            updateTaskState(taskDO, result);
            taskMapper.updateById(taskDO);
            
            // 如果成功且是子任务，检查父任务
            if (ErpTaskStatus.SUCCESS.getStatus().equals(taskDO.getStatus()) && taskDO.getParentTaskId() != null) {
                checkAndCompleteParent(taskDO.getParentTaskId());
            }
        }
    }

    private void updateTaskState(ErpScheduleTaskDO taskDO, ErpTaskResult result) {
        taskDO.setStatus(result.getStatus());
        if (result.getPlatformJobId() != null) {
            taskDO.setPlatformJobId(result.getPlatformJobId());
        }
        if (result.getErrorMessage() != null) {
            taskDO.setErrorMessage(result.getErrorMessage());
        }
        if (CollUtil.isNotEmpty(result.getContext())) {
            Map<String, Object> ctx = taskDO.getContext();
            if (ctx == null) ctx = new HashMap<>();
            ctx.putAll(result.getContext());
            taskDO.setContext(ctx);
        }
        if (ErpTaskStatus.isTerminal(result.getStatus())) {
            taskDO.setFinishedAt(LocalDateTime.now());
        }
        if (result.getNextScheduleTime() != null) {
            taskDO.setScheduledAt(result.getNextScheduleTime());
        }
    }

    private void handleTaskError(ErpScheduleTaskDO taskDO, String error) {
        taskDO.setRetryCount(taskDO.getRetryCount() + 1);
        if (taskDO.getRetryCount() >= taskDO.getMaxRetries()) {
            taskDO.setStatus(ErpTaskStatus.FAILED.getStatus());
            taskDO.setErrorMessage(error);
            taskDO.setFinishedAt(LocalDateTime.now());
        } else {
            // 指数退避或简单延迟 (1分钟)
            taskDO.setScheduledAt(System.currentTimeMillis() + 60000);
        }
        taskMapper.updateById(taskDO);
    }

    private void checkAndCompleteParent(Long parentId) {
        List<ErpScheduleTaskDO> children = taskMapper.selectList(ErpScheduleTaskDO::getParentTaskId, parentId);
        if (CollUtil.isEmpty(children)) return;

        boolean allSuccess = children.stream().allMatch(c -> ErpTaskStatus.SUCCESS.getStatus().equals(c.getStatus()));
        if (allSuccess) {
            completeTask(parentId, ErpTaskResult.success());
        }
    }
}
