package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告实体 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsAdMapper extends BaseMapperX<AdsAdDO> {

    default AdsAdDO selectByAdGroupAndExternalId(Long adGroupId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsAdDO>()
                .eq(AdsAdDO::getAdGroupId, adGroupId)
                .eq(AdsAdDO::getExternalId, externalId));
    }

    default AdsAdDO selectByAccountAndExternalId(Long accountId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsAdDO>()
                .eq(AdsAdDO::getAccountId, accountId)
                .eq(AdsAdDO::getExternalId, externalId));
    }

    default List<AdsAdDO> selectListByAdGroupId(Long adGroupId) {
        return selectList(AdsAdDO::getAdGroupId, adGroupId);
    }

    default PageResult<AdsAdDO> selectPage(AdsAdPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAdDO>()
                .eqIfPresent(AdsAdDO::getAccountId, reqVO.getAccountId())
                .inIfPresent(AdsAdDO::getCampaignId, reqVO.getCampaignIds())
                .inIfPresent(AdsAdDO::getAdGroupId, reqVO.getAdGroupIds())
                .eqIfPresent(AdsAdDO::getExternalId, reqVO.getExternalId())
                .likeIfPresent(AdsAdDO::getName, reqVO.getName())
                .eqIfPresent(AdsAdDO::getStatus, reqVO.getStatus())
                .last("ORDER BY FIELD(status, 'ENABLED', 'PAUSED', 'ARCHIVED') ASC, id DESC"));
    }

    default List<AdsAdDO> selectListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsAdDO>()
                .eq(AdsAdDO::getAccountId, accountId)
                .orderByDesc(AdsAdDO::getId));
    }
}
