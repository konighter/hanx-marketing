package com.hzltd.module.erplus.adv.metadata.service.rule;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleAssociateReqVO;

import java.util.List;

/**
 * 广告优化规则 Service 接口
 */
public interface AdsOptimizationRuleService {

    /**
     * 创建优化规则
     *
     * @param createReqVO 创建信息
     */
    void createOptimizationRule(AdsOptimizationRuleSaveReqVO createReqVO);

    /**
     * 关联优化规则
     *
     * @param campaignId 广告计划ID
     * @param associateReqVO 关联信息
     */
    void associateOptimizationRules(Long campaignId, AdsOptimizationRuleAssociateReqVO associateReqVO);

    /**
     * 获取优化规则列表
     *
     * @return 优化规则列表
     */
    List<AdsOptimizationRuleDO> getOptimizationRuleList();

}
