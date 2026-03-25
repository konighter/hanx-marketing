package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdAttributeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdsAdAttributeMapper extends BaseMapperX<AdsAdAttributeDO> {

    default void deleteByAdId(Long adId, String attrType) {
        delete(new LambdaQueryWrapperX<AdsAdAttributeDO>()
                .eq(AdsAdAttributeDO::getAdId, adId)
                .eq(AdsAdAttributeDO::getAttrType, attrType));
    }

}
