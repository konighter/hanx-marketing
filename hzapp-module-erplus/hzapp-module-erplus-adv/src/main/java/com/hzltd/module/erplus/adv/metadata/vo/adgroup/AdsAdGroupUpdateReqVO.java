package com.hzltd.module.erplus.adv.metadata.vo.adgroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 广告组更新 VO")
@Data
public class AdsAdGroupUpdateReqVO {

    @Schema(description = "广告组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "广告组编号不能为空")
    private Long id;

    @Schema(description = "广告组名称", example = "Electronics Group")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

    @Schema(description = "默认出价", example = "1.50")
    private BigDecimal defaultBid;

    @Schema(description = "平台扩展字段 (JSON)")
    private Object extData;

}
