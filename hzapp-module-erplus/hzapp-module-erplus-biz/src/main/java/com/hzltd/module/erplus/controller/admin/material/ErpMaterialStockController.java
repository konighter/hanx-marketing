package com.hzltd.module.erplus.controller.admin.material;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.material.vo.stock.ErpMaterialStockPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.stock.ErpMaterialStockRespVO;
import com.hzltd.module.erplus.service.material.ErpMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - ERP 耗材库存")
@RestController
@RequestMapping("/erplus/material-stock")
@Validated
public class ErpMaterialStockController {

    @Resource
    private ErpMaterialService materialService;

    @GetMapping("/page")
    @Operation(summary = "获得耗材库存分页")
    public CommonResult<PageResult<ErpMaterialStockRespVO>> getMaterialStockPage(@Validated ErpMaterialStockPageReqVO pageReqVO) {
        return success(materialService.getMaterialStockPage(pageReqVO));
    }

    @GetMapping("/get-count")
    @Operation(summary = "获得耗材库存数量")
    @Parameters({
            @Parameter(name = "materialId", description = "耗材编号", required = true, example = "1"),
            @Parameter(name = "warehouseId", description = "仓库编号", required = true, example = "1")
    })
    public CommonResult<BigDecimal> getMaterialStockCount(@RequestParam("materialId") Long materialId,
                                                          @RequestParam("warehouseId") Long warehouseId) {
        return success(materialService.getMaterialStockCount(materialId, warehouseId));
    }

}
