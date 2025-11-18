package com.hzltd.module.erplus.api.adptor.amz;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCache;
import org.springframework.stereotype.Service;

@Service
public class RedisLWAAccessTokenCache implements LWAAccessTokenCache {


    @Override
    public String get(Object key) {
        return "";
    }

    @Override
    public void put(Object key, String accessToken, long tokenTTLInSeconds) {

    }
}
