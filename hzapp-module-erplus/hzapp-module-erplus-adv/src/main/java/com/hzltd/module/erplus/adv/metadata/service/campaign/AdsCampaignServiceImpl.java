package com.hzltd.module.erplus.adv.metadata.service.campaign;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAdGroupMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsCampaignMapper;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AdsCampaignUpdateReqVO;
import com.hzltd.framework.common.util.object.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AdsCampaignServiceImpl implements AdsCampaignService {

    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
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
    public void updateCampaign(AdsCampaignUpdateReqVO updateReqVO) {
        // 1. 校验存在
        AdsCampaignDO campaign = adsCampaignMapper.selectById(updateReqVO.getId());
        if (campaign == null) {
            throw exception(new com.hzltd.framework.common.exception.ErrorCode(1_033_001_001, "广告计划不存在"));
        }

        // 2. 本地保存：将 VO 转为 DO 并更新数据库（包含 extData、deliverySchedule 等所有字段）
        AdsCampaignDO updateObj = BeanUtils.toBean(updateReqVO, AdsCampaignDO.class);
        adsCampaignMapper.updateById(updateObj);

        // 3. 平台同步 Hook：根据账户平台类型，调用对应适配器的 postCampaignUpdate
        //    目前仅记录日志，不阻塞本地保存。后续各平台适配器覆写此 Hook 实现 API 同步。
        AdsAccountDO account = adsAccountMapper.selectById(campaign.getAccountId());
        if (account != null && account.getPlatform() != null) {
            try {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
                // 重新查询已持久化的完整 DO，确保 Hook 拿到最新数据
                AdsCampaignDO updatedCampaign = adsCampaignMapper.selectById(updateReqVO.getId());
                adapter.postCampaignUpdate(account, updatedCampaign, updateReqVO.getExtData());
            } catch (UnsupportedOperationException e) {
                // 平台暂未支持，跳过
                log.debug("[updateCampaign] 平台 {} 暂不支持 postCampaignUpdate hook", account.getPlatform());
            } catch (Exception e) {
                // 平台同步失败不阻塞本地保存，仅记录日志
                log.warn("[updateCampaign] 平台同步 hook 执行异常, campaignId={}, platform={}",
                        updateReqVO.getId(), account.getPlatform(), e);
            }
        }
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
        AdsCampaignDO campaign = adsCampaignMapper.selectById(id);
        if (campaign != null) {
            // 我们可以在这里或者在 Controller 转换后填充 Platform
            // 为了方便 Convert，我们可能需要一个特殊的 VO 组装逻辑
        }
        return campaign;
    }

    public String getPlatformByAccountId(Long accountId) {
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        return account != null ? account.getPlatform() : null;
    }

}
