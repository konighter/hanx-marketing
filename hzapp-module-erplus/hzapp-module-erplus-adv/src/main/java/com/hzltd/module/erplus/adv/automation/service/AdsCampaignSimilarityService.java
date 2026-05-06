package com.hzltd.module.erplus.adv.automation.service;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import java.util.List;

/**
 * 广告活动相似性服务
 * 用于寻找同根词、同主题的广告活动，实现关键词归类
 *
 * @author antigravity
 */
public interface AdsCampaignSimilarityService {

    /**
     * 在给定的广告活动列表中，寻找与目标关键词具有相同词根或主题的广告活动
     *
     * @param associatedCampaignIds 备选广告活动 ID 列表
     * @param keyword 目标关键词
     * @return 匹配到的广告活动列表，按相关度排序
     */
    List<AdsCampaignDO> findSimilarCampaigns(List<Long> associatedCampaignIds, String keyword);

}
