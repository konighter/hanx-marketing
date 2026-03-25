package com.hzltd.module.amz.controller.admin.vo;

import com.hzltd.module.amz.api.adv.model.sp.AdsSpCampaign;
import com.hzltd.module.amz.api.adv.model.sp.AdsSpExtendedData;
import com.hzltd.module.amz.api.adv.model.sp.AdsSpNegativeKeyword;
import com.hzltd.module.amz.api.adv.model.sp.AdsSpOptimizationRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 亚马逊广告计划配置 VO
 *
 * 存储在 AdsCampaignDO.extData.platformConfig 中（JSON 格式）
 * 同时负责与 Amazon SP API 模型（AdsSpCampaign.DynamicBidding）之间的双向映射
 *
 * <pre>
 * extData 结构示例:
 * {
 *   "platformConfig": {
 *     "bidding": {
 *       "strategy": "LEGACY_FOR_SALES",
 *       "adjustments": [
 *         { "placement": "PLACEMENT_TOP", "percentage": 50 },
 *         { "placement": "PLACEMENT_PRODUCT_PAGE", "percentage": 30 }
 *       ],
 *       "rules": { "targetRoas": 2.5 }
 *     },
 *     "negativeKeywords": [
 *       { "keywordText": "cheap", "matchType": "NEGATIVE_EXACT" }
 *     ]
 *   }
 * }
 * </pre>
 */
@Schema(description = "管理后台 - 亚马逊广告计划配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonCampaignConfigVO {


    private String profileId;

    @Schema(description = "竞价配置")
    private AdsSpCampaign.DynamicBidding dynamicBidding;

    /** 扩展数据 (投放状态、创建/更新时间等) */
    private AdsSpExtendedData extendedData;

    /** 非亚马逊广告设置 */
    private AdsSpCampaign.OffAmazonSettings offAmazonSettings;

    /** 广告组合 */
    private String portfolioId;

    /** 全球广告活动 ID */
    private String globalCampaignId;

    /** 标签列表 (Map 格式) */
    private Map<String, String> tags;

    @Schema(description = "否定关键词列表")
    private List<AdsSpNegativeKeyword> negativeKeywords;

    @Schema(description = "优化规则列表")
    private List<AdsSpOptimizationRule> optimizationRules;

    // ==================== 竞价相关 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bidding {
        /**
         * 竞价策略（本地值 → Amazon API 值）:
         *   LEGACY_FOR_SALES → LEGACY_FOR_SALES (动态竞价-只降低)
         *   AUTO_OPTIMIZED   → AUTO_FOR_SALES   (动态竞价-提高和降低)
         *   MANUAL           → MANUAL           (固定竞价)
         *   RULE_BASED       → RULE_BASED_BIDDING (基于规则的竞价)
         */
        @Schema(description = "竞价策略", example = "LEGACY_FOR_SALES")
        private String strategy;

        @Schema(description = "分位置竞价调整列表")
        private List<Adjustment> adjustments;

        @Schema(description = "竞价规则（仅 RULE_BASED 策略时生效）")
        private Rule rules;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adjustment {
        /**
         * 位置类型:
         *   PLACEMENT_TOP            → Amazon: PLACEMENT_TOP
         *   PLACEMENT_PRODUCT_PAGE   → Amazon: PLACEMENT_PRODUCT_PAGE
         *   PLACEMENT_REST_OF_SEARCH → Amazon: PLACEMENT_REST_OF_SEARCH (本地字段, API 中无此值)
         */
        @Schema(description = "位置类型", example = "PLACEMENT_TOP")
        private String placement;

        @Schema(description = "调整百分比 (0~900)", example = "50")
        private Integer percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        @Schema(description = "目标 ROAS", example = "2.5")
        private BigDecimal targetRoas;
    }

    // ==================== 策略枚举映射 ====================

    /**
     * 本地策略值与 Amazon API 策略值的映射
     */
    private static final Map<String, String> LOCAL_TO_AMAZON_STRATEGY = Map.of(
            "LEGACY_FOR_SALES", "LEGACY_FOR_SALES",
            "AUTO_OPTIMIZED", "AUTO_FOR_SALES",
            "MANUAL", "MANUAL",
            "RULE_BASED", "RULE_BASED_BIDDING"
    );

    private static final Map<String, String> AMAZON_TO_LOCAL_STRATEGY = Map.of(
            "LEGACY_FOR_SALES", "LEGACY_FOR_SALES",
            "AUTO_FOR_SALES", "AUTO_OPTIMIZED",
            "MANUAL", "MANUAL",
            "RULE_BASED_BIDDING", "RULE_BASED"
    );

    // ==================== 映射方法 ====================

    /**
     * 从 Amazon SP Campaign API 模型创建本地配置
     * 用于数据同步（平台 → 本地）
     */
    public static AmazonCampaignConfigVO fromSpCampaign(AdsSpCampaign spCampaign) {
        if (spCampaign == null) {
            return null;
        }
        return AmazonCampaignConfigVO.builder()
                .dynamicBidding(spCampaign.getDynamicBidding())
                .extendedData(spCampaign.getExtendedData())
                .offAmazonSettings(spCampaign.getOffAmazonSettings())
                .globalCampaignId(spCampaign.getGlobalCampaignId())
                .tags(spCampaign.getTags())
                .portfolioId(spCampaign.getPortfolioId())
                .build();
    }

    /**
     * 将本地配置转换为 Amazon SP Campaign DynamicBidding
     * 用于数据同步（本地 → 平台）
     */
    public AdsSpCampaign.DynamicBidding toSpDynamicBidding() {

        return dynamicBidding;
    }
}
