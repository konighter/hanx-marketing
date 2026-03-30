package com.hzltd.module.erplus.adv.metadata.service.sync;

import com.hzltd.module.adv.model.*;
import com.hzltd.module.adv.service.AdsManagerApi;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.adapter.service.AdsManagerApiFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.metadata.service.ad.AdsAdService;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.erplus.adv.metadata.service.keyword.AdsKeywordService;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import com.hzltd.module.system.model.ShopModel;
import com.hzltd.module.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 广告元数据同步 Service 实现类
 */
@Service
@Slf4j
public class AdsMetadataSyncServiceImpl implements AdsMetadataSyncService {

    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsCampaignService adsCampaignService;
    @Resource
    private AdsAdGroupService adsAdGroupService;
    @Resource
    private AdsAdService adsAdService;
    @Resource
    private AdsKeywordService adsKeywordService;
    @Resource
    private AdsManagerApiFactory adsManagerApiFactory;
    @Resource
    private SystemShopService systemShopService;

    @Override
    public void syncAllMetadata(Long accountId) {
        log.info("[syncAllMetadata] 开始全量同步账号元数据: accountId={}", accountId);
        syncMetadata(accountId, new AdsQueryRequest());
    }

    @Override
    public void syncAllMetadataByShop(Long shopId) {
        log.info("[syncAllMetadataByShop] 开始全量同步店铺元数据: shopId={}", shopId);
        List<AdsAccountDO> accounts = adsAccountMapper.selectListByShopId(shopId);
        if (CollectionUtils.isEmpty(accounts)) {
            log.warn("[syncAllMetadataByShop] 店铺下无广告账号: shopId={}", shopId);
            return;
        }
        for (AdsAccountDO account : accounts) {
            syncAllMetadata(account.getId());
        }
    }

    @Override
    public void syncIncrementalMetadata(Long accountId) {
        log.info("[syncIncrementalMetadata] 开始增量同步账号元数据: accountId={}", accountId);
        // 增量同步逻辑可以根据需要填充 AdsQueryRequest，例如设置 startAt
        syncMetadata(accountId, new AdsQueryRequest());
    }

    @Override
    public void syncIncrementalMetadataByShop(Long shopId) {
        log.info("[syncIncrementalMetadataByShop] 开始增量同步店铺元数据: shopId={}", shopId);
        List<AdsAccountDO> accounts = adsAccountMapper.selectListByShopId(shopId);
        if (CollectionUtils.isEmpty(accounts)) {
            log.warn("[syncIncrementalMetadataByShop] 店铺下无广告账号: shopId={}", shopId);
            return;
        }
        for (AdsAccountDO account : accounts) {
            syncIncrementalMetadata(account.getId());
        }
    }

    @Override
    public void executeSyncTask(Long taskId) {
        log.info("[executeSyncTask] 执行同步任务: taskId={}", taskId);
        // 这里可以根据任务表中的 accountId 调用 syncMetadata
    }

    private void syncMetadata(Long accountId, AdsQueryRequest request) {
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        if (account == null) {
            log.warn("[syncMetadata] 账号不存在: accountId={}", accountId);
            return;
        }

        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
        
        // 1. 同步广告计划
        log.info("[syncMetadata] 同步广告计划: accountId={}", accountId);
        List<AdsCampaignResponse> campaigns = adapter.queryCampaigns(accountId, request);
        if (!CollectionUtils.isEmpty(campaigns)) {
            for (AdsCampaignResponse vo : campaigns) {
                adsCampaignService.saveCampaign(accountId, vo);
            }
        }

        // 2. 同步广告组
        log.info("[syncMetadata] 同步广告组: accountId={}", accountId);
        List<AdsAdGroupResponse> adGroups = adapter.queryGroups(accountId, request);
        if (!CollectionUtils.isEmpty(adGroups)) {
            for (AdsAdGroupResponse vo : adGroups) {
                // 需要根据外部 ID 查找本地计划 ID
                // 此处假设适配器返回的 VO 中包含足够的上下文，或者后面 saveAdGroup 内部处理
                adsAdGroupService.saveAdGroup(accountId, vo);
            }
        }

        // 3. 同步广告
        log.info("[syncMetadata] 同步广告: accountId={}", accountId);
        List<AdsAdResponse> ads = adapter.queryAds(accountId, request);
        if (!CollectionUtils.isEmpty(ads)) {
            for (AdsAdResponse vo : ads) {
                adsAdService.saveAd(accountId, vo);
            }
        }

        // 4. 同步关键词/定向
        log.info("[syncMetadata] 同步关键词/定向: accountId={}", accountId);
        List<AdsTargetResponse> keywords = adapter.queryTargets(accountId, request);
        if (!CollectionUtils.isEmpty(keywords)) {
            for (AdsTargetResponse vo : keywords) {
                adsKeywordService.saveKeyword(accountId, vo);
            }
        }
        
        log.info("[syncMetadata] 账号元数据同步完成: accountId={}", accountId);
    }


    private void syncMetadataByShopId(Long shopId, AdsQueryRequest request) {

        ShopModel shopModel = systemShopService.getShopById(shopId);
        if (shopModel == null) {
            log.error("[syncMetadataByShopID] shop not exist,shopId={}", shopId);
            return;
        }

        AdsManagerApi adsManagerApi = adsManagerApiFactory.getAdsApiService(AdsPlatformEnum.of(shopModel.getPlatformCode()));

        AdsResponse<List<AdsCampaignResponse>> campaignsResponse = adsManagerApi.queryCampaign(new AdsRequest<AdsQueryRequest>().setRequest(request).setShopId(shopId));
        if (!campaignsResponse.isSuccess()) {
            log.error("[syncMetadataByShopID] CampaignQuery Error, shopId={}, error={}", shopId, campaignsResponse.getMessage());
            return;
        }

        if (!CollectionUtils.isEmpty(campaignsResponse.getData())) {
            for (AdsCampaignResponse vo : campaignsResponse.getData()) {
                adsCampaignService.saveCampaign(null, vo);
            }
        }






    }


}
