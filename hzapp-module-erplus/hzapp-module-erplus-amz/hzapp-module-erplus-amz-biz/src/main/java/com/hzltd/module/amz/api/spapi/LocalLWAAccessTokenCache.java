package com.hzltd.module.amz.api.spapi;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCache;
import com.hzltd.module.erplus.api.adptor.RefreshTokenCacheAdaptor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalLWAAccessTokenCache implements RefreshTokenCacheAdaptor, LWAAccessTokenCache {

    private static final Map<Object, String> cache = new ConcurrentHashMap<>();

    @Override
    public String getCache(Object key) {
        return cache.get(key);
    }

    @Override
    public void putCache(Object key, String accessToken, long tokenTTLInSeconds) {
        cache.put(key, accessToken);
    }

    @Override
    public String get(Object key) {
        return getCache(key);
    }

    @Override
    public void put(Object key, String accessToken, long tokenTTLInSeconds) {
        this.putCache(key, accessToken, tokenTTLInSeconds);
    }
}
