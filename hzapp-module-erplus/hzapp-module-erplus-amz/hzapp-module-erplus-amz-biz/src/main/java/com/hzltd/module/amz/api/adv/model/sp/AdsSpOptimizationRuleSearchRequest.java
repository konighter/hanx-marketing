package com.hzltd.module.amz.api.adv.model.sp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Amazon SP API 优化规则查询请求
 * POST /sp/rules/optimization/search
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdsSpOptimizationRuleSearchRequest {

    /** 广告活动过滤条件 */
    private AdsSpCommonQueryRequest.StringFilter campaignFilter;

    /** 规则类别过滤条件 */
    private AdsSpCommonQueryRequest.StringFilter ruleCategory;

    /** 规则子类别过滤条件 */
    private AdsSpCommonQueryRequest.StringFilter ruleSubCategory;

    /** 最大结果数 */
    private Integer maxResults;

    /** 下一页令牌 */
    private String nextToken;
}
