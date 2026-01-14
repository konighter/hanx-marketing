package com.hzltd.module.erplus.api.amz;

import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.erplus.api.amz.model.AmzDestinationModel;
import com.hzltd.module.erplus.api.amz.model.AmzSubscriptionModel;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.notifications.v1.NotificationsApi;
import software.amazon.spapi.models.notifications.v1.*;

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
     * 创建AMZ事件通知订阅
     * @param request
     * @return
     */
    public ApiResponse<?> createSubscription(ApiRequest<AmzSubscriptionModel> request) {

        NotificationsApi notificationsApi = getNotificationApi(request);

        try {
            CreateSubscriptionResponse response = notificationsApi.createSubscription(new CreateSubscriptionRequest()
                    .payloadVersion(request.getRequest().getPayloadVersion())
                    .destinationId(request.getRequest().getDestinationId())
                    , request.getRequest().getNotificationType());

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
