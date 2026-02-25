package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsOptimizationRuleRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告优化规则关系 Mapper
 */
@Mapper
public interface AdsOptimizationRuleRelationMapper extends BaseMapperX<AdsOptimizationRuleRelationDO> {

    default List<AdsOptimizationRuleRelationDO> selectListByRuleId(Long ruleId) {
        return selectList(AdsOptimizationRuleRelationDO::getRuleId, ruleId);
    }

    default List<AdsOptimizationRuleRelationDO> selectListByEntity(Long entityId, String entityType) {
        return selectList(AdsOptimizationRuleRelationDO::getEntityId, entityId,
                AdsOptimizationRuleRelationDO::getEntityType, entityType);
    }
}
