package com.hzltd.module.amz.adv;

import com.hzltd.module.amz.service.AmzAdvLwaService;
import com.hzltd.module.erplus.api.adapter.LocalAuthProvider;
import com.hzltd.module.erplus.api.adapter.RefreshTokenCacheAdaptor;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.system.model.ShopModel;
import jakarta.annotation.Resource;
import okhttp3.Request;

public abstract class AbstractAmazonAdsService extends LocalAuthProvider {

    @Resource
    private AmzAdvLwaService amzAdvLwaService;

    @Resource
    private RefreshTokenCacheAdaptor localLWAAccessTokenCache;

    @Override
    public String getAuthEndpoint() {
        return "https://api.amazon.com/auth/o2/token";
    }

    @Override
    public String getAuthScope() {
        return "AMAZON_ADV";
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
        if (authModel == null) {
            throw new RuntimeException("Authorization not found for shop: " + apiRequest.getShopId());
        }

        // 为保持与未修改的 AmzAdvLwaService 兼容，临时转换回 V0
        AuthorizationModelV0 authModelV0 = AuthorizationModelV0.builder()
                .appKey(authModel.getAppKey())
                .appSecret(authModel.getAppSecret())
                .refreshToken(authModel.getRefreshToken())
                .shopModel(ShopModel.builder().id(authModel.getShopId().intValue()).build())
                .build();

        String accessToken = amzAdvLwaService.getAccessToken(authModelV0);

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
