package com.hzltd.module.amz.adv.controller.admin.manager;


import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzReportTaskPageReqVO;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzReportTaskRespVO;
import com.hzltd.module.amz.adv.controller.admin.vo.AmzReportTaskSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzReportTaskDO;
import com.hzltd.module.amz.spapi.service.AmzReportTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊报告任务")
@RestController
@RequestMapping("/erplus/amz-report")
@Validated
@Deprecated
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