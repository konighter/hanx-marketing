package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 广告计划 Mapper
 *
 * @author hzadd
 */
@Mapper
public interface AdsCampaignMapper extends BaseMapperX<AdsCampaignDO> {

    default AdsCampaignDO selectByAccountAndExternalId(Long accountId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getAccountId, accountId)
                .eq(AdsCampaignDO::getExternalId, externalId));
    }

    default List<AdsCampaignDO> selectListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getAccountId, accountId)
                .orderByDesc(AdsCampaignDO::getId));
    }

    default List<AdsCampaignDO> selectListByAccountIdAndStatus(Long accountId, String status) {
        return selectList(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getAccountId, accountId)
                .eqIfPresent(AdsCampaignDO::getStatus, status)
                .orderByDesc(AdsCampaignDO::getId));
    }

    default PageResult<AdsCampaignDO> selectPage(AdsCampaignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsCampaignDO>()
                .eqIfPresent(AdsCampaignDO::getAccountId, reqVO.getAccountId())
                .eqIfPresent(AdsCampaignDO::getExternalId, reqVO.getExternalId())
                .likeIfPresent(AdsCampaignDO::getName, reqVO.getName())
                .eqIfPresent(AdsCampaignDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdsCampaignDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdsCampaignDO::getId));
    }
}
