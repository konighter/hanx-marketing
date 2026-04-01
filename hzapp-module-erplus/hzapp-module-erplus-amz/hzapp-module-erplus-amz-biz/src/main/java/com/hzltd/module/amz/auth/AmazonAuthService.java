package com.hzltd.module.amz.auth;

import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.adv.model.AdsRequest;
import com.hzltd.module.adv.model.AdsResponse;
import com.hzltd.module.amz.adv.client.account.model.AdsAccountWithMetaData;
import com.hzltd.module.amz.adv.client.account.model.AlternateId;
import com.hzltd.module.amz.adv.client.profiles.model.Profile;
import com.hzltd.module.amz.adv.api.AdsAccountProfileApi;
import com.hzltd.module.amz.adv.service.AdsAmazonProfileService;
import com.hzltd.module.amz.adv.service.AdsAmazonStreamService;
import com.hzltd.module.amz.api.adv.AmazonAdsAdapter;
import com.hzltd.module.amz.api.enums.AmazonRegionEnum;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.amz.spapi.api.AmazonAccountService;
import com.hzltd.module.amz.spapi.api.AmazonNotificationSubscriptionService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.model.ShopModel;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.spapi.service.authorization.AuthorizationApi;
import com.hzltd.module.system.service.SystemAuthService;
import com.hzltd.module.system.service.SystemShopService;
import com.hzltd.module.system.model.PlatformAccountModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.spapi.models.sellers.v1.Account;
import software.amazon.spapi.models.sellers.v1.Participation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.CROSS_API_ERROR;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.CROSS_SERVICE_ERROR;

@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = AuthorizationApi.class)
public class AmazonAuthService implements AuthorizationApi {

    private String host = "https://api.amazon.com";

    private String grantAuthHost = "https://www.amazon.com/ap/oa";

    private OkHttpClient client = new OkHttpClient();

    @Resource
    private SystemAuthService systemAuthService;

    @Resource
    private SystemShopService systemShopService;

    @Resource
    private AmazonAccountService amazonAccountService;

    @Resource
    private AdsAmazonProfileService adsAmazonProfileService;

    @Resource
    private AdsAmazonStreamService adsAmazonStreamService;

    @Resource
    private AdsAccountCredentialMapper adsAccountCredentialMapper;

    @Resource
    private AdsAccountMapper adsAccountMapper;

    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    private AmazonAdsAdapter amazonAdsAdapter;

    @Resource
    private AdsAccountProfileApi amazonProfileNewService;

    @Resource
    private AmazonNotificationSubscriptionService subscriptionService;


    @Override
    public String grantAuthInfo(AuthorizationModelV0 authorizationModel) {
        return new StringBuilder(grantAuthHost)
                .append("?response_type=code")
                .append("&scope=advertising::campaign_management")
                .append("&client_id=").append(authorizationModel.getAppKey())
                .append("&redirect_uri=").append("http://localhost")
                .append("&state=").append(authorizationModel.getState())
                .toString();
    }

    @Override
    public String grantAuthInfo(Long appId, String region, AuthorizationModel authorizationModel) {

        StringBuilder urlBuilder = new StringBuilder(grantAuthHost)
                .append("?response_type=code")
                .append("&client_id=").append(authorizationModel.getAppKey())
                .append("&redirect_uri=").append(authorizationModel.getCallbackUrl())
                .append("&state=").append(authorizationModel.getState());
        if ("AMAZON_ADV".equalsIgnoreCase(authorizationModel.getAuthType())) {
            urlBuilder.append("&scope=advertising::campaign_management");
        }

        return urlBuilder.toString();
    }

    @Override
    public AuthorizationModelV0 grantAccessToken(AuthorizationModelV0 authorizationModel) {

//        AdsTokenResult tokenResult = amazonAdsAdapter.exchangeToken(authorizationModel.getGrantCode());


        Request request = new Request.Builder()
                //https://api.amazon.com/auth/o2/token
                .url(host + "/auth/o2/token")
                .post(new FormBody.Builder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", authorizationModel.getAppKey())
                        .add("client_secret", authorizationModel.getAppSecret())
                        .add("code", authorizationModel.getGrantCode())
                        .add("redirect_uri", "http://localhost/auth/callback/amazon_adv")
                        .build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            String retResult = response.body().string();
            log.info("exchangeAccessToken: {}", retResult);
            if (!response.isSuccessful()) {
                throw exception(CROSS_API_ERROR, "refresh access token failed, code: " + response.code() + ", message: " + response.message());
            }
            return JsonUtils.parseObject(retResult, AuthorizationModelV0.class);

        } catch (IOException e) {
            throw exception(CROSS_API_ERROR, "refresh access token failed");
        }

    }

