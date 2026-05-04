package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 亚马逊广告关键词推荐 Request VO")
@Data
public class AmzAdvHelpKeywordRecommendationReqVO {

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "推荐类型: KEYWORDS_FOR_ADGROUP, KEYWORDS_FOR_ASINS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "推荐类型不能为空")
    private String recommendationType;

    @Schema(description = "ASIN列表")
    private List<String> asins;

    @Schema(description = "广告活动编号")
    private String campaignId;

    @Schema(description = "广告组编号")
    private String adGroupId;

    @Schema(description = "输入关键词(用于排名推荐)")
    private List<TargetDTO> targets;

    @Data
    public static class TargetDTO {
        @Schema(description = "关键词内容")
        private String value;
        @Schema(description = "匹配方式: EXACT, BROAD, PHRASE")
        private String type;
    }

    @Schema(description = "最大推荐数")
    private Integer maxRecommendations;

    @Schema(description = "排序维度: CLICKS, CONVERSIONS, DEFAULT")
    private String sortDimension;

    @Schema(description = "本地化语言")
    private String locale;

}
