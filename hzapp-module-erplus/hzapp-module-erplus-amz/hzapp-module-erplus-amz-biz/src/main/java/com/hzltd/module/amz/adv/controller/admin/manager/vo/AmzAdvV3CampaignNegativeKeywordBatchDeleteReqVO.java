package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import com.hzltd.module.amz.adv.client.sp.model.SponsoredProductsCreateCampaignNegativeKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AmzAdvV3CampaignNegativeKeywordBatchDeleteReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告活动ID不能为空")
    private Long campaignId;

    @Schema(description = "否定关键词列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "否定关键词列表不能为空")
    private List<String> keywords;
}
