package com.hzltd.module.amz.adv.controller.admin.vo;

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

    public java.util.Map<String, Object> toAttributes() {
        java.util.Map<String, Object> attrs = new java.util.HashMap<>();
        if (profileId != null) attrs.put("amz.profile_id", profileId);
        if (extendedData != null) attrs.put("amz.extended_data", extendedData);
        if (globalAdGroupId != null) attrs.put("amz.global_ad_group_id", globalAdGroupId);
        if (targetingType != null) attrs.put("amz.targeting_type", targetingType);
        if (keywordTargetings != null) attrs.put("amz.keyword_targetings", keywordTargetings);
        if (productTargetings != null) attrs.put("amz.product_targetings", productTargetings);
        if (negativeKeywords != null) attrs.put("amz.negative_keywords", negativeKeywords);
        if (negativeTargetings != null) attrs.put("amz.negative_targetings", negativeTargetings);
        return attrs;
    }

    @SuppressWarnings("unchecked")
    public static AmazonAdGroupConfigVO fromAttributes(java.util.Map<String, Object> attributes) {
        if (attributes == null) return null;
        return AmazonAdGroupConfigVO.builder()
                .profileId((String) attributes.get("amz.profile_id"))
                .extendedData(attributes.get("amz.extended_data"))
                .globalAdGroupId((String) attributes.get("amz.global_ad_group_id"))
                .targetingType((String) attributes.get("amz.targeting_type"))
                .keywordTargetings((List<KeywordTargeting>) attributes.get("amz.keyword_targetings"))
                .productTargetings((List<ProductTargeting>) attributes.get("amz.product_targetings"))
                .negativeKeywords((List<com.hzltd.module.amz.api.adv.model.sp.AdsSpNegativeKeyword>) attributes.get("amz.negative_keywords"))
                .negativeTargetings((List<com.hzltd.module.amz.api.adv.model.sp.AdsSpNegativeTarget>) attributes.get("amz.negative_targetings"))
                .build();
    }
}
