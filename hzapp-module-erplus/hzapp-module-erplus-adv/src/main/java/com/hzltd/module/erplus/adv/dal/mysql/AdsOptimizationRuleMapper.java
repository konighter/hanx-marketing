package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告优化规则 Mapper
 */
@Mapper
public interface AdsOptimizationRuleMapper extends BaseMapperX<AdsOptimizationRuleDO> {

    default AdsOptimizationRuleDO selectByRuleId(String ruleId) {
        return selectOne(AdsOptimizationRuleDO::getRuleId, ruleId);
    }
}
