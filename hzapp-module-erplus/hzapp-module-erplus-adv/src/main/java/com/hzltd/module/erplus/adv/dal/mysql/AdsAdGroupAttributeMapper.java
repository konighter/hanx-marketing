package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupAttributeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AdsAdGroupAttributeMapper extends BaseMapperX<AdsAdGroupAttributeDO> {

    default void deleteByAdGroupId(Long adGroupId, String attrType) {
        delete(new LambdaQueryWrapperX<AdsAdGroupAttributeDO>()
                .eq(AdsAdGroupAttributeDO::getAdGroupId, adGroupId)
                .eq(AdsAdGroupAttributeDO::getAttrType, attrType));
    }

    default void deleteByAttributes(Long adGroupId, Collection<String> attrNames, String attrType) {
        delete(new LambdaQueryWrapperX<AdsAdGroupAttributeDO>()
                .eq(AdsAdGroupAttributeDO::getAdGroupId, adGroupId)
                .eq(AdsAdGroupAttributeDO::getAttrType, attrType)
                .inIfPresent(AdsAdGroupAttributeDO::getAttrKey, attrNames));
    }

}
