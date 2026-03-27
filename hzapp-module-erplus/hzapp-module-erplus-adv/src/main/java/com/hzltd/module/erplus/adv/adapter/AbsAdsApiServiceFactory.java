package com.hzltd.module.erplus.adv.adapter;

import com.hzltd.module.system.enums.AdsPlatformEnum;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsAdsApiServiceFactory<T> {

    private final Map<AdsPlatformEnum, T> adsApiServiceMap = new HashMap<>();

    /**
     * 获取跨平台服务
     *
     * @param platform 跨平台枚举
     * @return 跨平台服务
     */
    public T getAdsApiService(AdsPlatformEnum platform) {
        return adsApiServiceMap.get(platform);
    }

     /**
     * 注册跨平台服务
     *
     * @param platform 跨平台枚举
     * @param service  跨平台服务
     */
    public void registerAdsApiService(AdsPlatformEnum platform, T service) {
        adsApiServiceMap.putIfAbsent(platform, service);
    }

    public abstract Class<T> getAdsApiServiceClass();

}
