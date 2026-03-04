package com.hzltd.module.erplus.adv.adapter.amazon;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.*;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.AmazonAdsApiService;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.AmazonSpOptimizationRuleApiService;
import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsTokenResult;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.enums.amz.AmazonRegionEnum;
import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.adv.adapter.amazon.service.AdsAmazonProfileService;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdGroupVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsAdVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsCampaignVO;
import com.hzltd.module.erplus.adv.metadata.vo.AdsKeywordVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Amazon Ads 平台适配器实现
 */
@Slf4j
@Component
public class AmazonAdsAdapter extends AbstractAmazonAdsAdapter implements AdsPlatformAdapter {

    @Resource
    private AdsAuthService adsAuthService;
    @Resource
    private AmazonAdsApiService amazonAdsApiService;
    @Resource
    private AmazonSpOptimizationRuleApiService amazonSpOptimizationRuleApiService;
    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;
    @Resource
    private AdsAmazonProfileService adsAmazonProfileService;
    @Resource
    private AmazonAdvReportService amazonAdvReportService;

    @Override
    public AdsPlatformEnum getPlatform() {
        return AdsPlatformEnum.AMAZON;
    }

    @Override
    public String getAuthorizeUrl(String state) {
        String clientId = getRequiredConfig(AMZ_CLIENT_ID);
        String redirectUri = getRequiredConfig(AMZ_REDIRECT_URI);

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
        String endpoint = AmazonRegionEnum.NA.getAdsEndpoint();

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

    @Override
    public List<AdsCampaignVO> queryCampaigns(Long accountId, AdsQueryRequest request) {
        log.info("[queryCampaigns][Amazon] 查询广告计划, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsCampaignVO> allCampaigns = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            AdsSpCampaignQueryRequest spQuery = AdsSpCampaignQueryRequest.from(request);
            AdsSpCampaignQueryResp response = executeApiPost(credential, profile.getProfileId(), baseUrl + "/sp/campaigns/list",
                    null, JsonUtils.toJsonString(spQuery), "application/vnd.spCampaign.v3+json",
                    resp -> JsonUtils.parseObject(resp, AdsSpCampaignQueryResp.class));

            if (response != null && response.getCampaigns() != null) {
                response.getCampaigns().forEach(c -> allCampaigns.add(c.toVO()));
            }
        }
        return allCampaigns;
    }

    @Override
    public List<AdsAdGroupVO> queryGroups(Long accountId, AdsQueryRequest request) {
        log.info("[queryGroups][Amazon] 查询广告组, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsAdGroupVO> allAdGroups = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            AdsSpAdGroupQueryRequest spQuery = AdsSpAdGroupQueryRequest.from(request);
            AdsSpAdGroupQueryResp response = executeApiPost(credential, profile.getProfileId(), baseUrl + "/sp/adGroups/list",
                    null, JsonUtils.toJsonString(spQuery), "application/vnd.spAdGroup.v3+json",
                    resp -> JsonUtils.parseObject(resp, AdsSpAdGroupQueryResp.class));

            if (response != null && response.getAdGroups() != null) {
                response.getAdGroups().forEach(g -> allAdGroups.add(g.toVO()));
            }
        }
        return allAdGroups;
    }

    @Override
    public List<AdsKeywordVO> queryTargets(Long accountId, AdsQueryRequest request) {
        log.info("[queryTargets][Amazon] 查询关键词和定向, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsKeywordVO> allTargets = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            AdsSpKeywordQueryRequest spQuery = AdsSpKeywordQueryRequest.from(request);
            AdsSpKeywordQueryResp response = executeApiPost(credential, profile.getProfileId(), baseUrl + "/sp/keywords/list",
                    null, JsonUtils.toJsonString(spQuery), "application/vnd.spKeyword.v3+json",
                    resp -> JsonUtils.parseObject(resp, AdsSpKeywordQueryResp.class));

            if (response != null && response.getKeywords() != null) {
                response.getKeywords().forEach(k -> allTargets.add(k.toVO()));
            }
        }
        return allTargets;
    }

    @Override
    public List<AdsAdVO> queryAds(Long accountId, AdsQueryRequest request) {
        log.info("[queryAds][Amazon] 查询广告, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsAdVO> allAds = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            AdsSpProductAdQueryRequest spQuery = AdsSpProductAdQueryRequest.from(request);
            AdsSpProductAdQueryResp response = executeApiPost(credential, profile.getProfileId(), baseUrl + "/sp/productAds/list",
                    null, JsonUtils.toJsonString(spQuery), "application/vnd.spProductAd.v3+json",
                    resp -> JsonUtils.parseObject(resp, AdsSpProductAdQueryResp.class));

            if (response != null && response.getProductAds() != null) {
                response.getProductAds().forEach(a -> allAds.add(a.toVO()));
            }
        }
        return allAds;
    }

    @Override
    public Boolean updateStatus(Long accountId, AdsStatusUpdateRequest request) {
        log.info("[updateStatus][Amazon] 开始更新状态, accountId: {}, request: {}", accountId, request);
        // 原有实现可能为空或默认返回 true，这里按需补充逻辑
        return true;
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
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(account.getId());
        if (credential == null) {
            log.error("[postAccountAction][Amazon] 找不到凭证: accountId={}", account.getId());
            return;
        }
        // 核心同步逻辑已转移到 AdsAmazonProfileService
        adsAmazonProfileService.syncProfiles(account, credential);
    }

    private Map<String, List<String>> getProfileGroupByCountry(AdsAccountDO account) {
        AmzAccountVO amzAccount = JsonUtils.parseObject(account.getExtConfig(), AmzAccountVO.class);
        if (amzAccount == null || CollectionUtils.isEmpty(amzAccount.getAlternateIds())) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> result = Maps.newHashMap();
        for (Map<String, String> item : amzAccount.getAlternateIds()) {
            if (item.containsKey("profileId") && StringUtils.isNotEmpty(item.get("profileId"))) {
                String countryCode = item.get("countryCode");
                if (!result.containsKey(countryCode)) {
                    result.put(countryCode, Lists.newArrayList());
                }
                result.get(countryCode).add(item.get("profileId"));
            }
        }
        return result;
    }


    private Set<AmazonRegionEnum> getAccountSupportRegion(AdsAccountDO account) {
        AmzAccountVO amzAccount = JsonUtils.parseObject(account.getExtConfig(), AmzAccountVO.class);
        if (amzAccount == null || amzAccount.getCountryCodes() == null) return Collections.emptySet();
        Set<AmazonRegionEnum> result = Sets.newHashSet();
        amzAccount.getCountryCodes().forEach(code -> {
            result.add(AmazonRegionEnum.ofCountryCode(code));
        });
        return result.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public String postOptimizationRuleCreate(AdsAccountDO account, String profileId, Object saveReqVO) {
        return UUID.randomUUID().toString();
    }

    @Override
    public void postOptimizationRuleAssociate(AdsAccountDO account, String campaignId, String profileId, Object associateReqVO) {
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(account.getId());
        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
        String baseUrl = "https://" + regionEnum.getAdsEndpoint();

        amazonSpOptimizationRuleApiService.associateRules(credential,
                profile.getEntityId(), profile.getProfileId(), baseUrl, campaignId,
                (com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleAssociateReqVO) associateReqVO);
    }

    @Override
    public void executePerformanceSyncTask(AdsSyncTaskDO task) {
        // 核心任务分发与状态管理逻辑已迁移到 AmzSpReportParser
        amazonAdvReportService.executeSyncTask(task);
    }
}
