package com.hzltd.module.amz.adv.service;

import com.hzltd.module.amz.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvCampaignMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

/**
 * 亚马逊广告活动出价与关联管理 Service 实现类
 */
@Service
@Slf4j
public class AdsAmazonCampaignBiddingServiceImpl implements AdsAmazonCampaignBiddingService {

    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaignBiddingAndPlacement(AmzAdvCampaignSaveReqVO updateVo) {
        log.info("Updating Amz Campaign Bidding and Placement for shopId: {}, campaignId: {}", updateVo.getShopId(), updateVo.getId());
        
        AmzAdvCampaignDO campaignDO = amzAdvCampaignMapper.selectById(updateVo.getId());
        if (campaignDO == null) {
            log.error("Campaign ID {} not found", updateVo.getId());
            throw new RuntimeException("广告活动不存在");
        }
        
        // 1. 本地落盘逻辑：更新 AmzAdvCampaignDO 的 dynamicBiddingStrategy
        if (updateVo.getDynamicBidding() != null) {
            AmzAdvCampaignDO.DynamicBidding bidding = new AmzAdvCampaignDO.DynamicBidding();
            bidding.setStrategy(updateVo.getDynamicBidding().getStrategy());
            
            if (updateVo.getDynamicBidding().getPlacementBidding() != null) {
                java.util.List<AmzAdvCampaignDO.PlacementBidding> placementList = new ArrayList<>();
                for (AmzAdvCampaignSaveReqVO.PlacementBidding pb : updateVo.getDynamicBidding().getPlacementBidding()) {
                    AmzAdvCampaignDO.PlacementBidding placement = new AmzAdvCampaignDO.PlacementBidding();
                    placement.setPlacement(pb.getPlacement());
                    placement.setPercentage(pb.getPercentage());
                    placementList.add(placement);
                }
                bidding.setPlacementBidding(placementList);
            }
            campaignDO.setDynamicBidding(bidding);
            amzAdvCampaignMapper.updateById(campaignDO);
        }

        // 2. 亚马逊发送逻辑：调用 ADV API 修改远端 campaign bidding (待接入 AmazonAdsAdapter)
        log.info("Mock synchronizing bidding to Amazon ADV API: {}", updateVo.getDynamicBidding());

    }
}
