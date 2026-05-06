package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.system.dto.ErpTaskResult;
import com.hzltd.module.erplus.system.dto.ErpTaskSubmitRequest;
import com.hzltd.module.erplus.system.dto.ErpTaskDTO;

import java.util.List;

/**
 * 通用任务调度引擎接口
 *
 * @author antigravity
 */
public interface ErpTaskEngine {

    /**
     * 提交新任务
     *
     * @param request 提交请求
     * @return 任务ID
     */
    Long submitTask(ErpTaskSubmitRequest request);



    /**
     * 批量提交子任务
     *
     * @param parentId 父任务ID
     * @param requests 子任务请求列表
     */
    void submitChildTasks(Long parentId, List<ErpTaskSubmitRequest> requests);

    /**
     * 异步任务回写结果 (异步回调场景)
     *
     * @param taskId 任务ID
     * @param result 执行结果
     */
    void completeTask(Long taskId, ErpTaskResult result);

    /**
     * 根据唯一标识查询任务
     *
     * @param taskUniqueId 任务唯一标识
     * @param taskType     任务类型
     * @return 任务模型
     */
    ErpTaskDTO getTask(String taskUniqueId, String taskType);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    void pauseTask(Long taskId);

    /**
     * 重启/恢复任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(Long taskId);

    /**
     * 暂停任务 (通过唯一标识)
     *
     * @param taskUniqueId 唯一标识
     * @param taskType 任务类型
     */
    void pauseTask(String taskUniqueId, String taskType);

    /**
     * 恢复任务 (通过唯一标识)
     *
     * @param taskUniqueId 唯一标识
     * @param taskType 任务类型
     */
    void resumeTask(String taskUniqueId, String taskType);

}