    @Override
    public AuthorizationModelV0 refreshAccessToken(AuthorizationModelV0 authorizationModel) {
        Request request = new Request.Builder()
                .url(host + "/auth/o2/token")
                .post(new FormBody.Builder()
                        .add("grant_type", "refresh_token")
                        .add("client_id", authorizationModel.getAppKey())
                        .add("client_secret", authorizationModel.getAppSecret())
                        .add("refresh_token", authorizationModel.getRefreshToken())
                        .build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw exception(CROSS_API_ERROR, "refresh access token failed, code: " + response.code() + ", message: " + response.message());
            }
            String respBody = response.body().string();
            return JsonUtils.parseObject(respBody, AuthorizationModelV0.class);

        } catch (IOException e) {
            throw exception(CROSS_API_ERROR, "refresh access token failed");
        }
    }

    @Override
    public AuthorizationModelV0 renewRefreshToken(AuthorizationModelV0 authorizationModel) {
        return null;
    }


    @Async
    @Override
    public void postAuthorization(AuthorizationModel authorizationModel) {
        if ("AMAZON_SP".equals(authorizationModel.getAuthType())) {
            spapiPostAuthorization(authorizationModel);
        } else {
            // 如果是ADV_API授权, 初始化profile
            adsApiPostAuthorization(authorizationModel);
        }

    }

    /**
     *  创建/关联SHOP
     * @param authorizationModel
     */
    private void spapiPostAuthorization(AuthorizationModel authorizationModel) {
        // 拉取账号信息, 包括参与的市场(对应到店铺)
        ApiResponse<Account> response = amazonAccountService.getAccount(new ApiRequest<>()
                        .setCrossPlatform(CrossPlatformEnum.of(authorizationModel.getPlatform()))
                        .setAuthorizationModel(authorizationModel)
//
        );
        if (!response.success()) {
            log.error("");
            return;
        }

        Account account = response.getData();

        // 1. 查询或创建卖家账号 (PlatformAccount)
        String name = account.getBusiness() != null && StringUtils.isNotEmpty(account.getBusiness().getName())
                ? account.getBusiness().getName() : "Amazon Account - " + authorizationModel.getSellerId();
        String registrationNumber = account.getBusiness() != null ? account.getBusiness().getCompanyRegistrationNumber() : null;
        String address = account.getBusiness() != null ? JsonUtils.toJsonString(account.getBusiness().getRegisteredBusinessAddress()) : null;

        PlatformAccountModel platformAccount = systemAuthService.getOrCreatePlatformAccount(PlatformAccountModel.builder()
                .platform(CrossPlatformEnum.AMAZON.getCode())
                .name(name)
                .registrationNumber(registrationNumber)
                .address(address)
                .businessType(account.getBusinessType() != null ? account.getBusinessType().getValue() : null)
                .build());

        if (platformAccount == null) {
            log.error("[spapiPostAuthorization] Failed to create or load platform account for sellerId: {}", authorizationModel.getSellerId());
            return;
        }

        // 2. 遍历市场参与信息，创建或关联店铺

        final Long finalAccountId = platformAccount.getId();

        account.getMarketplaceParticipationList().forEach(marketplace -> {
            Participation participation = marketplace.getParticipation();
            // 只支持生效的和亚马逊平台的市场, 非亚马逊平台市场不支持
            if (!participation.isIsParticipating() || marketplace.getMarketplace().getName().startsWith("Non-")) {
                return;
            }
            ShopModel shop = ShopModel.builder().name(marketplace.getStoreName() + "[" + marketplace.getMarketplace().getName() + "]")
                    .platform(CrossPlatformEnum.AMAZON.getValue())
                    .platformCode(CrossPlatformEnum.AMAZON.getCode())
                    .region(authorizationModel.getRegion())
                    .marketplace(marketplace.getMarketplace().getId())
                    .sellerId(authorizationModel.getSellerId())
                    .accountId(finalAccountId)
                    .language(marketplace.getMarketplace().getDefaultLanguageCode())
                    .currency(marketplace.getMarketplace().getDefaultCurrencyCode())
                    .countryCode(marketplace.getMarketplace().getCountryCode()).build();

            shop = systemShopService.createOrLoadShop(shop);

            systemAuthService.grantShopAuth(authorizationModel.getId(), shop.getId().longValue());

            // 创建订阅
            try{
                subscriptionService.setupNotificationSubscriptions(shop.getId().longValue());
            } catch (Exception e) {
                log.error("[spapiPostAuthorization] fail to setupNotificationSubscriptions, shopId={}[{}]", shop.getName(), shop.getId());
            }


        });

    }

    private void adsApiPostAuthorization(AuthorizationModel authorizationModel) {
        log.info("[adsApiPostAuthorization] 开始处理广告授权: sellerId={}, region={}",
                authorizationModel.getSellerId(), authorizationModel.getRegion());


        AdsRequest<AuthorizationModel> request = new AdsRequest<AuthorizationModel>().setRequest(authorizationModel);
        AdsResponse<List<AdsAccountWithMetaData>> accountResponse = amazonProfileNewService.queryAccounts(request);

        Map<Long, AdsAccountWithMetaData> profile2Account = Maps.newHashMap();
        for (AdsAccountWithMetaData account : accountResponse.getData()) {
            if (CollectionUtils.isEmpty(account.getAlternateIds())) {
                continue;
            }
            for (AlternateId alternateId : account.getAlternateIds()) {
                if (alternateId.getProfileId() == null) {
                    continue;
                }
                profile2Account.putIfAbsent(alternateId.getProfileId().longValue(), account);
            }
        }

        AdsResponse<List<Profile>> response = amazonProfileNewService.queryProfiles(request);

        if (!response.isSuccess()) {
            log.error("获取Profile异常, error={}", response.getMessage());
            return;
        }

        for (Profile p : response.getData()) {
            log.info("Profile: {}", p);

            if (p.getAccountInfo() == null) {
                log.warn("Profile accountInfo is null, skip: {}", p.getProfileId());
                continue;
            }
            String sellerId = p.getAccountInfo().getId();
            String marketplaceId = p.getAccountInfo().getMarketplaceStringId();
            ShopModel shop = systemShopService.getShopBySellerIdAndMarketplaceId(sellerId, marketplaceId);
            Long shopId = shop != null ? shop.getId().longValue() : null;
            if (shopId == null) {
                throw exception(CROSS_SERVICE_ERROR, "请先进行店铺授权");
            }

            AdsAccountWithMetaData accountInfo = profile2Account.get(p.getProfileId());
            if (accountInfo == null) {
                log.warn("AccountInfo missing for Profile ID, skip: {}", p.getProfileId());
                continue;
            }
            AdsAccountDO adsAccountDO = amazonProfileNewService.saveOrUpdateAdsAccount(AdsAccountDO.builder()
                    .name(accountInfo.getAccountName())
                            .shopId(shopId)
                    .externalAccountId(accountInfo.getAdsAccountId())
                    .lastSyncedAt(LocalDateTime.now())
                    .build());

            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(authorizationModel.getRegion());

            var countryCodeEnum = p.getCountryCode();
            var currencyCodeEnum = p.getCurrencyCode();
            var timezoneEnum = p.getTimezone();

            AdsAmazonProfileDO profileDO = AdsAmazonProfileDO.builder()
                    .sellerId(sellerId)
                    .accountId(adsAccountDO.getId())
                    .profileId(String.valueOf(p.getProfileId()))
                    .countryCode(countryCodeEnum != null ? countryCodeEnum.getValue() : null)
                    .region(regionEnum.name())
                    .currencyCode(currencyCodeEnum != null ? currencyCodeEnum.name() : null)
                    .timezone(timezoneEnum != null ? timezoneEnum.getValue() : null)
                    .shopId(shopId)
                    .entityId(sellerId)
                    .status("ENABLED")
                    .build();

            // saveOrUpdate profile
            profileDO = amazonProfileNewService.saveOrUpdateProfile(profileDO);

            // 绑定shopId和auth
            systemAuthService.grantShopAuth(authorizationModel.getId(), shop.getId().longValue());

            // 初始化profile订阅信息
            adsAmazonStreamService.createStreamSubscription(profileDO);

            log.info("初始化Profile[{}], SellerId={}, Region={}, ShopId={}", profileDO.getProfileId(), profileDO.getSellerId(), profileDO.getRegion(), profileDO.getShopId());
        }
    }
}
