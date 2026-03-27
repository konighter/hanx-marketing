package com.hzltd.module.erplus.adv.metadata.service.adgroup;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupAttributeDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupAttributeMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.adv.model.AdsAdGroupResponse;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
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
 * 广告组 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AdsAdGroupServiceImpl implements AdsAdGroupService {

    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsAdGroupAttributeMapper adsAdGroupAttributeMapper;
    @Resource
    private ObjectMapper objectMapper;
    
    @Resource
    @Lazy
    private AdsCampaignService adsCampaignService;

    @Override
    public PageResult<AdsAdGroupDO> getAdGroupPage(AdsAdGroupPageReqVO pageReqVO) {
        return adsAdGroupMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdGroupStatus(Long id, String status) {
        // 1. 校验存在
        AdsAdGroupDO adGroup = adsAdGroupMapper.selectById(id);
        if (adGroup == null) {
            throw exception(new ErrorCode(1_033_002_001, "广告组不存在"));
        }

        // 同步平台
        AdsAccountDO account = adsAccountMapper.selectById(adGroup.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                updateReq.setEntityType(AdsEntityTypeEnum.ADGROUP);
                updateReq.setEntityId(adGroup.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setStatus(status);

                boolean success = adapter.updateStatus(account.getId(), updateReq);
                if (success) {
                    // 2. 更新本地状态
                    AdsAdGroupDO updateObj = new AdsAdGroupDO();
                    updateObj.setId(id);
                    updateObj.setStatus(status);
                    adsAdGroupMapper.updateById(updateObj);
                }
            } catch (Exception e) {
                log.warn("[updateAdGroupStatus] 平台状态同步异常: adGroupId={}, platform={}", id, account.getPlatform(), e);
            }
        }
    }

    @Override
    public AdsAdGroupDO getAdGroup(Long id) {
        return adsAdGroupMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getAdGroupAttributes(Long id) {
        List<AdsAdGroupAttributeDO> attributeDOs = adsAdGroupAttributeMapper.selectList(
                AdsAdGroupAttributeDO::getAdGroupId, id);
        if (CollectionUtils.isEmpty(attributeDOs)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (AdsAdGroupAttributeDO attr : attributeDOs) {
            try {
                Object value;
                if (StringUtils.hasText(attr.getAttrValueClass())) {
                    value = objectMapper.readValue(attr.getAttrValue(), Class.forName(attr.getAttrValueClass()));
                } else {
                    value = objectMapper.readValue(attr.getAttrValue(), Object.class);
                }
                map.put(attr.getAttrKey(), value);
            } catch (Exception e) {
                log.warn("[getAdGroupAttributes] 解析广告组属性失败, adGroupId={}, key={}", id, attr.getAttrKey(), e);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAdGroup(Long accountId, AdsAdGroupResponse vo) {
        // 1. 根据外部计划 ID 解析本地计划 ID
        AdsCampaignDO campaign = adsCampaignService.getCampaignByExternalId(accountId, vo.getCampaignExternalId());
        if (campaign == null) {
            log.warn("[saveAdGroup] 找不到广告计划: accountId={}, externalCampaignId={}", accountId, vo.getCampaignExternalId());
            return null;
        }
        Long campaignId = campaign.getId();

        AdsAdGroupDO existing = adsAdGroupMapper.selectByCampaignAndExternalId(campaignId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsAdGroupDO();
            existing.setAccountId(accountId);
            existing.setCampaignId(campaignId);
            existing.setExternalId(vo.getExternalId());

        }
        existing.setName(vo.getName());
        existing.setStatus(vo.getStatus());
        existing.setDefaultBid(vo.getDefaultBid());
        existing.setBidStrategy(vo.getBidStrategy());
        existing.setTargetingType(vo.getTargetingType());
        existing.setPlatform(vo.getPlatform());
        existing.setExtData(vo.getExtData());
        existing.setSyncedAt(LocalDateTime.now());
        adsAdGroupMapper.insertOrUpdate(existing);
        // 保存属性
        saveAdGroupAttributes(existing.getId(), vo.getAttributes());
        return existing.getId();
    }

    private void saveAdGroupAttributes(Long adGroupId, Map<String, Object> attributes) {
        if (CollUtil.isEmpty(attributes)) {
            return;
        }
        adsAdGroupAttributeMapper.deleteByAdGroupId(adGroupId, "PLATFORM");
        attributes.forEach((key, value) -> {
            AdsAdGroupAttributeDO attr = new AdsAdGroupAttributeDO();
            attr.setAdGroupId(adGroupId);
            attr.setAttrKey(key);
            attr.setAttrValue(JsonUtils.toJsonString(value));
            attr.setAttrValueClass(value.getClass().getName());
            attr.setAttrType("PLATFORM");
            adsAdGroupAttributeMapper.insert(attr);
        });
    }

    @Override
    public AdsAdGroupDO getAdGroupByCampaignAndExternalId(Long campaignId, String externalId) {
        return adsAdGroupMapper.selectByCampaignAndExternalId(campaignId, externalId);
    }

    @Override
    public AdsAdGroupDO getAdGroupByAccountAndExternalId(Long accountId, String externalId) {
        try{
            return adsAdGroupMapper.selectByAccountAndExternalId(accountId, externalId);
        } catch (Exception e) {
            log.error("getAdGroupByAccountAndExternalId error, accountId={}, externalId={}", accountId, externalId, e);
        }
        return null;

    }

}
