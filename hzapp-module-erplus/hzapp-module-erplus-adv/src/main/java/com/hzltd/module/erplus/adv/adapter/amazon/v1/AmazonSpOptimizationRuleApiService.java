package com.hzltd.module.erplus.adv.adapter.amazon.v1;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleAssociateReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Amazon SP Optimization Rules API 服务
 *
 * 职责：
 * - 封装 SP 优化规则的管理操作
 * - 使用 AmazonAdsApiClient 进行 HTTP 调用
 */
@Slf4j
@Service
public class AmazonSpOptimizationRuleApiService {

    @Resource
    private AdsApiClient apiClient;

    /**
     * 创建优化规则
     * POST /sp/rules/optimization
     */
    public String createRules(AdsAccountCredentialDO credential, String accountId,
                              String profileId, String baseUrl, AdsOptimizationRuleSaveReqVO request) {
        String url = baseUrl + "/sp/rules/optimization";
        log.info("[createRules] accountId={}, profileId={}, url={}", accountId, profileId, url);

        return apiClient.post(credential, accountId, profileId, url, request);
    }

    /**
     * 关联优化规则到广告计划
     * POST /sp/campaigns/{campaignId}/optimizationRules
     */
    public String associateRules(AdsAccountCredentialDO credential, String accountId,
                                 String profileId, String baseUrl, String campaignId,
                                 AdsOptimizationRuleAssociateReqVO request) {
        String url = baseUrl + "/sp/campaigns/" + campaignId + "/optimizationRules";
        log.info("[associateRules] accountId={}, profileId={}, campaignId={}, url={}", accountId, profileId, campaignId, url);

        return apiClient.post(credential, accountId, profileId, url, request);
    }

    /**
     * 查询广告活动关联的 Optimization Rules
     * GET /sp/campaigns/{campaignId}/optimizationRules
     *
     * @return JSON 字符串（包含优化规则列表），null 表示失败
     */
    public String listRulesByCampaignId(AdsAccountCredentialDO credential,
                                        String accountId, String profileId,
                                        String baseUrl, String campaignId) {
        String url = baseUrl + "/sp/rules/optimization/search";
        log.info("[listRulesByCampaignId] accountId={}, profileId={}, campaignId={}", accountId, profileId, campaignId);
        try {
            java.util.Map<String, Object> campaignFilter = java.util.Map.of("include", java.util.Collections.singletonList(campaignId));
            java.util.Map<String, Object> categoryFilter = java.util.Map.of("include", java.util.Collections.singletonList("BIDDING"));
            java.util.Map<String, Object> subCategoryFilter = java.util.Map.of("include", java.util.Collections.singletonList("SCHEDULE"));
            java.util.Map<String, Object> request = java.util.Map.of(
                "campaignFilter", campaignFilter,
                "ruleCategory", categoryFilter,
                "ruleSubCategory", subCategoryFilter
            );
            return apiClient.post(credential, accountId, profileId, url, request);
        } catch (Exception e) {
            log.warn("[listRulesByCampaignId] 查询失败 campaignId={}: {}", campaignId, e.getMessage());
            return null;
        }
    }
}
