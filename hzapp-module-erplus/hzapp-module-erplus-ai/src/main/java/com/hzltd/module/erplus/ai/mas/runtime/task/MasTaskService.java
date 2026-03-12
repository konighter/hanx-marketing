package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import java.util.List;

/**
 * MAS 业务任务服务接口
 * 处理任务生命周期及父子任务协同
 */
public interface MasTaskService {

    /**
     * 保存/更新任务
     */
    void saveTask(MasTaskDO task);

    /**
     * 根据 ID 获取任务
     */
    MasTaskDO getTask(Long id);

    /**
     * 获取未完成的待调度任务 (用于 Job 扫描)
     */
    List<MasTaskDO> getPendingTasks();

    /**
     * 更新任务状态，并处理父任务协同逻辑
     */
    void updateTaskStatus(Long taskId, String status, String outputData);

    /**
     * 手动重置任务为 PENDING (支持 Redo)
     */
    void resetTask(Long taskId);
    
    /**
     * 获取父任务的所有子任务
     */
    List<MasTaskDO> getChildTasks(Long parentId);

    /**
     * 将任务设置为 RESUME 状态，并设置下次执行时间
     */
    void resumeTask(Long taskId, java.time.LocalDateTime nextTime, String outputData);

    /**
     * 审核通过：REVIEW_REQUIRED → SUCCESS
     */
    void approveReview(Long taskId);

    /**
     * 审核拒绝：REVIEW_REQUIRED → FAILED
     */
    void rejectReview(Long taskId, String reason);
}
