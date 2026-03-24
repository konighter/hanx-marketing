package com.hzltd.module.erplus.adv.metadata.service.ad;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdMapper;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;



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

    @Override
    public PageResult<AdsAdDO> getAdPage(AdsAdPageReqVO pageReqVO) {
        return adsAdMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdStatus(Long id, String status) {
        // 1. 校验存在
        AdsAdDO ad = adsAdMapper.selectById(id);
        if (ad == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_003_001, "广告不存在"));
        }

        // 同步平台
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
                    // 2. 更新本地状态
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

}
