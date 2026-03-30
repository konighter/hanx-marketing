package com.hzltd.module.amz.api.adv.v1;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.api.client.ApiException;
import com.hzltd.module.amz.adv.api.sp.api.CampaignsApi;
import com.hzltd.module.amz.adv.api.sp.model.SponsoredProductsListSponsoredProductsCampaignsRequestContent;
import com.hzltd.module.amz.adv.api.sp.model.SponsoredProductsListSponsoredProductsCampaignsResponseContent;
import com.hzltd.module.amz.api.adv.model.sp.*;
import com.hzltd.module.amz.api.adv.v1.model.AmzCampaign;
import com.hzltd.module.amz.api.adv.v1.model.AmzCampaignListResponse;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Amazon Ads SP v3 + V1 API 服务层
 *
 * 职责：
 * - 封装所有对 Amazon SP API 的 HTTP 请求（Campaign / AdGroup / Keyword / Target 等维度）
 * - 使用 AdsApiClient 进行请求，向上层返回已解析的强类型响应对象
 * - 不包含任何业务组装逻辑，所有请求参数由调用方（AmazonAdsAdapter）构建传入
 */
@Slf4j
@Service
public class AmazonAdsApiService extends AbstractAmazonAdsService {

    private static final String CAMPAIGNS_PATH = "/campaigns";

    @Resource
    private AdsApiClient apiClient;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    // ==================== V1 Campaign 管理 ====================

    public AmzCampaignListResponse listCampaigns(AdsAccountCredentialDO credential, String accountId,
                                                 String profileId, String baseUrl,
                                                 Object request) {

        String url = baseUrl + "/adsApi/v1/query/campaigns";
        log.info("[listCampaigns] accountId={}, profileId={}, url={}", accountId, profileId, url);
        String resp = apiClient.post(credential, accountId, profileId, url, request);
        return JsonUtils.parseObject(resp, AmzCampaignListResponse.class);
    }

    public AmzCampaign getCampaign(AdsAccountCredentialDO credential, String accountId,
                                   String profileId, String baseUrl, String campaignId) {
        String url = baseUrl + CAMPAIGNS_PATH + "/" + campaignId;
        log.info("[getCampaign] accountId={}, campaignId={}", accountId, campaignId);
        String resp = apiClient.get(credential, accountId, profileId, url);
        return JsonUtils.parseObject(resp, AmzCampaign.class);
    }

    public String createCampaigns(AdsAccountCredentialDO credential, String accountId,
                                   String profileId, String baseUrl, List<AmzCampaign> campaigns) {
        String url = baseUrl + CAMPAIGNS_PATH;
        log.info("[createCampaigns] accountId={}, count={}", accountId, campaigns.size());
        return apiClient.post(credential, accountId, profileId, url, Map.of("campaigns", campaigns));
    }

    public String updateCampaigns(AdsAccountCredentialDO credential, String accountId,
                                   String profileId, String baseUrl, List<AmzCampaign> campaigns) {
        String url = baseUrl + CAMPAIGNS_PATH;
        log.info("[updateCampaigns] accountId={}, count={}", accountId, campaigns.size());
        return apiClient.put(credential, accountId, profileId, url, Map.of("campaigns", campaigns));
    }

    public String deleteCampaign(AdsAccountCredentialDO credential, String accountId,
                                  String profileId, String baseUrl, String campaignId) {
        String url = baseUrl + CAMPAIGNS_PATH + "/" + campaignId;
        log.info("[deleteCampaign] accountId={}, campaignId={}", accountId, campaignId);
        return apiClient.delete(credential, accountId, profileId, url);
    }

    public String updateCampaign(AdsAccountCredentialDO credential, String accountId,
                                  String profileId, String baseUrl, AmzCampaign campaign) {
        return updateCampaigns(credential, accountId, profileId, baseUrl, Collections.singletonList(campaign));
    }

    // ==================== SP v3 — Campaign ====================

    /**
     * 查询广告活动列表
     * POST /sp/campaigns/list
     */
    public AdsSpCampaignQueryResp listSpCampaigns(AdsAccountCredentialDO credential, String profileId,
                                                  String baseUrl, AdsSpCampaignQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/campaigns/list",
                JsonUtils.toJsonString(request), "application/vnd.spCampaign.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpCampaignQueryResp.class));
    }

    /**
     * 查询广告活动级否定关键词
     * POST /sp/campaignNegativeKeywords/list
     */
    public AdsSpNegativeKeywordQueryResp listCampaignNegativeKeywords(AdsAccountCredentialDO credential,
                                                                      String profileId, String baseUrl,
                                                                      AdsSpNegativeKeywordQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/campaignNegativeKeywords/list",
                JsonUtils.toJsonString(request), "application/vnd.spCampaignNegativeKeyword.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpNegativeKeywordQueryResp.class));
    }

