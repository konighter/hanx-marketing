package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.config.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.service.product.PricingInventoryApi;
import org.springframework.stereotype.Service;

@Service
public class PricingInventoryApiFactory extends AbsCrossApiServiceFactory<PricingInventoryApi> {
    @Override
    public Class<PricingInventoryApi> getCrossApiServiceClass() {
        return PricingInventoryApi.class;
    }
}
