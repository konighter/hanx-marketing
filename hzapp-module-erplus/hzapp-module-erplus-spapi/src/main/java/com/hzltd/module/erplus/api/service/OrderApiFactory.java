package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.config.AbsCrossApiServiceFactory;
import com.hzltd.module.spapi.service.order.OrderApi;
import org.springframework.stereotype.Service;

@Service
public class OrderApiFactory extends AbsCrossApiServiceFactory<OrderApi> {
    @Override
    public Class<OrderApi> getCrossApiServiceClass() {
        return OrderApi.class;
    }
}
