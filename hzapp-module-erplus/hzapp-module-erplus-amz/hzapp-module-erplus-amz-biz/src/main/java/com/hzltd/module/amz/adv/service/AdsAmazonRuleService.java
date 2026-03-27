package com.hzltd.module.amz.adv.service;

import com.hzltd.module.amz.adv.dal.dataobject.AmzAdvOptimizationRuleDO;

/**
 * 亚马逊广告特定 Optimization Rule (Bid Rule, Budget Rule) 管理服务接口
 */
public interface AdsAmazonRuleService {

    /**
     * 保存从页面传来的亚马逊广告优化规则配置
     * @param ruleDO 前端传来的规则实体
     */
    void saveOptimizationRule(AmzAdvOptimizationRuleDO ruleDO);

}
