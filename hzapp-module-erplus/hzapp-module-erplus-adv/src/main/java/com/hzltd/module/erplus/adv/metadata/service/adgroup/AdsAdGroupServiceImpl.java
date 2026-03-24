package com.hzltd.module.erplus.adv.metadata.service.adgroup;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import com.hzltd.module.adv.model.AdsStatusUpdateRequest;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;



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
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_002_001, "广告组不存在"));
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
    public String getPlatformByAccountId(Long accountId) {
        com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO account = adsAccountMapper.selectById(accountId);
        return account != null ? account.getPlatform() : null;
    }

}
