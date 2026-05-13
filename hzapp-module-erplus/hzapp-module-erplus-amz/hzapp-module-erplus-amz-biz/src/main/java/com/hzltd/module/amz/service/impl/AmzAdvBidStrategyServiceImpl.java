package com.hzltd.module.amz.service.impl;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvBidStrategyPageReqVO;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzAdvBidStrategySaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvBidStrategyMapper;
import com.hzltd.module.amz.service.AmzAdvBidStrategyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 亚马逊广告出价策略 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
@Validated
public class AmzAdvBidStrategyServiceImpl implements AmzAdvBidStrategyService {

    @Resource
    private AmzAdvBidStrategyMapper amzAdvBidStrategyMapper;

    @Override
    public Long createAmzAdvBidStrategy(AmzAdvBidStrategySaveReqVO createReqVO) {
        // 创建出价策略实体
        AmzAdvBidStrategyDO strategy = new AmzAdvBidStrategyDO();
        strategy.setShopId(createReqVO.getShopId());
        strategy.setName(createReqVO.getName());
        strategy.setStrategyType(createReqVO.getStrategyType());
        strategy.setConfig(createReqVO.getConfig());
        strategy.setDescription(createReqVO.getDescription());
        strategy.setEnabled(createReqVO.getEnabled() != null ? createReqVO.getEnabled() : true); // 默认启用
        strategy.setCampaignType(createReqVO.getCampaignType());
        strategy.setTriggerConditions(createReqVO.getTriggerConditions());
        strategy.setAdjustmentPercentage(createReqVO.getAdjustmentPercentage());
        strategy.setMinBid(createReqVO.getMinBid());
        strategy.setMaxBid(createReqVO.getMaxBid());
        strategy.setExecutionFrequency(createReqVO.getExecutionFrequency());
        
        // 插入数据库
        amzAdvBidStrategyMapper.insert(strategy);
        
        log.info("Created Amazon advertising bid strategy: {}", strategy.getId());
        return strategy.getId();
    }

    @Override
    public void updateAmzAdvBidStrategy(AmzAdvBidStrategySaveReqVO updateReqVO) {
        // 获取现有出价策略
        AmzAdvBidStrategyDO strategy = amzAdvBidStrategyMapper.selectById(updateReqVO.getId());
        if (strategy == null) {
            log.error("Bid strategy not found: {}", updateReqVO.getId());
            return;
        }
        
        // 更新字段
        strategy.setName(updateReqVO.getName());
        strategy.setStrategyType(updateReqVO.getStrategyType());
        strategy.setConfig(updateReqVO.getConfig());
        strategy.setDescription(updateReqVO.getDescription());
        strategy.setEnabled(updateReqVO.getEnabled());
        strategy.setCampaignType(updateReqVO.getCampaignType());
        strategy.setTriggerConditions(updateReqVO.getTriggerConditions());
        strategy.setAdjustmentPercentage(updateReqVO.getAdjustmentPercentage());
        strategy.setMinBid(updateReqVO.getMinBid());
        strategy.setMaxBid(updateReqVO.getMaxBid());
        strategy.setExecutionFrequency(updateReqVO.getExecutionFrequency());
        
        // 更新数据库
        amzAdvBidStrategyMapper.updateById(strategy);
        
        log.info("Updated Amazon advertising bid strategy: {}", strategy.getId());
    }

    @Override
    public void deleteAmzAdvBidStrategy(Long id) {
        AmzAdvBidStrategyDO strategy = amzAdvBidStrategyMapper.selectById(id);
        if (strategy == null) {
            log.warn("Attempted to delete non-existent bid strategy: {}", id);
            return;
        }
        
        // 逻辑删除，标记为禁用
        strategy.setEnabled(false);
        amzAdvBidStrategyMapper.updateById(strategy);
        
        log.info("Disabled Amazon advertising bid strategy: {}", id);
    }

    @Override
    public AmzAdvBidStrategyDO getAmzAdvBidStrategy(Long id) {
        return amzAdvBidStrategyMapper.selectById(id);
    }

    @Override
    public PageResult<AmzAdvBidStrategyDO> getAmzAdvBidStrategyPage(AmzAdvBidStrategyPageReqVO pageReqVO) {
        return amzAdvBidStrategyMapper.selectPage(pageReqVO);
    }

    @Override
    public AmzAdvBidStrategyDO getByShopIdAndId(String shopId, Long id) {
        return amzAdvBidStrategyMapper.selectByShopIdAndId(shopId, id);
    }
}