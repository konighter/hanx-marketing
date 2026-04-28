package com.hzltd.module.amz.adv.controller.admin.manager.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 亚马逊广告组关键词批量更新出价 Request VO")
@Data
public class AmzAdvV3KeywordBatchUpdateBidReqVO {
    @Schema(description = "店铺编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "店铺编号不能为空")
    private Long shopId;

    @Schema(description = "广告组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告组ID不能为空")
    private Long groupId;

    @Schema(description = "关键词更新列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关键词更新列表不能为空")
    private List<KeywordBidUpdateItem> items;

    @Data
    public static class KeywordBidUpdateItem {
        @Schema(description = "关键词ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "关键词ID不能为空")
        private String keywordId;

        @Schema(description = "出价", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "出价不能为空")
        private BigDecimal bid;
    }
}
