package com.hzltd.module.amz.service.impl;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvCampaignPageReqVO;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvCampaignSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvCampaignDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvCampaignMapper;
import com.hzltd.module.amz.service.AmzAdvCampaignService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 亚马逊广告活动 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
@Validated
public class AmzAdvCampaignServiceImpl implements AmzAdvCampaignService {

    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;

    @Override
    public Long createAmzAdvCampaign(AmzAdvCampaignSaveReqVO createReqVO) {
        // 创建广告活动实体
        AmzAdvCampaignDO campaign = new AmzAdvCampaignDO();
        campaign.setShopId(createReqVO.getShopId());
        campaign.setName(createReqVO.getName());
        campaign.setState(createReqVO.getState());
        campaign.setCampaignType(createReqVO.getCampaignType() != null ? createReqVO.getCampaignType() : "sponsoredProducts"); // 默认为商品推广
        campaign.setDailyBudget(createReqVO.getDailyBudget() != null ? createReqVO.getDailyBudget() : 10.0); // 默认每日预算
        campaign.setBiddingStrategy(createReqVO.getBiddingStrategy() != null ? createReqVO.getBiddingStrategy() : "manual"); // 默认手动出价
        campaign.setDescription(createReqVO.getDescription());
        campaign.setTargetingType(createReqVO.getTargetingType());
        campaign.setSyncStatus(0); // 默认为未同步
        
        // 插入数据库
        amzAdvCampaignMapper.insert(campaign);
        
        log.info("Created Amazon advertising campaign: {}", campaign.getId());
        return campaign.getId();
    }

    @Override
    public void updateAmzAdvCampaign(AmzAdvCampaignSaveReqVO updateReqVO) {
        // 获取现有广告活动
        AmzAdvCampaignDO campaign = amzAdvCampaignMapper.selectById(updateReqVO.getId());
        if (campaign == null) {
            log.error("Campaign not found: {}", updateReqVO.getId());
            return;
        }
        
        // 更新字段
        campaign.setName(updateReqVO.getName());
        campaign.setState(updateReqVO.getState());
        campaign.setDailyBudget(updateReqVO.getDailyBudget());
        campaign.setBiddingStrategy(updateReqVO.getBiddingStrategy());
        campaign.setDescription(updateReqVO.getDescription());
        campaign.setTargetingType(updateReqVO.getTargetingType());
        
        // 更新数据库
        amzAdvCampaignMapper.updateById(campaign);
        
        log.info("Updated Amazon advertising campaign: {}", campaign.getId());
    }

    @Override
    public void deleteAmzAdvCampaign(Long id) {
        AmzAdvCampaignDO campaign = amzAdvCampaignMapper.selectById(id);
        if (campaign == null) {
            log.warn("Attempted to delete non-existent campaign: {}", id);
            return;
        }
        
        // 更新状态为已归档而不是物理删除
        campaign.setState("archived");
        amzAdvCampaignMapper.updateById(campaign);
        
        log.info("Archived Amazon advertising campaign: {}", id);
    }

    @Override
    public AmzAdvCampaignDO getAmzAdvCampaign(Long id) {
        return amzAdvCampaignMapper.selectById(id);
    }

    @Override
    public PageResult<AmzAdvCampaignDO> getAmzAdvCampaignPage(AmzAdvCampaignPageReqVO pageReqVO) {
        return amzAdvCampaignMapper.selectPage(pageReqVO);
    }

    @Override
    public AmzAdvCampaignDO getByShopIdAndCampaignId(String shopId, String campaignId) {
        return amzAdvCampaignMapper.selectByShopIdAndCampaignId(shopId, campaignId);
    }
}