package com.hzltd.module.erplus.api.adptor.ozon;

import com.hzltd.module.erplus.api.adptor.AbsPlatformService;
import com.hzltd.module.erplus.api.adptor.RefreshTokenCacheAdaptor;

public class OzonPlatformApiService extends AbsPlatformService {
    @Override
    public String getAuthEndpoint() {
        return "";
    }

    @Override
    public String getApiEndpoint(String marketPlaceId) {
        return "";
    }

    @Override
    public RefreshTokenCacheAdaptor getTokenCache() {
        return null;
    }
}
