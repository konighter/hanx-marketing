package com.hzltd.module.erplus.adv.adapter.amazon;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdGroupsApi;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.AdsApi;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.CampaignsApi;
import com.hzltd.module.erplus.adv.adapter.amazon.api.sp.TargetsApi;
import com.hzltd.module.erplus.adv.adapter.amazon.client.ApiException;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.*;
import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsTokenResult;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.enums.AdsAmazonRegionEnum;
import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdGroupVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsCampaignVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsKeywordVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Amazon Ads 平台适配器实现
 */
@Slf4j
@Component
public class AmazonAdsAdapter extends AbstractAmazonAdsAdapter implements AdsPlatformAdapter {

    @Override
    public AdsPlatformEnum getPlatform() {
        return AdsPlatformEnum.AMAZON;
    }

    @Override
    public String getAuthorizeUrl(String state) {
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        String redirectUri = getRequiredConfig(AMZ_REDIRECT_URI);

        // Amazon LWA 授权链接
        return "https://www.amazon.com/ap/oa?client_id=" + clientId +
                "&scope=advertising::test:create_account%20advertising::campaign_management" +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&state=" + state;
    }

    @Override
    public AdsTokenResult exchangeToken(String code) {
        log.info("[exchangeToken][Amazon] 开始交换 Token, code: {}", code);
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        String clientSecret = getRequiredConfig(AMZ_CLIENT_SECRET);
        String redirectUri = getRequiredConfig(AMZ_REDIRECT_URI);

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        return executeTokenRequest(body);
    }

    @Override
    public AdsTokenResult refreshToken(AdsAccountCredentialDO credential) {
        log.info("[refreshToken][Amazon] 开始刷新 Token");
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        String clientSecret = getRequiredConfig(AMZ_CLIENT_SECRET);

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", credential.getRefreshToken())
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        return executeTokenRequest(body);
    }

    @Override
    public List<AdsAccountVO> fetchAdsAccounts(AdsAccountCredentialDO credential, Long shopId) {
        log.info("[fetchAdsAccounts][Amazon] 开始拉取广告主账号列表, CredentialId: {}", credential.getId());
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        // Default to NA for discovery
        String endpoint = AdsAmazonRegionEnum.NA.getEndpoint();

        return this.executePostequest(credential, null, "https://" + endpoint + "/adsAccounts/list", null, "", resp -> {

            JSONObject jsonResponse = JSONUtil.parseObj(resp);
            JSONArray adsAccountArray = jsonResponse.getJSONArray("adsAccounts");
            List<AmzAccountVO> amzAccounts = JSONUtil.toList(adsAccountArray, AmzAccountVO.class);
            if (CollUtil.isNotEmpty(amzAccounts)) {
                return amzAccounts.stream().map(a -> AdsAccountVO.builder()
                        .name(a.getAccountName())
                        .externalAccountId(a.getAdsAccountId())
                        .platformSpec(a)
                        .build()).collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        });
    }

    /**
     * 同步亚马逊广告 Profile 并关联到账号
     */
    public void syncAccountProfiles(AdsAccountDO account, AdsAccountCredentialDO credential) {
        log.info("[syncAccountProfiles][Amazon] 开始同步账号 {} ({}) 的站点 (Profiles)", account.getName(),
                account.getExternalAccountId());
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);

        for (AdsAmazonRegionEnum regionEnum : AdsAmazonRegionEnum.values()) {
            String endpoint = regionEnum.getEndpoint();
            log.info("[syncAccountProfiles][Amazon] 正在从 Region: {} 拉取 Profiles", regionEnum.name());
            String url = "https://" + endpoint + "/v2/profiles";

            List<AmzProfileVO> profiles = executeGetRequest(credential, null, url, null,
                    resp -> JSONUtil.toList(resp, AmzProfileVO.class));
            if (CollectionUtils.isEmpty(profiles)) {
                continue;
            }
            for (AmzProfileVO p : profiles) {
                String accountEntityId = this.getAccountEntityId(p.getCountryCode(), account);
                if (p.getAccountInfo() != null
                        && accountEntityId.equalsIgnoreCase(p.getAccountInfo().getId())) {
                    saveProfile(account.getId(), p.getProfileId(), p.getCountryCode(), regionEnum.name(),
                            p.getCurrencyCode(), p.getTimezone(), accountEntityId, account.getName());
                }
            }
        }
    }

