package com.hzltd.module.amz.service;


import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;

import java.util.List;

/**
 * 亚马逊广告同步服务接口
 * 用于同步亚马逊平台上的广告数据到本地
 */
public interface AmzAdvSyncService {

    /**
     * 同步广告活动数据
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @return 同步的广告活动数量
     */
    int syncCampaigns(String shopId, String profileId);

    /**
     * 同步广告组数据
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @return 同步的广告组数量
     */
    int syncAdGroups(String shopId, String profileId);

    /**
     * 同步关键词数据
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @return 同步的关键词数量
     */
    int syncKeywords(String shopId, String profileId);

    /**
     * 同步出价策略数据
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @return 同步的出价策略数量
     */
    int syncBidStrategies(String shopId, String profileId);

    /**
     * 全量同步广告数据
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     */
    void syncAllData(String shopId, String profileId);

    /**
     * 获取待同步的本地广告活动
     *
     * @param shopId 店铺ID
     * @return 待同步的广告活动列表
     */
    List<AmzAdvCampaignDO> getLocalCampaignsToSync(String shopId);

    /**
     * 获取待同步的本地广告组
     *
     * @param shopId 店铺ID
     * @return 待同步的广告组列表
     */
    List<AmzAdvAdGroupDO> getLocalAdGroupsToSync(String shopId);

    /**
     * 获取待同步的本地关键词
     *
     * @param shopId 店铺ID
     * @return 待同步的关键词列表
     */
    List<AmzAdvKeywordDO> getLocalKeywordsToSync(String shopId);

    /**
     * 获取待同步的本地出价策略
     *
     * @param shopId 店铺ID
     * @return 待同步的出价策略列表
     */
    List<AmzAdvBidStrategyDO> getLocalBidStrategiesToSync(String shopId);

    /**
     * 将本地广告活动同步到亚马逊
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @param localCampaignId 本地广告活动ID
     */
    void syncLocalCampaignToAmazon(String shopId, String profileId, Long localCampaignId);

    /**
     * 将本地广告组同步到亚马逊
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @param localAdGroupId 本地广告组ID
     */
    void syncLocalAdGroupToAmazon(String shopId, String profileId, Long localAdGroupId);

    /**
     * 将本地关键词同步到亚马逊
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @param localKeywordId 本地关键词ID
     */
    void syncLocalKeywordToAmazon(String shopId, String profileId, Long localKeywordId);

    /**
     * 将本地出价策略同步到亚马逊
     *
     * @param shopId 店铺ID
     * @param profileId 广告配置文件ID
     * @param localStrategyId 本地出价策略ID
     */
    void syncLocalBidStrategyToAmazon(String shopId, String profileId, Long localStrategyId);
}