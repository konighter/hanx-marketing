package com.hzltd.module.amz.dal.mapper;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
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

    default AdsAmazonProfileDO selectByShopId(Long shopId) {
        return selectOne(AdsAmazonProfileDO::getShopId, shopId);
    }

    default List<AdsAmazonProfileDO> selectListByShopId(Long shopId) {
        return selectList(AdsAmazonProfileDO::getShopId, shopId);
    }

    default List<AdsAmazonProfileDO> selectBySellerId(String sellerId) {
        return selectList(new LambdaQueryWrapperX<AdsAmazonProfileDO>()
                .eqIfPresent(AdsAmazonProfileDO::getSellerId, sellerId));
    }

    default List<AdsAmazonProfileDO> selectListByAccountId(Long accountId) {
        return selectList(AdsAmazonProfileDO::getAccountId, accountId);
    }

    default AdsAmazonProfileDO selectByEntityId(String entityId) {
        return selectOne(AdsAmazonProfileDO::getEntityId, entityId);
    }

}
