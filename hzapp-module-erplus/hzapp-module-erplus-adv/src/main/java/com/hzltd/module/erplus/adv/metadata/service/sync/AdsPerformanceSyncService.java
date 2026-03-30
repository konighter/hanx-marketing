package com.hzltd.module.erplus.adv.metadata.service.sync;

/**
 * 广告绩效数据同步 Service
 * 负责全平台的 Report 类任务调度与状态管理
 */
public interface AdsPerformanceSyncService {

    /**
     * 为所有已启用的广告账号创建每日绩效同步任务 (REPORT_DAILY)
     * 由定时任务调用（如每天凌晨），同步创建 PENDING 任务
     */
    void createDailySyncTasks();

    /**
     * 为指定账号创建每日绩效同步任务
     *
     * @param accountId 广告账号 ID
     */
    void createDailySyncTask(Long accountId);

    /**
     * 为指定店铺下所有已启用的广告账号创建每日绩效同步任务
     *
     * @param shopId 店铺 ID
     */
    void createDailySyncTasksByShop(Long shopId);

    /**
     * 处理所有活跃的（PENDING/RUNNING）Report 类同步任务
     * 由定时任务高频调用（如每 5 分钟），推进异步报表状态
     */
    void processActiveReportTasks();
}
