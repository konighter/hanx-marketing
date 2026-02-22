package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCreativeAssetDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告创意素材 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsCreativeAssetMapper extends BaseMapperX<AdsCreativeAssetDO> {

    default List<AdsCreativeAssetDO> selectListByAdId(Long adId) {
        return selectList(new LambdaQueryWrapperX<AdsCreativeAssetDO>()
                .eq(AdsCreativeAssetDO::getAdId, adId)
                .orderByAsc(AdsCreativeAssetDO::getSortOrder));
    }

    default List<AdsCreativeAssetDO> selectActiveByAdId(Long adId) {
        return selectList(new LambdaQueryWrapperX<AdsCreativeAssetDO>()
                .eq(AdsCreativeAssetDO::getAdId, adId)
                .eq(AdsCreativeAssetDO::getStatus, "ACTIVE")
                .orderByAsc(AdsCreativeAssetDO::getSortOrder));
    }
}
