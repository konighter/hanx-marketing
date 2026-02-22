package com.hzltd.module.erplus.adv.adapter.amazon;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiClient;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.*;
import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.adapter.model.AdsTokenResult;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdGroupVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsCampaignVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsKeywordVO;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADS_PLATFORM_NOT_SUPPORTED;

@Slf4j
public abstract class AbstractAmazonAdsAdapter {

    protected static final String AMZ_CLIENT_ID = "amz_ads_client_id";
    protected static final String AMZ_CLIENT_SECRET = "amz_ads_client_secret";
    protected static final String AMZ_REDIRECT_URI = "amz_ads_redirect_uri";
    protected static final String AMZ_TOKEN_URL = "https://api.amazon.com/auth/o2/token";

    protected final OkHttpClient httpClient = new OkHttpClient();

    @Resource
    protected ConfigService configService;

    @Resource
    protected AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Resource
    protected AdsAccountCredentialMapper adsAccountCredentialMapper;

    @Resource
    protected AdsAccountMapper adsAccountMapper;

    protected AdsAccountDO getAdsAccount(Long accountId) {
        return adsAccountMapper.selectById(accountId);
    }

    protected AdsAccountCredentialDO getAdsAccountCredential(Long accountId) {
        AdsAccountDO adsAccount = adsAccountMapper.selectById(accountId);
        if (adsAccount == null || adsAccount.getCredentialId() == null) {
            return null;
        }
        return adsAccountCredentialMapper.selectById(adsAccount.getCredentialId());
    }

    public String getRequiredConfig(String key) {
        return Optional.ofNullable(configService.getConfigByKey(key))
                .map(ConfigDO::getValue)
                .orElseThrow(() -> {
                    log.error("[getRequiredConfig][Amazon] 缺失关键配置: {}", key);
                    return exception(ADS_PLATFORM_NOT_SUPPORTED);
                });
    }

    protected ApiClient createApiClient(String baseUrl, String accessToken, String profileId) {
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(baseUrl);
        apiClient.setRequestInterceptor(builder -> {
            builder.header("Authorization", "Bearer " + accessToken);
            builder.header("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID));
            if (profileId != null) {
                builder.header("Amazon-Advertising-API-Scope", profileId);
            }
        });
        return apiClient;
    }

    protected List<AdsAmazonProfileDO> getProfiles(Long accountId) {
        return adsAmazonProfileMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdsAmazonProfileDO>()
                        .eq(AdsAmazonProfileDO::getAccountId, accountId));
    }

    protected AdsTokenResult executeTokenRequest(RequestBody body) {
        Request request = new Request.Builder()
                .url(AMZ_TOKEN_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body().string();
            if (!response.isSuccessful()) {
                log.error("[executeTokenRequest][Amazon] 请求失败, code: {}, body: {}", response.code(), respBody);
                throw new RuntimeException("Amazon Token Request Failed: " + respBody);
            }

            JSONObject json = JSONUtil.parseObj(respBody);
            return AdsTokenResult.builder()
                    .accessToken(json.getStr("access_token"))
                    .refreshToken(json.getStr("refresh_token"))
                    .expiresAt(LocalDateTime.now().plusSeconds(json.getLong("expires_in", 3600L)))
                    .externalAccountId(json.getStr("user_id"))
                    .tokenType(json.getStr("token_type"))
                    .scope(json.getStr("scope"))
                    .build();
        } catch (IOException e) {
            log.error("[executeTokenRequest][Amazon] 网络异常", e);
            throw new RuntimeException("Amazon Token Request Error", e);
        }
    }

