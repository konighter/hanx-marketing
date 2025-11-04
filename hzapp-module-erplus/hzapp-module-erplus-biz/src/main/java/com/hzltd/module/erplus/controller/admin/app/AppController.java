package com.hzltd.module.erplus.controller.admin.app;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.app.vo.AppPageReqVO;
import com.hzltd.module.erplus.controller.admin.app.vo.AppRespVO;
import com.hzltd.module.erplus.controller.admin.app.vo.AppSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.app.AppDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.service.app.AppService;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 应用注册信息")
@RestController
@RequestMapping("/erplus/app")
@Validated
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private SellPlatformService sellPlatformService;

    @PostMapping("/create")
    @Operation(summary = "创建应用注册信息")
    @PreAuthorize("@ss.hasPermission('erplus:app:create')")
    public CommonResult<Integer> createApp(@Valid @RequestBody AppSaveReqVO createReqVO) {
        return success(appService.createApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用注册信息")
    @PreAuthorize("@ss.hasPermission('erplus:app:update')")
    public CommonResult<Boolean> updateApp(@Valid @RequestBody AppSaveReqVO updateReqVO) {
        appService.updateApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用注册信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:app:delete')")
    public CommonResult<Boolean> deleteApp(@RequestParam("id") Integer id) {
        appService.deleteApp(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用注册信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:app:query')")
    public CommonResult<AppRespVO> getApp(@RequestParam("id") Integer id) {
        AppDO app = appService.getApp(id);
        return success(BeanUtils.toBean(app, AppRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用注册信息分页")
    @PreAuthorize("@ss.hasPermission('erplus:app:query')")
    public CommonResult<PageResult<AppRespVO>> getAppPage(@Valid AppPageReqVO pageReqVO) {
        PageResult<AppDO> pageResult = appService.getAppPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppRespVO.class, (r) -> {
            SellPlatformDO platform = sellPlatformService.getSellPlatformCache(r.getPlatformId());
            if (platform != null && StringUtils.isNoneBlank(platform.getName())) {
                r.setPlatformName(platform.getName());
            }
        }));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用注册信息 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:app:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAppExcel(@Valid AppPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppDO> list = appService.getAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "应用注册信息.xls", "数据", AppRespVO.class,
                        BeanUtils.toBean(list, AppRespVO.class));
    }

}