package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.adapter.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.spapi.service.order.OrderApi;
import org.springframework.stereotype.Service;

@Service
public class OrderApiFactory extends AbsCrossApiServiceFactory<OrderApi> {
    @Override
    public Class<OrderApi> getCrossApiServiceClass() {
        return OrderApi.class;
    }
}
