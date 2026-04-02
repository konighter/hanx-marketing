package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.adapter.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.spapi.service.notification.NotificationSubscriptionApi;
import org.springframework.stereotype.Service;

@Service
public class NotificationSubscriptionApiFactory extends AbsCrossApiServiceFactory<NotificationSubscriptionApi> {
    @Override
    public Class<NotificationSubscriptionApi> getCrossApiServiceClass() {
        return NotificationSubscriptionApi.class;
    }
}
