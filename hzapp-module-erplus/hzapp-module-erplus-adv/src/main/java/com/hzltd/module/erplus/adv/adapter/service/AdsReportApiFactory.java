package com.hzltd.module.erplus.adv.adapter.service;

import com.hzltd.module.erplus.adv.adapter.AbsAdsApiServiceFactory;
import com.hzltd.module.erplus.adv.service.AdsReportApi;
import org.springframework.stereotype.Service;

/**
 * 广告报表 API 工厂
 */
@Service
public class AdsReportApiFactory extends AbsAdsApiServiceFactory<AdsReportApi> {
    
    @Override
    public Class<AdsReportApi> getAdsApiServiceClass() {
        return AdsReportApi.class;
    }
    
}
