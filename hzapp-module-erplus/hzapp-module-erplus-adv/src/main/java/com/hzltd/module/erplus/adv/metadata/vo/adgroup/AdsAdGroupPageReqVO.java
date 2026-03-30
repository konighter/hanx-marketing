package com.hzltd.module.erplus.adv.metadata.vo.adgroup;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Schema(description = "管理后台 - 广告组分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsAdGroupPageReqVO extends PageParam {

    @Schema(description = "店铺编号", example = "1")
    private Long shopId;

    @Schema(description = "广告账户编号(已弃用，请使用 shopId)", example = "1")
    @Deprecated
    private Long accountId;

    @Schema(description = "广告计划编号集合", example = "10,11")
    private Collection<Long> campaignIds;

    @Schema(description = "平台原始 AdGroup ID", example = "G123")
    private String externalId;

    @Schema(description = "广告组名称", example = "Electronics Group")
    private String name;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

}