    @Override
    public List<AdsCampaignVO> queryCampaigns(Long accountId, AdsQueryRequest request) {
        log.info("[queryCampaigns][Amazon] 查询广告计划, accountId: {}, request: {}", accountId, request);
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        AdsAccountCredentialDO credential = this.getAdsAccountCredential(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsCampaignVO> allCampaigns = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AdsAmazonRegionEnum regionEnum = AdsAmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getEndpoint();

            CampaignsApi campaignsApi = new CampaignsApi(
                    createApiClient(baseUrl, credential.getAccessToken(), profile.getProfileId()));
            try {
                SPCampaignSuccessResponse response = campaignsApi.sPQueryCampaign(clientId,
                        convertQueryCampaignRequest(request), null,
                        profile.getProfileId());
                if (response != null && response.getCampaigns() != null) {
                    for (SPCampaign campaign : response.getCampaigns()) {
                        allCampaigns.add(convertCampaignResp(campaign));
                    }
                }
            } catch (ApiException e) {
                log.error("[queryCampaigns][Amazon] 查询广告计划失败, profileId: {}", profile.getProfileId(), e);
            }
        }
        return allCampaigns;
    }

    @Override
    public List<AdsAdGroupVO> queryGroups(Long accountId, AdsQueryRequest request) {
        log.info("[queryGroups][Amazon] 查询广告组, accountId: {}, request: {}", accountId, request);
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        AdsAccountCredentialDO credential = this.getAdsAccountCredential(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsAdGroupVO> allAdGroups = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AdsAmazonRegionEnum regionEnum = AdsAmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getEndpoint();

            AdGroupsApi adGroupsApi = new AdGroupsApi(
                    createApiClient(baseUrl, credential.getAccessToken(), profile.getProfileId()));
            try {
                SPAdGroupSuccessResponse response = adGroupsApi.sPQueryAdGroup(clientId,
                        convertQueryAdGroupRequest(request), null,
                        profile.getProfileId());
                if (response != null && response.getAdGroups() != null) {
                    for (SPAdGroup adGroup : response.getAdGroups()) {
                        allAdGroups.add(convertAdGroupResp(adGroup));
                    }
                }
            } catch (ApiException e) {
                log.error("[queryGroups][Amazon] 查询广告组失败, profileId: {}", profile.getProfileId(), e);
            }
        }
        return allAdGroups;
    }

    @Override
    public List<AdsKeywordVO> queryTargets(Long accountId, AdsQueryRequest request) {
        log.info("[queryTargets][Amazon] 查询关键词和定向, accountId: {}, request: {}", accountId, request);
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        AdsAccountCredentialDO credential = this.getAdsAccountCredential(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsKeywordVO> allTargets = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AdsAmazonRegionEnum regionEnum = AdsAmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getEndpoint();

            TargetsApi targetsApi = new TargetsApi(
                    createApiClient(baseUrl, credential.getAccessToken(), profile.getProfileId()));
            try {
                SPTargetSuccessResponse response = targetsApi.sPQueryTarget(clientId,
                        convertQueryTargetRequest(request), null,
                        profile.getProfileId());
                if (response != null && response.getTargets() != null) {
                    for (SPTarget target : response.getTargets()) {
                        allTargets.add(convertTargetResp(target));
                    }
                }
            } catch (ApiException e) {
                log.error("[queryTargets][Amazon] 查询关键词和定向失败, profileId: {}", profile.getProfileId(), e);
            }
        }
        return allTargets;
    }

    @Override
    public List<AdsAdVO> queryAds(Long accountId, AdsQueryRequest request) {
        log.info("[queryAds][Amazon] 查询广告, accountId: {}, request: {}", accountId, request);
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        AdsAccountCredentialDO credential = this.getAdsAccountCredential(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsAdVO> allAds = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AdsAmazonRegionEnum regionEnum = AdsAmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getEndpoint();

            AdsApi adsApi = new AdsApi(createApiClient(baseUrl, credential.getAccessToken(), profile.getProfileId()));
            try {
                SPAdSuccessResponse response = adsApi.sPQueryAd(clientId, convertQueryAdRequest(request), null,
                        profile.getProfileId());
                if (response != null && response.getAds() != null) {
                    for (SPAd ad : response.getAds()) {
                        allAds.add(convertAdResp(ad));
                    }
                }
            } catch (ApiException e) {
                log.error("[queryAds][Amazon] 查询广告失败, profileId: {}", profile.getProfileId(), e);
            }
        }
        return allAds;
    }

