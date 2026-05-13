package com.hzltd.module.amz.adv.service;

import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvCampaignSaveReqVO;

/**
 * 亚马逊广告活动出价与关联管理 Service 接口
 *
 * 提供基于亚马逊 ADV API 的 Campaign 竞价强化管理、Placement 修改、以及特有行为。
 */
public interface AdsAmazonCampaignBiddingService {

    /**
     * 更新亚马逊广告活动的竞价与位置策略
     * @param updateVo Campaign 的出价和位置更新对象
     */
    void updateCampaignBiddingAndPlacement(AmzAdvCampaignSaveReqVO updateVo);

}
