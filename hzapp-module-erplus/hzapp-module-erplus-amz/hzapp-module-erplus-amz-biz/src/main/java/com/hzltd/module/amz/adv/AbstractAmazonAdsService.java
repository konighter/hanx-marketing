package com.hzltd.module.amz.adv;

import com.hzltd.module.amz.adv.client.client.ApiClient;
import com.hzltd.module.amz.adv.service.AdsAmazonProfileService;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.service.AmzAdvLwaService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.api.adapter.LocalAuthProvider;
import com.hzltd.module.erplus.api.adapter.RefreshTokenCacheAdaptor;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.service.SystemAuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.context.annotation.Lazy;

@Slf4j
public abstract class AbstractAmazonAdsService extends LocalAuthProvider {

    @Resource
    private AmzAdvLwaService amzAdvLwaService;

    @Resource
    private RefreshTokenCacheAdaptor localLWAAccessTokenCache;

    @Resource
    private SystemAuthService systemAuthService;

    @Resource
    @Lazy
    private AdsAmazonProfileService amazonProfileService;

    @Resource
    private AdsAccountMapper adsAccountMapper;

    @Override
    public String getAuthEndpoint() {
        return "https://api.amazon.com/auth/o2/token";
    }

    @Override
    public String getAuthType() {
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

    /**
     * 根据 region 获取 API 端点
     * @param region NA, EU, FE
     * @return 对应的 API URL
     */
    public String getApiEndpointByRegion(String region) {
        if ("EU".equalsIgnoreCase(region)) {
            return "https://advertising-api-eu.amazon.com";
        } else if ("FE".equalsIgnoreCase(region)) {
            return "https://advertising-api-fe.amazon.com";
        }
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

    /**
     * 获取授权模型
     *
     * @param shopId 店铺 ID
     * @return AuthorizationModel
     */
    protected AuthorizationModel getAuthorizationModel(Long shopId) {
        AuthorizationModel authModel = systemAuthService.getAuthorizationModel(shopId, getAuthType());
        if (authModel == null) {
            throw new RuntimeException("Authorization not found for shop: " + shopId);
        }
        // 添加profileId
        AdsAmazonProfileDO profileDO = amazonProfileService.getProfileByShopId(shopId);
        if (profileDO == null) {
            throw new RuntimeException("Profile not found for shop: " + shopId);
        }

        authModel.setProfileId(profileDO.getProfileId());
        authModel.setRegion(profileDO.getRegion());

        // 获取 adsAccountId (externalAccountId)
        if (profileDO.getAccountId() != null) {
            AdsAccountDO accountDO = adsAccountMapper.selectById(profileDO.getAccountId());
            if (accountDO != null) {
                authModel.setAdsAccountId(accountDO.getExternalAccountId());
            }
        }

        return authModel;
    }

    /**
     * 获取 Amazon Advertising SP API Client (v3)
     *
     * @param authModel
     * @return ApiClient
     */
    protected ApiClient getApiClient(AuthorizationModel authModel) {
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(getApiEndpointByRegion(authModel.getRegion()));
        
        // 获取 LWA 访问令牌
//        String accessToken = amzAdvLwaService.getAccessToken(authModel);
        String accessToken = authModel.getAccessToken();

        apiClient.setRequestInterceptor(builder -> {
            builder.setHeader("Authorization", "Bearer " + accessToken);
            builder.setHeader("Amazon-Advertising-API-ClientId", authModel.getAppKey());

            if (authModel.getProfileId() != null) {
                builder.setHeader("Amazon-Advertising-API-Scope", authModel.getProfileId());
            }

            if (authModel.getAdsAccountId() != null) {
                builder.setHeader("Amazon-Advertising-API-Account-Id", authModel.getAdsAccountId());
            }

//            log.debug("[AmazonAds] Request Info -Url:{} Region: {}, profileId: {}, adsAccountId: {}", apiClient.getBaseUri(),
//                    authModel.getRegion(), authModel.getProfileId(), authModel.getAdsAccountId());
        });

        return apiClient;
    }


    protected ApiClient getApiClient(Long shopId) {
        return this.getApiClient(this.getAuthorizationModel(shopId));
    }
}
