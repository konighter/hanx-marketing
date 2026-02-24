package com.hzltd.module.erplus.adv.metadata.vo.campaign;

import com.hzltd.module.erplus.adv.adapter.amazon.model.sp.AdsSpCampaign;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 亚马逊广告计划配置 VO
 *
 * 存储在 AdsCampaignDO.extData.amazonConfig 中（JSON 格式）
 * 同时负责与 Amazon SP API 模型（AdsSpCampaign.DynamicBidding）之间的双向映射
 *
 * <pre>
 * extData 结构示例:
 * {
 *   "amazonConfig": {
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

    @Schema(description = "竞价配置")
    private Bidding bidding;

    @Schema(description = "否定关键词列表")
    private List<NegativeKeyword> negativeKeywords;

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

    // ==================== 否定关键词 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NegativeKeyword {
        @Schema(description = "关键词文本")
        private String keywordText;

        @Schema(description = "匹配类型: NEGATIVE_EXACT / NEGATIVE_PHRASE", example = "NEGATIVE_EXACT")
        private String matchType;
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
        AmazonCampaignConfigVO config = new AmazonCampaignConfigVO();

        AdsSpCampaign.DynamicBidding dynamicBidding = spCampaign.getDynamicBidding();
        if (dynamicBidding != null) {
            Bidding bidding = new Bidding();

            // 策略映射
            bidding.setStrategy(AMAZON_TO_LOCAL_STRATEGY.getOrDefault(
                    dynamicBidding.getStrategy(), dynamicBidding.getStrategy()));

            // 分位置竞价调整
            if (dynamicBidding.getPlacementBidding() != null) {
                bidding.setAdjustments(dynamicBidding.getPlacementBidding().stream()
                        .map(pb -> Adjustment.builder()
                                .placement(pb.getPlacement())
                                .percentage(pb.getPercentage())
                                .build())
                        .collect(Collectors.toList()));
            }

            // 竞价规则（RULE_BASED_BIDDING 时提取 targetRoas）
            if (dynamicBidding.getRules() != null && !dynamicBidding.getRules().isEmpty()) {
                dynamicBidding.getRules().stream()
                        .filter(r -> "BID".equals(r.getRuleType()))
                        .findFirst()
                        .ifPresent(r -> bidding.setRules(Rule.builder().targetRoas(r.getValue()).build()));
            }

            config.setBidding(bidding);
        }
        return config;
    }

    /**
     * 将本地配置转换为 Amazon SP Campaign DynamicBidding
     * 用于数据同步（本地 → 平台）
     */
    public AdsSpCampaign.DynamicBidding toSpDynamicBidding() {
        if (this.bidding == null) {
            return null;
        }

        AdsSpCampaign.DynamicBidding dynamicBidding = new AdsSpCampaign.DynamicBidding();

        // 策略映射
        dynamicBidding.setStrategy(LOCAL_TO_AMAZON_STRATEGY.getOrDefault(
                this.bidding.getStrategy(), this.bidding.getStrategy()));

        // 分位置竞价调整
        if (this.bidding.getAdjustments() != null) {
            List<AdsSpCampaign.PlacementBidding> placementBiddings = this.bidding.getAdjustments().stream()
                    .filter(adj -> !"PLACEMENT_REST_OF_SEARCH".equals(adj.getPlacement())) // API 不支持此位置
                    .map(adj -> AdsSpCampaign.PlacementBidding.builder()
                            .placement(adj.getPlacement())
                            .percentage(adj.getPercentage())
                            .build())
                    .collect(Collectors.toList());
            dynamicBidding.setPlacementBidding(placementBiddings);
        }

        // 竞价规则（RULE_BASED 时构建 BID 规则）
        if (this.bidding.getRules() != null && this.bidding.getRules().getTargetRoas() != null) {
            dynamicBidding.setRules(List.of(
                    AdsSpCampaign.BiddingRule.builder()
                            .ruleType("BID")
                            .value(this.bidding.getRules().getTargetRoas())
                            .build()
            ));
        }

        return dynamicBidding;
    }
}
