package com.hzltd.module.erplus.controller.admin.stock;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.ShipmentAuditReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.shipment.StockShipmentPlanReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ShipmentStatusUpdateReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.StockShipmentPlanPageReqVO;
import com.hzltd.module.erplus.service.stock.ErpAddressService;
import com.hzltd.module.erplus.service.stock.ErplusShipmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - ERP 货件")
@RestController
@RequestMapping("/erplus/inventory")
@Validated
public class StockShipmentController {

    @Resource
    private ErpAddressService addressService;

    @Resource
    private ErplusShipmentService shipmentService;
    /**
     * 保存货件计划
     *
     * @param reqVO 货件计划
     * @return 货件计划编号
     */
    @PostMapping("/shipment/save")
    public CommonResult<?> saveShipmentPlan(@RequestBody StockShipmentPlanReqVO reqVO) {
        return success(shipmentService.saveShipmentPlan(reqVO));
    }


    /**
     * 保存货件计划
     *
     * @param reqVO 货件计划
     * @return 货件计划编号
     */
    @PostMapping("/shipment/submit")
    public CommonResult<?> submitShipmentPlan(@RequestBody StockShipmentPlanReqVO reqVO) {
        return success(shipmentService.submitShipmentPlan(reqVO));
    }

    @PostMapping("/shipment/status")
    public CommonResult<?> updateShipmentStatus(@RequestBody ShipmentStatusUpdateReqVO reqVO) {
        shipmentService.updateShipmentStatus(reqVO.getShipmentId(), reqVO.getStatus());
        return success(null);
    }



     /**
     * 审核货件计划
     *
     * @param reqVO 货件计划
     */
    @PostMapping("/shipment/audit")
    public CommonResult<?> auditShipment(@RequestBody ShipmentAuditReqVO reqVO) {
        shipmentService.auditShipment(reqVO);
        return success(null);
    }



    @PostMapping("/shipment/page")
    public CommonResult<?> getShipmentPlanPage(@RequestBody StockShipmentPlanPageReqVO reqVO) {
        return success(shipmentService.getShipmentPlanPage(reqVO));
    }

    /**
     * 获得货件计划详情
     *
     * @param reqVO 货件计划
     * @return 货件计划详情
     */
    @GetMapping("/shipment")
    public CommonResult<?> getShipmentPlan(Integer id) {
        return success(shipmentService.getShipmentPlan(id));
    }


    /**
     * 获得货件地址列表
     *
     * @return 货件地址列表
     */
    @RequestMapping("/get-shipment-address")
    public CommonResult<?> getShipmentAddress() {
        return success(addressService.getShipmentAddress());
    }

}
