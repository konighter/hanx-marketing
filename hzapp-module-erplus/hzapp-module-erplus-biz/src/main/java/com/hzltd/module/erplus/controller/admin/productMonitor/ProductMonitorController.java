package com.hzltd.module.erplus.controller.admin.productMonitor;

import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMetricsDO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import static com.hzltd.framework.common.pojo.CommonResult.success;

import com.hzltd.framework.excel.core.util.ExcelUtils;

import com.hzltd.framework.operatelog.core.annotations.OperateLog;
import static com.hzltd.framework.operatelog.core.enums.OperateTypeEnum.*;

import com.hzltd.module.erplus.controller.admin.productMonitor.vo.*;
import com.hzltd.module.erplus.dal.dataobject.productMonitor.ProductMonitorDO;
import com.hzltd.module.erplus.service.productMonitor.ProductMonitorService;

@Tag(name = "管理后台 - 产品监控")
@RestController
@RequestMapping("/erplus/product-monitor")
@Validated
public class ProductMonitorController {

    @Resource
    private ProductMonitorService productMonitorService;

    @PostMapping("/create")
    @Operation(summary = "创建产品监控")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:create')")
    public CommonResult<Integer> createProductMonitor(@Valid @RequestBody ProductMonitorSaveReqVO createReqVO) {
        return success(productMonitorService.createProductMonitor(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新产品监控")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:update')")
    public CommonResult<Boolean> updateProductMonitor(@Valid @RequestBody ProductMonitorSaveReqVO updateReqVO) {
        productMonitorService.updateProductMonitor(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品监控")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:delete')")
    public CommonResult<Boolean> deleteProductMonitor(@RequestParam("id") Integer id) {
        productMonitorService.deleteProductMonitor(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得产品监控")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:query')")
    public CommonResult<ProductMonitorRespVO> getProductMonitor(@RequestParam("id") Integer id) {
        ProductMonitorDO productMonitor = productMonitorService.getProductMonitor(id);
        return success(BeanUtils.toBean(productMonitor, ProductMonitorRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得产品监控分页")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:query')")
    public CommonResult<PageResult<ProductMonitorRespVO>> getProductMonitorPage(@Valid ProductMonitorPageReqVO pageReqVO) {
        PageResult<ProductMonitorDO> pageResult = productMonitorService.getProductMonitorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProductMonitorRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出产品监控 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:export')")
    @OperateLog(type = EXPORT)
    public void exportProductMonitorExcel(@Valid ProductMonitorPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProductMonitorDO> list = productMonitorService.getProductMonitorPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "产品监控.xls", "数据", ProductMonitorRespVO.class,
                        BeanUtils.toBean(list, ProductMonitorRespVO.class));
    }


    @PostMapping("/metrics-data/page")
    @Operation(summary = "获得产品监控指标数据分页")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:query')")
    public CommonResult<PageResult<ProductMetricsDataRespVO>> getProductMetricsData(@Valid @RequestBody ProductMetricsDataPageReqVO reqVO) {
        PageResult<ProductMetricsDataRespVO> pageResult = productMonitorService.getProductMetricsData(reqVO);
        return success(pageResult);
    }

    @PostMapping("/metrics-def")
    @Operation(summary = "获得产品监控指标")
    @PreAuthorize("@ss.hasPermission('erplus:product-monitor:query')")
    public CommonResult<List<ProductMetricsRespVO>> getProductMetrics(@Valid @RequestBody ProductMetricsReqVO reqVO) {
        List<ProductMetricsRespVO> list = productMonitorService.getProductMetrics(reqVO.getMetrics());
        return success(list);
    }



}