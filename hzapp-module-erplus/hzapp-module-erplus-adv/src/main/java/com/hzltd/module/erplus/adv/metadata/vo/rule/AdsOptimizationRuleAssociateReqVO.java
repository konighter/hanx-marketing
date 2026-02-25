package com.hzltd.module.erplus.adv.metadata.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 广告优化规则关联 Request VO")
@Data
public class AdsOptimizationRuleAssociateReqVO {

    @Schema(description = "平台原始规则ID列表")
    private List<String> optimizationRuleIds;

}
