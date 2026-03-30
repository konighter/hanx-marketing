package com.hzltd.module.erplus.adv.metadata.service.ad;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdAttributeDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdAttributeMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdMapper;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.adv.model.AdsAdResponse;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.hzltd.framework.common.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 广告 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AdsAdServiceImpl implements AdsAdService {

    @Resource
    private AdsAdMapper adsAdMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsAdAttributeMapper adsAdAttributeMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Lazy
    private AdsAdGroupService adsAdGroupService;

    @Override
    public PageResult<AdsAdDO> getAdPage(AdsAdPageReqVO pageReqVO) {
        return adsAdMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdStatus(Long id, String status) {
        AdsAdDO ad = adsAdMapper.selectById(id);
        if (ad == null) {
            throw exception(new ErrorCode(1_033_003_001, "广告不存在"));
        }

        AdsAccountDO account = adsAccountMapper.selectById(ad.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.AD);
                updateReq.setEntityId(ad.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setStatus(status);

                boolean success = adapter.updateStatus(account.getId(), updateReq);
                if (success) {
                    AdsAdDO updateObj = new AdsAdDO();
                    updateObj.setId(id);
                    updateObj.setStatus(status);
                    adsAdMapper.updateById(updateObj);
                }
            } catch (Exception e) {
                log.warn("[updateAdStatus] 平台状态同步异常: adId={}, platform={}", id, account.getPlatform(), e);
            }
        }
    }

    @Override
    public AdsAdDO getAd(Long id) {
        return adsAdMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getAdAttributes(Long id) {
        List<AdsAdAttributeDO> attributeDOs = adsAdAttributeMapper.selectList(
                AdsAdAttributeDO::getAdId, id);
        if (CollectionUtils.isEmpty(attributeDOs)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (AdsAdAttributeDO attr : attributeDOs) {
            try {
                Object value;
                if (StringUtils.hasText(attr.getAttrValueClass())) {
                    value = objectMapper.readValue(attr.getAttrValue(), Class.forName(attr.getAttrValueClass()));
                } else {
                    value = objectMapper.readValue(attr.getAttrValue(), Object.class);
                }
                map.put(attr.getAttrKey(), value);
            } catch (Exception e) {
                log.warn("[getAdAttributes] 解析广告属性失败, adId={}, key={}", id, attr.getAttrKey(), e);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAd(Long accountId, AdsAdResponse vo) {
        // 1. 根据外部广告组 ID 解析本地关联 ID
        AdsAdGroupDO adGroup = adsAdGroupService.getAdGroupByAccountAndExternalId(accountId, vo.getAdGroupExternalId());
        if (adGroup == null) {
            log.warn("[saveAd] 找不到广告组: accountId={}, externalAdGroupId={}", accountId, vo.getAdGroupExternalId());
            return null;
        }
        Long adGroupId = adGroup.getId();
        Long campaignId = adGroup.getCampaignId();

        AdsAdDO existing = adsAdMapper.selectByAdGroupAndExternalId(adGroupId, vo.getExternalId());
        
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        Long shopId = account != null ? account.getShopId() : null;

        if (existing == null) {
            existing = new AdsAdDO();
            existing.setShopId(shopId);
            existing.setAccountId(accountId);
            existing.setCampaignId(campaignId);
            existing.setAdGroupId(adGroupId);
            existing.setExternalId(vo.getExternalId());
        } else {
            existing.setShopId(shopId);
        }
        existing.setName(vo.getName());
        existing.setAdFormat(vo.getAdFormat());
        existing.setStatus(vo.getStatus());
        existing.setHeadline(vo.getHeadline());
        existing.setDescription(vo.getDescription());
        existing.setLandingPageUrl(vo.getLandingPageUrl());
        existing.setCallToAction(vo.getCallToAction());
        existing.setReviewStatus(vo.getReviewStatus());
        existing.setAsin(vo.getAsin());
        existing.setSku(vo.getSku());
        existing.setPlatform(vo.getPlatform());
        existing.setExtData(vo.getExtData());
        existing.setSyncedAt(LocalDateTime.now());
        adsAdMapper.insertOrUpdate(existing);

        // 保存属性
        saveAdAttributes(existing.getId(), vo.getAttributes());
        return existing.getId();
    }

    private void saveAdAttributes(Long adId, Map<String, Object> attributes) {
        if (CollUtil.isEmpty(attributes)) {
            return;
        }
        adsAdAttributeMapper.deleteByAdId(adId,"PLATFORM");
        attributes.forEach((key, value) -> {
            AdsAdAttributeDO attr = new AdsAdAttributeDO();
            attr.setAdId(adId);
            attr.setAttrKey(key);
            attr.setAttrValue(JsonUtils.toJsonString(value));
            attr.setAttrValueClass(value.getClass().getName());
            attr.setAttrType("PLATFORM");
            adsAdAttributeMapper.insert(attr);
        });
    }

}
