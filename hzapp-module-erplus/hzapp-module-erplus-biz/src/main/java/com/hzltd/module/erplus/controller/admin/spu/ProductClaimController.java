package com.hzltd.module.erplus.controller.admin.spu;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.framework.operatelog.core.annotations.OperateLog;
import com.hzltd.module.erplus.controller.admin.spu.vo.*;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductClaimDO;
import com.hzltd.module.erplus.service.spu.ProductClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 商品认领")
@RestController
@RequestMapping("/erplus/product-claim")
@Validated
public class ProductClaimController {

    @Resource
    private ProductClaimService productClaimService;

    @PostMapping("/create")
    @Operation(summary = "创建商品认领")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:create')")
    public CommonResult<Integer> createProductClaim(@Valid @RequestBody ProductClaimSaveReqVO createReqVO) {
        return success(productClaimService.createProductClaim(createReqVO));
    }

    @PostMapping("/batch")
    @Operation(summary = "创建商品认领")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:create')")
    public CommonResult<List<Integer>> createBatchProductClaim(@Valid @RequestBody ProductClaimBatchReqVO createReqVO) {
        return success(productClaimService.batchCreateProductClaim(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品认领")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:update')")
    public CommonResult<Boolean> updateProductClaim(@Valid @RequestBody ProductClaimSaveReqVO updateReqVO) {
        productClaimService.updateProductClaim(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品认领")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:product-claim:delete')")
    public CommonResult<Boolean> deleteProductClaim(@RequestParam("id") Integer id) {
        productClaimService.deleteProductClaim(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品认领")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:query')")
    public CommonResult<ProductClaimRespVO> getProductClaim(@RequestParam("id") Integer id) {
        ProductClaimRespVO productClaim = productClaimService.getProductClaim(id);
        return success(productClaim);
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品认领分页")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:query')")
    public CommonResult<PageResult<ProductClaimRespVO>> getProductClaimPage(@Valid ProductClaimPageReqVO pageReqVO) {
        PageResult<ProductClaimRespVO> pageResult = productClaimService.getProductClaimPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出商品认领 Excel")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:export')")
    @OperateLog(type = EXPORT)
    public void exportProductClaimExcel(@Valid ProductClaimPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProductClaimRespVO> list = productClaimService.getProductClaimPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "商品认领.xls", "数据", ProductClaimRespVO.class,
                        BeanUtils.toBean(list, ProductClaimRespVO.class));
    }


    @GetMapping("/sync")
    @Operation(summary = "同步商品")
    @PreAuthorize("@ss.hasPermission('erp:product-claim:sync')")
    public CommonResult<Boolean> syncProductClaim(@Valid ProductClaimSyncReqVO reqVO) {
        List<ProductClaimDO> productClaims = productClaimService.getProductClaimBatch(reqVO.getClaimIds());
        productClaimService.syncProductClaimBatch(productClaims);
        return CommonResult.success(true);
    }


}