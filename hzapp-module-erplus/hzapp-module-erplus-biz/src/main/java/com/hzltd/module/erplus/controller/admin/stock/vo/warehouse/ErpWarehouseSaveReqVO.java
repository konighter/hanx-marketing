package com.hzltd.module.erplus.controller.admin.warehouse.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - ERP 仓库新增/修改 Request VO")
@Data
public class WarehouseSaveReqVO {

    @Schema(description = "仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21598")
    private Long id;

    @Schema(description = "仓库名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "仓库名称不能为空")
    private String name;

    @Schema(description = "类型: 平台仓/海外仓/家庭仓/活动仓", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型: 平台仓/海外仓/家庭仓/活动仓不能为空")
    private Integer type;

    @Schema(description = "店铺", example = "15932")
    private Integer shopId;

    @Schema(description = "平台ID", example = "16669")
    private Integer platformId;

    @Schema(description = "市场", example = "9111")
    private String marketId;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "负责人")
    private String principal;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "开启状态不能为空")
    private Integer status;

    @Schema(description = "是否默认", example = "1")
    private Boolean defaultStatus;

}