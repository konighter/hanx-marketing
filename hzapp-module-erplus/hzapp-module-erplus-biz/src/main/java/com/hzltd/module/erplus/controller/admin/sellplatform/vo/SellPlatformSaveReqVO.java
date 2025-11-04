package com.hzltd.module.erplus.controller.admin.sellplatform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 销售平台新增/修改 Request VO")
@Data
public class SellPlatformSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16072")
    private Integer id;

    @Schema(description = "平台名称", example = "李四")
    private String name;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "配送模式")
    private List<String> shipModes;

}