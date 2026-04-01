package com.hzltd.module.erplus.adv.metadata.service.sync;

import cn.hutool.core.lang.UUID;
import com.hzltd.module.adv.model.*;
import com.hzltd.module.adv.service.AdsManagerApi;

import com.hzltd.module.erplus.adv.adapter.service.AdsManagerApiFactory;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsCampaignDO;
import com.hzltd.module.erplus.adv.metadata.service.ad.AdsAdService;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.metadata.service.campaign.AdsCampaignService;
import com.hzltd.module.erplus.adv.metadata.service.keyword.AdsKeywordService;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import com.hzltd.module.system.model.ShopModel;
import com.hzltd.module.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
    private com.hzltd.module.erplus.adv.dal.mysql.AdsSyncTaskMapper adsSyncTaskMapper;

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

    @Async
    @Override
    public void syncAllMetadataByShop(Long shopId) {
        log.info("[syncAllMetadataByShop] 开始全量同步店铺元数据: shopId={}", shopId);
        syncMetadataByShopId(shopId, new AdsQueryRequest());
    }

    @Async
    @Override
    public void syncIncrementalMetadataByShop(Long shopId) {
        log.info("[syncIncrementalMetadataByShop] 开始增量同步店铺元数据: shopId={}", shopId);
        syncMetadataByShopId(shopId, new AdsQueryRequest());
    }

    @Async
    @Override
    public void syncMetadataByCampaign(Long campaignId) {
        AdsCampaignDO adsCampaign = adsCampaignService.getCampaign(campaignId);
        syncMetadataByShopId(adsCampaign.getShopId(), new AdsQueryRequest().setCampaignIds(List.of(adsCampaign.getExternalId())));
    }

    @Override
    public void executeSyncTask(Long taskId) {
        log.info("[executeSyncTask] 执行同步任务: taskId={}", taskId);
        com.hzltd.module.erplus.adv.dal.dataobject.AdsSyncTaskDO task = adsSyncTaskMapper.selectById(taskId);
        if (task == null) {
            log.warn("[executeSyncTask] 任务不存在: taskId={}", taskId);
            return;
        }

        if ("METADATA_INCR".equals(task.getTaskType())) {
            if (task.getShopId() != null) {
                syncIncrementalMetadataByShop(task.getShopId());
            } else {
                log.warn("[executeSyncTask] 任务未绑定 Shop, 无法执行增量同步: taskId={}", taskId);
            }
        } else {
            // 默认为 METADATA_FULL 等全量同步处理
            if (task.getShopId() != null) {
                syncAllMetadataByShop(task.getShopId());
            } else {
                log.warn("[executeSyncTask] 任务未绑定 Shop, 无法执行全量同步: taskId={}", taskId);
            }
        }
    }

    private void syncMetadataByShopId(Long shopId, AdsQueryRequest request) {
        String uuid = UUID.fastUUID().toString();

        ShopModel shopModel = systemShopService.getShopById(shopId);
        if (shopModel == null) {
            log.error("[syncMetadataByShopID] shop not exist, shopId={}", shopId);
            return;
        }

        AdsManagerApi adsManagerApi = adsManagerApiFactory.getAdsApiService(AdsPlatformEnum.of(shopModel.getPlatformCode()));
        AdsRequest<AdsQueryRequest> req = new AdsRequest<AdsQueryRequest>().setRequest(request).setShopId(shopId);

        // 1. 同步广告计划
        log.info("[syncMetadataByShopId:{}] 同步广告计划: shopId={}", uuid, shopId);
        AdsResponse<List<AdsCampaignModel>> campaignsResponse = adsManagerApi.queryCampaign(req);
        if (!campaignsResponse.isSuccess()) {
             log.error("[syncMetadataByShopId] CampaignQuery Error, shopId={}, error={}", shopId, campaignsResponse.getMessage());
        } else if (!CollectionUtils.isEmpty(campaignsResponse.getData())) {
            int index = 0;
            for (AdsCampaignModel vo : campaignsResponse.getData()) {
                adsCampaignService.saveCampaign(shopId, vo);
                index++;
                if (index % 5 == 0) {
                    log.info("[syncMetadataByShopId:{}] saveCampaigns: {}/{}", uuid, index, campaignsResponse.getData().size());
                }
            }
        }

        // 2. 同步广告组
        log.info("[syncMetadataByShopId:{}] 同步广告组: shopId={}", uuid, shopId);
        AdsResponse<List<AdsAdGroupModel>> adGroupResponse = adsManagerApi.queryAdGroup(req);
        if (!adGroupResponse.isSuccess()) {
             log.error("[syncMetadataByShopId] AdGroupQuery Error, shopId={}, error={}", shopId, adGroupResponse.getMessage());
        } else if (!CollectionUtils.isEmpty(adGroupResponse.getData())) {
            int index = 0;
            for (AdsAdGroupModel vo : adGroupResponse.getData()) {
                adsAdGroupService.saveAdGroup(shopId, vo);
                index++;
                if (index % 5 == 0) {
                    log.info("[syncMetadataByShopId:{}] saveAdGroup: {}/{}", uuid, index, adGroupResponse.getData().size());
                }
            }
        }

        // 3. 同步广告
        log.info("[syncMetadataByShopId:{}] 同步广告: shopId={}", uuid, shopId);
        AdsResponse<List<AdsAdModel>> adResponse = adsManagerApi.queryAd(req);
        if (!adResponse.isSuccess()) {
             log.error("[syncMetadataByShopId] AdQuery Error, shopId={}, error={}", shopId, adResponse.getMessage());
        } else if (!CollectionUtils.isEmpty(adResponse.getData())) {
            int index = 0;
            for (AdsAdModel vo : adResponse.getData()) {
                adsAdService.saveAd(shopId, vo);
                index++;
                if (index % 5 == 0) {
                    log.info("[syncMetadataByShopId:{}] saveAd: {}/{}", uuid, index, adResponse.getData().size());
                }

            }
        }

        // 4. 同步关键词/定向
        log.info("[syncMetadataByShopId: {}] 同步关键词/定向: shopId={}", uuid, shopId);
        AdsResponse<List<AdsTargetModel>> targetResponse = adsManagerApi.queryTarget(req);
        if (!targetResponse.isSuccess()) {
             log.error("[syncMetadataByShopId] TargetQuery Error, shopId={}, error={}", shopId, targetResponse.getMessage());
        } else if (!CollectionUtils.isEmpty(targetResponse.getData())) {
            int index = 0;
            for (AdsTargetModel vo : targetResponse.getData()) {
                adsKeywordService.saveKeyword(shopId, vo);
                index++;
                if (index % 5 == 0) {
                    log.info("[syncMetadataByShopId:{}] saveKeyword: {}/{}", uuid, index, targetResponse.getData().size());
                }
            }
        }

        log.info("[syncMetadataByShopId: {}] 店铺元数据同步完成: shopId={}", uuid, shopId);
    }


}
