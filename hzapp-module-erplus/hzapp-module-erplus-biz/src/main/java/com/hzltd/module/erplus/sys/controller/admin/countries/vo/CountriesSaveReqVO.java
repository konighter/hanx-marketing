package com.hzltd.module.erplus.sys.controller.admin.countries.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - [Erplus] 国家/地区定义新增/修改 Request VO")
@Data
public class CountriesSaveReqVO {

    @Schema(description = "国家ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23217")
    private Integer id;

    @Schema(description = "ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ISO 3166-1 Alpha-2 code (e.g., US, CN, GB)不能为空")
    private String isoCode2;

    @Schema(description = "ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ISO 3166-1 Alpha-3 code (e.g., USA, CHN, GBR)不能为空")
    private String isoCode3;

    @Schema(description = "国家英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "国家英文名称不能为空")
    private String name;

    @Schema(description = "默认语言代码 (e.g., en-US, zh-CN)")
    private String defaultLanguageCode;

    @Schema(description = "默认货币代码 (e.g., USD, CNY, GBP)")
    private String defaultCurrencyCode;

    @Schema(description = "是否作为目标市场启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否作为目标市场启用不能为空")
    private Boolean isActive;

}