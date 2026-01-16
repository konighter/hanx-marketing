package com.hzltd.module.erplus.api.amz;


import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.erplus.controller.admin.amz.vo.*;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.fulfillment.inbound.v2024_03_20.FbaInboundApi;
import software.amazon.spapi.models.fulfillment.inbound.v0.GetLabelsResponse;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AmazonFulfillmentService extends AbsAmzPlatformApiService {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public ApiResponse<CreateInboundPlanResponse> createInboundPlan(ApiRequest<CreateInboundPlanRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);

        try {
            CreateInboundPlanResponse response = fbaInboundApi.createInboundPlan(request.getRequest());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                log.error("创建入仓计划失败, 问题: {}", statusResponse.getData().getOperationProblems());
                throw new RuntimeException("创建入仓计划失败");
            }
            return ApiResponse.success(response);
        } catch (ApiException | LWAException e) {
            log.error("创建入仓计划失败", e);
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<InboundOperationStatus> cancelInboundPlan(ApiRequest<AmzCancelInboundPlanRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            CancelInboundPlanResponse response = fbaInboundApi.cancelInboundPlan(request.getRequest().getInboundPlanId());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("取消入仓计划失败");
            }
            return ApiResponse.success(statusResponse.getData());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }



    public ApiResponse<List<InboundPlanSummary>> listInboundPlans(ApiRequest<AmzListInboundPlansRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListInboundPlansResponse response = fbaInboundApi.listInboundPlans(10, request.getRequest().getCursor(), null, "CREATION_TIME", "DESC");
            return ApiResponse.success(response.getInboundPlans()).setCursor(response.getPagination() != null ? response.getPagination().getNextToken() : null);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<InboundPlan> getInboundPlan(ApiRequest<String> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            InboundPlan response = fbaInboundApi.getInboundPlan(request.getRequest());
            return ApiResponse.success(response);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<ConfirmPackingOptionResponse> confirmPackingOption(ApiRequest<AmzConfirmPackingOptionRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ConfirmPackingOptionResponse response = fbaInboundApi.confirmPackingOption(request.getRequest().getPlanId(), request.getRequest().getPackingOptionId());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("确认打包配置失败");
            }
            return ApiResponse.success(response);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<InboundOperationStatus> setPackingInfo(ApiRequest<AmzSetPackingInfoRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            SetPackingInformationResponse response = fbaInboundApi.setPackingInformation(convert(request.getRequest()), request.getRequest().getPlanId());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()), 60);
            return ApiResponse.success(statusResponse.getData());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }


    public ApiResponse<List<PackingOption>> generatePackingOptions(ApiRequest<String> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            GeneratePackingOptionsResponse response = fbaInboundApi.generatePackingOptions(request.getRequest());

            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("生成打包配置失败");
            }

            ListPackingOptionsResponse listResponse = fbaInboundApi.listPackingOptions(request.getRequest(), 10, null);

            return ApiResponse.success(listResponse.getPackingOptions());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<List<Item>> listPackingGroups(ApiRequest<AmzListPackingGroupItemRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListPackingGroupItemsResponse response = fbaInboundApi.listPackingGroupItems(request.getRequest().getInboundPlanId(), request.getRequest().getPackingGroupId(), 10, null);
            return ApiResponse.success(response.getItems());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<List<PlacementOption>> generatePlacementOptions(ApiRequest<String> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            GeneratePlacementOptionsResponse response = fbaInboundApi.generatePlacementOptions(new GeneratePlacementOptionsRequest(), request.getRequest());

            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("生成入仓配置失败");
            }

            ListPlacementOptionsResponse listResponse = fbaInboundApi.listPlacementOptions(request.getRequest(), 10, null);

            return ApiResponse.success(listResponse.getPlacementOptions());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

    public ApiResponse<List<PlacementOption>> listPlacementOptions(ApiRequest<String> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListPlacementOptionsResponse response = fbaInboundApi.listPlacementOptions(request.getRequest(), 10, null);
            return ApiResponse.success(response.getPlacementOptions());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<?> confirmPlacementOption(ApiRequest<AmzConfirmPlacementOptionsRequest> request) {

        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ConfirmPlacementOptionResponse response = fbaInboundApi.confirmPlacementOption(request.getRequest().getPlanId(), request.getRequest().getPlacementOptionId());

            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("确认入仓配置失败");
            }
            return ApiResponse.success(statusResponse.getData().getOperationStatus());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<?> generateTransportationOptions(ApiRequest<AmzTransportOptionGenerateRequest> request) {

        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            GenerateTransportationOptionsResponse response = fbaInboundApi.generateTransportationOptions(convert(request.getRequest()), request.getRequest().getPlanId());

            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()), 120);
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("生成运输配置失败");
            }
            return ApiResponse.success(statusResponse.getData().getOperationStatus());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<List<TransportationOption>> listTransportationOptions(ApiRequest<AmzListTransportOptionsRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListTransportationOptionsResponse response = fbaInboundApi.listTransportationOptions(request.getRequest().getPlanId(), 20, request.getRequest().getCursor(), request.getRequest().getPlacementOperationId(), request.getRequest().getShipmentId());
            return ApiResponse.success(response.getTransportationOptions()).setCursor(response.getPagination() != null ? response.getPagination().getNextToken() : null);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<?> generateDeliveryWindowOptions(ApiRequest<AmzTransportationBaseRequest> request) {

        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            GenerateDeliveryWindowOptionsResponse response = fbaInboundApi.generateDeliveryWindowOptions(request.getRequest().getPlanId(), request.getRequest().getShipmentId());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("生成入仓时间窗口失败");
            }

            return ApiResponse.success(response.getOperationId());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

    public ApiResponse<List<DeliveryWindowOption>> listDeliveryWindowOptions(ApiRequest<AmzTransportationBaseRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListDeliveryWindowOptionsResponse response = fbaInboundApi.listDeliveryWindowOptions(request.getRequest().getPlanId(), request.getRequest().getShipmentId(), 20, request.getRequest().getCursor());
            return ApiResponse.success(response.getDeliveryWindowOptions()).setCursor(response.getPagination() != null ? response.getPagination().getNextToken() : null);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }


    public ApiResponse<?> confirmTransportationOption(ApiRequest<AmzConfirmTransportOptionRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ConfirmTransportationOptionsResponse response = fbaInboundApi.confirmTransportationOptions(convert(request.getRequest()), request.getRequest().getPlanId());

            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("确认运输配置失败");
            }
            return ApiResponse.success(statusResponse.getData().getOperationStatus());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<?> confirmDeliveryWindowOption(ApiRequest<AmzConfirmDeliveryWindowRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ConfirmDeliveryWindowOptionsResponse response = fbaInboundApi.confirmDeliveryWindowOptions(request.getRequest().getPlanId(), request.getRequest().getShipmentId(), request.getRequest().getDeliveryWindowOptionId());
            ApiResponse<InboundOperationStatus> statusResponse = this.getOperationStatus(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(response.getOperationId()));
            if (!OperationStatus.SUCCESS.equals(statusResponse.getData().getOperationStatus())) {
                throw new RuntimeException("确认入仓时间窗口失败");
            }
            return ApiResponse.success(statusResponse.getData().getOperationStatus());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }


    public ApiResponse<?> confirmShipment(ApiRequest<?> request) {
        return ApiResponse.success(request.getRequest());
    }




    public ApiResponse<InboundOperationStatus> getOperationStatus(ApiRequest<String> request) {
        return this.getOperationStatus(request, 15);
    }


    public ApiResponse<InboundOperationStatus> getOperationStatus(ApiRequest<String> request, Integer second) {


        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        Future<InboundOperationStatus> statusFuture = executorService.submit(() -> {
            try {
                while (true) {
                    log.info("get operation status, operationId: {}", request.getRequest());
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    InboundOperationStatus status = fbaInboundApi.getInboundOperationStatus(request.getRequest());
                    if (OperationStatus.SUCCESS.equals(status.getOperationStatus())) {
                        return status;
                    }
                    TimeUnit.SECONDS.sleep(3);
                }

            } catch (ApiException | LWAException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            InboundOperationStatus status = statusFuture.get(second, TimeUnit.SECONDS);
            return ApiResponse.success(status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ApiResponse<Shipment> getShipment(ApiRequest<AmzShipmentRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            Shipment response = fbaInboundApi.getShipment(request.getRequest().getInboundPlanId(), request.getRequest().getShipmentId());
            return ApiResponse.success(response);
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }

    }

    public ApiResponse<List<Item>> listShipmentItem(ApiRequest<AmzShipmentRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListShipmentItemsResponse response = fbaInboundApi.listShipmentItems(request.getRequest().getInboundPlanId(), request.getRequest().getShipmentId(), 20, null);
            return ApiResponse.success(response.getItems());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<String> getShipmentLabels(ApiRequest<AmzLabelsRequest> request) {
        software.amazon.spapi.api.fulfillment.inbound.v0.FbaInboundApi fbaInboundApi = this.getFbaInboundApiV0(request);
        try {
            Integer boxNum = request.getRequest().getBoxIds().size();
            GetLabelsResponse response = fbaInboundApi.getLabels(request.getRequest().getShipmentId(), request.getRequest().getPageType(), request.getRequest().getLabelType(), boxNum, request.getRequest().getBoxIds(), null, boxNum , null);
            return ApiResponse.success(response.getPayload().getDownloadURL());
        } catch (ApiException | LWAException e) {
            log.error("getShipmentLabels, error: {}", ((ApiException) e).getResponseBody());
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<List<Box>> getShipmentBox(ApiRequest<AmzListShipmentBoxRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListShipmentBoxesResponse response = fbaInboundApi.listShipmentBoxes(request.getRequest().getPlanId(), request.getRequest().getShipmentId(),  request.getRequest().getPageSize(), request.getRequest().getCursor());
            return ApiResponse.success(response.getBoxes());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<List<Box>> getInboundPlanBox(ApiRequest<AmzListShipmentBoxRequest> request) {
        FbaInboundApi fbaInboundApi = this.getFbaInboundApi(request);
        try {
            ListInboundPlanBoxesResponse response = fbaInboundApi.listInboundPlanBoxes(request.getRequest().getPlanId(),  request.getRequest().getPageSize(), request.getRequest().getCursor());
            return ApiResponse.success(response.getBoxes());
        } catch (ApiException | LWAException e) {
            throw new RuntimeException(e);
        }
    }

}
