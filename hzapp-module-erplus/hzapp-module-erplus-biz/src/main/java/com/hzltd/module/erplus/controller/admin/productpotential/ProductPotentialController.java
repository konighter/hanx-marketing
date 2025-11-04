package com.hzltd.module.erplus.controller.admin.productpotential;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.productpotential.enums.ProductpotentialStatusEnum;
import com.hzltd.module.erplus.controller.admin.productpotential.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productpotential.ProductPotentialDO;
import com.hzltd.module.erplus.service.productpotential.ProductPotentialService;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 选品提案")
@RestController
@RequestMapping("/erplus/product-potential")
@Validated
public class ProductPotentialController {

    @Resource
    private ProductPotentialService productPotentialService;

    @Resource
    private SellPlatformService sellPlatformService;

    @PostMapping("/create")
    @Operation(summary = "创建选品提案")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:create')")
    public CommonResult<Integer> createProductPotential(@Valid @RequestBody ProductPotentialSaveReqVO createReqVO) {
        return success(productPotentialService.createProductPotential(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新选品提案")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:update')")
    public CommonResult<Boolean> updateProductPotential(@Valid @RequestBody ProductPotentialSaveReqVO updateReqVO) {
        productPotentialService.updateProductPotential(updateReqVO);
        return success(true);
    }


    @PutMapping("/updateSimple")
    @Operation(summary = "更新选品提案")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:update')")
    public CommonResult<Boolean> auditProductPotential(@Valid @RequestBody ProductPotentialSimpleReqVO updateReqVO) {
        productPotentialService.updateProductPotential(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除选品提案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:delete')")
    public CommonResult<Boolean> deleteProductPotential(@RequestParam("id") Integer id) {
        productPotentialService.deleteProductPotential(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得选品提案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:query')")
    public CommonResult<ProductPotentialRespVO> getProductPotential(@RequestParam("id") Integer id) {
        ProductPotentialRespVO productPotential = productPotentialService.getProductPotential(id);
        return success(productPotential);
    }

    @GetMapping("/getSimple")
    @Operation(summary = "获得选品提案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:query')")
    public CommonResult<ProductPotentialPageRespVO> getProductPotentialSimple(@RequestParam("id") Integer id) {
        ProductPotentialDO productPotential = productPotentialService.getProductPotentialSimple(id);
        return success(BeanUtils.toBean(productPotential, ProductPotentialPageRespVO.class, p -> {
            p.setPlatformName(sellPlatformService.getSellPlatformCache(p.getPlatformId()).getName());
            p.setStatusName(ProductpotentialStatusEnum.getByCode(p.getStatus()).getName());
        }));
    }

    @GetMapping("/page")
    @Operation(summary = "获得选品提案分页")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:query')")
    public CommonResult<PageResult<ProductPotentialPageRespVO>> getProductPotentialPage(@Valid ProductPotentialPageReqVO pageReqVO) {
        PageResult<ProductPotentialDO> pageResult = productPotentialService.getProductPotentialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProductPotentialPageRespVO.class, p -> {
            p.setPlatformName(sellPlatformService.getSellPlatformCache(p.getPlatformId()).getName());
            p.setStatusName(ProductpotentialStatusEnum.getByCode(p.getStatus()).getName());
        }));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出选品提案 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:product-potential:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductPotentialExcel(@Valid ProductPotentialPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProductPotentialDO> list = productPotentialService.getProductPotentialPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "选品提案.xls", "数据", ProductPotentialPageRespVO.class,
                        BeanUtils.toBean(list, ProductPotentialPageRespVO.class, p -> {
                            p.setPlatformName(sellPlatformService.getSellPlatformCache(p.getPlatformId()).getName());
                        }));
    }

}