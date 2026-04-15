package com.hzltd.module.erplus.adv.metadata.service.sync;

/**
 * 广告元数据同步 Service 接口
 */
public interface AdsMetadataSyncService {

    /**
     * 同步指定店铺下所有账号的广告元数据 (全量)
     *
     * @param shopId 店铺编号
     */
    void syncAllMetadataByShop(Long shopId);

    /**
     * 同步指定店铺下所有账号的广告元数据 (增量)
     *
     * @param shopId 店铺编号
     */
    void syncIncrementalMetadataByShop(Long shopId);

    /**
     * 同步广告活动
     * @param campaignId
     */
    void syncMetadataByCampaign(Long campaignId);

    /**
     * 执行同步任务
     * 由定时任务或手动触发调用
     *
     * @param taskId 任务编号
     */
    void executeSyncTask(Long taskId);

}
