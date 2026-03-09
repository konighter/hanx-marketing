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

    private static final String[] DATA_SET_IDS = {
            "sp-traffic", "sp-conversion",
//            "sb-traffic", "sb-conversion",
//            "sd-traffic", "sd-conversion"
    };

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
        log.info("[createStreamSubscription] SubscriptionInfo: {}", subscriptionResponses);

        List<String> dataSetToSubscription = Lists.newArrayList(DATA_SET_IDS);
        Map<String, String> subscriptions = config.getStreamSubscriptions();

        // 如果已经active，只需要更新 subscriptions 即可，其余的没有
        if (CollectionUtils.isNotEmpty(subscriptionResponses)) {
            for (AmzStreamSubscriptionResponse sub : subscriptionResponses) {
                if (subscriptions.containsKey(sub.getDataSetId())) {
                    dataSetToSubscription.remove(sub.getDataSetId());
                } else if ("ACTIVE".equals(sub.getStatus())) {
                    subscriptions.put(sub.getDataSetId(), sub.getSubscriptionId());
                    dataSetToSubscription.remove(sub.getDataSetId());
                }
            }
        }

        boolean updated = false;
        for (String dataSetId : dataSetToSubscription) {
            // 如果已经订阅过，则跳过
            if (subscriptions.containsKey(dataSetId)) {
                continue;
            }

            try {
                log.info("[createStreamSubscription] 开始为 Profile {} 订阅数据集 {}", profile.getProfileId(), dataSetId);
                AmzStreamSubscriptionRequest request = AmzStreamSubscriptionRequest.builder()
                        .clientRequestToken(java.util.UUID.randomUUID().toString())
                        .dataSetId(dataSetId)
                        .destination(AmzStreamSubscriptionRequest.Destination.builder()
                                .sqsDestination(AmzStreamSubscriptionRequest.SqsDestination.builder()
                                        .queueArn(AMZ_ADS_STREAM_SQS_QUEUE_ARN)
                                        .build())
                                .build())
                        .notes("Auto subscribed by HanX Erplus for " + dataSetId)
                        .build();

                AmzStreamSubscriptionResponse response = reportApiService.createStreamSubscription(
                        credential, profile.getEntityId(), profile.getProfileId(), baseUrl, request);

                if (response != null && response.getSubscriptionId() != null) {
                    subscriptions.put(dataSetId, response.getSubscriptionId());
                    updated = true;
                    log.info("[createStreamSubscription] Profile {} 订阅数据集 {} 成功, subscriptionId={}",
                            profile.getProfileId(), dataSetId, response.getSubscriptionId());
                }
            } catch (Exception e) {
                log.error("[createStreamSubscription] Profile {} 订阅数据集 {} 失败", profile.getProfileId(), dataSetId, e);
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