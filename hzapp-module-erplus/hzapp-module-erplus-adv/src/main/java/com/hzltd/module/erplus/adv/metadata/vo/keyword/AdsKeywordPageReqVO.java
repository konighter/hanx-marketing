package com.hzltd.module.erplus.adv.metadata.vo.keyword;

import com.hzltd.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Schema(description = "管理后台 - 广告关键词分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdsKeywordPageReqVO extends PageParam {

    @Schema(description = "广告账户编号", example = "1")
    private Long accountId;

    @Schema(description = "广告组编号集合", example = "20,21")
    private Collection<Long> adGroupIds;

    @Schema(description = "广告计划编号集合", example = "10,11")
    private Collection<Long> campaignIds;

    @Schema(description = "平台原始 Keyword ID", example = "K123")
    private String externalId;

    @Schema(description = "关键词文本", example = "laptop charger")
    private String keywordText;

    @Schema(description = "匹配类型", example = "EXACT")
    private String matchType;

    @Schema(description = "统一状态", example = "ENABLED")
    private String status;

}
