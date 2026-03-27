package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.adapter.AbsCrossApiServiceFactory;
import com.hzltd.module.spapi.service.order.FinancesApi;
import org.springframework.stereotype.Service;

@Service
public class FinancesApiFactory extends AbsCrossApiServiceFactory<FinancesApi> {
    @Override
    public Class<FinancesApi> getCrossApiServiceClass() {
        return FinancesApi.class;
    }
}
