package com.hzltd.module.erplus.amzadv.mapper;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.mapper.BaseMapperX;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvCampaignPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 亚马逊广告活动 Mapper
 *
 * @author 翰展科技
 */
@Mapper
public interface AmzAdvCampaignMapper extends BaseMapperX<AmzAdvCampaignDO> {

    /**
     * 分页查询广告活动
     *
     * @param pageReqVO 分页请求参数
     * @return 广告活动分页结果
     */
    PageResult<AmzAdvCampaignDO> selectPage(AmzAdvCampaignPageReqVO pageReqVO);

    /**
     * 根据店铺ID和广告活动ID查询
     *
     * @param shopId      店铺ID
     * @param campaignId  广告活动ID
     * @return 广告活动
     */
    AmzAdvCampaignDO selectByShopIdAndCampaignId(String shopId, String campaignId);
}