package com.hzltd.module.erplus.adv.metadata.service.sync;

import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.adapter.model.AdsQueryRequest;
import com.hzltd.module.erplus.adv.dal.dataobject.*;
import com.hzltd.module.erplus.adv.dal.mysql.*;
import com.hzltd.module.erplus.adv.enums.AdsSyncTaskTypeEnum;
import com.hzltd.module.erplus.adv.metadata.vo.*;
import com.hzltd.module.erplus.adv.enums.AdsPlatformEnum;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADS_ACCOUNT_NOT_EXISTS;

/**
 * 广告元数据同步 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AdsMetadataSyncServiceImpl implements AdsMetadataSyncService {

    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAccountCredentialMapper adsAccountCredentialMapper;
    @Resource
    private AdsSyncTaskMapper adsSyncTaskMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;
    @Resource
    private AdsCampaignMapper adsCampaignMapper;
    @Resource
    private AdsAdGroupMapper adsAdGroupMapper;
    @Resource
    private AdsAdMapper adsAdMapper;
    @Resource
    private AdsKeywordMapper adsKeywordMapper;
    @Resource
    private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    @Override
    @Async
    public void syncAllMetadata(Long accountId) {
        log.info("[syncAllMetadata][开始全量同步账号({})]", accountId);
        // 1. 创建任务记录
        AdsSyncTaskDO task = new AdsSyncTaskDO();
        task.setAccountId(accountId);
        task.setTaskType(AdsSyncTaskTypeEnum.METADATA_FULL.getCode());
        task.setStatus("RUNNING");
        task.setStartedAt(LocalDateTime.now());
        adsSyncTaskMapper.insert(task);

        try {
            executeSync(accountId, task);

            task.setStatus("SUCCESS");
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            log.info("[syncAllMetadata][全量同步账号({})成功]", accountId);
        } catch (Exception e) {
            log.error("[syncAllMetadata][全量同步账号({})失败]", accountId, e);
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
        }
    }

    @Override
    @Async
    public void syncIncrementalMetadata(Long accountId) {
        log.info("[syncIncrementalMetadata][开始增量同步账号({})]", accountId);
        // 逻辑类似全量，只是任务类型不同，Adapter 内部实现可能不同
        AdsSyncTaskDO task = new AdsSyncTaskDO();
        task.setAccountId(accountId);
        task.setTaskType(AdsSyncTaskTypeEnum.METADATA_INCR.getCode());
        task.setStatus("RUNNING");
        task.setStartedAt(LocalDateTime.now());
        adsSyncTaskMapper.insert(task);

        try {
            executeSync(accountId, task);

            task.setStatus("SUCCESS");
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
            log.info("[syncIncrementalMetadata][增量同步账号({})成功]", accountId);
        } catch (Exception e) {
            log.error("[syncIncrementalMetadata][增量同步账号({})失败]", accountId, e);
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            adsSyncTaskMapper.updateById(task);
        }
    }

    @Override
    public void executeSyncTask(Long taskId) {
        AdsSyncTaskDO task = adsSyncTaskMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        executeSync(task.getAccountId(), task);
    }

    private void executeSync(Long accountId, AdsSyncTaskDO task) {
        log.info("[executeSync] 开始执行账号 {} 的同步任务 {}", accountId, task.getTaskType());

        // 1. 获取账号和凭证
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        if (account == null) {
            throw exception(ADS_ACCOUNT_NOT_EXISTS);
        }
        Long credentialId = account.getCredentialId();
        if (credentialId == null && account.getParentId() != null) {
            AdsAccountDO parentAccount = adsAccountMapper.selectById(account.getParentId());
            if (parentAccount != null) {
                credentialId = parentAccount.getCredentialId();
            }
        }

        if (credentialId == null) {
            throw new IllegalStateException("账号未关联凭证，无法同步");
        }

        AdsAccountCredentialDO credential = adsAccountCredentialMapper.selectById(credentialId);
        if (credential == null) {
            throw new IllegalStateException("凭证记录不存在，无法同步");
        }

        // 2. 获取适配器
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory
                .getAdapter(AdsPlatformEnum.valueOf(account.getPlatform()));

        LocalDateTime since = AdsSyncTaskTypeEnum.METADATA_INCR.getCode().equals(task.getTaskType())
                ? account.getLastSyncedAt()
                : null;

        // 3. 同步广告计划
        AdsQueryRequest queryRequest = new AdsQueryRequest();
        if (since != null) {
            queryRequest.setStartAt(since.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        List<AdsCampaignVO> campaigns = adapter.queryCampaigns(accountId, queryRequest);
        if (CollUtil.isNotEmpty(campaigns)) {
            transactionTemplate.execute(status -> {
                for (AdsCampaignVO vo : campaigns) {
                    saveCampaign(accountId, vo);
                }
                return null;
            });
        }

        // 4. 遍历拉取下层实体
        java.util.List<AdsCampaignDO> localCampaigns = adsCampaignMapper.selectListByAccountId(accountId);
        for (AdsCampaignDO campaignDO : localCampaigns) {
            AdsQueryRequest subRequest = new AdsQueryRequest();
            subRequest.setCampaignIds(Collections.singletonList(campaignDO.getExternalId()));
            if (since != null) {
                subRequest.setStartAt(since.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }

            // 同步广告组
            List<AdsAdGroupVO> adGroups = adapter.queryGroups(accountId, subRequest);
            if (CollUtil.isNotEmpty(adGroups)) {
                transactionTemplate.execute(status -> {
                    for (AdsAdGroupVO vo : adGroups) {
                        saveAdGroup(accountId, campaignDO.getId(), vo);
                    }
                    return null;
                });
            }

            // 同步关键词
            List<AdsKeywordVO> keywords = adapter.queryTargets(accountId, subRequest);
            if (CollUtil.isNotEmpty(keywords)) {
                transactionTemplate.execute(status -> {
                    for (AdsKeywordVO vo : keywords) {
                        AdsAdGroupDO adGroup = adsAdGroupMapper.selectByCampaignAndExternalId(campaignDO.getId(),
                                vo.getAdGroupExternalId());
                        if (adGroup != null) {
                            saveKeyword(accountId, adGroup.getId(), vo);
                        } else {
                            log.warn("Cannot find AdGroup for Keyword {} in db", vo.getExternalId());
                        }
                    }
                    return null;
                });
            }

            // 同步广告实体
            List<AdsAdVO> ads = adapter.queryAds(accountId, subRequest);
            if (CollUtil.isNotEmpty(ads)) {
                transactionTemplate.execute(status -> {
                    for (AdsAdVO vo : ads) {
                        AdsAdGroupDO adGroup = adsAdGroupMapper.selectByCampaignAndExternalId(campaignDO.getId(),
                                vo.getAdGroupExternalId());
                        if (adGroup != null) {
                            saveAd(accountId, adGroup.getId(), vo);
                        } else {
                            log.warn("Cannot find AdGroup for Ad {} in db", vo.getExternalId());
                        }
                    }
                    return null;
                });
            }
        }

        // 更新账号的上次同步时间
        account.setLastSyncedAt(LocalDateTime.now());
        adsAccountMapper.updateById(account);
        log.info("[executeSync] 账号 {} 同步完成", accountId);
    }

    private void saveCampaign(Long accountId, AdsCampaignVO vo) {
        AdsCampaignDO existing = adsCampaignMapper.selectByAccountAndExternalId(accountId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsCampaignDO();
            existing.setAccountId(accountId);
            existing.setExternalId(vo.getExternalId());
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setCampaignType(vo.getCampaignType());
            existing.setDailyBudget(vo.getDailyBudget());
            existing.setExtData(vo.getExtData());
            adsCampaignMapper.insert(existing);
        } else {
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setCampaignType(vo.getCampaignType());
            existing.setDailyBudget(vo.getDailyBudget());
            existing.setExtData(vo.getExtData());
            adsCampaignMapper.updateById(existing);
        }
    }

    private void saveAdGroup(Long accountId, Long campaignId, AdsAdGroupVO vo) {
        AdsAdGroupDO existing = adsAdGroupMapper.selectByCampaignAndExternalId(campaignId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsAdGroupDO();
            existing.setAccountId(accountId);
            existing.setCampaignId(campaignId);
            existing.setExternalId(vo.getExternalId());
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setDefaultBid(vo.getDefaultBid());
            existing.setExtData(vo.getExtData());
            adsAdGroupMapper.insert(existing);
        } else {
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setDefaultBid(vo.getDefaultBid());
            existing.setExtData(vo.getExtData());
            adsAdGroupMapper.updateById(existing);
        }
    }

    private void saveKeyword(Long accountId, Long adGroupId, AdsKeywordVO vo) {
        AdsKeywordDO existing = adsKeywordMapper.selectByAdGroupAndExternalId(adGroupId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsKeywordDO();
            existing.setAccountId(accountId);
            existing.setAdGroupId(adGroupId);
            existing.setExternalId(vo.getExternalId());
            existing.setKeywordText(vo.getKeywordText());
            existing.setMatchType(vo.getMatchType());
            existing.setBid(vo.getBid());
            existing.setStatus(vo.getStatus());
            existing.setExtData(vo.getExtData());
            adsKeywordMapper.insert(existing);
        } else {
            existing.setKeywordText(vo.getKeywordText());
            existing.setMatchType(vo.getMatchType());
            existing.setBid(vo.getBid());
            existing.setStatus(vo.getStatus());
            existing.setExtData(vo.getExtData());
            adsKeywordMapper.updateById(existing);
        }
    }

    private void saveAd(Long accountId, Long adGroupId, AdsAdVO vo) {
        AdsAdDO existing = adsAdMapper.selectByAdGroupAndExternalId(adGroupId, vo.getExternalId());
        if (existing == null) {
            existing = new AdsAdDO();
            existing.setAccountId(accountId);
            existing.setAdGroupId(adGroupId);
            existing.setExternalId(vo.getExternalId());
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setExtData(vo.getExtData());
            adsAdMapper.insert(existing);
        } else {
            existing.setName(vo.getName());
            existing.setStatus(vo.getStatus());
            existing.setExtData(vo.getExtData());
            adsAdMapper.updateById(existing);
        }
    }
}
