package com.hzltd.module.amz.api.auth;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.spapi.AmazonAccountService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.spapi.models.sellers.v1.Account;
import software.amazon.spapi.models.sellers.v1.Participation;

import java.io.IOException;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.CROSS_API_ERROR;

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
    public AuthorizationModelV0 grantAccessToken(AuthorizationModelV0 authorizationModel) {
        Request request = new Request.Builder()
                .url(host + "/auth/o2/token")
                .post(new FormBody.Builder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", authorizationModel.getAppKey())
                        .add("client_secret", authorizationModel.getAppSecret())
                        .add("code", authorizationModel.getGrantCode())
                        .add("redirect_uri", "http://localhost")
                        .build())
                .build();


        return null;
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


    @Override
    public void postAuthorization(AuthorizationModel authorizationModel) {
        // todo -- 根据authScope来判断

        if ("AMAZON_SP".equals(authorizationModel.getAuthScope())) {
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
            if (!participation.isIsParticipating()) {
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

        });

    }

    private void adsApiPostAuthorization(AuthorizationModel authorizationModel) {
        // 获取profile, 筛选类型是seller的account, 保存account信息


        // 初始化profile, 关联shopId(sellerId + marketplace)


        // 初始化profile的订阅等信息


    }
}
