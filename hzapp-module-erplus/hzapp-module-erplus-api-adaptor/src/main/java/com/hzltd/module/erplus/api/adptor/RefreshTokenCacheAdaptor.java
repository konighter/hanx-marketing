package com.hzltd.module.erplus.api.adptor;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCache;

public interface RefreshTokenCacheAdaptor extends LWAAccessTokenCache {

    // ------- 通用定义 ---------
    // 不同平台直接default调用通用定义
    String getCache(Object key);
    void putCache(Object key, String accessToken, long tokenTTLInSeconds);



    // --------- AMZ 缓存 start ---------
    @Override
    default String get(Object key) {
        return getCache(key);
    }
     @Override
    default void put(Object key, String accessToken, long tokenTTLInSeconds) {
        putCache(key, accessToken, tokenTTLInSeconds);
    }
    // --------- AMZ 缓存 end ---------

}
