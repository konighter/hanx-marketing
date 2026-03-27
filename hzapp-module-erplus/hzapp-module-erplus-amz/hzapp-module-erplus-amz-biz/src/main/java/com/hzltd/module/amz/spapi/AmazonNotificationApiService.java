package com.hzltd.module.amz.spapi;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.model.AmzDestinationModel;
import com.hzltd.module.amz.spapi.model.AmzSubscriptionModel;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.notifications.v1.NotificationsApi;
import software.amazon.spapi.models.notifications.v1.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmazonNotificationApiService extends AbsAmzPlatformApiService {

    /**
     * 创建AMZ事件通知目的地
     * sqs-arn: "arn:aws:sqs:us-east-1:663959447324:HanXErplus-OrderEventQueue-us-east"
     * @param request
     * @return
     */
    public ApiResponse<AmzDestinationModel> createDestination(ApiRequest<String> request) {
        NotificationsApi notificationsApi = getNotificationApi(request);

        try {
            CreateDestinationResponse response = notificationsApi.createDestination(new CreateDestinationRequest()
                    .name("AMZ_EVENT_DESTINATION_COMMON")
                    .resourceSpecification(new DestinationResourceSpecification()
                            .sqs(new SqsResource().arn(request.getRequest()))));
            return ApiResponse.success(new AmzDestinationModel()
                    .setDestinationId(response.getPayload().getDestinationId())
                    .setName(response.getPayload().getName())
                    .setSqsArn(response.getPayload().getResource().getSqs().getArn()));
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取已有的 Destination 列表
     */
    public ApiResponse<List<AmzDestinationModel>> getDestinations(ApiRequest<?> request) {
        NotificationsApi notificationsApi = getNotificationApi(request);
        try {
            GetDestinationsResponse response = notificationsApi.getDestinations();
            List<AmzDestinationModel> destinations = response.getPayload().stream()
                    .map(d -> new AmzDestinationModel()
                            .setDestinationId(d.getDestinationId())
                            .setName(d.getName())
                            .setSqsArn(d.getResource() != null && d.getResource().getSqs() != null
                                    ? d.getResource().getSqs().getArn() : null))
                    .collect(Collectors.toList());
            return ApiResponse.success(destinations);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定 notificationType 的 Subscription
     */
    public ApiResponse<AmzSubscriptionModel> getSubscription(ApiRequest<?> request, String notificationType) {
        NotificationsApi notificationsApi = getNotificationApiAuth(request);
        try {
            GetSubscriptionResponse response = notificationsApi.getSubscription(notificationType, notificationType);
            Subscription sub = response.getPayload();
            AmzSubscriptionModel model = new AmzSubscriptionModel()
                    .setSubscriptionId(sub.getSubscriptionId())
                    .setNotificationType(notificationType)
                    .setDestinationId(sub.getDestinationId())
                    .setPayloadVersion(sub.getPayloadVersion());
            return ApiResponse.success(model);
        } catch (ApiException | LWAException e) {
            // 如果不存在订阅, API 返回 404
            log.warn("[getSubscription] notificationType={}, error={}", notificationType, e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建AMZ事件通知订阅
     * @param request
     * @return
     */
    public ApiResponse<?> createSubscription(ApiRequest<AmzSubscriptionModel> request) {

        NotificationsApi notificationsApi = getNotificationApiAuth(request);

        try {
            CreateSubscriptionRequest subscriptionRequest = new CreateSubscriptionRequest()
                    .payloadVersion(request.getRequest().getPayloadVersion())
                    .destinationId(request.getRequest().getDestinationId());

            // 如果是 ORDER_CHANGE 类型，添加 processingDirective
            if ("ORDER_CHANGE".equals(request.getRequest().getNotificationType())) {
                subscriptionRequest.processingDirective(new ProcessingDirective()
                        .eventFilter(new EventFilter()
                                .eventFilterType(EventFilter.EventFilterTypeEnum.ORDER_CHANGE)));
            }

            CreateSubscriptionResponse response = notificationsApi.createSubscription(
                    subscriptionRequest, request.getRequest().getNotificationType());

             AmzSubscriptionModel amzSubscriptionModel = new AmzSubscriptionModel()
                     .setSubscriptionId(response.getPayload().getSubscriptionId())
                     .setNotificationType(request.getRequest().getNotificationType())
                     .setPayloadVersion(request.getRequest().getPayloadVersion())
                     .setDestinationId(request.getRequest().getDestinationId());

            return ApiResponse.success(amzSubscriptionModel);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

}
