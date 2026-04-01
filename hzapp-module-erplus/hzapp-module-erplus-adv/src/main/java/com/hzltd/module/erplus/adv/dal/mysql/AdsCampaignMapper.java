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



    default AdsCampaignDO selectByShopAndExternalId(Long shopId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getShopId, shopId)
                .eq(AdsCampaignDO::getExternalId, externalId));
    }

    default List<AdsCampaignDO> selectListByShopId(Long shopId) {
        return selectList(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getShopId, shopId)
                .orderByDesc(AdsCampaignDO::getId));
    }

    default List<AdsCampaignDO> selectListByShopIdAndStatus(Long shopId, String status) {
        return selectList(new LambdaQueryWrapperX<AdsCampaignDO>()
                .eq(AdsCampaignDO::getShopId, shopId)
                .eqIfPresent(AdsCampaignDO::getStatus, status)
                .orderByDesc(AdsCampaignDO::getId));
    }

    @SuppressWarnings("deprecation")
    default PageResult<AdsCampaignDO> selectPage(AdsCampaignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdsCampaignDO>()
                .eqIfPresent(AdsCampaignDO::getShopId, reqVO.getShopId())
                .eqIfPresent(AdsCampaignDO::getAccountId, reqVO.getAccountId())
                .eqIfPresent(AdsCampaignDO::getExternalId, reqVO.getExternalId())
                .likeIfPresent(AdsCampaignDO::getName, reqVO.getName())
                .eqIfPresent(AdsCampaignDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdsCampaignDO::getCreateTime, reqVO.getCreateTime())
                .last("ORDER BY start_date DESC, FIELD(status, 'ENABLED', 'PAUSED', 'ARCHIVED') ASC"));
    }
}
