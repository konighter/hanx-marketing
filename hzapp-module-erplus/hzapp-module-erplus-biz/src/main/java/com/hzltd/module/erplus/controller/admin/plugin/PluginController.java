package com.hzltd.module.erplus.controller.admin.plugin;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginPageReqVO;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginRespVO;
import com.hzltd.module.erplus.controller.admin.plugin.vo.PluginSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.plugin.PluginDO;
import com.hzltd.module.erplus.service.plugin.PluginService;
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

@Tag(name = "管理后台 - 插件")
@RestController
@RequestMapping("/erplus/plugin")
@Validated
public class PluginController {

    @Resource
    private PluginService pluginService;

    @PostMapping("/create")
    @Operation(summary = "创建插件")
    @PreAuthorize("@ss.hasPermission('erplus:plugin:create')")
    public CommonResult<Integer> createPlugin(@Valid @RequestBody PluginSaveReqVO createReqVO) {
        return success(pluginService.createPlugin(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新插件")
    @PreAuthorize("@ss.hasPermission('erplus:plugin:update')")
    public CommonResult<Boolean> updatePlugin(@Valid @RequestBody PluginSaveReqVO updateReqVO) {
        pluginService.updatePlugin(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除插件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:plugin:delete')")
    public CommonResult<Boolean> deletePlugin(@RequestParam("id") Integer id) {
        pluginService.deletePlugin(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得插件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:plugin:query')")
    public CommonResult<PluginRespVO> getPlugin(@RequestParam("id") Integer id) {
        PluginDO plugin = pluginService.getPlugin(id);
        return success(BeanUtils.toBean(plugin, PluginRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得插件分页")
    @PreAuthorize("@ss.hasPermission('erplus:plugin:query')")
    public CommonResult<PageResult<PluginRespVO>> getPluginPage(@Valid PluginPageReqVO pageReqVO) {
        PageResult<PluginDO> pageResult = pluginService.getPluginPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PluginRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出插件 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:plugin:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPluginExcel(@Valid PluginPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PluginDO> list = pluginService.getPluginPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "插件.xls", "数据", PluginRespVO.class,
                        BeanUtils.toBean(list, PluginRespVO.class));
    }

}