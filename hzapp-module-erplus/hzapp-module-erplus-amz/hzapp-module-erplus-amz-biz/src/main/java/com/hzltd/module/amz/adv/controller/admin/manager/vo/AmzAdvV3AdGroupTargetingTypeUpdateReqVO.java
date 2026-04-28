package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 亚马逊广告组定向类型更新 Request VO")
@Data
public class AmzAdvV3AdGroupTargetingTypeUpdateReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;
    
    @Schema(description = "广告组编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告组编号不能为空")
    private Long adGroupId;

    @Schema(description = "定向类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "定向类型不能为空")
    private String targetingType;
}