    @Override
    public Boolean updateStatus(Long accountId, AdsStatusUpdateRequest request) {
        log.info("[updateStatus][Amazon] 开始更新状态, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = this.getAdsAccountCredential(accountId);
        List<AdsAmazonProfileDO> profiles = getProfiles(accountId);
        if (profiles.isEmpty()) {
            return false;
        }

        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        SPUpdateState targetState = SPUpdateState.fromValue(request.getStatus().toUpperCase());

        boolean anySuccess = false;
        for (AdsAmazonProfileDO profile : profiles) {
            AdsAmazonRegionEnum regionEnum = AdsAmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getEndpoint();

            CampaignsApi campaignsApi = new CampaignsApi(
                    createApiClient(baseUrl, credential.getAccessToken(), profile.getProfileId()));
            SPUpdateCampaignRequest amzRequest = new SPUpdateCampaignRequest()
                    .addCampaignsItem(new SPCampaignUpdate()
                            .campaignId(request.getCampaignId())
                            .state(targetState));

            try {
                campaignsApi.sPUpdateCampaign(clientId, null, profile.getProfileId(), amzRequest);
                anySuccess = true;
                log.info("[updateStatus][Amazon] 成功在 profile {} 下更新 campaign {}", profile.getProfileId(),
                        request.getCampaignId());
                break;
            } catch (ApiException e) {
                log.debug("Attempt failed in profile {}: {}", profile.getProfileId(), e.getMessage());
            }
        }
        return anySuccess;
    }

    private List<AdsAmazonProfileDO> getFilteredProfiles(Long accountId, AdsQueryRequest request) {
        List<AdsAmazonProfileDO> profiles = getProfiles(accountId);
        if (request == null || request.getExtraParam() == null) {
            return profiles;
        }

        Object profileIdsObj = request.getExtraParam().get("profileIds");
        if (profileIdsObj instanceof List) {
            List<?> profileIds = (List<?>) profileIdsObj;
            if (!profileIds.isEmpty()) {
                return profiles.stream()
                        .filter(p -> profileIds.contains(p.getProfileId()))
                        .collect(Collectors.toList());
            }
        }
        return profiles;
    }

    @Override
    public void postAccountAction(AdsAccountDO account) {
        log.info("[postAccountAction][Amazon] 账号 {} 处理完成，正在同步站点 (Profiles)", account.getId());
        AdsAccountCredentialDO credential = adsAccountCredentialMapper.selectById(account.getCredentialId());
        if (credential == null) {
            log.error("[postAccountAction][Amazon] 找不到凭证 ID: {}", account.getCredentialId());
            return;
        }
        syncAccountProfiles(account, credential);
    }

    private String getAccountEntityId(String country, AdsAccountDO account) {
        AmzAccountVO amzAccount = JsonUtils.parseObject(account.getExtConfig(), AmzAccountVO.class);
        if (amzAccount == null || amzAccount.getAlternateIds() == null) {
            return "";
        }
        for (Map<String, String> conf : amzAccount.getAlternateIds()) {
            if (country.equalsIgnoreCase(conf.get("countryCode")) && StringUtils.isNotEmpty(conf.get("entityId"))) {
                return conf.get("entityId");
            }
        }
        return "";
    }

    private void saveProfile(Long accountId, String profileId, String countryCode, String region,
            String currency, String timezone, String entityId, String entityName) {
        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        if (profile == null) {
            profile = AdsAmazonProfileDO.builder()
                    .accountId(accountId)
                    .profileId(profileId)
                    .countryCode(countryCode)
                    .region(region)
                    .currencyCode(currency)
                    .timezone(timezone)
                    .entityId(entityId)
                    .entityName(entityName)
                    .status("ENABLED")
                    .build();
            adsAmazonProfileMapper.insert(profile);
        } else {
            profile.setAccountId(accountId);
            profile.setEntityId(entityId);
            profile.setEntityName(entityName);
            profile.setStatus("ENABLED");
            adsAmazonProfileMapper.updateById(profile);
        }
    }
}
