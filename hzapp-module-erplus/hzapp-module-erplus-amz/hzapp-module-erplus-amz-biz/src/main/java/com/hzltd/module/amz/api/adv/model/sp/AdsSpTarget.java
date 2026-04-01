package com.hzltd.module.amz.api.adv.model.sp;

import com.hzltd.module.adv.model.AdsTargetModel;
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

    public AdsTargetModel toVO() {
        StringBuilder sb = new StringBuilder();
        if (expression != null) {
            for (Expression e : expression) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(e.getType()).append("=").append(e.getValue());
            }
        }
        return AdsTargetModel.builder()
                .externalId(this.getTargetId())
                .adEntityId(this.getAdGroupId())
                .matchValue(sb.length() > 0 ? sb.toString() : "Targeting")
                .status(this.getState())
                .bid(this.getBid())
                .build();
    }

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
