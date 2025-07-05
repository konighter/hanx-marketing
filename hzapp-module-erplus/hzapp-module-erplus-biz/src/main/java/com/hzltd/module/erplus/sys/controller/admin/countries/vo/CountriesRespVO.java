package com.hzltd.module.erplus.sys.controller.admin.countries.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - [Erplus] 国家/地区定义 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CountriesRespVO {

    @Schema(description = "国家ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23217")
    @ExcelProperty("国家ID")
    private Integer id;

    @Schema(description = "ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)")
    private String isoCode2;

    @Schema(description = "ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)")
    private String isoCode3;

    @Schema(description = "国家英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("国家英文名称")
    private String name;

    @Schema(description = "默认语言代码 (e.g., en-US, zh-CN)")
    @ExcelProperty("默认语言代码 (e.g., en-US, zh-CN)")
    private String defaultLanguageCode;

    @Schema(description = "默认货币代码 (e.g., USD, CNY, GBP)")
    @ExcelProperty("默认货币代码 (e.g., USD, CNY, GBP)")
    private String defaultCurrencyCode;

    @Schema(description = "是否作为目标市场启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否作为目标市场启用")
    private Boolean isActive;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}