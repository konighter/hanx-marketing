package com.hzltd.module.erplus.adv.adapter.service;

import com.hzltd.module.erplus.adv.adapter.AbsAdsApiServiceFactory;
import com.hzltd.module.erplus.adv.service.AdsManagerApi;
import org.springframework.stereotype.Service;

@Service
public class AdsManagerApiFactory extends AbsAdsApiServiceFactory<AdsManagerApi> {
    @Override
    public Class<AdsManagerApi> getAdsApiServiceClass() {
        return AdsManagerApi.class;
    }
}
