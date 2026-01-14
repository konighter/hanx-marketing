package com.hzltd.module.erplus.controller.admin.amz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊报告任务新增/修改 Request VO")
@Data
public class AmzReportTaskSaveReqVO {

    /**
     * 店铺 ID
     */
    private Integer shopId;

    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 时间周期
     */
    private String period;


    private List<String> dateRange;

    /**
     * 报告开始日期
     */
    private Date reportStartDate;

    /**
     * 报告结束日期
     */
    private Date reportEndDate;

    /**
     * ASIN 列表
     */
    private String asins;


}