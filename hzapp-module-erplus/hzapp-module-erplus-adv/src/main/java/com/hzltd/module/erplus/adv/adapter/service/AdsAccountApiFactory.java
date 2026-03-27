package com.hzltd.module.erplus.adv.adapter.service;

import com.hzltd.module.adv.service.AdsAccountApi;
import com.hzltd.module.erplus.adv.adapter.AbsAdsApiServiceFactory;
import org.springframework.stereotype.Service;

@Service
public class AdsAccountApiFactory extends AbsAdsApiServiceFactory<AdsAccountApi> {
    @Override
    public Class<AdsAccountApi> getAdsApiServiceClass() {
        return AdsAccountApi.class;
    }
}
