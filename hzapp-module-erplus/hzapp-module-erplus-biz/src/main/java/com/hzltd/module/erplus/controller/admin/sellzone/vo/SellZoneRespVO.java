package com.hzltd.module.erplus.controller.admin.sellzone.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 销售区域 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SellZoneRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9505")
    @ExcelProperty("ID")
    private Integer id;

    @Schema(description = "销售平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "23931")
    @ExcelProperty("销售平台")
    private Integer platformId;

    @Schema(description = "区域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("区域名称")
    private String zoneName;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区域编码")
    private String zoneCode;

    @Schema(description = "区域Locale")
    @ExcelProperty("区域Locale")
    private String locale;

    @Schema(description = "语言")
    @ExcelProperty("语言")
    private String language;

    @Schema(description = "货币")
    @ExcelProperty("货币")
    private String currency;

    @Schema(description = "所属大区 (如 NA, EU, FE)")
    @ExcelProperty("所属大区")
    private String region;

    @Schema(description = "国家代码 (如 US, GB, DE)")
    @ExcelProperty("国家代码")
    private String countryCode;

    @Schema(description = "SP-API Endpoint")
    @ExcelProperty("SP-API Endpoint")
    private String spEndpoint;

    @Schema(description = "广告 API Endpoint")
    @ExcelProperty("广告 API Endpoint")
    private String advEndpoint;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}