package com.hzltd.module.erplus.controller.admin.amz;


import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import static com.hzltd.framework.common.pojo.CommonResult.success;

import com.hzltd.framework.excel.core.util.ExcelUtils;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.*;

import com.hzltd.module.erplus.controller.admin.amz.vo.*;
import com.hzltd.module.erplus.dal.dataobject.amz.AmzReportTaskDO;
import com.hzltd.module.erplus.service.amz.AmzReportTaskService;

@Tag(name = "管理后台 - 亚马逊报告任务")
@RestController
@RequestMapping("/erplus/amz-report")
@Validated
public class AmzReportController {

    @Resource
    private AmzReportTaskService amzReportTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建亚马逊报告任务")
    @PreAuthorize("@ss.hasPermission('erplus:amz-report-task:create')")
    public CommonResult<Long> createAmzReportTask(@Valid @RequestBody AmzReportTaskSaveReqVO createReqVO) {
        return success(amzReportTaskService.createAmzReportTask(createReqVO));
    }


    @GetMapping("/check")
    @Operation(summary = "检查亚马逊报告任务状态")
    @PreAuthorize("@ss.hasPermission('erplus:amz-report-task:query')")
    public CommonResult<String> checkAmzReportTask(@RequestParam("id") Long id) {
        amzReportTaskService.checkReportStatus(id);
        return success("报告任务状态检查完成");
    }


    @GetMapping("/get")
    @Operation(summary = "获得亚马逊报告任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:amz-report-task:query')")
    public CommonResult<AmzReportTaskRespVO> getAmzReportTask(@RequestParam("id") Long id) {
        AmzReportTaskDO amzReportTask = amzReportTaskService.getAmzReportTask(id);
        return success(BeanUtils.toBean(amzReportTask, AmzReportTaskRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊报告任务分页")
    @PreAuthorize("@ss.hasPermission('erplus:amz-report-task:query')")
    public CommonResult<PageResult<AmzReportTaskRespVO>> getAmzReportTaskPage(@Valid AmzReportTaskPageReqVO pageReqVO) {
        PageResult<AmzReportTaskDO> pageResult = amzReportTaskService.getAmzReportTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AmzReportTaskRespVO.class));
    }

}