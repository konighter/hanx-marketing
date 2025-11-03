package com.hzltd.module.erplus.controller.admin.sellplatform;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.framework.operatelog.core.annotations.OperateLog;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformPageReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformReqVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformRespVO;
import com.hzltd.module.erplus.controller.admin.sellplatform.vo.SellPlatformSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.ServiceMode;
import com.hzltd.module.erplus.enums.ServiceModeEnum;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 销售平台")
@RestController
@RequestMapping("/erplus/sell-platform")
@Validated
public class SellPlatformController {

    @Resource
    private SellPlatformService sellPlatformService;

    @PostMapping("/create")
    @Operation(summary = "创建销售平台")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:create')")
    public CommonResult<Integer> createSellPlatform(@Valid @RequestBody SellPlatformSaveReqVO createReqVO) {
        return success(sellPlatformService.createSellPlatform(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新销售平台")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:update')")
    public CommonResult<Boolean> updateSellPlatform(@Valid @RequestBody SellPlatformSaveReqVO updateReqVO) {
        sellPlatformService.updateSellPlatform(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除销售平台")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:delete')")
    public CommonResult<Boolean> deleteSellPlatform(@RequestParam("id") Integer id) {
        sellPlatformService.deleteSellPlatform(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得销售平台")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:query')")
    public CommonResult<SellPlatformRespVO> getSellPlatform(@RequestParam("id") Integer id) {
        SellPlatformDO sellPlatform = sellPlatformService.getSellPlatform(id);
        return success(BeanUtils.toBean(sellPlatform, SellPlatformRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得销售平台分页")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:query')")
    public CommonResult<PageResult<SellPlatformRespVO>> getSellPlatformPage(@Valid SellPlatformPageReqVO pageReqVO) {
        PageResult<SellPlatformDO> pageResult = sellPlatformService.getSellPlatformPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SellPlatformRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得销售平台分页")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:query')")
    public CommonResult<List<SellPlatformRespVO>> getSellPlatformList(@Valid SellPlatformReqVO pageReqVO) {
        List<SellPlatformDO> pageResult = sellPlatformService.getSellPlatformList(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SellPlatformRespVO.class));
    }

    @GetMapping("/serviceModes")
    @Operation(summary = "获得销售模式")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:query')")
    public CommonResult<List<ServiceMode>> getServiceModeList() {
        List<ServiceMode> result = Arrays.stream(ServiceModeEnum.values())
                .map(e -> new ServiceMode(e.getName(), e.getCode())).collect(Collectors.toList());
        return CommonResult.success(result);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出销售平台 Excel")
    @PreAuthorize("@ss.hasPermission('erp:sell-platform:export')")
    @OperateLog(type = EXPORT)
    public void exportSellPlatformExcel(@Valid SellPlatformPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SellPlatformDO> list = sellPlatformService.getSellPlatformPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "销售平台.xls", "数据", SellPlatformRespVO.class,
                BeanUtils.toBean(list, SellPlatformRespVO.class));
    }

}