    protected <T> T executeGetRequest(AdsAccountCredentialDO credential, String profileId, String url,
            List<Pair<String, String>> params, Function<String, T> callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID))
                .addHeader("Authorization", "Bearer " + credential.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .get();
        if (StringUtils.isNotEmpty(profileId)) {
            builder.addHeader("Amazon-Advertising-API-Scope", profileId);
        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String respBody = response.body().string();
            return callback.apply(respBody);
        } catch (IOException e) {
            log.error("[executeGetRequest][Amazon] Url {} 网络异常", url, e);
        }
        return null;
    }

    protected <T> T executePostequest(AdsAccountCredentialDO credential, String profileId, String url,
            List<Pair<String, String>> params, String jsonBody, Function<String, T> callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Amazon-Advertising-API-ClientId", getRequiredConfig(AMZ_CLIENT_ID))
                .addHeader("Authorization", "Bearer " + credential.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")));
        if (StringUtils.isNotEmpty(profileId)) {
            builder.addHeader("Amazon-Advertising-API-Scope", profileId);
        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String respBody = response.body().string();
            return callback.apply(respBody);
        } catch (IOException e) {
            log.error("[executePostequest][Amazon] Url {} 网络异常", url, e);
        }
        return null;
    }

    @Data
    public static class AmzProfileVO {
        String profileId;
        String countryCode;
        String currencyCode;
        String timezone;

        AmzAccountVO accountInfo;
    }

    @Data
    public static class AmzAccountVO {
        // from account
        String accountName;
        String adsAccountId;
        List<String> countryCodes;
        List<Map<String, String>> alternateIds;

        // from profile
        String id;
        String name;
        String marketplaceStringId;
        String type;
        String subType;
    }

    protected SPQueryCampaignRequest convertQueryCampaignRequest(AdsQueryRequest request) {
        SPQueryCampaignRequest spRequest = new SPQueryCampaignRequest();
        if (request == null) {
            return spRequest;
        }

        if (CollectionUtils.isNotEmpty(request.getCampaignIds())) {
            spRequest.setCampaignIdFilter(new SPCampaignCampaignIdFilter().include(request.getCampaignIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getCampaignNames())) {
            spRequest.setNameFilter(new SPCampaignNameFilter().include(request.getCampaignNames()));
        }

        if (StringUtils.isNotEmpty(request.getNextToken())) {
            spRequest.setNextToken(request.getNextToken());
        }

        if (request.getLimit() != null) {
            spRequest.setMaxResults(request.getLimit());
        }

        return spRequest;
    }

    protected SPQueryAdGroupRequest convertQueryAdGroupRequest(AdsQueryRequest request) {
        SPQueryAdGroupRequest spRequest = new SPQueryAdGroupRequest();
        if (request == null) {
            return spRequest;
        }

        if (CollectionUtils.isNotEmpty(request.getAdGroupIds())) {
            spRequest.setAdGroupIdFilter(new SPAdGroupAdGroupIdFilter().include(request.getAdGroupIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getAdGroupNames())) {
            spRequest.setNameFilter(new SPAdGroupNameFilter().include(request.getAdGroupNames()));
        }

        if (CollectionUtils.isNotEmpty(request.getCampaignIds())) {
            spRequest.setCampaignIdFilter(new SPAdGroupCampaignIdFilter().include(request.getCampaignIds()));
        }

        if (StringUtils.isNotEmpty(request.getNextToken())) {
            spRequest.setNextToken(request.getNextToken());
        }

        if (request.getLimit() != null) {
            spRequest.setMaxResults(request.getLimit());
        }

        return spRequest;
    }

    protected SPQueryAdRequest convertQueryAdRequest(AdsQueryRequest request) {
        SPQueryAdRequest spRequest = new SPQueryAdRequest();
        if (request == null) {
            return spRequest;
        }

        if (CollectionUtils.isNotEmpty(request.getAdIds())) {
            spRequest.setAdIdFilter(new SPAdAdIdFilter().include(request.getAdIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getAdGroupIds())) {
            spRequest.setAdGroupIdFilter(new SPAdAdGroupIdFilter().include(request.getAdGroupIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getCampaignIds())) {
            spRequest.setCampaignIdFilter(new SPAdCampaignIdFilter().include(request.getCampaignIds()));
        }

        if (StringUtils.isNotEmpty(request.getNextToken())) {
            spRequest.setNextToken(request.getNextToken());
        }

        if (request.getLimit() != null) {
            spRequest.setMaxResults(request.getLimit());
        }

        return spRequest;
    }

    protected SPQueryTargetRequest convertQueryTargetRequest(AdsQueryRequest request) {
        SPQueryTargetRequest spRequest = new SPQueryTargetRequest();
        if (request == null) {
            return spRequest;
        }

        if (CollectionUtils.isNotEmpty(request.getTargetIds())) {
            spRequest.setTargetIdFilter(new SPTargetTargetIdFilter().include(request.getTargetIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getAdGroupIds())) {
            spRequest.setAdGroupIdFilter(new SPTargetAdGroupIdFilter().include(request.getAdGroupIds()));
        }

        if (CollectionUtils.isNotEmpty(request.getCampaignIds())) {
            spRequest.setCampaignIdFilter(new SPTargetCampaignIdFilter().include(request.getCampaignIds()));
        }

        if (StringUtils.isNotEmpty(request.getNextToken())) {
            spRequest.setNextToken(request.getNextToken());
        }

        if (request.getLimit() != null) {
            spRequest.setMaxResults(request.getLimit());
        }

        return spRequest;
    }

    protected AdsCampaignVO convertCampaignResp(SPCampaign campaign) {
        BigDecimal dailyBudget = BigDecimal.ZERO;
        if (campaign.getBudgets() != null && !campaign.getBudgets().isEmpty()) {
            try {
                SPBudget budget = campaign.getBudgets().get(0);
                if (budget.getBudgetValue() != null
                        && budget.getBudgetValue().getMonetaryBudgetValue() != null
                        && budget.getBudgetValue().getMonetaryBudgetValue()
                                .getMonetaryBudgetValue() != null
                        && budget.getBudgetValue().getMonetaryBudgetValue().getMonetaryBudgetValue()
                                .getMonetaryBudget() != null) {
                    Double amount = budget.getBudgetValue().getMonetaryBudgetValue()
                            .getMonetaryBudgetValue().getMonetaryBudget().getValue();
                    if (amount != null) {
                        dailyBudget = BigDecimal.valueOf(amount);
                    }
                }
            } catch (Exception e) {
                log.warn("[convertCampaignResp][Amazon] 解析广告计划预算失败, campaignId: {}",
                        campaign.getCampaignId());
            }
        }

        return AdsCampaignVO.builder()
                .externalId(campaign.getCampaignId())
                .name(campaign.getName())
                .campaignType(
                        campaign.getAdProduct() != null ? campaign.getAdProduct().getValue() : "SP")
                .status(campaign.getState() != null ? campaign.getState().getValue() : "")
                .dailyBudget(dailyBudget)
                .extData(JsonUtils.toJsonString(campaign))
                .build();
    }

    protected AdsAdGroupVO convertAdGroupResp(SPAdGroup adGroup) {
        return AdsAdGroupVO.builder()
                .externalId(adGroup.getAdGroupId())
                .campaignExternalId(adGroup.getCampaignId())
                .name(adGroup.getName())
                .status(adGroup.getState() != null ? adGroup.getState().getValue() : "")
                .defaultBid(adGroup.getBid() != null && adGroup.getBid().getDefaultBid() != null
                        ? BigDecimal.valueOf(adGroup.getBid().getDefaultBid())
                        : BigDecimal.ZERO)
                .extData(JsonUtils.toJsonString(adGroup))
                .build();
    }

    protected AdsAdVO convertAdResp(SPAd ad) {
        AdsAdVO vo = AdsAdVO.builder()
                .externalId(ad.getAdId())
                .adGroupExternalId(ad.getAdGroupId())
                .status(ad.getState() != null ? ad.getState().getValue() : "")
                .extData(JsonUtils.toJsonString(ad))
                .build();

        // Extract SKU/ASIN from creative
        if (ad.getCreative() != null
                && ad.getCreative().getActualInstance() instanceof ProductCreative1) {
            ProductCreative1 pc1 = (ProductCreative1) ad.getCreative().getActualInstance();
            if (pc1.getProductCreative() != null
                    && pc1.getProductCreative().getProductCreativeSettings() != null) {
                SPProductCreativeSettings settings = pc1.getProductCreative()
                        .getProductCreativeSettings();
                if (settings.getAdvertisedProduct() != null) {
                    vo.setSku(settings.getAdvertisedProduct().getProductId());
                    vo.setAsin(settings.getAdvertisedProduct().getProductId());
                }
            }
        }
        return vo;
    }

    protected AdsKeywordVO convertTargetResp(SPTarget target) {
        AdsKeywordVO vo = AdsKeywordVO.builder()
                .externalId(target.getTargetId())
                .adGroupExternalId(target.getAdGroupId())
                .status(target.getState() != null ? target.getState().getValue() : "")
                .bid(target.getBid() != null && target.getBid().getBid() != null
                        ? BigDecimal.valueOf(target.getBid().getBid())
                        : BigDecimal.ZERO)
                .extData(JsonUtils.toJsonString(target))
                .build();

        // Extract keyword text and match type from targetDetails
        if (target.getTargetDetails() != null
                && target.getTargetDetails().getActualInstance() instanceof KeywordTarget1) {
            KeywordTarget1 kt1 = (KeywordTarget1) target.getTargetDetails().getActualInstance();
            if (kt1.getKeywordTarget() != null) {
                vo.setKeywordText(kt1.getKeywordTarget().getKeyword());
                vo.setMatchType(kt1.getKeywordTarget().getMatchType() != null
                        ? kt1.getKeywordTarget().getMatchType().getValue()
                        : "");
            }
        }
        return vo;
    }

}
