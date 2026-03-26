package com.hzltd.module.amz.adv.service.impl;

import com.hzltd.module.amz.adv.dal.dataobject.AmzAdvOptimizationRuleDO;
import com.hzltd.module.amz.adv.dal.mapper.AmzAdvOptimizationRuleMapper;
import com.hzltd.module.amz.adv.service.AmzAdvRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 亚马逊广告特定 Optimization Rule (Bid Rule, Budget Rule) 管理服务实现类
 */
@Service
@Slf4j
public class AmzAdvRuleServiceImpl implements AmzAdvRuleService {
    @Resource
    private AmzAdvOptimizationRuleMapper amzAdvOptimizationRuleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOptimizationRule(AmzAdvOptimizationRuleDO ruleDO) {
        log.info("Saving Optimization Rule for shopId: {}, campaignId: {}, category: {}", 
                 ruleDO.getShopId(), ruleDO.getCampaignId(), ruleDO.getRuleCategory());
        
        // 1. 本地落盘逻辑
        if (ruleDO.getId() == null) {
            amzAdvOptimizationRuleMapper.insert(ruleDO);
        } else {
            amzAdvOptimizationRuleMapper.updateById(ruleDO);
        }
        
        // 2. 亚马逊发送逻辑：ADV API /sp/campaigns/optimizationRules 或 /sp/campaigns/budgetRules
        log.info("Mock synchronizing optimization rule to Amazon ADV API: {}", ruleDO.getRuleId());
    }
}
