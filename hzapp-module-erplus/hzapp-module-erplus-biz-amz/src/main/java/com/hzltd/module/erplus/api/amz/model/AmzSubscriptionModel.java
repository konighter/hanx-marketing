package com.hzltd.module.erplus.api.amz.model;


import lombok.Data;

@Data
public class AmzSubscriptionModel {

    private String subscriptionId;

    private String destinationId;

    private String notificationType;

    private String payloadVersion;
}
