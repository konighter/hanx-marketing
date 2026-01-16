package com.hzltd.module.erplus.service.amz;

import cn.hutool.core.util.URLUtil;
import com.google.common.collect.Lists;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.io.FileUtils;
import com.hzltd.framework.common.util.io.IoUtils;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.amz.AmazonFulfillmentService;
import com.hzltd.module.erplus.api.amz.AmzCancelInboundPlanRequest;
import com.hzltd.module.erplus.controller.admin.amz.vo.*;
import com.hzltd.module.erplus.convert.AmzFulfillConvert;
import com.hzltd.module.erplus.dal.dataobject.amz.AmzInboundPlanDO;
import com.hzltd.module.erplus.dal.mysql.amz.AmzInboundPlanMapper;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.service.ErplusFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.spapi.models.fulfillment.inbound.v2024_03_20.*;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 亚马逊入仓计划 Service 实现类
 *
 * @author 翰展科技
 */
@Slf4j
@Service
public class AmzFulfillmentServiceImpl implements AmzFulfillmentService {


    @Resource
    private AmzInboundPlanMapper inboundPlanMapper;
    /**
     *
     */
    @Resource
    private AmazonFulfillmentService amazonFulfillmentService;

    @Resource
    private ErplusFileService fileService;


    /**
     * wf5ccb8490-3129-44e1-95a8-e7505e1c2216
     * {
     *   "inboundPlanId" : "wf0785f998-19e0-4fa2-b32d-62c75f652cbe",
     *   "operationId" : "29804f2b-1619-4e47-b636-e39435321727"
     * }
     * @param request
     * @return
     */
    @Override
    public String createInboundPlan(AmzInboundPlanCreateRequest request) {
        CreateInboundPlanRequest createRequest = AmzFulfillConvert.INSTANCE.convert(request);
        ApiResponse<CreateInboundPlanResponse> response = amazonFulfillmentService.createInboundPlan(new ApiRequest<CreateInboundPlanRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setMarketId(request.getMarketId())
                .setRequest(createRequest));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        InboundPlan plan = getInboundPlan(request.getShopId(), response.getData().getInboundPlanId());

        inboundPlanMapper.insert(AmzFulfillConvert.INSTANCE.convert(plan).setShopId(request.getShopId()).setPlatformId(request.getPlatformId()).setItems(JsonUtils.toJsonString(createRequest.getItems())));

//        List<PlacementOption> placementOptions = this.generatePlacementOptions(new AmzGeneratePlacementOptionRequest()
//                .setShopId(request.getShopId())
//                .setInboundPlanId(plan.getInboundPlanId()));

//        log.info("placementOptions: {}", placementOptions);


        return plan.getInboundPlanId();
    }

    @Override
    public String cancelInboundPlan(AmzCancelInboundPlanRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getInboundPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        ApiResponse<InboundOperationStatus> response = amazonFulfillmentService.cancelInboundPlan(new ApiRequest<AmzCancelInboundPlanRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getData().getOperationId();
    }

    @Override
    public List<InboundPlan> listInboundPlans(AmzListInboundPlansRequest request) {

        List<InboundPlanSummary> inboundPlans = Lists.newArrayList();
        String cursor = request.getCursor();
        do {
            request.setCursor(cursor);
            ApiResponse<List<InboundPlanSummary>> response = amazonFulfillmentService.listInboundPlans(new ApiRequest<AmzListInboundPlansRequest>()
                    .setShopIdInt(request.getShopId())
                    .setCrossPlatform(CrossPlatformEnum.AMAZON)
                    .setMarketId(request.getMarketId())
                    .setRequest(request));
            if (!response.success()) {
                break;
            }
            inboundPlans.addAll(response.getData());
            cursor = response.getCursor();
        } while (StringUtils.isNotEmpty(cursor));


        return inboundPlans.stream().map(plan -> getInboundPlan(request.getShopId(), plan.getInboundPlanId()))
                .collect(Collectors.toList());

    }

    @Override
    public PageResult<AmzInboundPlanDO> listInboundPlansPage(AmzListInboundPlansRequest request) {
        return inboundPlanMapper.selectPage(request);
    }


    @Override
    public List<AmzPlacementOption> generatePlacementOptions(AmzPlacementOptionRequest request) {

        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());
        resetPlacementOptions(inboundPlan, true);

        ApiResponse<List<PlacementOption>> response = amazonFulfillmentService.generatePlacementOptions(new ApiRequest<String>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request.getPlanId()));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        List<AmzPlacementOption> amzPlacementOptions = response.getData().stream().map(option -> {
            return fillPlacementOptionDetail(request.getShopId(), request.getPlanId(), option);
        }).toList();

