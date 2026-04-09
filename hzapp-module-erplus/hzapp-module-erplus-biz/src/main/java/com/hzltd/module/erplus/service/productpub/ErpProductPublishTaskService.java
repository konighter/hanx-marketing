package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;

import java.util.Optional;

public interface ErpProductPublishTaskService {

    Optional<ErpProductPublishTaskDO> getProductPublishTask(Long taskId);
    Long createProductPublishTask(ErpProductPublishTaskDO task);

    void updateProductPublishTask(ErpProductPublishTaskDO task);

    /**
     * 获取待发布的预约任务
     *
     * @return 任务列表
     */
    java.util.List<ErpProductPublishTaskDO> getPendingPublishTasks();

    /**
     * 提交刊登任务异步执行
     *
     * @param taskId 任务 ID
     * @return 是否提交成功
     */
    boolean submitProductPublishTask(Long taskId);



}
