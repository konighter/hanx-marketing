package com.hzltd.module.erplus.adv.metadata.service.campaign;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignUpdateReqVO;
import jakarta.validation.Valid;

/**
 * 广告计划 Service 接口
 */
public interface AdsCampaignService {

    /**
     * 获得广告计划分页
     *
     * @param pageReqVO 分页查询
     * @return 广告计划分页
     */
    PageResult<AdsCampaignDO> getCampaignPage(AdsCampaignPageReqVO pageReqVO);

    /**
     * 更新广告计划
     *
     * @param updateReqVO 更新信息
     */
    void updateCampaign(@Valid AdsCampaignUpdateReqVO updateReqVO);

    /**
     * 更新广告计划状态
     *
     * @param id 编号
     * @param status 统一状态
     */
    void updateCampaignStatus(Long id, String status);

    /**
     * 获得广告计划
     *
     * @param id 编号
     * @return 广告计划
     */
    AdsCampaignDO getCampaign(Long id);

    /**
     * 根据账户 ID 获取平台信息
     * @param accountId 账户ID
     * @return 平台
     */
    String getPlatformByAccountId(Long accountId);

}
