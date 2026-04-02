package com.hzltd.module.amz.adv.api;

import com.hzltd.module.adv.model.*;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.CampaignNegativeKeywordsApi;
import com.hzltd.module.amz.adv.client.sp.api.CampaignNegativeTargetingClausesApi;
import com.hzltd.module.amz.adv.client.sp.api.CampaignsApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdsCampaignMangerApi extends AbstractAmazonAdsService {

    public static final String PROFILE_ID = "amz_profile_id";
    public static final String DYNAMIC_BIDDING = "amz_dynamic_bidding";
    public static final String EXTENDED_DATA = "amz_extended_data";
    public static final String OFF_AMAZON_SETTINGS = "amz_off_amazon_settings";
    public static final String PORTFOLIO_ID = "amz_portfolio_id";
    public static final String GLOBAL_CAMPAIGN_ID = "amz_global_campaign_id";
    public static final String TAGS = "amz_tags";
    public static final String NEGATIVE_KEYWORDS = "amz_negative_keywords";
    public static final String NEGATIVE_TARGET = "amz_negative_target";
    public static final String OPTIMIZATION_RULES = "amz_optimization_rules";
    public static final String BUDGET_RULE = "amz_budget_rules";
    public static final String BID_OPTIMIZATION_RULES = "amz_bid_optimization_rules";

    @Resource
    private AdsCampaignRuleApi adsCampaignRuleApi;

    public AdsResponse<List<AdsCampaignModel>> queryCampaign(AdsRequest<AdsQueryRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignsApi campaignsApi = new CampaignsApi(getApiClient(authModel));

            AdsQueryRequest queryRequest = request.getRequest();
            SponsoredProductsListSponsoredProductsCampaignsRequestContent apiRequest = new SponsoredProductsListSponsoredProductsCampaignsRequestContent();
            if (queryRequest.getCampaignIds() != null && !queryRequest.getCampaignIds().isEmpty()) {
                apiRequest.setCampaignIdFilter(new SponsoredProductsObjectIdFilter()
                        .include(queryRequest.getCampaignIds()));
            }
            if (queryRequest.getCampaignNames() != null && !queryRequest.getCampaignNames().isEmpty()) {
                apiRequest.setNameFilter(new SponsoredProductsNameFilter()
                        .include(queryRequest.getCampaignNames()));
            }

            SponsoredProductsListSponsoredProductsCampaignsResponseContent response = campaignsApi.listSponsoredProductsCampaigns(
                    authModel.getAppKey(),
                    authModel.getProfileId(),
                    apiRequest
            );

            if (response == null || response.getCampaigns() == null) {
                return AdsResponse.success(Collections.emptyList());
            }

            List<String> campaignIds = response.getCampaigns().stream().map(SponsoredProductsCampaign::getCampaignId).toList();
            AdsRequest<AdsQueryRequest> queryByCampaignIds = new AdsRequest<AdsQueryRequest>().setShopId(request.getShopId())
                    .setRequest(new AdsQueryRequest().setCampaignIds(campaignIds));

            // 批量获取广告活动的否定关键词和否定定向
            AdsResponse<Map<String, SponsoredProductsCampaignNegativeKeyword>> nKeywordResponse = this.queryCampaignNegativeKeywords(queryByCampaignIds);
            Map<String, List<SponsoredProductsCampaignNegativeKeyword>> groupedKeywords = nKeywordResponse.isSuccess() && MapUtils.isNotEmpty(nKeywordResponse.getData())
                    ? nKeywordResponse.getData().values().stream().collect(Collectors.groupingBy(SponsoredProductsCampaignNegativeKeyword::getCampaignId))
                    : Collections.emptyMap();

            AdsResponse<Map<String, SponsoredProductsCampaignNegativeTargetingClause>> targetResponse = this.queryCampaignNegativeTargetingClauses(queryByCampaignIds);
            Map<String, List<SponsoredProductsCampaignNegativeTargetingClause>> groupedTargets = targetResponse.isSuccess() && MapUtils.isNotEmpty(targetResponse.getData())
                    ? targetResponse.getData().values().stream().collect(Collectors.groupingBy(SponsoredProductsCampaignNegativeTargetingClause::getCampaignId))
                    : Collections.emptyMap();

            List<AdsCampaignModel> campaigns = response.getCampaigns().stream()
                    .map(c -> this.mapToAdsCampaignResponse(request.getShopId(), c, groupedKeywords.get(c.getCampaignId()), groupedTargets.get(c.getCampaignId())))
                    .collect(Collectors.toList());

            return AdsResponse.success(campaigns);
        } catch (ApiException e) {
            log.error("Failed to query Amazon campaigns", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    private AdsCampaignModel mapToAdsCampaignResponse(Long shopId, SponsoredProductsCampaign campaign,
                                                      List<SponsoredProductsCampaignNegativeKeyword> nKeywords,
                                                      List<SponsoredProductsCampaignNegativeTargetingClause> nTargets) {
        AdsCampaignModel builder = AdsCampaignModel.builder()
                .externalId(campaign.getCampaignId())
                .name(campaign.getName())
                .campaignType("Sponsored Products")
                .targetType(campaign.getTargetingType().getValue())
                .status(campaign.getState().getValue())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .platform(AdsPlatformEnum.AMAZON.name())
                .budget(BigDecimal.valueOf(campaign.getBudget().getBudget()))
                .budgetType(campaign.getBudget().getBudgetType().getValue())
                .build();

        builder.addAttribute(OFF_AMAZON_SETTINGS, campaign.getOffAmazonSettings());
        builder.addAttribute(TAGS, campaign.getTags());
        builder.addAttribute(PORTFOLIO_ID, campaign.getPortfolioId());
        builder.addAttribute(EXTENDED_DATA, campaign.getExtendedData());

        // 预算规则
        AdsResponse<List<SPCampaignBudgetRule>> budgetRules = adsCampaignRuleApi.getBudgetRuleWithCampaign(new AdsRequest<String>().setShopId(shopId).setRequest(campaign.getCampaignId()));
        if (budgetRules.isSuccess() && CollectionUtils.isNotEmpty(budgetRules.getData())) {
            builder.addAttribute(BUDGET_RULE, budgetRules.getData());
        }

        // 出价规则
        if (campaign.getDynamicBidding() != null) {
            // 竞价策略
            builder.addAttribute(DYNAMIC_BIDDING, campaign.getDynamicBidding());
            if (SponsoredProductsBiddingStrategy.RULE_BASED.equals(campaign.getDynamicBidding().getStrategy())) {
                AdsResponse<CampaignOptimizationRule> campaignRuleResp = adsCampaignRuleApi.getCampaignOptimizationRuleByCampaign(new AdsRequest<String>().setShopId(shopId).setRequest(campaign.getCampaignId()));
                if (campaignRuleResp.isSuccess() && campaignRuleResp.getData() != null) {
                    builder.addAttribute(BID_OPTIMIZATION_RULES, campaignRuleResp.getData());
                }
            }
        }
        // 出价规则
        AdsResponse<List<OptimizationRulesAPISwaggerOptimizationRule>> optimizationRules = adsCampaignRuleApi.queryOptimizationRules(new AdsRequest<AdsQueryRequest>().setShopId(shopId).setRequest(new AdsQueryRequest().setCampaignIds(List.of(campaign.getCampaignId()))));
        if (optimizationRules.isSuccess() && CollectionUtils.isNotEmpty(optimizationRules.getData())) {
            builder.addAttribute(OPTIMIZATION_RULES, optimizationRules.getData().get(0));
        }


        // 否定关键词
        if (CollectionUtils.isNotEmpty(nKeywords)) {
            builder.addAttribute(NEGATIVE_KEYWORDS, nKeywords);
        }

        // 否定定向
        if (CollectionUtils.isNotEmpty(nTargets)) {
            builder.addAttribute(NEGATIVE_TARGET, nTargets);
        }

        return builder;
    }

    /**
     * 更新广告活动
     * @param request
     * @return
     */
    public AdsResponse<Boolean> updateCampaign(AdsRequest<AdsEntityUpdateRequest> request) {
        // 广告活动主体


        // 动态BID: 位置 + ROAS + 时间


        // Budget rule


        // 否定定向




        return AdsResponse.error("Not implemented yet");
    }

    /**
     * 更新广告活动状态
     * @param request
     * @return
     */
    public AdsResponse<Boolean> updateStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        CampaignsApi campaignsApi = new CampaignsApi(getApiClient(authModel));

        try {
            SponsoredProductsUpdateSponsoredProductsCampaignsRequestContent requestContent = new SponsoredProductsUpdateSponsoredProductsCampaignsRequestContent()
                    .addCampaignsItem(new SponsoredProductsUpdateCampaign()
                            .campaignId(request.getRequest().getEntityId())
                            .state(SponsoredProductsCreateOrUpdateEntityState.fromValue(request.getRequest().getStatus())));
            SponsoredProductsUpdateSponsoredProductsCampaignsResponseContent responseContent = campaignsApi.updateSponsoredProductsCampaigns(authModel.getAppKey(), authModel.getProfileId(), requestContent, "");

            boolean success = responseContent.getCampaigns().getSuccess() != null && responseContent.getCampaigns().getSuccess().stream().anyMatch(item -> item.getCampaignId().equalsIgnoreCase(request.getRequest().getEntityId()));
            return AdsResponse.success(success);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 更新广告活动预算
     * @param request
     * @return
     */
    public AdsResponse<Boolean> updateBudget(AdsRequest<AdsBudgetUpdateRequest> request) {
        AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
        CampaignsApi campaignsApi = new CampaignsApi(getApiClient(authModel));

        try {
            SponsoredProductsUpdateSponsoredProductsCampaignsRequestContent requestContent = new SponsoredProductsUpdateSponsoredProductsCampaignsRequestContent()
                    .addCampaignsItem(new SponsoredProductsUpdateCampaign()
                            .campaignId(request.getRequest().getEntityId())
                            .budget(new SponsoredProductsCreateOrUpdateBudget()
                                    .budgetType(SponsoredProductsCreateOrUpdateBudgetType.DAILY)
                                    .budget(request.getRequest().getBudget().doubleValue())));

            SponsoredProductsUpdateSponsoredProductsCampaignsResponseContent responseContent = campaignsApi.updateSponsoredProductsCampaigns(authModel.getAppKey(), authModel.getProfileId(), requestContent, "");
            boolean success = responseContent.getCampaigns().getSuccess() != null && responseContent.getCampaigns().getSuccess().stream().anyMatch(item -> item.getCampaignId().equalsIgnoreCase(request.getRequest().getEntityId()));
            return AdsResponse.success(success);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    // -------------------------------------------------------------------------
    // Campaign Negative Keywords API
    // -------------------------------------------------------------------------

    public AdsResponse<Map<String, SponsoredProductsCampaignNegativeKeyword>> queryCampaignNegativeKeywords(AdsRequest<AdsQueryRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeKeywordsApi api = new CampaignNegativeKeywordsApi(getApiClient(authModel));
            
            SponsoredProductsListSponsoredProductsCampaignNegativeKeywordsRequestContent apiRequest = new SponsoredProductsListSponsoredProductsCampaignNegativeKeywordsRequestContent();
            if (request.getRequest().getCampaignIds() != null && !request.getRequest().getCampaignIds().isEmpty()) {
                apiRequest.setCampaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getKeywordIds())) {
                apiRequest.campaignNegativeKeywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest().getKeywordIds()));
            }
            if (CollectionUtils.isNotEmpty(request.getRequest().getKeywordTexts())) {
                apiRequest.campaignNegativeKeywordTextFilter(new SponsoredProductsKeywordTextFilter().include(request.getRequest().getKeywordTexts()));
            }
            
            SponsoredProductsListSponsoredProductsCampaignNegativeKeywordsResponseContent response = api.listSponsoredProductsCampaignNegativeKeywords(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest
            );
            
            if (response == null || response.getCampaignNegativeKeywords() == null) {
                return AdsResponse.success(Collections.emptyMap());
            }
            
            Map<String, SponsoredProductsCampaignNegativeKeyword> map = response.getCampaignNegativeKeywords().stream()
                    .filter(kw -> kw.getKeywordId() != null)
                    .collect(Collectors.toMap(SponsoredProductsCampaignNegativeKeyword::getKeywordId, kw -> kw));
            
            return AdsResponse.success(map);
        } catch (ApiException e) {
            log.error("Failed to query Campaign Negative Keywords", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Map<String, SponsoredProductsCampaignNegativeKeyword>> createCampaignNegativeKeywords(AdsRequest<List<SponsoredProductsCreateCampaignNegativeKeyword>> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeKeywordsApi api = new CampaignNegativeKeywordsApi(getApiClient(authModel));
            
            List<SponsoredProductsCreateCampaignNegativeKeyword> keywords = request.getRequest();
            
            SponsoredProductsCreateSponsoredProductsCampaignNegativeKeywordsRequestContent apiRequest = new SponsoredProductsCreateSponsoredProductsCampaignNegativeKeywordsRequestContent()
                .campaignNegativeKeywords(keywords);
                
            SponsoredProductsCreateSponsoredProductsCampaignNegativeKeywordsResponseContent response = api.createSponsoredProductsCampaignNegativeKeywords(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest, "return=representation"
            );

            if (response == null || response.getCampaignNegativeKeywords() == null || response.getCampaignNegativeKeywords().getSuccess() == null) {
                return AdsResponse.success(Collections.emptyMap());
            }

            Map<String, SponsoredProductsCampaignNegativeKeyword> resultMap = response.getCampaignNegativeKeywords().getSuccess().stream()
                    .filter(item -> item.getCampaignNegativeKeywordId() != null && item.getCampaignNegativeKeyword() != null)
                    .collect(Collectors.toMap(
                            SponsoredProductsCampaignNegativeKeywordSuccessResponseItem::getCampaignNegativeKeywordId,
                            SponsoredProductsCampaignNegativeKeywordSuccessResponseItem::getCampaignNegativeKeyword
                    ));

            return AdsResponse.success(resultMap);
        } catch (ApiException e) {
            log.error("Failed to create Campaign Negative Keywords", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateCampaignNegativeKeywordStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeKeywordsApi api = new CampaignNegativeKeywordsApi(getApiClient(authModel));
            
            SponsoredProductsUpdateCampaignNegativeKeyword kw = new SponsoredProductsUpdateCampaignNegativeKeyword()
                .keywordId(request.getRequest().getEntityId())
                .state(SponsoredProductsCreateOrUpdateEntityState.fromValue(request.getRequest().getStatus()));
                
            SponsoredProductsUpdateSponsoredProductsCampaignNegativeKeywordsRequestContent apiRequest = new SponsoredProductsUpdateSponsoredProductsCampaignNegativeKeywordsRequestContent()
                .addCampaignNegativeKeywordsItem(kw);
                
            SponsoredProductsUpdateSponsoredProductsCampaignNegativeKeywordsResponseContent response = api.updateSponsoredProductsCampaignNegativeKeywords(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest, ""
            );
            
            boolean success = response.getCampaignNegativeKeywords() != null && response.getCampaignNegativeKeywords().getSuccess() != null && !response.getCampaignNegativeKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Failed to update Campaign Negative Keyword status", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> deleteCampaignNegativeKeywords(AdsRequest<List<String>> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeKeywordsApi api = new CampaignNegativeKeywordsApi(getApiClient(authModel));
            
            SponsoredProductsDeleteSponsoredProductsCampaignNegativeKeywordsRequestContent apiRequest = new SponsoredProductsDeleteSponsoredProductsCampaignNegativeKeywordsRequestContent()
                .campaignNegativeKeywordIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
                
            SponsoredProductsDeleteSponsoredProductsCampaignNegativeKeywordsResponseContent response = api.deleteSponsoredProductsCampaignNegativeKeywords(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest
            );
            
            boolean success = response.getCampaignNegativeKeywords() != null && response.getCampaignNegativeKeywords().getSuccess() != null && !response.getCampaignNegativeKeywords().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Failed to delete Campaign Negative Keywords", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }


    // -------------------------------------------------------------------------
    // Campaign Negative Targeting Clauses API
    // -------------------------------------------------------------------------

    public AdsResponse<Map<String, SponsoredProductsCampaignNegativeTargetingClause>> queryCampaignNegativeTargetingClauses(AdsRequest<AdsQueryRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeTargetingClausesApi api = new CampaignNegativeTargetingClausesApi(getApiClient(authModel));
            
            SponsoredProductsListSponsoredProductsCampaignNegativeTargetingClausesRequestContent apiRequest = new SponsoredProductsListSponsoredProductsCampaignNegativeTargetingClausesRequestContent();
            if (request.getRequest().getCampaignIds() != null && !request.getRequest().getCampaignIds().isEmpty()) {
                apiRequest.setCampaignIdFilter(new SponsoredProductsReducedObjectIdFilter().include(request.getRequest().getCampaignIds()));
            }
            
            SponsoredProductsListSponsoredProductsCampaignNegativeTargetingClausesResponseContent response = api.listSponsoredProductsCampaignNegativeTargetingClauses(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest
            );
            
            if (response == null || response.getCampaignNegativeTargetingClauses() == null) {
                return AdsResponse.success(Collections.emptyMap());
            }

            Map<String, SponsoredProductsCampaignNegativeTargetingClause> map = response.getCampaignNegativeTargetingClauses().stream()
                    .filter(tc -> tc.getTargetId() != null)
                    .collect(Collectors.toMap(SponsoredProductsCampaignNegativeTargetingClause::getTargetId, tc -> tc));
            
            return AdsResponse.success(map);
        } catch (ApiException e) {
            log.error("Failed to query Campaign Negative Targeting Clauses", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Map<String, SponsoredProductsCampaignNegativeTargetingClause>> createCampaignNegativeTargetingClauses(AdsRequest<List<SponsoredProductsCreateCampaignNegativeTargetingClause>> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeTargetingClausesApi api = new CampaignNegativeTargetingClausesApi(getApiClient(authModel));
            
            List<SponsoredProductsCreateCampaignNegativeTargetingClause> clauses = request.getRequest();
            
            SponsoredProductsCreateSponsoredProductsCampaignNegativeTargetingClausesRequestContent apiRequest = new SponsoredProductsCreateSponsoredProductsCampaignNegativeTargetingClausesRequestContent()
                .campaignNegativeTargetingClauses(clauses);
                
            SponsoredProductsCreateSponsoredProductsCampaignNegativeTargetingClausesResponseContent response = api.createSponsoredProductsCampaignNegativeTargetingClauses(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest, "return=representation"
            );
            
            if (response == null || response.getCampaignNegativeTargetingClauses() == null || response.getCampaignNegativeTargetingClauses().getSuccess() == null) {
                return AdsResponse.success(Collections.emptyMap());
            }

            Map<String, SponsoredProductsCampaignNegativeTargetingClause> resultMap = response.getCampaignNegativeTargetingClauses().getSuccess().stream()
                    .filter(item -> item.getCampaignNegativeTargetingClauseId() != null && item.getCampaignNegativeTargetingClauses() != null)
                    .collect(Collectors.toMap(
                            SponsoredProductsCampaignNegativeTargetingClauseSuccessResponseItem::getCampaignNegativeTargetingClauseId,
                            SponsoredProductsCampaignNegativeTargetingClauseSuccessResponseItem::getCampaignNegativeTargetingClauses
                    ));

            return AdsResponse.success(resultMap);
        } catch (ApiException e) {
            log.error("Failed to create Campaign Negative Targeting Clauses", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> updateCampaignNegativeTargetingClauseStatus(AdsRequest<AdsStatusUpdateRequest> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeTargetingClausesApi api = new CampaignNegativeTargetingClausesApi(getApiClient(authModel));
            
            SponsoredProductsUpdateCampaignNegativeTargetingClause tc = new SponsoredProductsUpdateCampaignNegativeTargetingClause()
                .targetId(request.getRequest().getEntityId())
                .state(SponsoredProductsCreateOrUpdateEntityState.fromValue(request.getRequest().getStatus()));
                
            SponsoredProductsUpdateSponsoredProductsCampaignNegativeTargetingClausesRequestContent apiRequest = new SponsoredProductsUpdateSponsoredProductsCampaignNegativeTargetingClausesRequestContent()
                .addCampaignNegativeTargetingClausesItem(tc);
                
            SponsoredProductsUpdateSponsoredProductsCampaignNegativeTargetingClausesResponseContent response = api.updateSponsoredProductsCampaignNegativeTargetingClauses(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest, ""
            );
            
            boolean success = response.getCampaignNegativeTargetingClauses() != null && response.getCampaignNegativeTargetingClauses().getSuccess() != null && !response.getCampaignNegativeTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Failed to update Campaign Negative Targeting Clause status", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<Boolean> deleteCampaignNegativeTargetingClauses(AdsRequest<List<String>> request) {
        try {
            AuthorizationModel authModel = getAuthorizationModel(request.getShopId());
            CampaignNegativeTargetingClausesApi api = new CampaignNegativeTargetingClausesApi(getApiClient(authModel));
            
            SponsoredProductsDeleteSponsoredProductsCampaignNegativeTargetingClausesRequestContent apiRequest = new SponsoredProductsDeleteSponsoredProductsCampaignNegativeTargetingClausesRequestContent()
                .campaignNegativeTargetIdFilter(new SponsoredProductsObjectIdFilter().include(request.getRequest()));
                
            SponsoredProductsDeleteSponsoredProductsCampaignNegativeTargetingClausesResponseContent response = api.deleteSponsoredProductsCampaignNegativeTargetingClauses(
                authModel.getAppKey(), authModel.getProfileId(), apiRequest
            );
            
            boolean success = response.getCampaignNegativeTargetingClauses() != null && response.getCampaignNegativeTargetingClauses().getSuccess() != null && !response.getCampaignNegativeTargetingClauses().getSuccess().isEmpty();
            return AdsResponse.success(success);
        } catch (ApiException e) {
            log.error("Failed to delete Campaign Negative Targeting Clauses", e);
            return AdsResponse.error(e.getResponseBody());
        }
    }

}
