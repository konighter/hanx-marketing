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
}
