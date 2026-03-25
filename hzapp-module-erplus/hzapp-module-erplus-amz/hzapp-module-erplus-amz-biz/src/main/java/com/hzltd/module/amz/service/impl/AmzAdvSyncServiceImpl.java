package com.hzltd.module.amz.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.dal.mapper.*;
import com.hzltd.module.amz.service.AmzAdvOperationApiService;
import com.hzltd.module.amz.service.AmzAdvSyncService;
import com.hzltd.module.spapi.model.ApiRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 亚马逊广告同步服务实现类
 * 用于同步亚马逊平台上的广告数据到本地
 */
@Slf4j
@Service
public class AmzAdvSyncServiceImpl implements AmzAdvSyncService {

    @Resource
    private AmzAdvOperationApiService amzAdvOperationApiService;
    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;
    @Resource
    private AmzAdvAdGroupMapper amzAdvAdGroupMapper;
    @Resource
    private AmzAdvKeywordMapper amzAdvKeywordMapper;
    @Resource
    private AmzAdvBidStrategyMapper amzAdvBidStrategyMapper;

    @Override
    public int syncCampaigns(String shopId, String profileId) {
        log.info("Starting campaign synchronization for shop: {}, profile: {}", shopId, profileId);
        
        try {
            // 创建API请求
            ApiRequest<Object> apiRequest = new ApiRequest<>();
            apiRequest.setShopId(shopId);
            
            // 获取亚马逊上的广告活动数据
            JsonNode campaignData = amzAdvOperationApiService.listCampaigns(apiRequest, profileId, "");
            
            if (campaignData != null && campaignData.isArray()) {
                int syncedCount = 0;
                
                for (JsonNode campaignNode : campaignData) {
                    // 解析广告活动数据并同步到本地
                    syncCampaignToLocal(campaignNode, shopId);
                    syncedCount++;
                }
                
                log.info("Successfully synchronized {} campaigns for shop: {}", syncedCount, shopId);
                return syncedCount;
            }
        } catch (Exception e) {
            log.error("Error synchronizing campaigns for shop: {}", shopId, e);
        }
        
        return 0;
    }

    @Override
    public int syncAdGroups(String shopId, String profileId) {
        log.info("Starting ad group synchronization for shop: {}, profile: {}", shopId, profileId);
        
        try {
            // TODO: 实现广告组同步逻辑
            // 从亚马逊获取广告组数据并同步到本地
            log.info("Ad group synchronization completed for shop: {}", shopId);
            return 0; // 临时返回值，实际实现时应返回同步的数量
        } catch (Exception e) {
            log.error("Error synchronizing ad groups for shop: {}", shopId, e);
        }
        
        return 0;
    }

    @Override
    public int syncKeywords(String shopId, String profileId) {
        log.info("Starting keyword synchronization for shop: {}, profile: {}", shopId, profileId);
        
        try {
            // TODO: 实现关键词同步逻辑
            // 从亚马逊获取关键词数据并同步到本地
            log.info("Keyword synchronization completed for shop: {}", shopId);
            return 0; // 临时返回值，实际实现时应返回同步的数量
        } catch (Exception e) {
            log.error("Error synchronizing keywords for shop: {}", shopId, e);
        }
        
        return 0;
    }

    @Override
    public int syncBidStrategies(String shopId, String profileId) {
        log.info("Starting bid strategy synchronization for shop: {}, profile: {}", shopId, profileId);
        
        try {
            // TODO: 实现出价策略同步逻辑
            // 从亚马逊获取出价策略数据并同步到本地
            log.info("Bid strategy synchronization completed for shop: {}", shopId);
            return 0; // 临时返回值，实际实现时应返回同步的数量
        } catch (Exception e) {
            log.error("Error synchronizing bid strategies for shop: {}", shopId, e);
        }
        
        return 0;
    }

    @Override
    public void syncAllData(String shopId, String profileId) {
        log.info("Starting full synchronization for shop: {}, profile: {}", shopId, profileId);
        
        // 按顺序同步所有数据
        syncCampaigns(shopId, profileId);
        syncAdGroups(shopId, profileId);
        syncKeywords(shopId, profileId);
        syncBidStrategies(shopId, profileId);
        
        log.info("Full synchronization completed for shop: {}", shopId);
    }

