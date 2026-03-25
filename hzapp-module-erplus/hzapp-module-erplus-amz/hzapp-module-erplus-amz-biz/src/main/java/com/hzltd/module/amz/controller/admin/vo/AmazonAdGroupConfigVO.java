package com.hzltd.module.amz.controller.admin.vo;

import com.hzltd.module.amz.api.adv.model.sp.AdsSpAdGroup;
import com.hzltd.module.amz.api.adv.model.sp.AdsSpNegativeKeyword;
import com.hzltd.module.amz.api.adv.model.sp.AdsSpNegativeTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "管理后台 - 亚马逊广告组配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdGroupConfigVO {

    @Schema(description = "站点的 Profile ID")
    private String profileId;

    @Schema(description = "扩展数据")
    private Object extendedData;

    @Schema(description = "全球广告组 ID")
    private String globalAdGroupId;

    @Schema(description = "定向类型 (KEYWORD / PRODUCT)")
    private String targetingType;

    @Schema(description = "关键词定向列表")
    private List<KeywordTargeting> keywordTargetings;

    @Schema(description = "商品/品类定向列表")
    private List<ProductTargeting> productTargetings;

    @Schema(description = "否定关键词列表")
    private List<AdsSpNegativeKeyword> negativeKeywords;

    @Schema(description = "否定商品/品牌定向列表")
    private List<AdsSpNegativeTarget> negativeTargetings;

    // ==================== 嵌套类 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordTargeting {
        @Schema(description = "外部目标 ID")
        private String targetId;

        @Schema(description = "关键词文本")
        private String keywordText;

        @Schema(description = "匹配类型: EXACT / PHRASE / BROAD", example = "EXACT")
        private String matchType;

        @Schema(description = "出价")
        private java.math.BigDecimal bid;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductTargeting {
        @Schema(description = "外部目标 ID")
        private String targetId;

        @Schema(description = "定向表达式值 (ASIN 或者是 CategoryId)")
        private String expressionValue;

        @Schema(description = "定向类型详情 (如 asinSameAs, category 等，前端可借此分拆)")
        private String expressionType;

        @Schema(description = "出价")
        private java.math.BigDecimal bid;
    }



    // ==================== 构造方法 ====================

    public static AmazonAdGroupConfigVO fromSpAdGroup(AdsSpAdGroup spAdGroup) {
        if (spAdGroup == null) {
            return null;
        }
        return AmazonAdGroupConfigVO.builder()
                .globalAdGroupId(spAdGroup.getGlobalAdGroupId())
                .extendedData(spAdGroup.getExtendedData())
                .build();
    }
}
