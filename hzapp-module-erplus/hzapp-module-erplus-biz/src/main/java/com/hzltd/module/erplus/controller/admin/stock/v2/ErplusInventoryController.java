package com.hzltd.module.erplus.controller.admin.stock.v2;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.warehouse.ErpWarehouseInventoryPageReqVO;
import com.hzltd.module.erplus.service.stock.ErplusStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - ERP 产品库存")
@RestController
@RequestMapping("/erplus/inventory")
@Validated
public class ErplusInventoryController {

    @Resource
    @Qualifier("erplusStockService")
    private ErplusStockService erplusStockService;


    @PostMapping("/transfer_available")
    @Operation(summary = "仓库可用库存")
    @PreAuthorize("@ss.hasPermission('erp:stock:query')")
    public CommonResult<?> getAvailableStockForTransfer( @RequestBody ErpTransferAvailableReqVO reqVO) {
        return success(erplusStockService.getTransferAvailableStock(reqVO));
    }

    @PostMapping("/page")
    @Operation(summary = "查询产品库存分页")
    @PreAuthorize("@ss.hasPermission('erp:stock:query')")
    public CommonResult<?> getWarehouseInventoryPage(@RequestBody ErpWarehouseInventoryPageReqVO reqVO) {
        return success(erplusStockService.getWarehouseInventoryPage(reqVO));
    }






}
