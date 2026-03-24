package com.hzltd.module.amz.service.impl;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvKeywordPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvKeywordSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvKeywordDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvKeywordMapper;
import com.hzltd.module.amz.service.AmzAdvKeywordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 亚马逊广告关键词 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
@Validated
public class AmzAdvKeywordServiceImpl implements AmzAdvKeywordService {

    @Resource
    private AmzAdvKeywordMapper amzAdvKeywordMapper;

    @Override
    public Long createAmzAdvKeyword(AmzAdvKeywordSaveReqVO createReqVO) {
        // 创建关键词实体
        AmzAdvKeywordDO keyword = new AmzAdvKeywordDO();
        keyword.setShopId(createReqVO.getShopId());
        keyword.setCampaignId(createReqVO.getCampaignId());
        keyword.setAdGroupId(createReqVO.getAdGroupId());
        keyword.setKeywordText(createReqVO.getKeywordText());
        keyword.setMatchType(createReqVO.getMatchType());
        keyword.setState(createReqVO.getState());
        keyword.setBid(createReqVO.getBid() != null ? createReqVO.getBid() : 1.0); // 默认出价
        keyword.setPriority(createReqVO.getPriority());
        keyword.setDescription(createReqVO.getDescription());
        
        // 插入数据库
        amzAdvKeywordMapper.insert(keyword);
        
        log.info("Created Amazon advertising keyword: {}", keyword.getId());
        return keyword.getId();
    }

    @Override
    public void updateAmzAdvKeyword(AmzAdvKeywordSaveReqVO updateReqVO) {
        // 获取现有关键词
        AmzAdvKeywordDO keyword = amzAdvKeywordMapper.selectById(updateReqVO.getId());
        if (keyword == null) {
            log.error("Keyword not found: {}", updateReqVO.getId());
            return;
        }
        
        // 更新字段
        keyword.setKeywordText(updateReqVO.getKeywordText());
        keyword.setMatchType(updateReqVO.getMatchType());
        keyword.setState(updateReqVO.getState());
        keyword.setBid(updateReqVO.getBid());
        keyword.setPriority(updateReqVO.getPriority());
        keyword.setDescription(updateReqVO.getDescription());
        
        // 更新数据库
        amzAdvKeywordMapper.updateById(keyword);
        
        log.info("Updated Amazon advertising keyword: {}", keyword.getId());
    }

    @Override
    public void deleteAmzAdvKeyword(Long id) {
        AmzAdvKeywordDO keyword = amzAdvKeywordMapper.selectById(id);
        if (keyword == null) {
            log.warn("Attempted to delete non-existent keyword: {}", id);
            return;
        }
        
        // 更新状态为已归档而不是物理删除
        keyword.setState("archived");
        amzAdvKeywordMapper.updateById(keyword);
        
        log.info("Archived Amazon advertising keyword: {}", id);
    }

    @Override
    public AmzAdvKeywordDO getAmzAdvKeyword(Long id) {
        return amzAdvKeywordMapper.selectById(id);
    }

    @Override
    public PageResult<AmzAdvKeywordDO> getAmzAdvKeywordPage(AmzAdvKeywordPageReqVO pageReqVO) {
        return amzAdvKeywordMapper.selectPage(pageReqVO);
    }

    @Override
    public AmzAdvKeywordDO getByShopIdAndKeywordId(String shopId, String keywordId) {
        return amzAdvKeywordMapper.selectByShopIdAndKeywordId(shopId, keywordId);
    }
}