package com.hzltd.module.erplus.adv.metadata.service.rule;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import com.hzltd.module.erplus.adv.metadata.vo.rule.AdsOptimizationRuleSaveReqVO;

import java.util.List;

/**
 * 广告优化规则 Service 接口
 */
public interface AdsOptimizationRuleService {

    /**
     * 创建优化规则
     *
     * @param createReqVO 创建信息
     * @return 规则ID
     */
    String createOptimizationRule(AdsOptimizationRuleSaveReqVO createReqVO);


    /**
     * 获取优化规则列表
     *
     * @return 优化规则列表
     */
    List<AdsOptimizationRuleDO> getOptimizationRuleList();

}
