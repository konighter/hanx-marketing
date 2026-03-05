package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Amazon SP Target（商品/品类定向）API 响应模型
 * 端点：POST /sp/targets/list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpTarget {

    /** 目标唯一标识 */
    private String targetId;

    /** 所属广告组 ID */
    private String adGroupId;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 出价 */
    private BigDecimal bid;

    /** 表达式列表 */
    private List<Expression> expression;

    /** 解析后的表达式 (可读形式) */
    private List<Expression> resolvedExpression;

    /** 状态 */
    private String state;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expression {
        /** 表达式类型: asinSameAs / asinCategorySameAs / asinBrandSameAs 等 */
        private String type;
        /** 表达式值（ASIN / 品类 ID / 品牌 ID） */
        private String value;
    }
}
