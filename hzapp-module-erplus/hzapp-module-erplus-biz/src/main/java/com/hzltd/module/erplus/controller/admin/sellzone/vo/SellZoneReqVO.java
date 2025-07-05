package com.hzltd.module.erplus.controller.admin.sellzone.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 销售区域 Request VO")
@Data
@ToString(callSuper = true)
public class SellZoneReqVO  {

    @Schema(description = "销售平台", example = "23931")
    private Integer platformId;

    @Schema(description = "区域名称", example = "芋艿")
    private String zoneName;

    @Schema(description = "区域编码")
    private String zoneCode;

    @Schema(description = "区域Locale")
    private String locale;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}