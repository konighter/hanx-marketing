package com.hzltd.module.amz.spapi.model;


import lombok.Data;

@Data
public class AmzSubscriptionModel {

    private String subscriptionId;

    private String destinationId;

    private String notificationType;

    private String payloadVersion;
}
