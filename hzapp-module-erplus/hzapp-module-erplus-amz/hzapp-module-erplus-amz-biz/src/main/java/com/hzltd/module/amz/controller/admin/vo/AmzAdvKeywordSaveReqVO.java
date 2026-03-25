package com.hzltd.module.amz.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 亚马逊广告关键词创建/更新 Request VO")
@Data
public class AmzAdvKeywordSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long id;

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "店铺ID不能为空")
    private String shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "campaign123")
    @NotNull(message = "广告活动ID不能为空")
    private String campaignId;

    @Schema(description = "广告组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "adgroup123")
    @NotNull(message = "广告组ID不能为空")
    private String adGroupId;

    @Schema(description = "关键词文本", requiredMode = Schema.RequiredMode.REQUIRED, example = "wireless headphones")
    @NotNull(message = "关键词文本不能为空")
    private String keywordText;

    @Schema(description = "关键词匹配类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "exact")
    @NotNull(message = "关键词匹配类型不能为空")
    private String matchType; // exact, phrase, broad

    @Schema(description = "关键词状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "enabled")
    @NotNull(message = "关键词状态不能为空")
    private String state; // enabled, paused, archived

    @Schema(description = "出价", example = "1.5")
    private Double bid;

    @Schema(description = "关键词优先级", example = "1")
    private Integer priority;

    @Schema(description = "描述", example = "主要关键词")
    private String description;
}