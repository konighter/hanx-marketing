package com.hzltd.module.erplus.controller.admin.sellzone.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 销售区域新增/修改 Request VO")
@Data
public class SellZoneSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9505")
    private Integer id;

    @Schema(description = "销售平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "23931")
    @NotNull(message = "销售平台不能为空")
    private Integer platformId;

    @Schema(description = "区域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "区域名称不能为空")
    private String zoneName;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区域编码不能为空")
    private String zoneCode;

    @Schema(description = "区域Locale")
    private String locale;

    @Schema(description = "语言")
    private String language;

}