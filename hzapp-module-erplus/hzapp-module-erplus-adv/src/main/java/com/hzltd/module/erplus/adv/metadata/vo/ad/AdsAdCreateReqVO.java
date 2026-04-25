package com.hzltd.module.erplus.adv.metadata.vo.ad;

import com.hzltd.module.erplus.adv.model.AdsAdCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 广告创建请求 VO")
@Data
public class AdsAdCreateReqVO {

    @Schema(description = "广告组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long adGroupId;

    @Schema(description = "广告计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long campaignId;

    @Schema(description = "创建广告数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private List<AdsAdCreateRequest> createRequests;

}
