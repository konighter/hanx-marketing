package com.hzltd.module.erplus.api.config;

import com.hzltd.module.system.enums.CrossPlatformEnum;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsCrossApiServiceFactory<T> {

    private final Map<CrossPlatformEnum, T> crossApiServiceMap = new HashMap<>();

    /**
     * 获取跨平台服务
     *
     * @param platform 跨平台枚举
     * @return 跨平台服务
     */
    public T getCrossApiService(CrossPlatformEnum platform) {
        return crossApiServiceMap.get(platform);
    }

     /**
     * 注册跨平台服务
     *
     * @param platform 跨平台枚举
     * @param service  跨平台服务
     */
    public void registerCrossApiService(CrossPlatformEnum platform, T service) {
        crossApiServiceMap.put(platform, service);
    }

    public abstract Class<T> getCrossApiServiceClass();

}
