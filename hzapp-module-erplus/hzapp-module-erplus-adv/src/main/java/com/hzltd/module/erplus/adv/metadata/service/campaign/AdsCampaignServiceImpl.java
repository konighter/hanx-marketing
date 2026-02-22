package com.hzltd.module.erplus.adv.metadata.service.campaign;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

/**
 * 广告计划 Service 实现类
 */
@Service
@Validated
public class AdsCampaignServiceImpl implements AdsCampaignService {

    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAccountCredentialMapper adsAccountCredentialMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;

    @Override
    public PageResult<AdsCampaignDO> getCampaignPage(AdsCampaignPageReqVO pageReqVO) {
        return adsCampaignMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampaignStatus(Long id, String status) {
        // 1. 校验存在
        AdsCampaignDO campaign = adsCampaignMapper.selectById(id);
        if (campaign == null) {
            // 这里应该抛出广告计划不存在的错误，暂时用类似错误代替或直接写死
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_001, "广告计划不存在"));
        }

        // 2. 调用平台 API 同步状态 (如果需要同步的话)
        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getCredentialId() != null) {
            AdsAccountCredentialDO credential = adsAccountCredentialMapper.selectById(account.getCredentialId());
            if (credential != null && campaign.getExternalId() != null) {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory
                        .getAdapter(com.hzltd.module.erplus.adv.enums.AdsPlatformEnum.valueOf(account.getPlatform()));
                Boolean success = false;
                if (Boolean.FALSE.equals(success)) {
                    throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_002, "平台更新状态失败"));
                }
            }
        }

        // 3. 更新本地状态
        AdsCampaignDO updateObj = new AdsCampaignDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        adsCampaignMapper.updateById(updateObj);
    }

    @Override
    public AdsCampaignDO getCampaign(Long id) {
        return adsCampaignMapper.selectById(id);
    }

}
