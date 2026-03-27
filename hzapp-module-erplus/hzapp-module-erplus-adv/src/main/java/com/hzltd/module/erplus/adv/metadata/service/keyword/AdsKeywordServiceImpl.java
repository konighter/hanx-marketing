package com.hzltd.module.erplus.adv.metadata.service.keyword;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsBidUpdateRequest;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordAttributeDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordAttributeMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordMapper;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.adv.model.AdsTargetResponse;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.hzltd.framework.common.exception.ErrorCode;
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
    private AdsKeywordAttributeMapper adsKeywordAttributeMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Lazy
    private AdsAdGroupService adsAdGroupService;

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

        AdsAccountDO account = adsAccountMapper.selectById(keyword.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsBidUpdateRequest updateReq = new AdsBidUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.KEYWORD);
                updateReq.setEntityId(keyword.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setBid(bid);

                boolean success = adapter.updateBid(account.getId(), updateReq);
                if (success) {
                    AdsKeywordDO updateObj = new AdsKeywordDO();
                    updateObj.setId(id);
                    updateObj.setBid(bid);
                    adsKeywordMapper.updateById(updateObj);
                }
            } catch (Exception e) {
                log.warn("[updateKeywordBid] 平台出价同步异常: keywordId={}, platform={}", id, account.getPlatform(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKeywordStatus(Long id, String status) {
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new ErrorCode(1_033_004_001, "关键词不存在"));
        }

        AdsAccountDO account = adsAccountMapper.selectById(keyword.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.KEYWORD);
                updateReq.setEntityId(keyword.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setStatus(status);

                boolean success = adapter.updateStatus(account.getId(), updateReq);
                if (success) {
                    AdsKeywordDO updateObj = new AdsKeywordDO();
                    updateObj.setId(id);
                    updateObj.setStatus(status);
                    adsKeywordMapper.updateById(updateObj);
                }
            } catch (Exception e) {
                log.warn("[updateKeywordStatus] 平台状态同步异常: keywordId={}, platform={}", id, account.getPlatform(), e);
            }
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
            try {
                Object value;
                if (StringUtils.hasText(attr.getAttrValueClass())) {
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
    public Long saveKeyword(Long accountId, AdsTargetResponse vo) {
        // 1. 根据外部广告组 ID 解析本地关联 ID
        AdsAdGroupDO adGroup = adsAdGroupService.getAdGroupByAccountAndExternalId(accountId, vo.getAdGroupExternalId());
        if (adGroup == null) {
            log.warn("[saveKeyword] 找不到广告组: accountId={}, externalAdGroupId={}", accountId, vo.getAdGroupExternalId());
            return null;
        }
        Long adGroupId = adGroup.getId();
        Long campaignId = adGroup.getCampaignId();

        AdsKeywordDO existing = adsKeywordMapper.selectByAdGroupAndExternalId(adGroupId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsKeywordDO();
            existing.setAccountId(accountId);
            existing.setCampaignId(campaignId);
            existing.setAdGroupId(adGroupId);
            existing.setExternalId(vo.getExternalId());
        }

        existing.setKeywordText(vo.getKeywordText());
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
