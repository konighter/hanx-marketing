package com.hzltd.module.amz.controller.admin.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 亚马逊报告任务分页 Request VO")
@Data
public class AmzReportTaskPageReqVO extends PageParam {

    @Schema(description = "查询条件")
    private String query;

    @Schema(description = "查询状态", example = "2")
    private Integer status;

    @Schema(description = "报告结果")
    private String reportResult;

    @Schema(description = "上次检索时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastCheckTime;

    @Schema(description = "结果是否已归档")
    private Integer isArchive;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}