package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.hzltd.module.amz.adv.client.sp.model.SponsoredProductsCreateKeyword;

@Schema(description = "管理后台 - 亚马逊广告组关键词批量创建 Request VO")
@Data
public class AmzAdvV3KeywordBatchCreateReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    private Long groupId;

    @Schema(description = "关键词列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关键词列表不能为空")
    private List<SponsoredProductsCreateKeyword> keywords;
}
