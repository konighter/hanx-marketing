package com.hzltd.module.erplus.amzadv.service.impl;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.amzadv.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.erplus.amzadv.mapper.AmzAdvAdGroupMapper;
import com.hzltd.module.erplus.amzadv.service.AmzAdvAdGroupService;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvAdGroupPageReqVO;
import com.hzltd.module.erplus.controller.admin.amzadv.vo.AmzAdvAdGroupSaveReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 亚马逊广告组 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
@Validated
public class AmzAdvAdGroupServiceImpl implements AmzAdvAdGroupService {

    @Resource
    private AmzAdvAdGroupMapper amzAdvAdGroupMapper;

    @Override
    public Long createAmzAdvAdGroup(AmzAdvAdGroupSaveReqVO createReqVO) {
        // 创建广告组实体
        AmzAdvAdGroupDO adGroup = new AmzAdvAdGroupDO();
        adGroup.setShopId(createReqVO.getShopId());
        adGroup.setCampaignId(createReqVO.getCampaignId());
        adGroup.setName(createReqVO.getName());
        adGroup.setState(createReqVO.getState());
        adGroup.setDefaultBid(createReqVO.getDefaultBid() != null ? createReqVO.getDefaultBid() : 1.0); // 默认出价
        adGroup.setMaxBid(createReqVO.getMaxBid());
        adGroup.setPlacement(createReqVO.getPlacement());
        adGroup.setPlacementType(createReqVO.getPlacementType());
        adGroup.setDescription(createReqVO.getDescription());
        
        // 插入数据库
        amzAdvAdGroupMapper.insert(adGroup);
        
        log.info("Created Amazon advertising ad group: {}", adGroup.getId());
        return adGroup.getId();
    }

    @Override
    public void updateAmzAdvAdGroup(AmzAdvAdGroupSaveReqVO updateReqVO) {
        // 获取现有广告组
        AmzAdvAdGroupDO adGroup = amzAdvAdGroupMapper.selectById(updateReqVO.getId());
        if (adGroup == null) {
            log.error("Ad group not found: {}", updateReqVO.getId());
            return;
        }
        
        // 更新字段
        adGroup.setName(updateReqVO.getName());
        adGroup.setState(updateReqVO.getState());
        adGroup.setDefaultBid(updateReqVO.getDefaultBid());
        adGroup.setMaxBid(updateReqVO.getMaxBid());
        adGroup.setPlacement(updateReqVO.getPlacement());
        adGroup.setPlacementType(updateReqVO.getPlacementType());
        adGroup.setDescription(updateReqVO.getDescription());
        
        // 更新数据库
        amzAdvAdGroupMapper.updateById(adGroup);
        
        log.info("Updated Amazon advertising ad group: {}", adGroup.getId());
    }

    @Override
    public void deleteAmzAdvAdGroup(Long id) {
        AmzAdvAdGroupDO adGroup = amzAdvAdGroupMapper.selectById(id);
        if (adGroup == null) {
            log.warn("Attempted to delete non-existent ad group: {}", id);
            return;
        }
        
        // 更新状态为已归档而不是物理删除
        adGroup.setState("archived");
        amzAdvAdGroupMapper.updateById(adGroup);
        
        log.info("Archived Amazon advertising ad group: {}", id);
    }

    @Override
    public AmzAdvAdGroupDO getAmzAdvAdGroup(Long id) {
        return amzAdvAdGroupMapper.selectById(id);
    }

    @Override
    public PageResult<AmzAdvAdGroupDO> getAmzAdvAdGroupPage(AmzAdvAdGroupPageReqVO pageReqVO) {
        return amzAdvAdGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public AmzAdvAdGroupDO getByShopIdAndAdGroupId(String shopId, String adGroupId) {
        return amzAdvAdGroupMapper.selectByShopIdAndAdGroupId(shopId, adGroupId);
    }
}