package com.hzltd.module.erplus.adv.adapter.amazon.v1.model;

import lombok.Builder;
import lombok.Data;

/**
 * Amazon Ads Stream Subscription Request
 * See: https://advertising.amazon.com/API/docs/en-us/amazon-ads-api-data-streams
 */
@Data
@Builder
public class AmzStreamSubscriptionRequest {
    /** 幂等令牌 (UUID) */
    private String clientRequestToken;
    /** 数据集 ID: sp-traffic / sp-conversion / sb-traffic / sb-conversion / sd-traffic / sd-conversion */
    private String dataSetId;
    /** 目标详情对象 */
    private Destination destination;
    /** 目标资源 ARN (如果是引用已有资源) */
    private String destinationArn;
    /** 备注 */
    private String notes;

    @Data
    @Builder
    public static class Destination {
        private FirehoseDestination firehoseDestination;
        private SqsDestination sqsDestination;
    }

    @Data
    @Builder
    public static class FirehoseDestination {
        private String deliveryStreamArn;
        private String subscriberRoleArn;
        private String subscriptionRoleArn;
    }

    @Data
    @Builder
    public static class SqsDestination {
        private String queueArn;
    }
}
