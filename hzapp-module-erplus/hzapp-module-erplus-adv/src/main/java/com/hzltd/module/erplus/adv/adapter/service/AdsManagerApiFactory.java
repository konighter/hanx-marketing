package com.hzltd.module.erplus.adv.adapter.service;

import com.hzltd.module.adv.service.AdsManagerApi;
import com.hzltd.module.erplus.adv.adapter.AbsAdsApiServiceFactory;
import org.springframework.stereotype.Service;

@Service
public class AdsManagerApiFactory extends AbsAdsApiServiceFactory<AdsManagerApi> {
    @Override
    public Class<AdsManagerApi> getAdsApiServiceClass() {
        return AdsManagerApi.class;
    }
}
