package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 亚马逊广告 帮助 - 产品推荐 Request VO")
@Data
public class AmzAdvHelpProductRecommendationReqVO {

    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告产品 ASIN 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "广告产品 ASIN 列表不能为空")
    private List<String> adAsins;

    @Schema(description = "返回数量", example = "10")
    private Integer count;

    @Schema(description = "分页游标", example = "cursor_token")
    private String cursor;

    @Schema(description = "语言区域", example = "zh_CN")
    private String locale;

}
