package com.hzltd.module.erplus.adv.metadata.service.sync;

/**
 * 广告同步任务核心服务
 * 
 * 处理任务的：
 * 1. 按时区创建 (REPORT_DAILY)
 * 2. 任务拆分 (REPORT_DAILY -> REPORT_DIMENSION)
 * 3. 任务分发 (提交异步请求)
 * 4. 任务轮询 (检查平台状态并下载)
 */
public interface AdsReportSyncTaskService {

    /**
     * 为所有启用广告的店铺创建昨日同步任务 (REPORT_DAILY)
     * 需满足：
     * 1. 根据店铺时区，当地时间已过缓冲期（默认凌晨 4 点）
     * 2. 该日期任务尚未创建
     */
    void createDailyTasks();

    /**
     * 处理所有活跃任务：
     * 1. 将 PENDING 状态的父任务 (REPORT_DAILY) 拆分为子任务
     * 2. 将 PENDING 状态的子任务 (REPORT_DIMENSION) 提交给平台 API
     * 3. 轮询 SUBMITTED 状态的任务状态，若完成则触发下载入库
     */
    void processActiveTasks();

}
