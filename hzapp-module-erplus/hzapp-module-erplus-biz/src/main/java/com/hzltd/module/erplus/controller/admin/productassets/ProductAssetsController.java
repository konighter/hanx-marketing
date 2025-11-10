package com.hzltd.module.erplus.controller.admin.productassets;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import static com.hzltd.framework.common.pojo.CommonResult.success;

import com.hzltd.framework.excel.core.util.ExcelUtils;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.*;

import com.hzltd.module.erplus.controller.admin.productassets.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productassets.ProductAssetsDO;
import com.hzltd.module.erplus.service.productassets.ProductAssetsService;

@Tag(name = "管理后台 - 商品素材")
@RestController
@RequestMapping("/erplus/product-assets")
@Validated
public class ProductAssetsController {

    @Resource
    private ProductAssetsService productAssetsService;

    @PostMapping("/create")
    @Operation(summary = "创建商品素材")
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:create')")
    public CommonResult<Integer> createProductAssets(@Valid @RequestBody ProductAssetsSaveReqVO createReqVO) {
        return success(productAssetsService.createProductAssets(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品素材")
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:update')")
    public CommonResult<Boolean> updateProductAssets(@Valid @RequestBody ProductAssetsSaveReqVO updateReqVO) {
        productAssetsService.updateProductAssets(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品素材")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:delete')")
    public CommonResult<Boolean> deleteProductAssets(@RequestParam("id") Integer id) {
        productAssetsService.deleteProductAssets(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除商品素材")
                @PreAuthorize("@ss.hasPermission('erplus:product-assets:delete')")
    public CommonResult<Boolean> deleteProductAssetsList(@RequestParam("ids") List<Integer> ids) {
        productAssetsService.deleteProductAssetsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品素材")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:query')")
    public CommonResult<ProductAssetsRespVO> getProductAssets(@RequestParam("id") Integer id) {
        ProductAssetsDO productAssets = productAssetsService.getProductAssets(id);
        return success(BeanUtils.toBean(productAssets, ProductAssetsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品素材分页")
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:query')")
    public CommonResult<PageResult<ProductAssetsRespVO>> getProductAssetsPage(@Valid ProductAssetsPageReqVO pageReqVO) {
        PageResult<ProductAssetsDO> pageResult = productAssetsService.getProductAssetsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProductAssetsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出商品素材 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:product-assets:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductAssetsExcel(@Valid ProductAssetsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProductAssetsDO> list = productAssetsService.getProductAssetsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "商品素材.xls", "数据", ProductAssetsRespVO.class,
                        BeanUtils.toBean(list, ProductAssetsRespVO.class));
    }

}