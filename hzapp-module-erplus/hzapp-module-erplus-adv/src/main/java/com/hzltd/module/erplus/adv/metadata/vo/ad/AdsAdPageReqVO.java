package com.hzltd.module.erplus.adv.metadata.vo.ad;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 广告分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsAdPageReqVO extends PageParam {

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "广告组编号", example = "20")
    private Long adGroupId;

    @Schema(description = "平台原始 Ad ID", example = "A123")
    private String externalId;

    @Schema(description = "广告名称", example = "Summer Banner")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

}
