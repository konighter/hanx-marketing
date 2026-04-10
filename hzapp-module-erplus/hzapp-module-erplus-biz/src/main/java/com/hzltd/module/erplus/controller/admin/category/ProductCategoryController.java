package com.hzltd.module.erplus.controller.admin.category;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategoryListReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategoryRespVO;
import com.hzltd.module.erplus.controller.admin.category.vo.ProductCategorySaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.product.ProductCategoryDO;
import com.hzltd.module.erplus.service.categoryattr.ProductCategoryService;
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
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertList;


@Tag(name = "管理后台 - ERP 产品分类")
@RestController
@RequestMapping("/erplus/product-category")
@Validated
public class ProductCategoryController {

    @Resource
    private ProductCategoryService productCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建产品分类")
    @PreAuthorize("@ss.hasPermission('erp:product-category:create')")
    public CommonResult<Long> createProductCategory(@Valid @RequestBody ProductCategorySaveReqVO createReqVO) {
        return success(productCategoryService.createProductCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新产品分类")
    @PreAuthorize("@ss.hasPermission('erp:product-category:update')")
    public CommonResult<Boolean> updateProductCategory(@Valid @RequestBody ProductCategorySaveReqVO updateReqVO) {
        productCategoryService.updateProductCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:product-category:delete')")
    public CommonResult<Boolean> deleteProductCategory(@RequestParam("id") Long id) {
        productCategoryService.deleteProductCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:product-category:query')")
    public CommonResult<ProductCategoryRespVO> getProductCategory(@RequestParam("id") Long id) {
        ProductCategoryDO category = productCategoryService.getProductCategory(id);
        return success(BeanUtils.toBean(category, ProductCategoryRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得产品分类列表")
    @PreAuthorize("@ss.hasPermission('erp:product-category:query')")
    public CommonResult<List<ProductCategoryRespVO>> getProductCategoryList(@Valid ProductCategoryListReqVO listReqVO) {
        List<ProductCategoryDO> list = productCategoryService.getProductCategoryList(listReqVO);
        return success(BeanUtils.toBean(list, ProductCategoryRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得产品分类精简列表", description = "只包含被开启的分类，主要用于前端的下拉选项")
    public CommonResult<List<ProductCategoryRespVO>> getProductCategorySimpleList() {
        List<ProductCategoryDO> list = productCategoryService.getProductCategoryList(
                new ProductCategoryListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(convertList(list, category -> new ProductCategoryRespVO()
                .setId(category.getId()).setName(category.getName()).setParentId(category.getParentId())));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品分类 Excel")
    @PreAuthorize("@ss.hasPermission('erp:product-category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProductCategoryExcel(@Valid ProductCategoryListReqVO listReqVO,
              HttpServletResponse response) throws IOException {
        List<ProductCategoryDO> list = productCategoryService.getProductCategoryList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "产品分类.xls", "数据", ProductCategoryRespVO.class,
                        BeanUtils.toBean(list, ProductCategoryRespVO.class));
    }

}