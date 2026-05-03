package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import com.hzltd.module.amz.adv.client.sp.model.SponsoredProductsCreateOrUpdateDynamicBidding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 亚马逊广告活动动态竞价更新 Request VO")
@Data
public class AmzAdvV3CampaignDynamicBiddingUpdateReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告活动ID不能为空")
    private Long campaignId;

    @Schema(description = "动态竞价配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "动态竞价配置不能为空")
    private SponsoredProductsCreateOrUpdateDynamicBidding dynamicBidding;
}
