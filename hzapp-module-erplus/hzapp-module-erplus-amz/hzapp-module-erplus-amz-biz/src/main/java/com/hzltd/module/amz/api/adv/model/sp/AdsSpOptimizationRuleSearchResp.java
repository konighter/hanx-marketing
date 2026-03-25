package com.hzltd.module.amz.api.adv.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Amazon SP API 优化规则查询响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpOptimizationRuleSearchResp {

    /** 优化规则列表 */
    private List<AdsSpOptimizationRule> optimizationRules;

    /** 下一页令牌 */
    private String nextToken;
}
