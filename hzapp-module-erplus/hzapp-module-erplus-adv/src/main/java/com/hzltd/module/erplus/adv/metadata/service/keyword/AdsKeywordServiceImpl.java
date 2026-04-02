package com.hzltd.module.erplus.adv.metadata.service.keyword;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.exception.ErrorCode;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.adapter.service.AdsManagerApiFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordAttributeDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordAttributeMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordMapper;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import com.hzltd.module.erplus.adv.model.*;
import com.hzltd.module.erplus.adv.service.AdsManagerApi;
import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
import com.hzltd.module.erplus.system.model.ShopModel;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告关键词 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AdsKeywordServiceImpl implements AdsKeywordService {

    @Resource
    private AdsKeywordMapper adsKeywordMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsManagerApiFactory adsManagerApiFactory;
    @Resource
    private AdsKeywordAttributeMapper adsKeywordAttributeMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Lazy
    private AdsAdGroupService adsAdGroupService;
    @Resource
    private SystemShopService systemShopService;

    @Override
    public PageResult<AdsKeywordDO> getKeywordPage(AdsKeywordPageReqVO pageReqVO) {
        return adsKeywordMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKeywordBid(Long id, BigDecimal bid) {
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new ErrorCode(1_033_004_001, "关键词不存在"));
        }

        AdsManagerApi adsManagerApi = adsManagerApiFactory.getAdsApiService(AdsPlatformEnum.of(keyword.getPlatform()));
        AdsResponse<Boolean> bidUpdateResp = adsManagerApi.updateBid(new AdsRequest<AdsBidUpdateRequest>()
                .setShopId(keyword.getShopId())
                .setRequest(new AdsBidUpdateRequest()
                        .setEntityType(AdsEntityTypeEnum.KEYWORD)
                        .setEntityId(keyword.getExternalId())
                        .setLocalId(id)
                        .setBid(bid)));
        if (bidUpdateResp.isSuccess()) {
            AdsKeywordDO updateObj = new AdsKeywordDO();
            updateObj.setId(id);
            updateObj.setBid(bid);
            adsKeywordMapper.updateById(updateObj);
        } else {
            throw exception(new ErrorCode(1_033_004_002, "更新关键词出价失败"));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKeywordStatus(Long id, String status) {
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new ErrorCode(1_033_004_001, "关键词不存在"));
        }

        AdsManagerApi adsManagerApi = adsManagerApiFactory.getAdsApiService(AdsPlatformEnum.of(keyword.getPlatform()));
        AdsResponse<Boolean> statusUpdateResp = adsManagerApi.updateStatus(new AdsRequest<AdsStatusUpdateRequest>()
                .setShopId(keyword.getShopId())
                .setRequest(new AdsStatusUpdateRequest()
                        .setEntityType(AdsEntityTypeEnum.KEYWORD)
                        .setEntityId(keyword.getExternalId())
                        .setLocalId(id)
                        .setStatus(status)
                ));
        if (statusUpdateResp.isSuccess()) {
            AdsKeywordDO updateObj = new AdsKeywordDO();
            updateObj.setId(id);
            updateObj.setStatus(status);
            adsKeywordMapper.updateById(updateObj);
        } else {
            throw exception(new ErrorCode(1_033_004_003, "关键词状态更新失败"));
        }

    }

    @Override
    public AdsKeywordDO getKeyword(Long id) {
        return adsKeywordMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getKeywordAttributes(Long id) {
        List<AdsKeywordAttributeDO> attributeDOs = adsKeywordAttributeMapper.selectList(
                AdsKeywordAttributeDO::getKeywordId, id);
        if (CollectionUtils.isEmpty(attributeDOs)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (AdsKeywordAttributeDO attr : attributeDOs) {
            if (StringUtils.isEmpty(attr.getAttrValue())) {
                continue;
            }
            try {
                Object value;
                if (StringUtils.isNotEmpty(attr.getAttrValueClass())) {
                    value = objectMapper.readValue(attr.getAttrValue(), Class.forName(attr.getAttrValueClass()));
                } else {
                    value = objectMapper.readValue(attr.getAttrValue(), Object.class);
                }
                map.put(attr.getAttrKey(), value);
            } catch (Exception e) {
                log.warn("[getKeywordAttributes] 解析关键词属性失败, keywordId={}, key={}", id, attr.getAttrKey(), e);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveKeyword(Long shopId, AdsTargetModel vo) {
        // 1. 根据外部广告组 ID 解析本地关联 ID
        String externalAdGroupId = vo.getAdEntityId();
        if (externalAdGroupId == null && vo.getAttributes() != null && vo.getAttributes().containsKey("adGroupId")) {
            externalAdGroupId = String.valueOf(vo.getAttributes().get("adGroupId"));
        }
        AdsAdGroupDO adGroup = adsAdGroupService.getAdGroupByShopAndExternalId(shopId, externalAdGroupId);
        if (adGroup == null) {
            log.warn("[saveKeyword] 找不到广告组: shopId={}, externalAdGroupId={}", shopId, externalAdGroupId);
            return null;
        }
        Long adGroupId = adGroup.getId();
        Long campaignId = adGroup.getCampaignId();

        AdsKeywordDO existing = adsKeywordMapper.selectByAdGroupAndExternalId(adGroupId, vo.getExternalId());

        ShopModel shopModel = systemShopService.getShopById(shopId);
        Long fallbackAccountId = shopModel.getAccountId();

        if (existing == null) {
            existing = new AdsKeywordDO();
            existing.setShopId(shopId);
            existing.setAccountId(fallbackAccountId);
            existing.setCampaignId(campaignId);
            existing.setAdGroupId(adGroupId);
            existing.setExternalId(vo.getExternalId());
        } else {
            existing.setShopId(shopId);
            if (existing.getAccountId() == null) {
                existing.setAccountId(fallbackAccountId);
            }
        }

        existing.setKeywordText(vo.getMatchValue());
        existing.setMatchType(vo.getMatchType());
        existing.setBid(vo.getBid());
        existing.setStatus(vo.getStatus());
        existing.setIsNegative(vo.getIsNegative());
        existing.setPlatform(vo.getPlatform());
        existing.setExtData(vo.getExtData());
        existing.setSyncedAt(LocalDateTime.now());
        adsKeywordMapper.insertOrUpdate(existing);

        // 保存属性
        saveKeywordAttributes(existing.getId(), vo.getAttributes());
        return existing.getId();
    }

    private void saveKeywordAttributes(Long keywordId, Map<String, Object> attributes) {
        if (CollUtil.isEmpty(attributes)) {
            return;
        }
        adsKeywordAttributeMapper.deleteByKeywordId(keywordId, "PLATFORM");
        attributes.forEach((key, value) -> {
            AdsKeywordAttributeDO attr = new AdsKeywordAttributeDO();
            attr.setKeywordId(keywordId);
            attr.setAttrKey(key);
            attr.setAttrValue(JsonUtils.toJsonString(value));
            attr.setAttrValueClass(value.getClass().getName());
            attr.setAttrType("PLATFORM");
            adsKeywordAttributeMapper.insert(attr);
        });
    }

}
