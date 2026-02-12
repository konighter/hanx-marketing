package com.hzltd.module.erplus.controller.admin.amzadv.vo;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 亚马逊广告关键词分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmzAdvKeywordPageReqVO extends PageParam {

    @Schema(description = "店铺ID", example = "123")
    private String shopId;

    @Schema(description = "广告活动ID", example = "campaign123")
    private String campaignId;

    @Schema(description = "广告组ID", example = "adgroup123")
    private String adGroupId;

    @Schema(description = "关键词文本", example = "wireless headphones")
    private String keywordText;

    @Schema(description = "关键词匹配类型", example = "exact")
    private String matchType;

    @Schema(description = "关键词状态", example = "enabled")
    private String state;
}