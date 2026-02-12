package com.hzltd.module.erplus.amzadv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.erplus.amzadv.mapper.AmzAdvCampaignMapper;
import com.hzltd.module.erplus.amzadv.mapper.AmzAdvAdGroupMapper;
import com.hzltd.module.erplus.amzadv.mapper.AmzAdvKeywordMapper;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvAdGroupSaveReqVO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvKeywordSaveReqVO;
import com.hzltd.module.erplus.model.ApiRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 亚马逊广告运营集成服务
 * 整合本地数据库操作和远程API调用，提供完整的广告运营功能
 */
@Slf4j
@Service
public class AmzAdvOperationIntegrationService {

    @Resource
    private AmzAdvCampaignService amzAdvCampaignService;
    @Resource
    private AmzAdvAdGroupService amzAdvAdGroupService;
    @Resource
    private AmzAdvKeywordService amzAdvKeywordService;
    @Resource
    private AmzAdvOperationApiService amzAdvOperationApiService;
    
    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;
    @Resource
    private AmzAdvAdGroupMapper amzAdvAdGroupMapper;
    @Resource
    private AmzAdvKeywordMapper amzAdvKeywordMapper;
    @Resource
    private AmzAdvSyncService amzAdvSyncService;

    /**
     * 创建广告活动（同步到亚马逊）
     *
     * @param shopId 店铺ID
     * @param createReqVO 创建请求参数
     * @return 本地广告活动ID
     */
    public Long createCampaignSync(String shopId, AmzAdvCampaignSaveReqVO createReqVO) {
        // 1. 在本地创建广告活动记录
        Long localCampaignId = amzAdvCampaignService.createAmzAdvCampaign(createReqVO);
        
        // 2. 标记为待同步，由同步服务异步处理
        AmzAdvCampaignDO campaignDO = amzAdvCampaignMapper.selectById(localCampaignId);
        if (campaignDO != null) {
            // 设置同步状态为未同步
            campaignDO.setSyncStatus(0); // 0-未同步
            amzAdvCampaignMapper.updateById(campaignDO);
            
            log.info("Created campaign locally. Local ID: {}. Will be synced to Amazon by sync service.", localCampaignId);
        }
        
        return localCampaignId;
    }

    /**
     * 创建广告组（同步到亚马逊）
     *
     * @param shopId 店铺ID
     * @param createReqVO 创建请求参数
     * @return 本地广告组ID
     */
    public Long createAdGroupSync(String shopId, AmzAdvAdGroupSaveReqVO createReqVO) {
        // 1. 在本地创建广告组记录
        Long localAdGroupId = amzAdvAdGroupService.createAmzAdvAdGroup(createReqVO);
        
        // 2. 记录创建，同步由所属广告活动的状态决定
        AmzAdvAdGroupDO adGroupDO = amzAdvAdGroupMapper.selectById(localAdGroupId);
        if (adGroupDO != null) {
            log.info("Created ad group locally. Local ID: {}. Sync status depends on parent campaign.", localAdGroupId);
        }
        
        return localAdGroupId;
    }

    /**
     * 创建关键词（同步到亚马逊）
     *
     * @param shopId 店铺ID
     * @param createReqVO 创建请求参数
     * @return 本地关键词ID
     */
    public Long createKeywordSync(String shopId, AmzAdvKeywordSaveReqVO createReqVO) {
        // 1. 在本地创建关键词记录
        Long localKeywordId = amzAdvKeywordService.createAmzAdvKeyword(createReqVO);
        
        // 2. 记录创建，同步由所属广告活动的状态决定
        AmzAdvKeywordDO keywordDO = amzAdvKeywordMapper.selectById(localKeywordId);
        if (keywordDO != null) {
            log.info("Created keyword locally. Local ID: {}. Sync status depends on parent campaign.", localKeywordId);
        }
        
        return localKeywordId;
    }

    /**
     * 同步广告活动状态到亚马逊
     *
     * @param shopId 店铺ID
     * @param campaignId 本地广告活动ID
     */
    public void syncCampaignToAmazon(String shopId, Long campaignId) {
        // 使用同步服务来同步数据
        amzAdvSyncService.syncLocalCampaignToAmazon(shopId, "profile-placeholder", campaignId);
    }

    /**
     * 获取广告活动表现数据
     *
     * @param shopId 店铺ID
     * @param campaignId 本地广告活动ID
     * @return 表现数据
     */
    public JsonNode getCampaignPerformance(String shopId, Long campaignId) {
        AmzAdvCampaignDO campaignDO = amzAdvCampaignMapper.selectById(campaignId);
        if (campaignDO == null) {
            log.warn("Campaign not found for performance report: {}", campaignId);
            return null;
        }

        try {
            // 创建API请求
            ApiRequest<Object> apiRequest = new ApiRequest<>();
            apiRequest.setShopId(shopId);

            // 调用亚马逊API获取表现数据
            return amzAdvOperationApiService.getCampaignPerformance(
                apiRequest, 
                "profile-placeholder", // 实际应用中需要替换为真实的profileId
                "20230101", // 实际应用中应使用具体日期
                campaignDO.getCampaignId()
            );
        } catch (Exception e) {
            log.error("Failed to get campaign performance from Amazon: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 分页获取本地广告活动及同步状态
     *
     * @param pageReqVO 分页请求参数
     * @return 广告活动分页结果
     */
    public PageResult<AmzAdvCampaignDO> getCampaignsWithSyncStatus(com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvCampaignPageReqVO pageReqVO) {
        return amzAdvCampaignService.getAmzAdvCampaignPage(pageReqVO);
    }
}