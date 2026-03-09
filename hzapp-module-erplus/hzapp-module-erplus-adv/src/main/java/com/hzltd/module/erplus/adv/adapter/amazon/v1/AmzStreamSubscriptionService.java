package com.hzltd.module.erplus.adv.adapter.amazon.v1;

import com.google.common.collect.Lists;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.model.AmzStreamSubscriptionRequest;
import com.hzltd.module.erplus.adv.adapter.amazon.v1.model.AmzStreamSubscriptionResponse;
import com.hzltd.module.erplus.adv.auth.service.AdsAuthService;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.enums.amz.AmazonRegionEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Amazon Marketing Stream 订阅管理服务
 * 
 * 负责为 Profile 创建和管理 SQS Stream 订阅
 */
@Slf4j
@Service
public class AmzStreamSubscriptionService {

    @Resource
    private AdsAuthService adsAuthService;
    @Resource
    private AmazonAdsReportApiService reportApiService;
    @Resource
    private AdsAmazonProfileMapper adsAmazonProfileMapper;

    @Value("${hzapp.aws.sqs.adv-queue}")
    private String AMZ_ADS_STREAM_SQS_QUEUE_ARN;

    @Value("${hzapp.aws.sqs.ads-manager-queue}")
    private String AMZ_ADS_MANAGER_SQS_QUEUE_ARN;

    private Map<String, List<String>> getDataSetToQueueMap() {
        Map<String, List<String>> map = new HashMap<>();
        // adv-queue 绑定的 dataset
        map.put(AMZ_ADS_STREAM_SQS_QUEUE_ARN, Lists.newArrayList(
                "sp-traffic", "sp-conversion"
                // "sb-traffic", "sb-conversion",
                // "sd-traffic", "sd-conversion"
        ));
        
        // 测试等其他 queue，比如 budget-usage
        map.put(AMZ_ADS_MANAGER_SQS_QUEUE_ARN, Lists.newArrayList(
                "budget-usage"
        ));
        
        return map;
    }

    /**
     * 为指定 Profile 创建 Stream 订阅
     * todo -- 目前SQS只授权了sp广告, 后续添加sb和sd
     *
     * @param profile 站点信息
     */
    public void createStreamSubscription(AdsAmazonProfileDO profile) {
        if (profile == null || !"ENABLED".equals(profile.getStatus())) {
            return;
        }
        // 1. 获取 SQS ARN 配置
        if (AMZ_ADS_STREAM_SQS_QUEUE_ARN == null) {
            log.warn("[createStreamSubscription] 缺失 SQS ARN 配置 , 跳过订阅", AMZ_ADS_STREAM_SQS_QUEUE_ARN);
            return;
        }

        // 2. 获取凭证
        AdsAccountCredentialDO credential = adsAuthService.getAccountCredentialByAccount(profile.getAccountId());
        if (credential == null) {
            log.warn("[createStreamSubscription] 找不到账号 {} 的凭证, 跳过订阅", profile.getAccountId());
            return;
        }

        // 3. 准备请求信息
        String baseUrl = "https://" + AmazonRegionEnum.valueOf(profile.getRegion()).getAdsEndpoint();
        AdsAmazonProfileDO.Config config = profile.getConfig();
        if (config == null) {
            config = new AdsAmazonProfileDO.Config();
            config.setStreamSubscriptions(new HashMap<>());
        }

        List<AmzStreamSubscriptionResponse> subscriptionResponses = reportApiService.listStreamSubscription(credential, profile.getEntityId(), profile.getProfileId(), baseUrl);
        log.info("[createStreamSubscription] Existing SubscriptionInfo: {}", subscriptionResponses);

        Map<String, String> existingSubscriptions = config.getStreamSubscriptions();
        if (existingSubscriptions == null) {
            existingSubscriptions = new HashMap<>();
            config.setStreamSubscriptions(existingSubscriptions);
        }

        // 收集远端已存在且 Active 的订阅
        Map<String, String> remoteActiveSubscriptions = new HashMap<>();
        if (CollectionUtils.isNotEmpty(subscriptionResponses)) {
            for (AmzStreamSubscriptionResponse sub : subscriptionResponses) {
                if ("ACTIVE".equals(sub.getStatus())) {
                    remoteActiveSubscriptions.put(sub.getDataSetId(), sub.getSubscriptionId());
                }
            }
        }

        boolean updated = false;
        Map<String, List<String>> dataSetToQueueMap = getDataSetToQueueMap();

        for (Map.Entry<String, List<String>> entry : dataSetToQueueMap.entrySet()) {
            String queueArn = entry.getKey();
            List<String> dataSetIds = entry.getValue();

            if (queueArn == null || queueArn.isEmpty()) {
                continue;
            }

            for (String dataSetId : dataSetIds) {
                // 如果本地配置显示该 dataset 已经有订阅，并且远端也有这个活跃的订阅，就可以跳过
                if (existingSubscriptions.containsKey(dataSetId) && remoteActiveSubscriptions.containsKey(dataSetId)) {
                    // 同步一下 SubscriptionId 以防不一致
                    String existingSubId = existingSubscriptions.get(dataSetId);
                    String remoteSubId = remoteActiveSubscriptions.get(dataSetId);
                    if (!existingSubId.equals(remoteSubId)) {
                        existingSubscriptions.put(dataSetId, remoteSubId);
                        updated = true;
                    }
                    continue;
                }

                // 没有订阅或者远端被删除了，重新发起订阅
                try {
                    log.info("[createStreamSubscription] 开始为 Profile {} 订阅数据集 {} 到队列 {}", profile.getProfileId(), dataSetId, queueArn);
                    AmzStreamSubscriptionRequest request = AmzStreamSubscriptionRequest.builder()
                            .clientRequestToken(java.util.UUID.randomUUID().toString())
                            .dataSetId(dataSetId)
                            .destination(AmzStreamSubscriptionRequest.Destination.builder()
                                    .sqsDestination(AmzStreamSubscriptionRequest.SqsDestination.builder()
                                            .queueArn(queueArn)
                                            .build())
                                    .build())
                            .notes("Auto subscribed by HanX Erplus for " + dataSetId)
                            .build();

                    AmzStreamSubscriptionResponse response = reportApiService.createStreamSubscription(
                            credential, profile.getEntityId(), profile.getProfileId(), baseUrl, request);

                    if (response != null && response.getSubscriptionId() != null) {
                        existingSubscriptions.put(dataSetId, response.getSubscriptionId());
                        updated = true;
                        log.info("[createStreamSubscription] Profile {} 订阅数据集 {} 成功, subscriptionId={}",
                                profile.getProfileId(), dataSetId, response.getSubscriptionId());
                    }
                } catch (Exception e) {
                    log.error("[createStreamSubscription] Profile {} 订阅数据集 {} 失败", profile.getProfileId(), dataSetId, e);
                }
            }
        }

        // 4. 保存订阅信息
        if (updated) {
            profile.setConfig(config);
            adsAmazonProfileMapper.updateById(profile);
        }
    }

    /**
     * 为指定账号下所有站点创建 Stream 订阅
     *
     * @param accountId 账号 ID
     */
    public void createStreamSubscriptionByAccountId(Long accountId) {
        List<AdsAmazonProfileDO> profiles = adsAmazonProfileMapper.selectList(AdsAmazonProfileDO::getAccountId, accountId);
        for (AdsAmazonProfileDO profile : profiles) {
            createStreamSubscription(profile);
        }
    }
}