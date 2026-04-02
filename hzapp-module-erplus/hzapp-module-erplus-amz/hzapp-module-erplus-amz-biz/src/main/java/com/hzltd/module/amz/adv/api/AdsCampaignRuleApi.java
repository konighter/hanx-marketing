package com.hzltd.module.amz.adv.api;


import com.hzltd.module.adv.model.AdsQueryRequest;
import com.hzltd.module.adv.model.AdsRequest;
import com.hzltd.module.adv.model.AdsResponse;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.api.vo.CampaignBudgetRuleAssociatedRequest;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.sp.api.BudgetRulesApi;
import com.hzltd.module.amz.adv.client.sp.api.CampaignOptimizationRulesApi;
import com.hzltd.module.amz.adv.client.sp.api.OptimizationRulesApi;
import com.hzltd.module.amz.adv.client.sp.model.*;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * BudgetRule:
 */
@Service
public class AdsCampaignRuleApi extends AbstractAmazonAdsService {

    // =============================================================== 预算规则 ======================================================

    /**
     * 获取广告活动下的budgetRule
     * @param request campaignId
     * @return
     */
    public AdsResponse<List<SPCampaignBudgetRule>> getBudgetRuleWithCampaign(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        BudgetRulesApi budgetRulesApi = new BudgetRulesApi(this.getApiClient(authorizationModel));

        try {
            SPListAssociatedBudgetRulesResponse response = budgetRulesApi.listAssociatedBudgetRulesForSPCampaigns(authorizationModel.getAppKey(), authorizationModel.getProfileId(), BigDecimal.valueOf(Long.valueOf(request.getRequest())));
            return AdsResponse.success(response.getAssociatedRules());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 当前广告主下所有的预算规则
     * @param request
     * @return
     */
    public AdsResponse<List<SPBudgetRule>> getBudgetRules(AdsRequest<?> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        BudgetRulesApi budgetRulesApi = new BudgetRulesApi(this.getApiClient(authorizationModel));

        try {
            GetSPBudgetRulesForAdvertiserResponse response = budgetRulesApi.getSPBudgetRulesForAdvertiser(authorizationModel.getAppKey(), authorizationModel.getProfileId(), BigDecimal.valueOf(100), "");
            return AdsResponse.success(response.getBudgetRulesForAdvertiserResponse());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    public AdsResponse<BudgetRuleResponse> createBudgetRule(AdsRequest<SPBudgetRuleDetails> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        BudgetRulesApi budgetRulesApi = new BudgetRulesApi(this.getApiClient(authorizationModel));

        try {
            CreateBudgetRulesResponse response = budgetRulesApi.createBudgetRulesForSPCampaigns(authorizationModel.getAppKey(), authorizationModel.getProfileId(), new CreateSPBudgetRulesRequest().addBudgetRulesDetailsItem(request.getRequest()));
            return AdsResponse.success(response.getResponses().get(0));
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    public AdsResponse<List<AssociatedBudgetRuleResponse>> createAssociatedBudgetRulesForSPCampaigns(AdsRequest<CampaignBudgetRuleAssociatedRequest> request) {

        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        BudgetRulesApi budgetRulesApi = new BudgetRulesApi(this.getApiClient(authorizationModel));

        try {
            CreateAssociatedBudgetRulesResponse response = budgetRulesApi.createAssociatedBudgetRulesForSPCampaigns(authorizationModel.getAppKey(), authorizationModel.getProfileId(), BigDecimal.valueOf(Long.valueOf(request.getRequest().getCampaignId())), new CreateAssociatedBudgetRulesRequest().budgetRuleIds(request.getRequest().getRuleIds()));
            return AdsResponse.success(response.getResponses());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    // =============================================================== 预算规则 end ======================================================


    // =============================================================== 出价规则 ======================================================

    /**
     * 搜索优化规则（自定义时间的出价调整）
     * 根据广告活动ID查询
     * @param request OptimizationRulesAPISwaggerSearchOptimizationRulesRequest
     * @return
     */
    public AdsResponse<List<OptimizationRulesAPISwaggerOptimizationRule>> queryOptimizationRules(AdsRequest<AdsQueryRequest> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        OptimizationRulesApi optimizationRulesApi = new OptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();

            OptimizationRulesAPISwaggerSearchOptimizationRulesRequest rulesRequest = new OptimizationRulesAPISwaggerSearchOptimizationRulesRequest()
                    .campaignFilter(new OptimizationRulesAPISwaggerCampaignFilter()
                            .campaignId(new OptimizationRulesAPISwaggerEntityFieldFilter()
                                    .values(request.getRequest().getCampaignIds()))

                    )

                    .optimizationRuleFilter(new OptimizationRulesAPISwaggerOptimizationRuleFilter()
                            .ruleCategory(new OptimizationRulesAPISwaggerEntityFieldFilter()
                                    .filterType(OptimizationRulesAPISwaggerFilterType.EXACT_MATCH)
                                    .addValuesItem(OptimizationRulesAPISwaggerRuleCategory.BID.getValue()))
                            .ruleSubCategory(new OptimizationRulesAPISwaggerEntityFieldFilter()
                                    .filterType(OptimizationRulesAPISwaggerFilterType.EXACT_MATCH)
                                    .addValuesItem(OptimizationRulesAPISwaggerRuleSubCategory.SCHEDULE.getValue())));

            OptimizationRulesAPISwaggerSearchOptimizationRulesResponse response = optimizationRulesApi.searchOptimizationRules(appKey, profileId, rulesRequest);
            return AdsResponse.success(response.getOptimizationRules());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 创建优化规则（自定义时间的出价调整）
     * @param request OptimizationRulesAPISwaggerCreateOptimizationRulesRequest
     * @return
     */
    public AdsResponse<OptimizationRulesAPISwaggerOptimizationRulesResponse> createOptimizationRules(AdsRequest<List<OptimizationRulesAPISwaggerOptimizationRuleWithoutRuleId>> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        OptimizationRulesApi optimizationRulesApi = new OptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();
            if (appKey == null || profileId == null) {
                return AdsResponse.error("Missing AppKey or ProfileId");
            }
            OptimizationRulesAPISwaggerOptimizationRulesResponse response = optimizationRulesApi.createOptimizationRules(appKey, profileId, new OptimizationRulesAPISwaggerCreateOptimizationRulesRequest().optimizationRules(request.getRequest()));
            return AdsResponse.success(response);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 将优化规则关联到广告活动
     * @param request OptimizationRulesAPISwaggerAssociateOptimizationRulesToCampaignRequest (needs campaignId from wrapper)
     * @return
     */
    public AdsResponse<OptimizationRulesAPISwaggerAssociateOptimizationRulesToCampaignResponse> associateOptimizationRulesToCampaign(Long shopId, String campaignId, OptimizationRulesAPISwaggerAssociateOptimizationRulesToCampaignRequest request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(shopId);
        OptimizationRulesApi optimizationRulesApi = new OptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();
            if (appKey == null || profileId == null || campaignId == null) {
                return AdsResponse.error("Missing AppKey, ProfileId, or CampaignId");
            }
            OptimizationRulesAPISwaggerAssociateOptimizationRulesToCampaignResponse response = optimizationRulesApi.associateOptimizationRulesToCampaign(appKey, campaignId, profileId, request);
            return AdsResponse.success(response);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    /**
     * 获取广告活动的SP优化规则 (根据ROAS调整出价等)
     * @param request campaignId
     * @return
     */
    public AdsResponse<CampaignOptimizationRule> getCampaignOptimizationRuleByCampaign(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        CampaignOptimizationRulesApi campaignOptimizationRulesApi = new CampaignOptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            SPCampaignOptimizationNotificationAPIResponse campaignRules = campaignOptimizationRulesApi.getRuleNotification(authorizationModel.getAppKey(),
                    new SPCampaignOptimizationNotificationAPIRequest().addCampaignIdsItem(request.getRequest()),
                    authorizationModel.getProfileId());

            Optional<String> ruleId = campaignRules.getCampaignOptimizationNotifications().stream()
                    .filter(r -> r.getCampaignId().equalsIgnoreCase(request.getRequest()))
                    .map(RuleNotification::getCampaignOptimizationId).findAny();
            if (ruleId.isEmpty()) {
                return AdsResponse.error("empty rules");
            }

            GetSPCampaignOptimizationRuleResponse response = campaignOptimizationRulesApi.getCampaignOptimizationRule(authorizationModel.getAppKey(), ruleId.get(), authorizationModel.getProfileId());
            return AdsResponse.success(response.getCampaignOptimizationRule());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 获取广告活动的SP优化规则 (根据ROAS调整出价等)
     * @param request campaignOptimizationId
     * @return
     */
    public AdsResponse<CampaignOptimizationRule> getCampaignOptimizationRule(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        CampaignOptimizationRulesApi campaignOptimizationRulesApi = new CampaignOptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();
            String ruleId = request.getRequest();
            if (appKey == null || ruleId == null) {
                return AdsResponse.error("Missing AppKey or RuleId");
            }
            GetSPCampaignOptimizationRuleResponse response = campaignOptimizationRulesApi.getCampaignOptimizationRule(appKey, ruleId, profileId);
            return AdsResponse.success(response.getCampaignOptimizationRule());
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 创建广告活动的SP优化规则 (根据ROAS调整出价等)
     * @param request CreateSPCampaignOptimizationRulesRequest
     * @return
     */
    public AdsResponse<CreateSPCampaignOptimizationRulesResponse> createCampaignOptimizationRule(AdsRequest<CreateSPCampaignOptimizationRulesRequest> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        CampaignOptimizationRulesApi campaignOptimizationRulesApi = new CampaignOptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();
            CreateSPCampaignOptimizationRulesRequest createRequest = request.getRequest();
            if (appKey == null || createRequest == null) {
                return AdsResponse.error("Missing AppKey or Request Body");
            }
            CreateSPCampaignOptimizationRulesResponse response = campaignOptimizationRulesApi.createOptimizationRule(appKey, createRequest, profileId);
            return AdsResponse.success(response);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }

    /**
     * 删除广告活动的SP优化规则 (根据ROAS调整出价等)
     * @param request campaignOptimizationId
     * @return
     */
    public AdsResponse<DeleteSPCampaignOptimizationRuleResponse> deleteCampaignOptimizationRule(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = this.getAuthorizationModel(request.getShopId());
        CampaignOptimizationRulesApi campaignOptimizationRulesApi = new CampaignOptimizationRulesApi(this.getApiClient(authorizationModel));

        try {
            String appKey = authorizationModel.getAppKey();
            String profileId = authorizationModel.getProfileId();
            String ruleId = request.getRequest();
            if (appKey == null || ruleId == null) {
                return AdsResponse.error("Missing AppKey or RuleId");
            }
            DeleteSPCampaignOptimizationRuleResponse response = campaignOptimizationRulesApi.deleteCampaignOptimizationRule(appKey, ruleId, profileId);
            return AdsResponse.success(response);
        } catch (ApiException e) {
            return AdsResponse.error(e.getResponseBody());
        }
    }


    // =============================================================== 出价规则 ======================================================

}
