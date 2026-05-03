package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.hzltd.module.amz.adv.client.sp.model.SponsoredProductsCreateOrUpdateBiddingStrategy;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 亚马逊广告活动竞价策略更新 Request VO")
@Data
public class AmzAdvV3CampaignBiddingUpdateReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告活动ID不能为空")
    private Long campaignId;

    @Schema(description = "竞价策略", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "竞价策略不能为空")
    private SponsoredProductsCreateOrUpdateBiddingStrategy strategy;

    @Schema(description = "目标ROAS (当策略为 RULE_BASED_BIDDING 时)")
    private BigDecimal targetRoas;
}
