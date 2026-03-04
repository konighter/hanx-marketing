package com.hzltd.module.erplus.adv.adapter.amazon.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;

import java.util.List;

public interface AdsAmazonReportService {

    /**
     * 执行同步任务状态机
     */
    void executeSyncTask(AdsSyncTaskDO task);

    /**
     * 创建 Stream 订阅
     * @param profile 站点信息
     */
    void createStreamSubscription(AdsAmazonProfileDO profile);

    /**
     * 为指定账号下所有站点创建 Stream 订阅
     * @param accountId 账号 ID
     */
    void createStreamSubscriptionByAccountId(Long accountId);

    /**
     * 处理 Amazon Marketing Stream SQS 消息
     * 解析 stream 数据并按 group_column 拆分写入 ads_report_hourly (Doris)
     *
     * @param sqsMessageBody SQS 消息体 JSON
     */
    void processStreamMessage(String sqsMessageBody);

}
