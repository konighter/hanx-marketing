package com.hzltd.module.amz.api.adv;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.adv.service.AdsAmazonProfileService;
import com.hzltd.module.amz.adv.service.AdsAmazonReportService;
import com.hzltd.module.amz.api.adv.model.sp.*;
import com.hzltd.module.amz.api.adv.v1.AmazonAdsApiService;
import com.hzltd.module.amz.api.adv.v1.AmazonSpOptimizationRuleApiService;
import com.hzltd.module.amz.api.enums.AmazonRegionEnum;
import com.hzltd.module.amz.controller.admin.vo.AmazonAdConfigVO;
import com.hzltd.module.amz.controller.admin.vo.AmazonAdGroupConfigVO;
import com.hzltd.module.amz.controller.admin.vo.AmazonCampaignConfigVO;
import com.hzltd.module.amz.controller.admin.vo.AmazonKeywordConfigVO;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.dal.dataobject.*;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordMapper;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.erplus.adv.enums.AdsStatusEnum;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleAssociateReqVO;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
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
    private AdsAmazonReportService adsAmazonReportService;
    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
    @Resource
    private AdsAdMapper adsAdMapper;
    @Resource
    private AdsKeywordMapper adsKeywordMapper;
    @Resource
    private ObjectMapper objectMapper;

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
    public List<AdsCampaignModel> queryCampaigns(Long accountId, AdsQueryRequest request) {
        log.info("[queryCampaigns][Amazon] accountId: {}", accountId);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsCampaignModel> allCampaigns = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            String profileId = profile.getProfileId();
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            // 1. 拉取广告活动列表
            AdsSpCampaignQueryResp response = amazonAdsApiService.listSpCampaigns(
                    credential, profileId, baseUrl, AdsSpCampaignQueryRequest.from(request));
            if (response == null || response.getCampaigns() == null) continue;

            List<String> campaignIds = response.getCampaigns().stream()
                    .map(AdsSpCampaign::getCampaignId).collect(Collectors.toList());

            // 2. 拉取广告活动级否定关键词
            AdsSpNegativeKeywordQueryResp negKwResp = amazonAdsApiService.listCampaignNegativeKeywords(
                    credential, profileId, baseUrl,
                    AdsSpNegativeKeywordQueryRequest.byCampaignIds(campaignIds));

            Map<String, List<AdsSpNegativeKeyword>> negKwMap = negKwResp != null && negKwResp.getNegativeKeywords() != null
                    ? negKwResp.getNegativeKeywords().stream().collect(Collectors.groupingBy(AdsSpNegativeKeyword::getCampaignId))
                    : Collections.emptyMap();

            // 3. 批量拉取广告活动关联的 Optimization Rules
            List<AdsSpOptimizationRule> apiRules = amazonAdsApiService.searchBiddingRulesByCampaignIds(
                    credential, profileId, baseUrl, campaignIds);

            Map<String, List<AdsSpOptimizationRule>> ruleMap = apiRules != null
                    ? apiRules.stream()
                        .filter(r -> r.getCampaignId() != null)
                        .collect(Collectors.groupingBy(AdsSpOptimizationRule::getCampaignId))
                    : Collections.emptyMap();

            // 4. 逐 Campaign 将否定词 + Optimization Rules 补充到 platformConfig
            for (AdsSpCampaign c : response.getCampaigns()) {
                AdsCampaignModel vo = c.toVO();
                AmazonCampaignConfigVO config = AmazonCampaignConfigVO.fromSpCampaign(c);
                config.setProfileId(profileId);

                // 补充 negativeKeywords
                List<AdsSpNegativeKeyword> negKwList = negKwMap.getOrDefault(c.getCampaignId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(negKwList)) {
                    config.setNegativeKeywords(negKwList);
                }

                // 补充 optimizationRules
                List<AdsSpOptimizationRule> optRules = ruleMap.getOrDefault(c.getCampaignId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(optRules)) {
                    config.setOptimizationRules(optRules);
                }

                vo.setPlatform(AdsPlatformEnum.AMAZON.getCode());
                vo.setAttributes(config.toAttributes());
                vo.setExtData(buildExtData(config));
                allCampaigns.add(vo);
            }
        }
        return allCampaigns;
    }


    @Override
    public List<AdsAdGroupModel> queryGroups(Long accountId, AdsQueryRequest request) {
        log.info("[queryGroups][Amazon] accountId: {}", accountId);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAmazonProfileDO> profiles = getFilteredProfiles(accountId, request);
        List<AdsAdGroupModel> allAdGroups = new ArrayList<>();

        for (AdsAmazonProfileDO profile : profiles) {
            String profileId = profile.getProfileId();
            AmazonRegionEnum regionEnum = AmazonRegionEnum.valueOf(profile.getRegion());
            String baseUrl = "https://" + regionEnum.getAdsEndpoint();

            // 1. 拉取广告组列表
            AdsSpAdGroupQueryResp response = amazonAdsApiService.listSpAdGroups(
                    credential, profileId, baseUrl, AdsSpAdGroupQueryRequest.from(request));
            if (response == null || response.getAdGroups() == null) continue;

            List<String> adGroupIds = response.getAdGroups().stream()
                    .map(AdsSpAdGroup::getAdGroupId).collect(Collectors.toList());

            // 2. 拉取关键词定向
            AdsSpKeywordQueryRequest kwQuery = AdsSpKeywordQueryRequest.builder()
                    .maxResults(200)
                    .adGroupIdFilter(AdsSpCommonQueryRequest.StringFilter.from(adGroupIds))
                    .campaignIdFilter(AdsSpCommonQueryRequest.StringFilter.from(request.getCampaignIds()))
                    .build();
            AdsSpKeywordQueryResp kwResp = amazonAdsApiService.listSpKeywords(credential, profileId, baseUrl, kwQuery);
            Map<String, List<AdsSpKeyword>> kwByGroup = kwResp != null && kwResp.getKeywords() != null
                    ? kwResp.getKeywords().stream().collect(Collectors.groupingBy(AdsSpKeyword::getAdGroupId))
                    : Collections.emptyMap();

            // 3. 拉取商品/品类定向
            AdsSpTargetQueryResp targetResp = amazonAdsApiService.listSpTargets(
                    credential, profileId, baseUrl, AdsSpTargetQueryRequest.byAdGroupIds(adGroupIds));
            Map<String, List<AdsSpTarget>> targetByGroup = targetResp != null && targetResp.getTargets() != null
                    ? targetResp.getTargets().stream().collect(Collectors.groupingBy(AdsSpTarget::getAdGroupId))
                    : Collections.emptyMap();

            // 4. 拉取广告组级否定关键词
            AdsSpNegativeKeywordQueryResp negKwResp = amazonAdsApiService.listAdGroupNegativeKeywords(
                    credential, profileId, baseUrl, AdsSpNegativeKeywordQueryRequest.byAdGroupIds(adGroupIds));
            Map<String, List<AdsSpNegativeKeyword>> negKwByGroup = negKwResp != null && negKwResp.getNegativeKeywords() != null
                    ? negKwResp.getNegativeKeywords().stream().collect(Collectors.groupingBy(AdsSpNegativeKeyword::getAdGroupId))
                    : Collections.emptyMap();

            // 5. 拉取否定商品定向
            AdsSpNegativeTargetQueryResp negTargetResp = amazonAdsApiService.listSpNegativeTargets(
                    credential, profileId, baseUrl, AdsSpNegativeTargetQueryRequest.byAdGroupIds(adGroupIds));
            Map<String, List<AdsSpNegativeTarget>> negTargetByGroup = negTargetResp != null && negTargetResp.getNegativeTargets() != null
                    ? negTargetResp.getNegativeTargets().stream().collect(Collectors.groupingBy(AdsSpNegativeTarget::getAdGroupId))
                    : Collections.emptyMap();

            // 6. 组装每个 AdGroup VO 并补充配置
            for (AdsSpAdGroup g : response.getAdGroups()) {
                AdsAdGroupModel vo = g.toVO();
                AmazonAdGroupConfigVO config = AmazonAdGroupConfigVO.fromSpAdGroup(g);
                config.setProfileId(profileId);

                // 1. 补充关键词定向
                List<AdsSpKeyword> kwList = kwByGroup.getOrDefault(g.getAdGroupId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(kwList)) {
                    config.setTargetingType("KEYWORD");
                    config.setKeywordTargetings(kwList.stream()
                            .map(k -> AmazonAdGroupConfigVO.KeywordTargeting.builder()
                                    .targetId(k.getKeywordId())
                                    .keywordText(k.getKeywordText())
                                    .matchType(k.getMatchType())
                                    .bid(k.getBid())
                                    .build())
                            .collect(Collectors.toList()));
                }

                // 2. 补充商品/品类定向
                List<AdsSpTarget> targetList = targetByGroup.getOrDefault(g.getAdGroupId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(targetList)) {
                    config.setTargetingType("PRODUCT");
                    List<AmazonAdGroupConfigVO.ProductTargeting> pts = new ArrayList<>();
                    for (AdsSpTarget t : targetList) {
                        if (CollUtil.isEmpty(t.getExpression())) continue;
                        for (AdsSpTarget.Expression expr : t.getExpression()) {
                            pts.add(AmazonAdGroupConfigVO.ProductTargeting.builder()
                                    .targetId(t.getTargetId())
                                    .expressionValue(expr.getValue())
                                    .expressionType(expr.getType())
                                    .bid(t.getBid())
                                    .build());
                        }
                    }
                    if (CollUtil.isNotEmpty(pts)) config.setProductTargetings(pts);
                }

                // 3. 补充否定关键词
                List<AdsSpNegativeKeyword> negKwList = negKwByGroup.getOrDefault(g.getAdGroupId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(negKwList)) {
                    config.setNegativeKeywords(negKwList);
                }

                // 4. 补充否定商品定向
                List<AdsSpNegativeTarget> negTargetList = negTargetByGroup.getOrDefault(g.getAdGroupId(), Collections.emptyList());
                if (CollUtil.isNotEmpty(negTargetList)) {
                    config.setNegativeTargetings(negTargetList);
                }

                vo.setPlatform(AdsPlatformEnum.AMAZON.getCode());
                vo.setAttributes(config.toAttributes());
                vo.setExtData(buildExtData(config));
                allAdGroups.add(vo);
            }
        }
        return allAdGroups;
    }

    @Override
    public List<AdsTargetModel> queryTargets(Long accountId, AdsQueryRequest request) {
        log.info("[queryTargets][Amazon] accountId: {}", accountId);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsTargetModel> allTargets = new ArrayList<>();
        for (AdsAmazonProfileDO profile : getFilteredProfiles(accountId, request)) {
            String profileId = profile.getProfileId();
            String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
            AdsSpKeywordQueryResp keywordResponse = amazonAdsApiService.listSpKeywords(
                    credential, profileId, baseUrl, AdsSpKeywordQueryRequest.from(request));
            if (keywordResponse != null && keywordResponse.getKeywords() != null) {
                keywordResponse.getKeywords().forEach(k -> {
                    AdsTargetModel vo = k.toVO();
                    AmazonKeywordConfigVO config = AmazonKeywordConfigVO.fromSpKeyword(k);
                    config.setProfileId(profileId);
                    vo.setPlatform(AdsPlatformEnum.AMAZON.getCode());
                    vo.setAttributes(config.toAttributes());
                    vo.setExtData(buildExtData(config));
                    allTargets.add(vo);
                });
            }

            // Sync targets (Product/Category targeting)
            AdsSpTargetQueryResp targetResponse = amazonAdsApiService.listSpTargets(
                    credential, profileId, baseUrl, AdsSpTargetQueryRequest.from(request));
            if (targetResponse != null && targetResponse.getTargets() != null) {
                targetResponse.getTargets().forEach(t -> {
                    AdsTargetModel vo = t.toVO();
                    // We can reuse AmazonKeywordConfigVO or just put profileId in extData
                    Map<String, Object> extData = new HashMap<>();
                    Map<String, Object> config = new HashMap<>();
                    config.put("profileId", profileId);
                    extData.put("platformConfig", config);
                    extData.put("isTarget", true);
                    vo.setExtData(extData);
                    allTargets.add(vo);
                });
            }
        }
        return allTargets;
    }

    @Override
    public List<AdsAdModel> queryAds(Long accountId, AdsQueryRequest request) {
        log.info("[queryAds][Amazon] accountId: {}", accountId);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        List<AdsAdModel> allAds = new ArrayList<>();
        for (AdsAmazonProfileDO profile : getFilteredProfiles(accountId, request)) {
            String profileId = profile.getProfileId();
            String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
            AdsSpProductAdQueryResp response = amazonAdsApiService.listSpProductAds(
                    credential, profileId, baseUrl, AdsSpProductAdQueryRequest.from(request));
            if (response != null && response.getProductAds() != null) {
                response.getProductAds().forEach(a -> {
                    AdsAdModel vo = a.toVO();
                    AmazonAdConfigVO config = AmazonAdConfigVO.fromSpProductAd(a);
                    config.setProfileId(profileId);
                    vo.setPlatform(AdsPlatformEnum.AMAZON.getCode());
                    vo.setAttributes(config.toAttributes());
                    vo.setExtData(buildExtData(config));
                    allAds.add(vo);
                });
            }
        }
        return allAds;
    }

    // ==================== 辅助方法 ====================



    /**
     * 将 platformConfig 对象写回 extData
     */
    private Object buildExtData(Object platformConfig) {
        Map<String, Object> extData = new HashMap<>();
        extData.put("platformConfig", platformConfig);
        return extData;
    }


    @Override
    public Boolean updateStatus(Long accountId, AdsStatusUpdateRequest request) {
        log.info("[updateStatus][Amazon] 开始更新状态, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        if (credential == null) {
            log.error("[updateStatus][Amazon] 缺少凭证: accountId={}", accountId);
            return false;
        }

        String profileId = request.getProfileId();
        if (StringUtils.isEmpty(profileId)) {
            profileId = getProfileId(request.getEntityType(), request.getLocalId());
        }
        if (StringUtils.isEmpty(profileId)) {
            log.error("[updateStatus][Amazon] 无法获取 ProfileId: entityType={}, localId={}", request.getEntityType(), request.getLocalId());
            return false;
        }

        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        if (profile == null) {
            log.error("[updateStatus][Amazon] 找不到 Profile: profileId={}", profileId);
            return false;
        }
        String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
        String entityId = request.getEntityId();
        String status = request.getStatus();


        switch (request.getEntityType()) {
            case CAMPAIGN:
                if (AdsStatusEnum.STOPPED.getCode().equals(status)) {
                    status = AdsStatusEnum.PAUSED.getCode();
                }
                AdsSpCampaign c = AdsSpCampaign.builder().campaignId(entityId).state(status).build();
                return amazonAdsApiService.updateSpCampaigns(credential, profileId, baseUrl, Collections.singletonList(c)) != null;
            case ADGROUP:
                AdsSpAdGroup g = AdsSpAdGroup.builder().adGroupId(entityId).state(status).build();
                return amazonAdsApiService.updateSpAdGroups(credential, profileId, baseUrl, Collections.singletonList(g)) != null;
            case AD:
                AdsSpProductAd a = AdsSpProductAd.builder().adId(entityId).state(status).build();
                return amazonAdsApiService.updateSpProductAds(credential, profileId, baseUrl, Collections.singletonList(a)) != null;
            case KEYWORD:
                AdsSpKeyword k = AdsSpKeyword.builder().keywordId(entityId).state(status).build();
                return amazonAdsApiService.updateSpKeywords(credential, profileId, baseUrl, Collections.singletonList(k)) != null;
            case TARGET:
                AdsSpTarget t = AdsSpTarget.builder().targetId(entityId).state(status).build();
                return amazonAdsApiService.updateSpTargets(credential, profileId, baseUrl, Collections.singletonList(t)) != null;
            default:
                log.warn("[updateStatus][Amazon] 不支持的实体类型: {}", request.getEntityType());
                return false;
        }
    }

    @Override
    public Boolean updateBudget(Long accountId, AdsBudgetUpdateRequest request) {
        log.info("[updateBudget][Amazon] 开始更新预算, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        if (credential == null) return false;

        String profileId = request.getProfileId();
        if (StringUtils.isEmpty(profileId)) {
            profileId = getProfileId(request.getEntityType(), request.getLocalId());
        }
        if (StringUtils.isEmpty(profileId)) return false;

        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        if (profile == null) return false;

        String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
        AdsSpCampaign c = AdsSpCampaign.builder()
                .campaignId(request.getEntityId())
                .budget(AdsSpCampaign.Budget.builder()
                        .budget(request.getBudget())
                        .budgetType("DAILY")
                        .build())
                .build();
        return amazonAdsApiService.updateSpCampaigns(credential, profileId, baseUrl, Collections.singletonList(c)) != null;
    }

    @Override
    public Boolean updateBid(Long accountId, AdsBidUpdateRequest request) {
        log.info("[updateBid][Amazon] 开始更新出价, accountId: {}, request: {}", accountId, request);
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(accountId);
        if (credential == null) return false;

        String profileId = request.getProfileId();
        if (StringUtils.isEmpty(profileId)) {
            profileId = getProfileId(request.getEntityType(), request.getLocalId());
        }
        if (StringUtils.isEmpty(profileId)) return false;

        AdsAmazonProfileDO profile = adsAmazonProfileMapper.selectByProfileId(profileId);
        if (profile == null) return false;

        String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
        String entityId = request.getEntityId();
        if (AdsEntityTypeEnum.KEYWORD == request.getEntityType()) {
            AdsSpKeyword k = AdsSpKeyword.builder().keywordId(entityId).bid(request.getBid()).build();
            return amazonAdsApiService.updateSpKeywords(credential, profileId, baseUrl, Collections.singletonList(k)) != null;
        } else if (AdsEntityTypeEnum.TARGET == request.getEntityType()) {
            AdsSpTarget t = AdsSpTarget.builder().targetId(entityId).bid(request.getBid()).build();
            return amazonAdsApiService.updateSpTargets(credential, profileId, baseUrl, Collections.singletonList(t)) != null;
        }
        return false;
    }


    private String getProfileId(AdsEntityTypeEnum entityType, Long localId) {
        if (localId == null || entityType == null) {
            return null;
        }
        Object extData = null;
        switch (entityType) {
            case CAMPAIGN:
                AdsCampaignDO campaign = adsCampaignMapper.selectById(localId);
                extData = campaign != null ? campaign.getExtData() : null;
                break;
            case ADGROUP:
                AdsAdGroupDO adGroup = adsAdGroupMapper.selectById(localId);
                extData = adGroup != null ? adGroup.getExtData() : null;
                break;
            case AD:
                AdsAdDO ad = adsAdMapper.selectById(localId);
                extData = ad != null ? ad.getExtData() : null;
                break;
            case KEYWORD:
            case TARGET:
                AdsKeywordDO keyword = adsKeywordMapper.selectById(localId);
                extData = keyword != null ? keyword.getExtData() : null;
                break;
            default:
                break;
        }

        if (extData != null) {
            try {
                Map<String, Object> extDataMap = objectMapper.convertValue(extData, new TypeReference<Map<String, Object>>() {});
                if (extDataMap != null && extDataMap.get("platformConfig") instanceof Map) {
                    Map<String, Object> config = objectMapper.convertValue(extDataMap.get("platformConfig"), new TypeReference<Map<String, Object>>() {});
                    if (config != null && config.get("profileId") != null) {
                        return String.valueOf(config.get("profileId"));
                    }
                }
            } catch (Exception e) {
                log.warn("[getProfileId] 解析 extData 异常: entityType={}, localId={}", entityType, localId, e);
            }
        }
        return null;
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
                (AdsOptimizationRuleAssociateReqVO) associateReqVO);
    }

    @Override
    public void executePerformanceSyncTask(AdsSyncTaskDO task) {
        // 核心任务分发与状态管理逻辑已迁移到 AmzSpReportParser
        adsAmazonReportService.executeSyncTask(task);
    }
}
