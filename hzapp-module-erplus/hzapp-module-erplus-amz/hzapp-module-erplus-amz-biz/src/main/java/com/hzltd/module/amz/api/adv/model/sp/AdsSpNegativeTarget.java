package com.hzltd.module.amz.api.adv.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Amazon SP 否定定向（商品/品牌）API 响应模型
 * 端点：POST /sp/negativeTargets/list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpNegativeTarget {

    /** 否定定向唯一标识 */
    private String targetId;

    /** 所属广告组 ID */
    private String adGroupId;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 否定表达式列表 */
    private List<Expression> expression;

    /** 状态 */
    private String state;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expression {
        /** 表达式类型: asinSameAs / asinBrandSameAs */
        private String type;
        /** ASIN 或 品牌 ID */
        private String value;
    }
}
