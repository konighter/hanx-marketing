package com.hzltd.module.erplus.amzadv.service;

import com.hzltd.module.erplus.api.adptor.LocalAuthProvider;
import com.hzltd.module.erplus.api.adptor.RefreshTokenCacheAdaptor;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import jakarta.annotation.Resource;
import okhttp3.Request;

public abstract class AbsAmzAdvService extends LocalAuthProvider {

    @Resource
    private AmzAdvLwaService amzAdvLwaService;

    @Resource
    private RefreshTokenCacheAdaptor localLWAAccessTokenCache;

    @Override
    public String getAuthEndpoint() {
        return "https://api.amazon.com/auth/o2/token";
    }

    @Override
    public String getApiEndpoint(String marketPlaceId) {
        // Amazon Advertising API 常用端点
        // 北美: https://advertising-api.amazon.com
        // 欧洲: https://advertising-api-eu.amazon.com
        // 远东: https://advertising-api-fe.amazon.com
        // 这里可以根据 marketId 动态返回，暂时默认北美
        return "https://advertising-api.amazon.com";
    }

    @Override
    public RefreshTokenCacheAdaptor getTokenCache() {
        return localLWAAccessTokenCache;
    }

    /**
     * 构建带有 LWA 认证的请求
     *
     * @param apiRequest 请求对象
     * @param url        请求 URL
     * @param profileId  Advertising Profile ID
     * @return Request.Builder
     */
    protected Request.Builder newAuthenticatedRequestBuilder(ApiRequest<?> apiRequest, String url, String profileId) {
        AuthorizationModel authModel = getAuthorizationModel(apiRequest);
        String accessToken = amzAdvLwaService.getAccessToken(authModel);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Amazon-Ads-ClientId", authModel.getAppKey());

        if (profileId != null) {
            builder.addHeader("Amazon-Advertising-API-Scope", profileId);
        }

        return builder;
    }
}
