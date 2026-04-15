package com.hzltd.module.amz.adv.service;

import com.google.common.collect.Lists;
import com.hzltd.module.amz.adv.AbstractAmazonAdsService;
import com.hzltd.module.amz.adv.client.client.ApiException;
import com.hzltd.module.amz.adv.client.subscription.api.StreamSubscriptionApi;
import com.hzltd.module.amz.adv.client.subscription.model.*;
import com.hzltd.module.amz.dal.dataobject.AdsAmazonProfileDO;
import com.hzltd.module.amz.dal.mapper.AdsAmazonProfileMapper;
import com.hzltd.module.erplus.adv.model.AdsRequest;
import com.hzltd.module.erplus.adv.model.AdsResponse;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class AdsAmazonStreamService extends AbstractAmazonAdsService {

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

    public void createStreamSubscription(AdsAmazonProfileDO profile) {
        if (profile == null || !"ENABLED".equals(profile.getStatus())) {
            return;
        }

        AdsAmazonProfileDO.Config config = profile.getConfig();
        if (config == null) {
            config = new AdsAmazonProfileDO.Config();
        }
        Map<String, String> existingSubscriptions = config.getStreamSubscriptions();
        if (existingSubscriptions == null) {
            existingSubscriptions = new HashMap<>();
            config.setStreamSubscriptions(existingSubscriptions);
        }

        // 1. 获取远端已存在的订阅列表，同步本地状态
        try {
            AdsRequest<Map<String, Object>> listRequest = new AdsRequest<>();
            listRequest.setShopId(profile.getShopId());
            AdsResponse<ListStreamSubscriptionsResponseContent> listResponse = this.listStreamSubscriptionsApi(listRequest);
            
            if (listResponse.isSuccess() && listResponse.getData() != null && CollectionUtils.isNotEmpty(listResponse.getData().getSubscriptions())) {
                for (StreamSubscription sub : listResponse.getData().getSubscriptions()) {
                    if (SubscriptionEntityStatus.ACTIVE.equals(sub.getStatus())) {
                        existingSubscriptions.put(sub.getDataSetId(), sub.getSubscriptionId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("[createStreamSubscription] Failed to sync existing subscriptions for profile {}", profile.getProfileId(), e);
        }

        Map<String, List<String>> dataSetToQueueMap = getDataSetToQueueMap();
        boolean updated = false;

        for (Map.Entry<String, List<String>> entry : dataSetToQueueMap.entrySet()) {
            String queueArn = Objects.requireNonNull(entry.getKey());
            List<String> dataSetIds = entry.getValue();

            if (queueArn.isEmpty()) {
                continue;
            }

            for (String dataSetId : dataSetIds) {
                // 如果本地配置显示该 dataset 已经有订阅，直接跳过 (上面已经同步过了)
                if (existingSubscriptions.containsKey(dataSetId)) {
                    continue;
                }

                try {
                    log.info("[createStreamSubscription] Subscribing dataset {} to queue {} for profile {}", 
                            dataSetId, queueArn, profile.getProfileId());

                    CreateStreamSubscriptionRequestContent requestContent = new CreateStreamSubscriptionRequestContent();
                    requestContent.setClientRequestToken(UUID.randomUUID().toString());
                    requestContent.setDataSetId(Objects.requireNonNull(dataSetId));
                    
                    Destination destination = new Destination();
                    SqsDestination sqsDestination = new SqsDestination();
                    sqsDestination.setQueueArn(queueArn);
                    destination.setSqsDestination(sqsDestination);
                    requestContent.setDestination(destination);
                    requestContent.setNotes("Auto subscribed by HanX Erplus for " + dataSetId);

                    AdsRequest<CreateStreamSubscriptionRequestContent> adsRequest = new AdsRequest<>();
                    adsRequest.setShopId(profile.getShopId());
                    adsRequest.setRequest(requestContent);

                    AdsResponse<CreateStreamSubscriptionResponseContent> response = this.createStreamSubscriptionApi(adsRequest);

                    if (response.isSuccess() && response.getData() != null && response.getData().getSubscriptionId() != null) {
                        existingSubscriptions.put(dataSetId, response.getData().getSubscriptionId());
                        updated = true;
                        log.info("[createStreamSubscription] Profile {} subscribed to {} successfully, subId={}", 
                                profile.getProfileId(), dataSetId, response.getData().getSubscriptionId());
                    } else {
                        log.warn("[createStreamSubscription] Profile {} failed to subscribe to {}: {}", 
                                profile.getProfileId(), dataSetId, response.getMessage());
                    }
                } catch (Exception e) {
                    log.error("[createStreamSubscription] Exception during subscription for profile {}", 
                            profile.getProfileId(), e);
                }
            }
        }

        if (updated || !existingSubscriptions.isEmpty()) { // Even if no new subscription, we might have updated existing ones from remote
            profile.setConfig(config);
            adsAmazonProfileMapper.updateById(profile);
        }
    }



    public AdsResponse<CreateStreamSubscriptionResponseContent> createStreamSubscriptionApi(AdsRequest<CreateStreamSubscriptionRequestContent> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : null;
        if (authorizationModel == null) {
            return AdsResponse.error("Authorization not found for shop: " + request.getShopId());
        }
        if (request.getRequest() == null) {
            return AdsResponse.error("Request content is null");
        }
        StreamSubscriptionApi api = new StreamSubscriptionApi(getApiClient(authorizationModel));
        try {
            CreateStreamSubscriptionResponseContent response = api.createStreamSubscription(
                    Objects.requireNonNull(authorizationModel.getAppKey()),
                    Objects.requireNonNull(request.getRequest()),
                    authorizationModel.getAdsAccountId(),
                    authorizationModel.getProfileId());
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[createStreamSubscriptionApi] Failed to create stream subscription", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<GetStreamSubscriptionResponseContent> getStreamSubscriptionApi(AdsRequest<String> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : null;
        if (authorizationModel == null) {
            return AdsResponse.error("Authorization not found for shop: " + request.getShopId());
        }
        String subscriptionId = request.getRequest();
        if (subscriptionId == null) {
            return AdsResponse.error("SubscriptionId is null");
        }
        StreamSubscriptionApi api = new StreamSubscriptionApi(getApiClient(authorizationModel));
        try {
            GetStreamSubscriptionResponseContent response = api.getStreamSubscription(
                    Objects.requireNonNull(subscriptionId),
                    Objects.requireNonNull(authorizationModel.getAppKey()),
                    "amzn1.ads-account.g.46urv0w7o9cejxduu598uh7vs",
                    authorizationModel.getProfileId());
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[getStreamSubscriptionApi] Failed to get stream subscription", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<ListStreamSubscriptionsResponseContent> listStreamSubscriptionsApi(AdsRequest<Map<String, Object>> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : null;
        if (authorizationModel == null) {
            return AdsResponse.error("Authorization not found for shop: " + request.getShopId());
        }

        Map<String, Object> params = request.getRequest() != null ? request.getRequest() : new HashMap<>();
        BigDecimal maxResults = params.containsKey("maxResults") ? new BigDecimal(params.get("maxResults").toString()) : null;
        String startingToken = params.containsKey("startingToken") ? (String) params.get("startingToken") : null;

        StreamSubscriptionApi api = new StreamSubscriptionApi(getApiClient(authorizationModel));
        try {
            ListStreamSubscriptionsResponseContent response = api.listStreamSubscriptions(
                    Objects.requireNonNull(authorizationModel.getAppKey()),
                    maxResults,
                    startingToken,
                    null,
                    authorizationModel.getProfileId());
            return AdsResponse.success(response);
        } catch (ApiException e) {
            log.error("[listStreamSubscriptionsApi] Failed to list stream subscriptions", e);
            return AdsResponse.error(e.getMessage());
        }
    }

    public AdsResponse<Void> updateStreamSubscriptionApi(AdsRequest<Map<String, Object>> request) {
        AuthorizationModel authorizationModel = request.getShopId() != null ? this.getAuthorizationModel(request.getShopId()) : null;
        if (authorizationModel == null) {
            return AdsResponse.error("Authorization not found for shop: " + request.getShopId());
        }

        Map<String, Object> params = request.getRequest();
        if (params == null || !params.containsKey("subscriptionId")) {
            return AdsResponse.error("Missing subscriptionId in request");
        }
        String subscriptionId = (String) params.get("subscriptionId");
        UpdateStreamSubscriptionRequestContent content = (UpdateStreamSubscriptionRequestContent) params.get("content");

        StreamSubscriptionApi api = new StreamSubscriptionApi(getApiClient(authorizationModel));
        try {
            api.updateStreamSubscription(
                    Objects.requireNonNull(subscriptionId),
                    Objects.requireNonNull(authorizationModel.getAppKey()),
                    "amzn1.ads-account.g.46urv0w7o9cejxduu598uh7vs",
                    authorizationModel.getProfileId(),
                    content);
            return AdsResponse.success(null);
        } catch (ApiException e) {
            log.error("[updateStreamSubscriptionApi] Failed to update stream subscription", e);
            return AdsResponse.error(e.getMessage());
        }
    }



}
