package com.hzltd.module.erplus.adv.dal.mysql;

import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignAttributeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告计划属性 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AdsCampaignAttributeMapper extends BaseMapperX<AdsCampaignAttributeDO> {

    default void deleteByCampaignId(Long campaignId, String attrType) {
        delete(new LambdaQueryWrapperX<AdsCampaignAttributeDO>()
                .eq(AdsCampaignAttributeDO::getCampaignId, campaignId)
                .eq(AdsCampaignAttributeDO::getAttrType, attrType));
    }

}