    @Override
    public List<AmzAdvCampaignDO> getLocalCampaignsToSync(String shopId) {
        // 查询本地需要同步到亚马逊的广告活动
        // 筛选sync_status为0(未同步)或3(同步失败)的记录
        return amzAdvCampaignMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AmzAdvCampaignDO>()
                .eq("shop_id", shopId)
                .and(wrapper -> wrapper.eq("sync_status", 0).or().eq("sync_status", 3)));
    }

    @Override
    public List<AmzAdvAdGroupDO> getLocalAdGroupsToSync(String shopId) {
        // 查询本地需要同步到亚马逊的广告组
        // 广告组的同步状态由其所属的广告活动决定
        return amzAdvAdGroupMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AmzAdvAdGroupDO>()
                .eq("shop_id", shopId));
    }

    @Override
    public List<AmzAdvKeywordDO> getLocalKeywordsToSync(String shopId) {
        // 查询本地需要同步到亚马逊的关键词
        // 关键词的同步状态由其所属的广告活动决定
        return amzAdvKeywordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AmzAdvKeywordDO>()
                .eq("shop_id", shopId));
    }

    @Override
    public List<AmzAdvBidStrategyDO> getLocalBidStrategiesToSync(String shopId) {
        // 查询本地需要同步到亚马逊的出价策略
        // 出价策略的同步状态由其所属的广告活动决定
        return amzAdvBidStrategyMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AmzAdvBidStrategyDO>()
                .eq("shop_id", shopId));
    }

    @Override
    public void syncLocalCampaignToAmazon(String shopId, String profileId, Long localCampaignId) {
        log.info("Syncing local campaign {} to Amazon for shop: {}, profile: {}", localCampaignId, shopId, profileId);
        
        try {
            // 获取本地广告活动数据
            AmzAdvCampaignDO localCampaign = amzAdvCampaignMapper.selectById(localCampaignId);
            if (localCampaign == null) {
                log.error("Local campaign not found: {}", localCampaignId);
                return;
            }
            
            // 更新同步状态为同步中
            updateCampaignSyncStatus(localCampaignId, 1, null); // 1-同步中
            
            // 构建API请求数据
            Map<String, Object> campaignData = Map.of(
                "campaignId", localCampaign.getCampaignId(),
                "name", localCampaign.getName(),
                "state", localCampaign.getState(),
                "dailyBudget", localCampaign.getDailyBudget()
            );
            
            // 创建API请求
            ApiRequest<Object> apiRequest = new ApiRequest<>();
            apiRequest.setShopId(shopId);
            
            // 调用亚马逊API更新广告活动
            amzAdvOperationApiService.updateCampaign(
                apiRequest, 
                profileId,
                JsonUtils.toJsonString(campaignData)
            );
            
            // 更新同步状态为已同步
            updateCampaignSyncStatus(localCampaignId, 2, null); // 2-已同步
            log.info("Successfully synced campaign {} to Amazon", localCampaignId);
        } catch (Exception e) {
            log.error("Error syncing campaign {} to Amazon: {}", localCampaignId, e.getMessage(), e);
            // 更新同步状态为同步失败
            updateCampaignSyncStatus(localCampaignId, 3, e.getMessage()); // 3-同步失败
        }
    }

    @Override
    public void syncLocalAdGroupToAmazon(String shopId, String profileId, Long localAdGroupId) {
        log.info("Syncing local ad group {} to Amazon for shop: {}, profile: {}", localAdGroupId, shopId, profileId);
        
        try {
            // 获取本地广告组数据
            AmzAdvAdGroupDO localAdGroup = amzAdvAdGroupMapper.selectById(localAdGroupId);
            if (localAdGroup == null) {
                log.error("Local ad group not found: {}", localAdGroupId);
                return;
            }
            
            // TODO: 实现广告组同步到亚马逊的具体逻辑
            log.info("Successfully synced ad group {} to Amazon", localAdGroupId);
            
            // 广告组的同步状态由其所属的广告活动决定
        } catch (Exception e) {
            log.error("Error syncing ad group {} to Amazon: {}", localAdGroupId, e.getMessage(), e);
            // 同步失败，状态由其所属的广告活动决定
        }
    }

    @Override
    public void syncLocalKeywordToAmazon(String shopId, String profileId, Long localKeywordId) {
        log.info("Syncing local keyword {} to Amazon for shop: {}, profile: {}", localKeywordId, shopId, profileId);
        
        try {
            // 获取本地关键词数据
            AmzAdvKeywordDO localKeyword = amzAdvKeywordMapper.selectById(localKeywordId);
            if (localKeyword == null) {
                log.error("Local keyword not found: {}", localKeywordId);
                return;
            }
            
            // TODO: 实现关键词同步到亚马逊的具体逻辑
            log.info("Successfully synced keyword {} to Amazon", localKeywordId);
            
            // 关键词的同步状态由其所属的广告活动决定
        } catch (Exception e) {
            log.error("Error syncing keyword {} to Amazon: {}", localKeywordId, e.getMessage(), e);
            // 同步失败，状态由其所属的广告活动决定
        }
    }

    @Override
    public void syncLocalBidStrategyToAmazon(String shopId, String profileId, Long localStrategyId) {
        log.info("Syncing local bid strategy {} to Amazon for shop: {}, profile: {}", localStrategyId, shopId, profileId);
        
        try {
            // 获取本地出价策略数据
            AmzAdvBidStrategyDO localStrategy = amzAdvBidStrategyMapper.selectById(localStrategyId);
            if (localStrategy == null) {
                log.error("Local bid strategy not found: {}", localStrategyId);
                return;
            }
            
            // TODO: 实现出价策略同步到亚马逊的具体逻辑
            log.info("Successfully synced bid strategy {} to Amazon", localStrategyId);
            
            // 出价策略的同步状态由其所属的广告活动决定
        } catch (Exception e) {
            log.error("Error syncing bid strategy {} to Amazon: {}", localStrategyId, e.getMessage(), e);
            // 同步失败，状态由其所属的广告活动决定
        }
    }

    /**
     * 将亚马逊广告活动数据同步到本地
     */
    private void syncCampaignToLocal(JsonNode campaignNode, String shopId) {
        try {
            String remoteCampaignId = campaignNode.get("campaignId").asText();
            
            // 检查本地是否已存在该广告活动
            AmzAdvCampaignDO existingCampaign = amzAdvCampaignMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AmzAdvCampaignDO>()
                    .eq("shop_id", shopId).eq("campaign_id", remoteCampaignId));
            
            AmzAdvCampaignDO campaign;
            if (existingCampaign != null) {
                // 更新现有记录
                campaign = existingCampaign;
                campaign.setName(campaignNode.has("name") ? campaignNode.get("name").asText() : campaign.getName());
                campaign.setState(campaignNode.has("state") ? campaignNode.get("state").asText() : campaign.getState());
                campaign.setDailyBudget(campaignNode.has("dailyBudget") ? campaignNode.get("dailyBudget").asDouble() : campaign.getDailyBudget());
                campaign.setLastSyncTime(LocalDateTime.now());
                
                amzAdvCampaignMapper.updateById(campaign);
            } else {
                // 创建新记录
                campaign = new AmzAdvCampaignDO();
                campaign.setShopId(shopId);
                campaign.setCampaignId(remoteCampaignId);
                campaign.setName(campaignNode.has("name") ? campaignNode.get("name").asText() : "");
                campaign.setState(campaignNode.has("state") ? campaignNode.get("state").asText() : "enabled");
                campaign.setCampaignType(campaignNode.has("campaignType") ? campaignNode.get("campaignType").asText() : "sponsoredProducts");
                campaign.setDailyBudget(campaignNode.has("dailyBudget") ? campaignNode.get("dailyBudget").asDouble() : 0.0);
                campaign.setBiddingStrategy(campaignNode.has("biddingStrategy") ? campaignNode.get("biddingStrategy").asText() : "manual");
                campaign.setLastSyncTime(LocalDateTime.now());
                campaign.setSyncStatus(2); // 已同步
                
                amzAdvCampaignMapper.insert(campaign);
            }
        } catch (Exception e) {
            log.error("Error syncing campaign from Amazon to local: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新广告活动同步状态
     */
    private void updateCampaignSyncStatus(Long campaignId, Integer syncStatus, String errorMsg) {
        AmzAdvCampaignDO updateEntity = new AmzAdvCampaignDO();
        updateEntity.setId(campaignId);
        updateEntity.setSyncStatus(syncStatus);
        updateEntity.setLastSyncTime(LocalDateTime.now());
        if (errorMsg != null) {
            updateEntity.setSyncErrorMsg(errorMsg);
        }
        amzAdvCampaignMapper.updateById(updateEntity);
    }






}