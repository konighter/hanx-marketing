package com.hzltd.module.erplus.adv.adapter.amazon.v1.model;

import lombok.Data;

import java.util.Map;

/**
 * Amazon Ads Stream Subscription Response
 */
@Data
public class AmzStreamSubscriptionResponse {
    /** 订阅 ID */
    private String subscriptionId;
    /** 数据集 ID */
    private String dataSetId;
    /** 订阅状态: ACTIVE / ARCHIVED / PENDING */
    private String status;
    /** 备注 */
    private String notes;
    /** 目标详情 */
    private Map<String, Object> destinationDetails;
    /** 创建时间 */
    private String createdAt;
    /** 更新时间 */
    private String updatedAt;
}
