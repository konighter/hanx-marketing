package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordAttributeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdsKeywordAttributeMapper extends BaseMapperX<AdsKeywordAttributeDO> {

    default void deleteByKeywordId(Long keywordId, String attrType) {
        delete(new LambdaQueryWrapperX<AdsKeywordAttributeDO>()
                .eq(AdsKeywordAttributeDO::getKeywordId, keywordId)
                .eq(AdsKeywordAttributeDO::getAttrType, attrType));
    }

}
