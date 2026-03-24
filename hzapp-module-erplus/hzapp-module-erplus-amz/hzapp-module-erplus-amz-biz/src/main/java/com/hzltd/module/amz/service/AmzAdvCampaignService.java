package com.hzltd.module.amz.service;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;

/**
 * 亚马逊广告活动 Service 接口
 *
 * @author 翰展科技
 */
public interface AmzAdvCampaignService {

    /**
     * 创建广告活动
     *
     * @param createReqVO 创建请求参数
     * @return 广告活动ID
     */
    Long createAmzAdvCampaign(AmzAdvCampaignSaveReqVO createReqVO);

    /**
     * 更新广告活动
     *
     * @param updateReqVO 更新请求参数
     */
    void updateAmzAdvCampaign(AmzAdvCampaignSaveReqVO updateReqVO);

    /**
     * 删除广告活动
     *
     * @param id 广告活动ID
     */
    void deleteAmzAdvCampaign(Long id);

    /**
     * 获取广告活动
     *
     * @param id 广告活动ID
     * @return 广告活动
     */
    AmzAdvCampaignDO getAmzAdvCampaign(Long id);

    /**
     * 分页查询广告活动
     *
     * @param pageReqVO 分页请求参数
     * @return 广告活动分页结果
     */
    PageResult<AmzAdvCampaignDO> getAmzAdvCampaignPage(AmzAdvCampaignPageReqVO pageReqVO);

    /**
     * 根据店铺ID和广告活动ID查询
     *
     * @param shopId      店铺ID
     * @param campaignId  广告活动ID
     * @return 广告活动
     */
    AmzAdvCampaignDO getByShopIdAndCampaignId(String shopId, String campaignId);
}