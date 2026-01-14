package com.hzltd.module.erplus.controller.admin.stock;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpStockPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpStockRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.stock.ErpTransferAvailableReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.service.cross.backup.ProductService;
import com.hzltd.module.erplus.service.stock.ErpStockService;
import com.hzltd.module.erplus.service.stock.ErpWarehouseService;
import com.hzltd.module.erplus.service.stock.ErplusStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertSet;


@Tag(name = "管理后台 - ERP 产品库存")
@RestController
@RequestMapping("/erplus/stock")
@Validated
public class ErpStockController {

    @Resource
    private ErpStockService stockService;
    @Resource
    private ProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;

    @GetMapping("/get")
    @Operation(summary = "获得产品库存")
    @Parameters({
            @Parameter(name = "id", description = "编号", example = "1"), // 方案一：传递 id
            @Parameter(name = "productId", description = "产品编号", example = "10"), // 方案二：传递 productId + warehouseId
            @Parameter(name = "warehouseId", description = "仓库编号", example = "2")
    })
    @PreAuthorize("@ss.hasPermission('erp:stock:query')")
    public CommonResult<ErpStockRespVO> getStock(@RequestParam(value = "id", required = false) Long id,
                                                 @RequestParam(value = "productId", required = false) Long productId,
                                                 @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        ErpStockDO stock = id != null ? stockService.getStock(id) : stockService.getStock(productId, warehouseId);
        return success(BeanUtils.toBean(stock, ErpStockRespVO.class));
    }


    @GetMapping("/get-count")
    @Operation(summary = "获得产品库存数量")
    @Parameter(name = "productId", description = "产品编号", example = "10")
    public CommonResult<BigDecimal> getStockCount(@RequestParam("productId") Long productId) {
        return success(stockService.getStockCount(productId));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品库存分页")
    @PreAuthorize("@ss.hasPermission('erp:stock:query')")
    public CommonResult<PageResult<ErpStockRespVO>> getStockPage(@Valid ErpStockPageReqVO pageReqVO) {
        PageResult<ErpStockDO> pageResult = stockService.getStockPage(pageReqVO);
        return success(buildStockVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品库存 Excel")
    @PreAuthorize("@ss.hasPermission('erp:stock:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockExcel(@Valid ErpStockPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpStockRespVO> list = buildStockVOPageResult(stockService.getStockPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品库存.xls", "数据", ErpStockRespVO.class, list);
    }

    private PageResult<ErpStockRespVO> buildStockVOPageResult(PageResult<ErpStockDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Map<Long, ProductSpuRespVO> productMap = productService.getProductVOMap(
                convertSet(pageResult.getList(), ErpStockDO::getProductId));
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(pageResult.getList(), ErpStockDO::getWarehouseId));
        return BeanUtils.toBean(pageResult, ErpStockRespVO.class, stock -> {
            MapUtils.findAndThen(productMap, stock.getProductId(), product -> stock.setProductName(product.getName())
                    .setCategoryName(product.getCategoryName()).setUnitName(product.getUnitName()));
            MapUtils.findAndThen(warehouseMap, stock.getWarehouseId(), warehouse -> stock.setWarehouseName(warehouse.getName()));
        });
    }
}