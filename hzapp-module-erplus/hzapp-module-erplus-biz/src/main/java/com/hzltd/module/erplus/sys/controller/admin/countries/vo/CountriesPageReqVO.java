package com.hzltd.module.erplus.sys.controller.admin.countries.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hzltd.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.hzltd.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - [Erplus] 国家/地区定义分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CountriesPageReqVO extends PageParam {

    @Schema(description = "ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)")
    private String isoCode2;

    @Schema(description = "ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)")
    private String isoCode3;

    @Schema(description = "国家英文名称", example = "李四")
    private String name;

    @Schema(description = "默认语言代码 (e.g., en-US, zh-CN)")
    private String defaultLanguageCode;

    @Schema(description = "默认货币代码 (e.g., USD, CNY, GBP)")
    private String defaultCurrencyCode;

    @Schema(description = "是否作为目标市场启用")
    private Boolean isActive;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}