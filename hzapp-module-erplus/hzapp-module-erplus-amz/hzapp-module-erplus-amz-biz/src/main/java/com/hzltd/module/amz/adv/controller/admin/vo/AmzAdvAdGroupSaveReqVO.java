package com.hzltd.module.amz.adv.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 亚马逊广告组创建/更新 Request VO")
@Data
public class AmzAdvAdGroupSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long id;

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "店铺ID不能为空")
    private String shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "campaign123")
    @NotNull(message = "广告活动ID不能为空")
    private String campaignId;

    @Schema(description = "广告组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "主推产品组")
    @NotNull(message = "广告组名称不能为空")
    private String name;

    @Schema(description = "广告组状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "enabled")
    @NotNull(message = "广告组状态不能为空")
    private String state; // enabled, paused, archived

    @Schema(description = "默认出价", example = "1.0")
    private Double defaultBid;

    @Schema(description = "最高出价", example = "2.0")
    private Double maxBid;

    @Schema(description = "目标设备", example = "mobile")
    private String placement; // desktop, mobile, other

    @Schema(description = "商品投放类型", example = "detail")
    private String placementType; // detail, homepage, other

    @Schema(description = "描述", example = "这是主推产品的广告组")
    private String description;
}