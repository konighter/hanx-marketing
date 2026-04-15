package com.hzltd.module.erplus.controller.admin.shop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 店铺信息新增/修改 Request VO")
@Data
public class ShopSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3188")
    private Integer id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "平台不能为空")
    private String platform;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区域不能为空")
    private String region;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "时区", example = "Asia/Shanghai")
    private String timezone;

}