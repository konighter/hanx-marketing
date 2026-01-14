package com.hzltd.module.erplus.controller.admin.stock;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.biz.system.user.AdminUserApi;
import com.hzltd.framework.common.biz.system.user.dto.AdminUserRespDTO;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.record.ErpStockRecordPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.record.ErpStockRecordRespVO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpStockRecordDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.service.cross.backup.ProductService;
import com.hzltd.module.erplus.service.stock.ErpStockRecordService;
import com.hzltd.module.erplus.service.stock.ErpWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertSet;


@Tag(name = "管理后台 - ERP 产品库存明细")
@RestController
@RequestMapping("/erplus/stock-record")
@Validated
public class ErpStockRecordController {

    @Resource
    private ErpStockRecordService stockRecordService;
    @Resource
    private ProductService productService;
    @Resource
    private ErpWarehouseService warehouseService;

    @Resource
    private AdminUserApi adminUserApi;

    @GetMapping("/get")
    @Operation(summary = "获得产品库存明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:query')")
    public CommonResult<ErpStockRecordRespVO> getStockRecord(@RequestParam("id") Long id) {
        ErpStockRecordDO stockRecord = stockRecordService.getStockRecord(id);
        return success(BeanUtils.toBean(stockRecord, ErpStockRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品库存明细分页")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:query')")
    public CommonResult<PageResult<ErpStockRecordRespVO>> getStockRecordPage(@Valid ErpStockRecordPageReqVO pageReqVO) {
        PageResult<ErpStockRecordDO> pageResult = stockRecordService.getStockRecordPage(pageReqVO);
        return success(buildStockRecrodVOPageResult(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品库存明细 Excel")
    @PreAuthorize("@ss.hasPermission('erp:stock-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStockRecordExcel(@Valid ErpStockRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpStockRecordRespVO> list = buildStockRecrodVOPageResult(stockRecordService.getStockRecordPage(pageReqVO)).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品库存明细.xls", "数据", ErpStockRecordRespVO.class, list);
    }

    private PageResult<ErpStockRecordRespVO> buildStockRecrodVOPageResult(PageResult<ErpStockRecordDO> pageResult) {
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Map<Long, ProductSpuRespVO> productMap = productService.getProductVOMap(
                convertSet(pageResult.getList(), ErpStockRecordDO::getProductId));
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(pageResult.getList(), ErpStockRecordDO::getWarehouseId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
                convertSet(pageResult.getList(), record -> Long.parseLong(record.getCreator())));
        return BeanUtils.toBean(pageResult, ErpStockRecordRespVO.class, stock -> {
            MapUtils.findAndThen(productMap, stock.getProductId(), product -> stock.setProductName(product.getName())
                    .setCategoryName(product.getCategoryName()).setUnitName(product.getUnitName()));
            MapUtils.findAndThen(warehouseMap, stock.getWarehouseId(), warehouse -> stock.setWarehouseName(warehouse.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(stock.getCreator()), user -> stock.setCreatorName(user.getNickname()));
        });
    }

}