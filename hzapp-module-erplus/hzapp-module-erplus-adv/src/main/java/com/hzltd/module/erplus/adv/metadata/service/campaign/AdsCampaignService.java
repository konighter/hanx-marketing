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

    AdsCampaignDO getCampaign(Long id);

    /**
     * 更新广告计划预算
     *
     * @param id 编号
     * @param budget 预算
     */
    void updateCampaignBudget(Long id, java.math.BigDecimal budget);

    /**
     * 根据账户 ID 获取平台信息
     * @param accountId 账户ID
     * @return 平台
     */
    String getPlatformByAccountId(Long accountId);

    /**
     * 计算并保存广告计划的下一次分时调度时间
     * @param campaignId 广告计划ID
     * @param scheduleJson 分时计划 JSON
     */
    void calculateAndSaveNextTransition(Long campaignId, String scheduleJson);

    /**
     * 计算并保存广告计划的下一次分时调度时间
     * @param campaignId 广告计划ID
     * @param scheduleJson 分时计划 JSON
     * @param reconcile 是否立即按当前网格状态校准广告活动开启/暂停
     */
    void calculateAndSaveNextTransition(Long campaignId, String scheduleJson, boolean reconcile);

}
