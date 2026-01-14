package com.hzltd.module.erplus.controller.admin.amz;


import com.hzltd.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 亚马逊Fulfillment")
@RestController
@RequestMapping("/erplus/amz-fulfill")
@Validated
public class AmzFulfillController {

    @PostMapping("/create-inbound-plan")
    public CommonResult<?> createInboundPlan() {
        return CommonResult.success(null);
    }


    @PostMapping("/set-packing-info")
    public CommonResult<?> setPackingInfo() {
        return CommonResult.success(null);
    }

    @PostMapping("/generate-placement-options")
    public CommonResult<?> generatePlacementOptions() {
        return CommonResult.success(null);
    }

    @PostMapping("/confirm-placement-option")
    public CommonResult<?> comfirmPlacementOption() {
        return CommonResult.success(null);
    }

    @PostMapping("/create-shipment")
    public CommonResult<?> createShipment() {
        return CommonResult.success(null);
    }

    @PostMapping("/confirm-shipment")
    public CommonResult<?> confirmShipment() {
        return CommonResult.success(null);
    }

    // generateTransportationOptions

    @PostMapping("/generate-transportation-options")
    public CommonResult<?> generateTransportationOptions() {
        return CommonResult.success(null);
    }

    @PostMapping("/confirm-transportation-option")
    public CommonResult<?> confirmTransportationOption() {
        return CommonResult.success(null);
    }

    // getLabels

    @PostMapping("/get-labels")
    public CommonResult<?> getLabels() {
        return CommonResult.success(null);
    }


}
