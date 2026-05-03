package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.hzltd.module.amz.adv.client.sp.model.SponsoredProductsCreateCampaignNegativeTargetingClause;

@Schema(description = "管理后台 - 亚马逊广告活动否定定向批量创建 Request VO")
@Data
public class AmzAdvV3CampaignNegativeTargetingBatchCreateReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告活动ID不能为空")
    private Long campaignId;

    @Schema(description = "否定定向列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "否定定向列表不能为空")
    private List<SponsoredProductsCreateCampaignNegativeTargetingClause> clauses;
}
