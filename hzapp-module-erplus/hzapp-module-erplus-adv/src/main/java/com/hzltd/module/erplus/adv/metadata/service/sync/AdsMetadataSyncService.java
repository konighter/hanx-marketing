package com.hzltd.module.erplus.adv.metadata.service.sync;

/**
 * 广告元数据同步 Service 接口
 */
public interface AdsMetadataSyncService {

    /**
     * 同步指定账号的广告元数据 (全量)
     *
     * @param accountId 账号编号
     */
    void syncAllMetadata(Long accountId);

    /**
     * 同步指定账号的广告元数据 (增量)
     *
     * @param accountId 账号编号
     */
    void syncIncrementalMetadata(Long accountId);

    /**
     * 执行同步任务
     * 由定时任务或手动触发调用
     *
     * @param taskId 任务编号
     */
    void executeSyncTask(Long taskId);

}
