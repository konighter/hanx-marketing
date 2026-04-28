package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 亚马逊广告组定向批量删除 Request VO")
@Data
public class AmzAdvV3TargetingBatchDeleteReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "定向ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "定向ID列表不能为空")
    private List<String> targetIds;
}
