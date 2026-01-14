package com.hzltd.module.erplus.api.adptor.amz;

import com.hzltd.module.erplus.api.adptor.RefreshTokenCacheAdaptor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalLWAAccessTokenCache implements RefreshTokenCacheAdaptor {

    private static final Map<Object, String> cache = new ConcurrentHashMap<>();

    @Override
    public String getCache(Object key) {
        return cache.get(key);
    }

    @Override
    public void putCache(Object key, String accessToken, long tokenTTLInSeconds) {
        cache.put(key, accessToken);
    }


}
