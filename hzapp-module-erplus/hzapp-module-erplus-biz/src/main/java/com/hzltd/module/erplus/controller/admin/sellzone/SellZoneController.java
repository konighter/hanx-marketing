package com.hzltd.module.erplus.controller.admin.sellzone;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZonePageReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneReqVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneRespVO;
import com.hzltd.module.erplus.controller.admin.sellzone.vo.SellZoneSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellzone.SellZoneDO;
import com.hzltd.module.erplus.service.sellzone.SellZoneService;
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


@Tag(name = "管理后台 - 销售区域")
@RestController
@RequestMapping("/erplus/sell-zone")
@Validated
public class SellZoneController {

    @Resource
    private SellZoneService sellZoneService;

    @PostMapping("/create")
    @Operation(summary = "创建销售区域")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:create')")
    public CommonResult<Integer> createSellZone(@Valid @RequestBody SellZoneSaveReqVO createReqVO) {
        return success(sellZoneService.createSellZone(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新销售区域")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:update')")
    public CommonResult<Boolean> updateSellZone(@Valid @RequestBody SellZoneSaveReqVO updateReqVO) {
        sellZoneService.updateSellZone(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除销售区域")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:delete')")
    public CommonResult<Boolean> deleteSellZone(@RequestParam("id") Integer id) {
        sellZoneService.deleteSellZone(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得销售区域")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:query')")
    public CommonResult<SellZoneRespVO> getSellZone(@RequestParam("id") Integer id) {
        SellZoneDO sellZone = sellZoneService.getSellZone(id);
        return success(BeanUtils.toBean(sellZone, SellZoneRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得销售区域")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:query')")
    public CommonResult<List<SellZoneRespVO>> getSellZoneSimpleList(@Valid SellZoneReqVO reqVO) {
        List<SellZoneDO> sellZone = sellZoneService.getSellZoneList(reqVO);
        return success(BeanUtils.toBean(sellZone, SellZoneRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得销售区域分页")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:query')")
    public CommonResult<PageResult<SellZoneRespVO>> getSellZonePage(@Valid SellZonePageReqVO pageReqVO) {
        PageResult<SellZoneDO> pageResult = sellZoneService.getSellZonePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SellZoneRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出销售区域 Excel")
    @PreAuthorize("@ss.hasPermission('erp:sell-zone:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSellZoneExcel(@Valid SellZonePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SellZoneDO> list = sellZoneService.getSellZonePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "销售区域.xls", "数据", SellZoneRespVO.class,
                        BeanUtils.toBean(list, SellZoneRespVO.class));
    }

}