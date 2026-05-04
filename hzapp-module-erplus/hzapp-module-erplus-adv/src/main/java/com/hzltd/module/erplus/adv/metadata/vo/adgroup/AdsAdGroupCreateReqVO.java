package com.hzltd.module.erplus.adv.metadata.vo.adgroup;

import com.hzltd.module.erplus.adv.model.AdsAdCreateRequest;
import com.hzltd.module.erplus.adv.model.AdsTargetModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 广告组创建 Request VO")
@Data
public class AdsAdGroupCreateReqVO {

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "计划编号不能为空")
    private Long campaignId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "默认竞价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "默认竞价不能为空")
    private Double defaultBid;

    @Schema(description = "广告列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "广告不能为空")
    private List<AdsAdCreateRequest> ads;

    /**
     * 已合并到attributes 中
     */
    @Deprecated
    @Schema(description = "投放列表", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "投放不能为空")
    private List<AdsTargetModel> targeting;

    @Schema(description = "属性数据")
    private Map<String, Object> attributes;

}
