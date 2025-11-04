package com.hzltd.module.erplus.sys.controller.admin.hscodes;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesRespVO;
import com.hzltd.module.erplus.sys.controller.admin.hscodes.vo.HsCodesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.hscodes.HsCodesDO;
import com.hzltd.module.erplus.sys.service.hscodes.HsCodesService;
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

@Tag(name = "管理后台 - [Erplus] 海关编码(HS Code)")
@RestController
@RequestMapping("/erplus/hs-codes")
@Validated
public class HsCodesController {

    @Resource
    private HsCodesService hsCodesService;

    @PostMapping("/create")
    @Operation(summary = "创建[Erplus] 海关编码(HS Code)")
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:create')")
    public CommonResult<Long> createHsCodes(@Valid @RequestBody HsCodesSaveReqVO createReqVO) {
        return success(hsCodesService.createHsCodes(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新[Erplus] 海关编码(HS Code)")
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:update')")
    public CommonResult<Boolean> updateHsCodes(@Valid @RequestBody HsCodesSaveReqVO updateReqVO) {
        hsCodesService.updateHsCodes(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除[Erplus] 海关编码(HS Code)")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:delete')")
    public CommonResult<Boolean> deleteHsCodes(@RequestParam("id") Long id) {
        hsCodesService.deleteHsCodes(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得[Erplus] 海关编码(HS Code)")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:query')")
    public CommonResult<HsCodesRespVO> getHsCodes(@RequestParam("id") Long id) {
        HsCodesDO hsCodes = hsCodesService.getHsCodes(id);
        return success(BeanUtils.toBean(hsCodes, HsCodesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得[Erplus] 海关编码(HS Code)分页")
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:query')")
    public CommonResult<PageResult<HsCodesRespVO>> getHsCodesPage(@Valid HsCodesPageReqVO pageReqVO) {
        PageResult<HsCodesDO> pageResult = hsCodesService.getHsCodesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HsCodesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出[Erplus] 海关编码(HS Code) Excel")
    @PreAuthorize("@ss.hasPermission('erplus:hs-codes:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHsCodesExcel(@Valid HsCodesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HsCodesDO> list = hsCodesService.getHsCodesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "[Erplus] 海关编码(HS Code).xls", "数据", HsCodesRespVO.class,
                        BeanUtils.toBean(list, HsCodesRespVO.class));
    }

}