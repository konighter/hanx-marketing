package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.system.dto.ErpTaskDTO;
import com.hzltd.module.erplus.system.dto.ErpTaskResult;

/**
 * 任务处理器接口
 * 各业务模块实现此接口并注册到引擎中
 *
 * @author antigravity
 */
public interface ErpTaskHandler {

    /**
     * 任务开始执行逻辑 (PENDING 状态时调用)
     *
     * @param task 任务模型
     * @return 执行结果
     */
    ErpTaskResult onStart(ErpTaskDTO task);

    /**
     * 任务轮询逻辑 (SUBMITTED 状态时调用)
     *
     * @param task 任务模型
     * @return 执行结果
     */
    ErpTaskResult onPoll(ErpTaskDTO task);

    /**
     * 获取处理器支持的任务类型
     *
     * @return 任务类型标识
     */
    String getTaskType();
    
}
