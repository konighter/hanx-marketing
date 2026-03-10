package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 亚马逊广告 Profile Mapper
 */
@Mapper
public interface AdsAmazonProfileMapper extends BaseMapperX<AdsAmazonProfileDO> {

    default AdsAmazonProfileDO selectByProfileId(String profileId) {
        return selectOne(AdsAmazonProfileDO::getProfileId, profileId);
    }

    default List<AdsAmazonProfileDO> selectBySellerId(String sellerId) {
        return selectList(new LambdaQueryWrapperX<AdsAmazonProfileDO>()
                .eqIfPresent(AdsAmazonProfileDO::getSellerId, sellerId));
    }

    default java.util.List<AdsAmazonProfileDO> selectListByAccountId(Long accountId) {
        return selectList(AdsAmazonProfileDO::getAccountId, accountId);
    }

    default AdsAmazonProfileDO selectByEntityId(String entityId) {
        return selectOne(AdsAmazonProfileDO::getEntityId, entityId);
    }

}
