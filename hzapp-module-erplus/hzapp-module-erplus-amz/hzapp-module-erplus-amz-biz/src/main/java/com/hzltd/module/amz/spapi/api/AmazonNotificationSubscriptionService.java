package com.hzltd.module.amz.spapi.api;

import com.hzltd.module.amz.spapi.model.AmzDestinationModel;
import com.hzltd.module.amz.spapi.model.AmzSubscriptionModel;
import com.hzltd.module.erplus.spapi.api.ServiceRegister;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.service.notification.NotificationSubscriptionApi;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Amazon SP-API 消息通知订阅编排服务
 * 
 * 在店铺授权成功后自动执行: createDestination -> createSubscription(ORDER_CHANGE)
 */
@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = NotificationSubscriptionApi.class)
public class AmazonNotificationSubscriptionService implements NotificationSubscriptionApi {

    @Resource
    private AmazonNotificationApiService notificationApiService;

    @Value("${hzapp.aws.sqs.spapi-queue}")
    private String sqsQueueArn;

    /**
     * 需要自动订阅的通知类型列表
     * 后续可扩展: LISTINGS_ITEM_STATUS_CHANGE, REPORT_PROCESSING_FINISHED 等
     */
    private static final String[] NOTIFICATION_TYPES = {
            "ORDER_CHANGE"
    };

    @Override
    public void setupNotificationSubscriptions(Long shopId) {
        log.info("[setupNotificationSubscriptions][Amazon] shopId={}, 开始初始化通知订阅", shopId);

        try {
            // 1. 确保 Destination 存在
            String destinationId = ensureDestination(shopId);
            if (destinationId == null) {
                log.error("[setupNotificationSubscriptions] 无法创建或获取 Destination, shopId={}", shopId);
                return;
            }

            // 2. 为每种通知类型创建订阅
            for (String notificationType : NOTIFICATION_TYPES) {
                try {
                    createSubscriptionIfNeeded(shopId, destinationId, notificationType);
                } catch (Exception e) {
                    log.error("[setupNotificationSubscriptions] 订阅 {} 失败, shopId={}", notificationType, shopId, e);
                }
            }

            log.info("[setupNotificationSubscriptions][Amazon] shopId={}, 通知订阅初始化完成", shopId);
        } catch (Exception e) {
            log.error("[setupNotificationSubscriptions][Amazon] shopId={}, 初始化失败", shopId, e);
        }
    }

    /**
     * 确保 Destination 存在，返回 destinationId
     * 先查已有列表，匹配 SQS ARN; 如果不存在则创建
     */
    private String ensureDestination(Long shopId) {
        String shopIdStr = String.valueOf(shopId);

        // 先尝试获取已有的 Destinations
        try {
            ApiResponse<List<AmzDestinationModel>> existingResp = notificationApiService.getDestinations(
                    new ApiRequest<>().setShopId(shopIdStr));
            if (existingResp.success() && existingResp.getData() != null) {
                for (AmzDestinationModel dest : existingResp.getData()) {
                    if (sqsQueueArn.equals(dest.getSqsArn())) {
                        log.info("[ensureDestination] 已存在匹配的 Destination: id={}, arn={}", 
                                dest.getDestinationId(), dest.getSqsArn());
                        return dest.getDestinationId();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[ensureDestination] 获取已有 Destination 失败, 尝试创建新的", e);
        }

        // 创建新 Destination
        try {
            ApiRequest<String> createReq = new ApiRequest<>();
            createReq.setShopId(shopIdStr);
            createReq.setRequest(sqsQueueArn);
            ApiResponse<AmzDestinationModel> createResp = notificationApiService.createDestination(createReq);
            if (createResp.success()) {
                log.info("[ensureDestination] 创建 Destination 成功: id={}", createResp.getData().getDestinationId());
                return createResp.getData().getDestinationId();
            }
        } catch (Exception e) {
            log.error("[ensureDestination] 创建 Destination 失败", e);
        }

        return null;
    }

    /**
     * 如果该通知类型尚未订阅，则创建订阅
     */
    private void createSubscriptionIfNeeded(Long shopId, String destinationId, String notificationType) {
        String shopIdStr = String.valueOf(shopId);

        // 检查是否已存在订阅
        try {
            ApiResponse<AmzSubscriptionModel> existingResp = notificationApiService.getSubscription(
                    new ApiRequest<>().setShopId(shopIdStr), notificationType);
            if (existingResp.success() && existingResp.getData() != null) {
                log.info("[createSubscriptionIfNeeded] {} 已存在订阅: subscriptionId={}", 
                        notificationType, existingResp.getData().getSubscriptionId());
                return;
            }
        } catch (Exception e) {
            log.warn("[createSubscriptionIfNeeded] 查询 {} 订阅状态失败, 继续尝试创建", notificationType);
        }

        // 创建订阅
        AmzSubscriptionModel subModel = new AmzSubscriptionModel()
                .setDestinationId(destinationId)
                .setNotificationType(notificationType)
                .setPayloadVersion("1.0");

        ApiRequest<AmzSubscriptionModel> subReq = new ApiRequest<>();
        subReq.setShopId(shopIdStr);
        subReq.setRequest(subModel);

        ApiResponse<?> subResp = notificationApiService.createSubscription(subReq);
        log.info("[createSubscriptionIfNeeded] 订阅 {} 结果: success={}", notificationType, subResp.success());
    }
}