    /**
     * 查询 Campaign 关联的 Optimization Rules (通过 Search 接口批量查询)
     * POST /sp/rules/optimization/search
     */
    public List<AdsSpOptimizationRule> listCampaignOptimizationRules(AdsAccountCredentialDO credential,
                                                                      String profileId, String baseUrl, String campaignId) {
        String url = baseUrl + "/sp/rules/optimization/search";
        AdsSpOptimizationRuleSearchRequest request = AdsSpOptimizationRuleSearchRequest.builder()
                .campaignFilter(AdsSpCommonQueryRequest.StringFilter.builder()
                        .include(Collections.singletonList(campaignId))
                        .build())
                .ruleCategory(AdsSpCommonQueryRequest.StringFilter.builder()
                        .include(Collections.singletonList("BIDDING"))
                        .build())
                .ruleSubCategory(AdsSpCommonQueryRequest.StringFilter.builder()
                        .include(Collections.singletonList("SCHEDULE"))
                        .build())
                .build();
        AdsSpOptimizationRuleSearchResp searchResp = executeSpPost(credential, profileId, url,
                JsonUtils.toJsonString(request), "application/vnd.spOptimizationRule.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpOptimizationRuleSearchResp.class));
        return searchResp != null ? searchResp.getOptimizationRules() : null;
    }

    /**
     * 批量查询 Campaign 关联的 Bidding 优化规则
     * POST /sp/rules/optimization/search
     */
    public List<AdsSpOptimizationRule> searchBiddingRulesByCampaignIds(AdsAccountCredentialDO credential,
                                                                       String profileId, String baseUrl,
                                                                       List<String> campaignIds) {
        String url = baseUrl + "/sp/rules/optimization/search";
        AdsSpOptimizationRuleSearchRequest request = AdsSpOptimizationRuleSearchRequest.builder()
                .campaignFilter(AdsSpCommonQueryRequest.StringFilter.from(campaignIds))
                .ruleCategory(AdsSpCommonQueryRequest.StringFilter.builder()
                        .include(Collections.singletonList("BIDDING"))
                        .build())
                .ruleSubCategory(AdsSpCommonQueryRequest.StringFilter.builder()
                        .include(Collections.singletonList("SCHEDULE"))
                        .build())
                .build();
        AdsSpOptimizationRuleSearchResp searchResp = executeSpPost(credential, profileId, url,
                JsonUtils.toJsonString(request), "application/vnd.spoptimizationrules.v1+json",
                resp -> JsonUtils.parseObject(resp, AdsSpOptimizationRuleSearchResp.class));
        return searchResp != null ? searchResp.getOptimizationRules() : null;
    }

    /**
     * 批量查询 Campaign 关联的 Optimization Rules
     * POST /sp/rules/optimization/search
     */
    public AdsSpOptimizationRuleSearchResp searchOptimizationRules(AdsAccountCredentialDO credential,
                                                                    String profileId, String baseUrl,
                                                                    AdsSpOptimizationRuleSearchRequest request) {
        String url = baseUrl + "/sp/rules/optimization/search";
        return executeSpPost(credential, profileId, url,
                JsonUtils.toJsonString(request), "application/vnd.spOptimizationRule.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpOptimizationRuleSearchResp.class));
    }

    // ==================== SP v3 — AdGroup ====================

    /**
     * 查询广告组列表
     * POST /sp/adGroups/list
     */
    public AdsSpAdGroupQueryResp listSpAdGroups(AdsAccountCredentialDO credential, String profileId,
                                                 String baseUrl, AdsSpAdGroupQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/adGroups/list",
                JsonUtils.toJsonString(request), "application/vnd.spAdGroup.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpAdGroupQueryResp.class));
    }

    /**
     * 查询关键词定向
     * POST /sp/keywords/list
     */
    public AdsSpKeywordQueryResp listSpKeywords(AdsAccountCredentialDO credential, String profileId,
                                                 String baseUrl, AdsSpKeywordQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/keywords/list",
                JsonUtils.toJsonString(request), "application/vnd.spKeyword.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpKeywordQueryResp.class));
    }

    /**
     * 查询商品/品类定向
     * POST /sp/targets/list
     */
    public AdsSpTargetQueryResp listSpTargets(AdsAccountCredentialDO credential, String profileId,
                                               String baseUrl, AdsSpTargetQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/targets/list",
                JsonUtils.toJsonString(request), "application/vnd.sptargetingClause.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpTargetQueryResp.class));
    }

    /**
     * 查询广告组级否定关键词
     * POST /sp/negativeKeywords/list
     */
    public AdsSpNegativeKeywordQueryResp listAdGroupNegativeKeywords(AdsAccountCredentialDO credential,
                                                                      String profileId, String baseUrl,
                                                                      AdsSpNegativeKeywordQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/negativeKeywords/list",
                JsonUtils.toJsonString(request), "application/vnd.spNegativeKeyword.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpNegativeKeywordQueryResp.class));
    }

    /**
     * 查询否定商品定向
     * POST /sp/negativeTargets/list
     */
    public AdsSpNegativeTargetQueryResp listSpNegativeTargets(AdsAccountCredentialDO credential,
                                                               String profileId, String baseUrl,
                                                               AdsSpNegativeTargetQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/negativeTargets/list",
                JsonUtils.toJsonString(request), "application/vnd.spnegativeTargetingClause.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpNegativeTargetQueryResp.class));
    }

    // ==================== SP v3 — ProductAd / Keyword ====================

    /**
     * 查询广告单品
     * POST /sp/productAds/list
     */
    public AdsSpProductAdQueryResp listSpProductAds(AdsAccountCredentialDO credential, String profileId,
                                                     String baseUrl, AdsSpProductAdQueryRequest request) {
        return executeSpPost(credential, profileId, baseUrl + "/sp/productAds/list",
                JsonUtils.toJsonString(request), "application/vnd.spProductAd.v3+json",
                resp -> JsonUtils.parseObject(resp, AdsSpProductAdQueryResp.class));
    }

    // ==================== SP v3 Update ====================

    public String updateSpCampaigns(AdsAccountCredentialDO credential, String profileId, String baseUrl, List<AdsSpCampaign> campaigns) {
        return executeSpRequest(credential, profileId, baseUrl + "/sp/campaigns", "PUT",
                JsonUtils.toJsonString(Map.of("campaigns", campaigns)), "application/vnd.spCampaign.v3+json",
                resp -> resp);
    }

    public String updateSpAdGroups(AdsAccountCredentialDO credential, String profileId, String baseUrl, List<AdsSpAdGroup> adGroups) {
        return executeSpRequest(credential, profileId, baseUrl + "/sp/adGroups", "PUT",
                JsonUtils.toJsonString(Map.of("adGroups", adGroups)), "application/vnd.spAdGroup.v3+json",
                resp -> resp);
    }

    public String updateSpKeywords(AdsAccountCredentialDO credential, String profileId, String baseUrl, List<AdsSpKeyword> keywords) {
        return executeSpRequest(credential, profileId, baseUrl + "/sp/keywords", "PUT",
                JsonUtils.toJsonString(Map.of("keywords", keywords)), "application/vnd.spKeyword.v3+json",
                resp -> resp);
    }

    public String updateSpProductAds(AdsAccountCredentialDO credential, String profileId, String baseUrl, List<AdsSpProductAd> ads) {
        return executeSpRequest(credential, profileId, baseUrl + "/sp/productAds", "PUT",
                JsonUtils.toJsonString(Map.of("productAds", ads)), "application/vnd.spProductAd.v3+json",
                resp -> resp);
    }

    public String updateSpTargets(AdsAccountCredentialDO credential, String profileId, String baseUrl, List<AdsSpTarget> targets) {
        return executeSpRequest(credential, profileId, baseUrl + "/sp/targets", "PUT",
                JsonUtils.toJsonString(Map.of("targets", targets)), "application/vnd.sptargetingClause.v3+json",
                resp -> resp);
    }

    // ==================== 内部工具方法 ====================

    /**
     * 统一的 SP v3 请求调用
     */
    private <T> T executeSpRequest(AdsAccountCredentialDO credential, String profileId,
                                    String url, String method, String jsonBody, String contentType,
                                    Function<String, T> parser) {
        try {
            String clientId = apiClient.getClientId();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Amazon-Advertising-API-ClientId", clientId)
                    .header("Authorization", "Bearer " + credential.getAccessToken())
                    .header("Accept", contentType)
                    .header("Content-Type", contentType)
                    .header("Amazon-Advertising-API-Scope", profileId);

            HttpRequest request;
            if ("PUT".equalsIgnoreCase(method)) {
                request = builder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : "{}")).build();
            } else if ("PATCH".equalsIgnoreCase(method)) {
                request = builder.method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : "{}")).build();
            } else {
                request = builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : "{}")).build();
            }

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return parser.apply(resp.body());
            }
            log.error("[executeSpRequest] {} {} → HTTP {}: {}", method, url, resp.statusCode(), resp.body());
        } catch (Exception e) {
            log.error("[executeSpRequest] {} {} 网络异常: {}", method, url, e.getMessage());
        }
        return null;
    }

    /**
     * 统一的 SP v3 POST 调用（注入 Accept / Content-Type 版本头）
     */
    private <T> T executeSpPost(AdsAccountCredentialDO credential, String profileId,
                                 String url, String jsonBody, String contentType,
                                 Function<String, T> parser) {
        try {
            String clientId = apiClient.getClientId();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Amazon-Advertising-API-ClientId", clientId)
                    .header("Authorization", "Bearer " + credential.getAccessToken())
                    .header("Accept", contentType)
                    .header("Content-Type", contentType)
                    .header("Amazon-Advertising-API-Scope", profileId)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : "{}"))
                    .build();
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                return parser.apply(resp.body());
            }
            log.error("[executeSpPost] {} → HTTP {}: {}", url, resp.statusCode(), resp.body());
        } catch (Exception e) {
            log.error("[executeSpPost] {} 网络异常: {}", url, e.getMessage());
        }
        return null;
    }
}

