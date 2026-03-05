package com.hzltd.module.erplus.adv.metadata.service.keyword;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.adapter.model.AdsBidUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsKeywordMapper;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
    private ObjectMapper objectMapper;

    @Override
    public PageResult<AdsKeywordDO> getKeywordPage(AdsKeywordPageReqVO pageReqVO) {
        return adsKeywordMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateKeywordStatus(Long id, String status) {
        // 1. 校验存在
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_004_001, "广告关键词不存在"));
        }

        // 同步到平台
        AdsAccountDO account = adsAccountMapper.selectById(keyword.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsStatusUpdateRequest updateReq = new AdsStatusUpdateRequest();
                // Determine if it's KEYWORD or TARGET from extData
                AdsEntityTypeEnum entityType = AdsEntityTypeEnum.KEYWORD;
                if (keyword.getExtData() != null) {
                     Map<String, Object> extDataMap = objectMapper.convertValue(keyword.getExtData(), new TypeReference<Map<String, Object>>() {});
                     if (extDataMap != null && Boolean.TRUE.equals(extDataMap.get("isTarget"))) {
                         entityType = AdsEntityTypeEnum.TARGET;
                     }
                }
                updateReq.setEntityType(entityType);
                updateReq.setEntityId(keyword.getExternalId());
                updateReq.setLocalId(id);
                updateReq.setStatus(status);

                boolean success = adapter.updateStatus(account.getId(), updateReq);
                if (success) {
                    // 2. 更新本地状态
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
    @Transactional(rollbackFor = Exception.class)
    public void updateKeywordBid(Long id, java.math.BigDecimal bid) {
        AdsKeywordDO keyword = adsKeywordMapper.selectById(id);
        if (keyword == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_004_001, "广告关键词不存在"));
        }

        AdsAccountDO account = adsAccountMapper.selectById(keyword.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                AdsBidUpdateRequest updateReq = new AdsBidUpdateRequest();
                AdsEntityTypeEnum entityType = AdsEntityTypeEnum.KEYWORD;
                if (keyword.getExtData() != null) {
                     Map<String, Object> extDataMap = objectMapper.convertValue(keyword.getExtData(), new TypeReference<Map<String, Object>>() {});
                     if (extDataMap != null && Boolean.TRUE.equals(extDataMap.get("isTarget"))) {
                         entityType = AdsEntityTypeEnum.TARGET;
                     }
                }
                updateReq.setEntityType(entityType);
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

}
