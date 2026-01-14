package com.hzltd.module.erplus.api.adptor.amz;


import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.fulfillment.inbound.v2024_03_20.FbaInboundApi;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.CreateInboundPlanRequest;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.CreateInboundPlanResponse;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.InboundOperationStatus;

@Service
public class AmazonFulfillmentService extends AbsAmzPlatformApiService {

    ApiResponse<?> createInboundPlan(ApiRequest<CreateInboundPlanRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);

        try {
            CreateInboundPlanResponse response = fbaInboundApi.createInboundPlan(request.getRequest());
            fbaInboundApi.getInboundOperationStatus(response.getOperationId());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }


        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> setPackingInfo(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> generatePlacementOptions(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> comfirmPlacementOption(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> confirmShipment(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> generateTransportationOptions(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> confirmTransportationOption(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<InboundOperationStatus> getOperationStatus(ApiRequest<String> request) {

        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            InboundOperationStatus status = fbaInboundApi.getInboundOperationStatus(request.getRequest());
            return ApiResponse.success(status);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }



    ApiResponse<?> createShipment(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }

    ApiResponse<?> getShipmentLabels(ApiRequest<?> request) {
        return ApiResponse.success(null);
    }


}
