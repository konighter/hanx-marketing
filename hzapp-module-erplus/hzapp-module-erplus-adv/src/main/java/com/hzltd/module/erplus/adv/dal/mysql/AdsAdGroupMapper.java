package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告组 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsAdGroupMapper extends BaseMapperX<AdsAdGroupDO> {

    default AdsAdGroupDO selectByCampaignAndExternalId(Long campaignId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsAdGroupDO>()
                .eq(AdsAdGroupDO::getCampaignId, campaignId)
                .eq(AdsAdGroupDO::getExternalId, externalId));
    }

    default AdsAdGroupDO selectByAccountAndExternalId(Long accountId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsAdGroupDO>()
                .eq(AdsAdGroupDO::getAccountId, accountId)
                .eq(AdsAdGroupDO::getExternalId, externalId));
    }

    default List<AdsAdGroupDO> selectListByCampaignId(Long campaignId) {
        return selectList(new LambdaQueryWrapperX<AdsAdGroupDO>()
                .eq(AdsAdGroupDO::getCampaignId, campaignId)
                .orderByDesc(AdsAdGroupDO::getId));
    }

    default PageResult<AdsAdGroupDO> selectPage(AdsAdGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsAdGroupDO>()
                .eqIfPresent(AdsAdGroupDO::getAccountId, reqVO.getAccountId())
                .inIfPresent(AdsAdGroupDO::getCampaignId, reqVO.getCampaignIds())
                .eqIfPresent(AdsAdGroupDO::getExternalId, reqVO.getExternalId())
                .likeIfPresent(AdsAdGroupDO::getName, reqVO.getName())
                .eqIfPresent(AdsAdGroupDO::getStatus, reqVO.getStatus())
                .last("ORDER BY FIELD(status, 'ENABLED', 'PAUSED', 'ARCHIVED') ASC, id DESC"));
    }

    default List<AdsAdGroupDO> selectListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsAdGroupDO>()
                .eq(AdsAdGroupDO::getAccountId, accountId)
                .orderByDesc(AdsAdGroupDO::getId));
    }
}