        inboundPlan.setPlacementOptions(JsonUtils.toJsonString(amzPlacementOptions));
        inboundPlanMapper.updateById(inboundPlan);

        return amzPlacementOptions;
    }

    private void resetPlacementOptions(AmzInboundPlanDO inboundPlanDO, boolean doUpdate) {
        inboundPlanDO.setPlacementId("").setPlacementOptions("").setPlacementDetail("");
        if (doUpdate) {
            inboundPlanMapper.updateById(inboundPlanDO);
        }
    }

    private void resetTransportOptions(AmzInboundPlanDO inboundPlanDO, boolean doUpdate) {
        inboundPlanDO.setTransportOptionId("").setTransportOptions("");
        if (doUpdate) {
            inboundPlanMapper.updateById(inboundPlanDO);
        }
    }

    private void resetPackingOptions(AmzInboundPlanDO inboundPlanDO, boolean doUpdate) {
        inboundPlanDO.setPackingOptionId("").setPackingOptions("").setPackingInfo("");
        if (doUpdate) {
            inboundPlanMapper.updateById(inboundPlanDO);
        }
    }

    private void resetLabels(AmzInboundPlanDO inboundPlanDO, boolean doUpdate) {
        inboundPlanDO.setLableInfo("");
        if (doUpdate) {
            inboundPlanMapper.updateById(inboundPlanDO);
        }
    }

    private void resetAllOptions(AmzInboundPlanDO inboundPlanDO) {
        inboundPlanDO.setTransactionId(UUID.randomUUID().toString());
        resetPlacementOptions(inboundPlanDO, false);
        resetTransportOptions(inboundPlanDO,false);
        resetPackingOptions(inboundPlanDO,false);
        resetLabels(inboundPlanDO, false);
        inboundPlanMapper.updateById(inboundPlanDO);
    }



    private AmzPlacementOption fillPlacementOptionDetail(Integer shopId, String inboundPlanId, PlacementOption option) {
        AmzPlacementOption amzPlacementOption = BeanUtils.toBean(option, AmzPlacementOption.class);

        amzPlacementOption.getShipmentIds().forEach(shipmentId -> {
            AmzShipment amzShipment = BeanUtils.toBean(getShipment(shopId, inboundPlanId, shipmentId), AmzShipment.class);
            amzShipment.setShipmentItems(getShipmentItems(shopId, inboundPlanId, shipmentId));
            amzPlacementOption.addShipmentDetail(shipmentId, amzShipment);

        });

        return amzPlacementOption;
    }


    private Shipment getShipment(Integer shopId, String inboundPlanId, String shipmentId) {

        ApiResponse<Shipment> response = amazonFulfillmentService.getShipment(new ApiRequest<AmzShipmentRequest>()
                .setShopIdInt(shopId)
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(new AmzShipmentRequest()
                        .setShopId(shopId)
                        .setInboundPlanId(inboundPlanId)
                        .setShipmentId(shipmentId)));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        return response.getData();
    }

    private List<Item> getShipmentItems(Integer shopId, String inboundPlanId, String shipmentId) {
        ApiResponse<List<Item>> response = amazonFulfillmentService.listShipmentItem(new ApiRequest<AmzShipmentRequest>()
                .setShopIdInt(shopId)
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(new AmzShipmentRequest()
                        .setShopId(shopId)
                        .setInboundPlanId(inboundPlanId)
                        .setShipmentId(shipmentId)));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        return response.getData();
    }


    @Override
    public List<AmzPlacementOption> listPlacementOptions(AmzPlacementOptionRequest request) {

        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());

        if (StringUtils.isNotEmpty(inboundPlan.getPlacementOptions())) {
            return JsonUtils.parseArray(inboundPlan.getPlacementOptions(), AmzPlacementOption.class);
        }
        request.setShopId(inboundPlan.getShopId());

        ApiResponse<List<PlacementOption>> response = amazonFulfillmentService.listPlacementOptions(new ApiRequest<String>().setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request.getPlanId()));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        List<AmzPlacementOption> amzPlacementOptions = response.getData().stream().map(option -> {
            return fillPlacementOptionDetail(request.getShopId(), request.getPlanId(), option);
        }).toList();


        inboundPlan.setPlacementOptions(JsonUtils.toJsonString(amzPlacementOptions));
        inboundPlanMapper.updateById(inboundPlan);
        return amzPlacementOptions;
    }


    @Override
    public String confirmPlacementOptions(AmzConfirmPlacementOptionsRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());
        if (StringUtils.isNotEmpty(inboundPlan.getPlacementId()) && inboundPlan.getPlacementId().equals(request.getPlacementOptionId())) {
            log.warn("placementOptionId {} already confirmed", request.getPlacementOptionId());
            return request.getPlacementOptionId();
        }

        ApiResponse<?> response = amazonFulfillmentService.confirmPlacementOption(new ApiRequest<AmzConfirmPlacementOptionsRequest>()
                .setShopIdInt(inboundPlan.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        inboundPlan.setPlacementId(request.getPlacementOptionId());
        inboundPlanMapper.updateById(inboundPlan);

        return request.getPlacementOptionId();
    }


    @Override
    public String setPlacementOption(AmzSetPlacementOptionRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        // confirm placement option
        confirmPlacementOptions(new AmzConfirmPlacementOptionsRequest()
                .setShopId(inboundPlan.getShopId())
                .setPlanId(inboundPlan.getPlanId())
                .setPlacementOptionId(request.getPlacementOptionId()));

        // confirm delivery window option
        request.getShipments().forEach(shipment -> {
            this.confirmDeliveryWindowOption(new AmzConfirmDeliveryWindowRequest()
                    .setShopId(inboundPlan.getShopId())
                    .setPlanId(inboundPlan.getPlanId())
                    .setShipmentId(shipment.getShipmentId())
                    .setDeliveryWindowOptionId(shipment.getDeliveryWindowOptionId()));
        });

        // confirm transportation option
        confirmTransportOption(new AmzConfirmTransportOptionRequest()
                .setShopId(inboundPlan.getShopId())
                .setPlanId(inboundPlan.getPlanId())
                .setShipments(request.getShipments()));



        inboundPlan.setPlacementDetail(JsonUtils.toJsonString(request));
        inboundPlanMapper.updateById(inboundPlan);

        return "";
    }

    private String confirmDeliveryWindowOption(AmzConfirmDeliveryWindowRequest request) {
        ApiResponse<?> response = amazonFulfillmentService.confirmDeliveryWindowOption(new ApiRequest<AmzConfirmDeliveryWindowRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        return request.getDeliveryWindowOptionId();
    }


    @Override
    public AmzPlacementDetailResp getPlacementOption(AmzPlacementOptionRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());
        if (StringUtils.isEmpty(inboundPlan.getPlacementDetail())) {
            return null;
        }

        AmzSetPlacementOptionRequest amzSetPlacementOptionRequest = JsonUtils.parseObject(inboundPlan.getPlacementDetail(), AmzSetPlacementOptionRequest.class);
        List<AmzTransportationOption> amzTransportationOptions = JsonUtils.parseArray(inboundPlan.getTransportOptions(), AmzTransportationOption.class);

        AmzPlacementDetailResp amzPlacementDetailResp = new AmzPlacementDetailResp();
        amzPlacementDetailResp.setShipmentDate(amzSetPlacementOptionRequest.getShipmentDate());
        amzPlacementDetailResp.setShipments(amzSetPlacementOptionRequest.getShipments());
        amzPlacementDetailResp.setPlacementOption(amzSetPlacementOptionRequest.getPlacementOption());
        amzPlacementDetailResp.setAmzTransportationOptions(amzTransportationOptions);


        return amzPlacementDetailResp;
    }

    @Override
    public List<AmzTransportationOption> generateTransportOptions(AmzTransportOptionGenerateRequest request) {
        // generate transportation options
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());


        Map<String, List<TransportationOption>> transportationOptions = doGenerateTransportationOptions(request);
        Map<String, List<DeliveryWindowOption>> deliveryWindows = doGenerateDeliveryWindows(request);
        List<AmzTransportationOption> amzTransportationOptions = new ArrayList<>();

        transportationOptions.forEach((key, value) -> {
            AmzTransportationOption amzTransportationOption = new AmzTransportationOption();
            amzTransportationOption.setShipmentId(key);
            amzTransportationOption.setTransportationOptions(value);
            amzTransportationOption.setDeliveryWindows(deliveryWindows.get(key));
            amzTransportationOptions.add(amzTransportationOption);
        });

        inboundPlan.setTransportOptions(JsonUtils.toJsonString(amzTransportationOptions));
        inboundPlanMapper.updateById(inboundPlan);

        return amzTransportationOptions;
    }

    private Map<String, List<TransportationOption>> doGenerateTransportationOptions(AmzTransportOptionGenerateRequest request) {
        // generate transportation options
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());


        ApiResponse<?> response = amazonFulfillmentService.generateTransportationOptions(new ApiRequest<AmzTransportOptionGenerateRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));

        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }
        List<TransportationOption> transportationOptions = new ArrayList<>();
        boolean hasNext = false;
        AmzListTransportOptionsRequest amzListTransportOptionsRequest = new AmzListTransportOptionsRequest()
                .setPlanId(request.getPlanId())
                .setPlacementOperationId(request.getPlacementOptionId());
        do{
            ApiResponse<List<TransportationOption>> listApiResponse = amazonFulfillmentService.listTransportationOptions(new ApiRequest<AmzListTransportOptionsRequest>()
                    .setShopIdInt(request.getShopId())
                    .setCrossPlatform(CrossPlatformEnum.AMAZON)
                    .setRequest(amzListTransportOptionsRequest));
            if (!listApiResponse.success()) {
                throw new RuntimeException(listApiResponse.getMsg());
            }
            transportationOptions.addAll(listApiResponse.getData());
            hasNext = listApiResponse.hasNext();
            amzListTransportOptionsRequest.setCursor(listApiResponse.getCursor());
        } while(hasNext);

        Map<String, List<TransportationOption>> transportationOptionsMap = new HashMap<>();
        transportationOptions.forEach(item -> {
            if (!transportationOptionsMap.containsKey(item.getShipmentId())) {
                transportationOptionsMap.put(item.getShipmentId(), new ArrayList<>());
            }
            transportationOptionsMap.get(item.getShipmentId()).add(item);
        });


        return transportationOptionsMap;
    }

    private Map<String, List<DeliveryWindowOption>> doGenerateDeliveryWindows(AmzTransportOptionGenerateRequest request) {
        // generate transport windows
        Map<String, List<DeliveryWindowOption>> deliveryWindowOptionsMap = new HashMap<>();

        request.getShipmentIds().forEach(shipmentId -> {
            AmzTransportationBaseRequest amzTransportationBaseRequest = new AmzTransportationBaseRequest()
                    .setPlanId(request.getPlanId())
                    .setShipmentId(shipmentId);
            ApiResponse<?> response = amazonFulfillmentService.generateDeliveryWindowOptions(new ApiRequest<AmzTransportationBaseRequest>()
                    .setShopIdInt(request.getShopId())
                    .setCrossPlatform(CrossPlatformEnum.AMAZON)
                    .setRequest(amzTransportationBaseRequest));

            if (!response.success()) {
                throw new RuntimeException(response.getMsg());
            }

            ApiResponse<List<DeliveryWindowOption>> listApiResponse = amazonFulfillmentService.listDeliveryWindowOptions(new ApiRequest<AmzTransportationBaseRequest>()
                    .setShopIdInt(request.getShopId())
                    .setCrossPlatform(CrossPlatformEnum.AMAZON)
                    .setRequest(amzTransportationBaseRequest));

            if (!listApiResponse.success()) {
                throw new RuntimeException(listApiResponse.getMsg());
            }
            deliveryWindowOptionsMap.put(shipmentId, listApiResponse.getData());
        });

        return deliveryWindowOptionsMap;

    }


    @Override
    public void confirmTransportOption(AmzConfirmTransportOptionRequest request) {

        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        if (StringUtils.isNotEmpty(inboundPlan.getTransportDetail())) {
            AmzConfirmTransportOptionRequest confirmedTransport = JsonUtils.parseObject(inboundPlan.getTransportOptionId(), AmzConfirmTransportOptionRequest.class);
            Map<String, String> shipmentTransportOptionMap = confirmedTransport.getShipments().stream().collect(Collectors.toMap(item -> item.getShipmentId(), item -> item.getTransportationOptionId()));
            List<AmzShipment> shipmentsToConfirm = request.getShipments().stream().filter(item -> !shipmentTransportOptionMap.containsKey(item.getShipmentId()) || !shipmentTransportOptionMap.get(item.getShipmentId()).equals(item.getTransportationOptionId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(shipmentsToConfirm)) {
                log.warn("all shipments have been confirmed");
                return;
            }

            request.setShipments(shipmentsToConfirm);
        }

        ApiResponse<?> response = amazonFulfillmentService.confirmTransportationOption(new ApiRequest<AmzConfirmTransportOptionRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        inboundPlan.setTransportDetail(JsonUtils.toJsonString(request));
        inboundPlanMapper.updateById(inboundPlan);

    }

    @Override
    public List<AmzTransportationOption> listTransportOptions(AmzListTransportOptionsRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        return JsonUtils.parseArray(inboundPlan.getTransportOptions(), AmzTransportationOption.class);
    }

    @Override
    public List<AmzPackingOption> generatePackingOptions(AmzPackingOptionRequest request) {

        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());


        ApiResponse<List<PackingOption>> response = amazonFulfillmentService.generatePackingOptions(new ApiRequest<String>()
                .setShopIdInt(inboundPlan.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request.getPlanId()));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }


        List<AmzPackingOption> amzPackingOptions = response.getData().stream().map(packingOption -> {
            AmzPackingOption amzPackingOption = BeanUtils.toBean(packingOption, AmzPackingOption.class);
            packingOption.getPackingGroups().forEach(packingGroup -> {
                List<Item> items = listPackingGroupItems(request.getShopId(), request.getPlanId(), packingGroup);
                amzPackingOption.putGroupItems(packingGroup, BeanUtils.toBean(items, AmzBoxItem.class));
            });
            return amzPackingOption;
        }).toList();

        inboundPlan.setPackingOptions(JsonUtils.toJsonString(amzPackingOptions));
        inboundPlanMapper.updateById(inboundPlan);

        return amzPackingOptions;
    }

    @Override
    public String confirmPackingOption(AmzConfirmPackingOptionRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        ApiResponse<ConfirmPackingOptionResponse> response = amazonFulfillmentService.confirmPackingOption(new ApiRequest<AmzConfirmPackingOptionRequest>()
                .setShopIdInt(inboundPlan.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        inboundPlan.setPackingOptionId(request.getPackingOptionId());
        inboundPlanMapper.updateById(inboundPlan);

        return "";
    }

    @Override
    public String setPackingInfo(AmzSetPackingInfoRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        request.setShopId(inboundPlan.getShopId());

        ApiResponse<InboundOperationStatus> response = amazonFulfillmentService.setPackingInfo(new ApiRequest<AmzSetPackingInfoRequest>()
                .setShopIdInt(inboundPlan.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }

        inboundPlan.setPackingInfo(JsonUtils.toJsonString(request.getOption()));
        inboundPlanMapper.updateById(inboundPlan);

        return response.getData().toString();
    }

    @Override
    public List<AmzPackingOption> listPackingOptions(AmzPackingOptionRequest request) {

        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        if (StringUtils.isNotEmpty(inboundPlan.getPackingOptions())) {
            return JsonUtils.parseArray(inboundPlan.getPackingOptions(), AmzPackingOption.class);
        } else {
            return List.of();
        }
    }

    @Override
    public AmzPackingOption getPackingInfo(AmzSetPackingInfoRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        if (StringUtils.isNotEmpty(inboundPlan.getPackingInfo())) {
            return JsonUtils.parseObject(inboundPlan.getPackingInfo(), AmzPackingOption.class);
        }

        return null;
    }

    @Override
    public List<Box> getShipmentBox(AmzListShipmentBoxRequest request) {
        ApiResponse<List<Box>> response = amazonFulfillmentService.getInboundPlanBox(new ApiRequest<AmzListShipmentBoxRequest>()
                .setShopIdInt(request.getShopId())
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(request));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getData();
    }

    @Override
    public List<AmzShipment> getShipmentLabel(AmzLabelsRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }
        if (StringUtils.isNotEmpty(inboundPlan.getShipmentDetail())) {
            return JsonUtils.parseArray(inboundPlan.getShipmentDetail(), AmzShipment.class);
        } else {
            return this.generateShipmentLabel(request);
        }

    }


    /**
     * `ABANDONED`, `CANCELLED`, `CHECKED_IN`, `CLOSED`, `DELETED`, `DELIVERED`, `IN_TRANSIT`, `MIXED`, `READY_TO_SHIP`, `RECEIVING`, `SHIPPED`, `UNCONFIRMED`, `WORKING`
     */
    private static final List<String> SHIPMENT_STATUS_Labeled = List.of("DELIVERED", "READY_TO_SHIP", "RECEIVING", "SHIPPED", "WORKING");
    public List<AmzShipment> generateShipmentLabel(AmzLabelsRequest request) {
        AmzInboundPlanDO inboundPlan = getAmzInboundPlanLocal(request.getShopId(), request.getPlanId());
        if (inboundPlan == null) {
            throw new RuntimeException("inboundPlan not found");
        }

        AmzPlacementDetailResp placementOption = getPlacementOption(new AmzPlacementOptionRequest().setShopId(inboundPlan.getShopId()).setPlanId(inboundPlan.getPlanId()));
        for (AmzShipment shipment : placementOption.getShipments()) {
            Shipment shipmentDetail = this.getShipment(inboundPlan.getShopId(), inboundPlan.getPlanId(), shipment.getShipmentId());
//            if (shipmentDetail == null || !SHIPMENT_STATUS_Labeled.contains(shipment.getStatus())) {
//                log.warn("generateShipmentLabel, invalid shipment: {} ", shipment);
//                continue;
//            }

            shipment.setShipmentConfirmationId(shipmentDetail.getShipmentConfirmationId());

            List<Box> boxes = this.getShipmentBox((AmzListShipmentBoxRequest) new AmzListShipmentBoxRequest().setShipmentId(shipmentDetail.getShipmentId()).setPlanId(inboundPlan.getPlanId()).setShopId(inboundPlan.getShopId()));
            if (CollectionUtils.isEmpty(boxes)) {
                log.warn("generateShipmentLabel, invalid boxes: {} ", boxes);
                continue;
            }

            for (Box box : boxes) {
                AmzBox amzBox = BeanUtils.toBean(box, AmzBox.class);
                shipment.addBox(amzBox);
                AmzLabelsRequest boxLabelRequest = new AmzLabelsRequest()
                        .setLabelType("UNIQUE")
                        .setShipmentId(shipment.getShipmentConfirmationId())
                        .setBoxIds(List.of(box.getBoxId()))
                        .setPageType("PackageLabel_Thermal_NonPCP");

                ApiResponse<String> labelResponse = amazonFulfillmentService.getShipmentLabels(new ApiRequest<AmzLabelsRequest>()
                        .setShopIdInt(inboundPlan.getShopId())
                        .setCrossPlatform(CrossPlatformEnum.AMAZON)
                        .setRequest(boxLabelRequest));
                if (!labelResponse.success()) {
                    log.warn("generateShipmentLabel, getShipmentLabels failed, shipmentId: {}, boxId: {}, response: {}", shipment.getShipmentConfirmationId(), box.getBoxId(), labelResponse);
                    continue;
                }

                String fileUrl = fileService.createFile(labelResponse.getData(), box.getBoxId() + ".pdf", inboundPlan.getPlanId());
                amzBox.setLabelUrl(fileUrl);
            }
        }

        inboundPlan.setShipmentDetail(JsonUtils.toJsonString(placementOption.getShipments()));
        inboundPlanMapper.updateById(inboundPlan);
        return placementOption.getShipments();
    }


    private List<Item> listPackingGroupItems(Integer shopId, String inboundPlanId, String packingGroupId) {
        ApiResponse<List<Item>> response = amazonFulfillmentService.listPackingGroups(new ApiRequest<AmzListPackingGroupItemRequest>()
                .setShopIdInt(shopId)
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(new AmzListPackingGroupItemRequest()
                        .setInboundPlanId(inboundPlanId)
                        .setPackingGroupId(packingGroupId)));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getData();
    }

    private InboundPlan getInboundPlan(Integer shopId, String inboundPlanId) {
        ApiResponse<InboundPlan> response = amazonFulfillmentService.getInboundPlan(new ApiRequest<String>()
                .setShopIdInt(shopId)
                .setCrossPlatform(CrossPlatformEnum.AMAZON)
                .setRequest(inboundPlanId));
        if (!response.success()) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getData();
    }

    private AmzInboundPlanDO getAmzInboundPlanLocal(Integer shopId, String inboundPlanId) {
        return inboundPlanMapper.selectOne(new LambdaQueryWrapperX<AmzInboundPlanDO>()
                .eqIfPresent(AmzInboundPlanDO::getShopId, shopId)
                .eqIfPresent(AmzInboundPlanDO::getPlanId, inboundPlanId));
    }

    private InboundOperationStatus getOperationStatus(Integer shopId, String operationId) {
        ApiResponse<InboundOperationStatus> response = amazonFulfillmentService.getOperationStatus(new ApiRequest<String>().setShopIdInt(shopId).setRequest(operationId));
        return response.getData();
    }
}
