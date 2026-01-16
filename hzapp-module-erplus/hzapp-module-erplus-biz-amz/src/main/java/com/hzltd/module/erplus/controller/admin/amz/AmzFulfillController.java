package com.hzltd.module.erplus.controller.admin.amz;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.amz.vo.*;
import com.hzltd.module.erplus.dal.dataobject.amz.AmzInboundPlanDO;
import com.hzltd.module.erplus.service.amz.AmzFulfillmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - 亚马逊Fulfillment")
@RestController
@RequestMapping("/erplus/amz-fulfill")
@Validated
public class AmzFulfillController {

    @Resource
    private AmzFulfillmentService amzFulfillmentService;

    @PostMapping("/create-inbound-plan")
    public CommonResult<?> createInboundPlan(@RequestBody AmzInboundPlanCreateRequest request) {
        return CommonResult.success(amzFulfillmentService.createInboundPlan(request));
    }

    @GetMapping("/list-inbound-plans")
    public CommonResult<?> listInboundPlans(AmzListInboundPlansRequest request) {
        return CommonResult.success(amzFulfillmentService.listInboundPlans(request));
    }



    @PostMapping("/generate-placement-options")
    public CommonResult<?> generatePlacementOptions(@RequestBody AmzPlacementOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.generatePlacementOptions(request));
    }

    @PostMapping("/list-placement-options")
    public CommonResult<?> listPlacementOptions(@RequestBody AmzPlacementOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.listPlacementOptions(request));
    }

    @PostMapping("/confirm-placement-option")
    public CommonResult<?> confirmPlacementOption(@RequestBody AmzConfirmPlacementOptionsRequest request) {
        return CommonResult.success(amzFulfillmentService.confirmPlacementOptions(request));
    }

    @PostMapping("/set-placement-info")
    public CommonResult<?> setPlacementOption(@RequestBody AmzSetPlacementOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.setPlacementOption(request));
    }

    @PostMapping("/get-placement-option")
    public CommonResult<?> getPlacementOption(@RequestBody AmzPlacementOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.getPlacementOption(request));
    }


    // generateTransportationOptions

    @PostMapping("/generate-transportation-options")
    public CommonResult<?> generateTransportationOptions(@RequestBody AmzTransportOptionGenerateRequest request) {
        return CommonResult.success(amzFulfillmentService.generateTransportOptions(request));
    }


    @PostMapping("/list-transportation-options")
    public CommonResult<?> listTransportationOptions(@RequestBody AmzListTransportOptionsRequest request) {
        return CommonResult.success(amzFulfillmentService.listTransportOptions(request));
    }

    @PostMapping("/confirm-transportation-option")
    public CommonResult<?> confirmTransportationOption(@RequestBody AmzConfirmTransportOptionRequest request) {
        amzFulfillmentService.confirmTransportOption(request);
        return CommonResult.success(true);
    }



    @PostMapping("/generate-packing-options")
    public CommonResult<?> generatePackingOptions(@RequestBody AmzPackingOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.generatePackingOptions(request));
    }

    @PostMapping("/list-packing-options")
    public CommonResult<?> listPackingOptions(@RequestBody AmzPackingOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.listPackingOptions(request));
    }

    @PostMapping("/confirm-packing-option")
    public CommonResult<?> confirmPackingOption(@RequestBody AmzConfirmPackingOptionRequest request) {
        return CommonResult.success(amzFulfillmentService.confirmPackingOption(request));
    }


    @PostMapping("/set-packing-info")
    public CommonResult<?> setPackingInfo(@RequestBody AmzSetPackingInfoRequest request) {
        return CommonResult.success(amzFulfillmentService.setPackingInfo(request));
    }

    @PostMapping("/get-packing-info")
    public CommonResult<?> getPackingInfo(@RequestBody AmzSetPackingInfoRequest request) {
        return CommonResult.success(amzFulfillmentService.getPackingInfo(request));
    }


    @PostMapping("/create-shipment")
    public CommonResult<?> createShipment() {
        return CommonResult.success(null);
    }

    @PostMapping("/confirm-shipment")
    public CommonResult<?> confirmShipment() {
        return CommonResult.success(null);
    }


    // getLabels

    @PostMapping("/get-labels")
    public CommonResult<?> getLabels(@RequestBody AmzLabelsRequest request) {
        return CommonResult.success(amzFulfillmentService.getShipmentLabel(request));
    }

    /**
     * 分页列出所有入仓计划
     * @return 入仓计划列表
     */
    @GetMapping("/list-inbound-plans-page")
    public CommonResult<PageResult<AmzInboundPlanDO>> getInboundPlans(AmzListInboundPlansRequest request) {
        return CommonResult.success(amzFulfillmentService.listInboundPlansPage(request));
    }





}
