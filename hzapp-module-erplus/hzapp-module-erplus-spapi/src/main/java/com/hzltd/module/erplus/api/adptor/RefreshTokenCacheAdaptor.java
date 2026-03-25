package com.hzltd.module.erplus.api.adptor;


public interface RefreshTokenCacheAdaptor {

    // ------- 通用定义 ---------
    // 不同平台直接default调用通用定义
    String getCache(Object key);
    void putCache(Object key, String accessToken, long tokenTTLInSeconds);

}
