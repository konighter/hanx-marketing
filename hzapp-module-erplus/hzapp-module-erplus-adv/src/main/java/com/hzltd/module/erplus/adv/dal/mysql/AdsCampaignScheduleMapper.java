package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignScheduleDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告计划分时调度跟踪 Mapper
 */
@Mapper
public interface AdsCampaignScheduleMapper extends BaseMapperX<AdsCampaignScheduleDO> {

    default AdsCampaignScheduleDO selectByCampaignId(Long campaignId) {
        return selectOne(AdsCampaignScheduleDO::getCampaignId, campaignId);
    }

    default List<AdsCampaignScheduleDO> selectListByNextTransitionTime(LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<AdsCampaignScheduleDO>()
                .le(AdsCampaignScheduleDO::getNextTransitionTime, now));
    }

    default int deleteByCampaignId(Long campaignId) {
        return delete(AdsCampaignScheduleDO::getCampaignId, campaignId);
    }

}
