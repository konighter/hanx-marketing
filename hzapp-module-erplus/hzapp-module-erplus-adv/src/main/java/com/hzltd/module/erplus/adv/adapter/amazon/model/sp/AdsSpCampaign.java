package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.metadata.vo.AdsCampaignVO;
import com.hzltd.module.erplus.adv.metadata.vo.campaign.AmazonCampaignConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Amazon SP Campaign API v3 响应模型
 * 对应 application/vnd.spCampaign.v3+json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpCampaign {

    /** 是否自动管理广告活动 */
    private Boolean autoManageCampaign;

    /** 预算信息 */
    private Budget budget;

    /** 广告活动唯一标识 */
    private String campaignId;

    /** 动态调价设置 */
    private DynamicBidding dynamicBidding;

    /** 结束日期 (YYYY-MM-DD) */
    private String endDate;

    /** 扩展数据 (投放状态、创建/更新时间等) */
    private AdsSpExtendedData extendedData;

    /** 全球广告活动 ID */
    private String globalCampaignId;

    /** 市场预算分配方式 */
    private String marketplaceBudgetAllocation;

    /** 广告活动名称 */
    private String name;

    /** 非亚马逊广告设置 */
    private OffAmazonSettings offAmazonSettings;

    /** Portfolio ID */
    private String portfolioId;

    /** 站点限制 */
    private List<String> siteRestrictions;

    /** 开始日期 (YYYY-MM-DD) */
    private String startDate;

    /** 广告活动状态: ENABLED, PAUSED, ARCHIVED */
    private String state;

    /** 标签列表 (Map 格式) */
    private Map<String, String> tags;

    /** 定向类型: AUTO, MANUAL */
    private String targetingType;

    // ==================== 嵌套类 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Budget {
        /** 预算金额 */
        private BigDecimal budget;

        /** 预算类型: DAILY */
        private String budgetType;

        /** 有效预算金额 */
        private BigDecimal effectiveBudget;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicBidding {
        /** 展示位置出价调整 */
        private List<PlacementBidding> placementBidding;

        /** 受众群体出价调整 */
        private List<ShopperCohortBidding> shopperCohortBidding;

        /** 竞价策略: LEGACY_FOR_SALES, AUTO_FOR_SALES, MANUAL, RULE_BASED_BIDDING */
        private String strategy;

        /** 竞价规则列表（仅 RULE_BASED_BIDDING 策略时有效） */
        private List<BiddingRule> rules;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BiddingRule {
        /** 规则类型: BID */
        private String ruleType;

        /** 规则值（如目标 ROAS） */
        private BigDecimal value;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacementBidding {
        /** 调整百分比 */
        private Integer percentage;

        /** 展示位置: PLACEMENT_PRODUCT_PAGE, PLACEMENT_TOP_OF_SEARCH 等 */
        private String placement;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopperCohortBidding {
        /** 受众细分列表 */
        private List<AudienceSegment> audienceSegments;

        /** 调整百分比 */
        private Integer percentage;

        /** 受众群体类型: AUDIENCE_SEGMENT */
        private String shopperCohortType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AudienceSegment {
        /** 受众 ID */
        private String audienceId;

        /** 受众细分类型: BEHAVIOR_DYNAMIC */
        private String audienceSegmentType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffAmazonSettings {
        /** 非亚马逊预算控制策略: MAXIMIZE_REACH */
        private String offAmazonBudgetControlStrategy;
    }

    public AdsCampaignVO toVO() {
        // 将 Amazon 动态竞价配置映射为本地 AmazonCampaignConfigVO
        AmazonCampaignConfigVO amazonConfig = AmazonCampaignConfigVO.fromSpCampaign(this);
        // 构造 extData: { "amazonConfig": { ... } }
        Map<String, Object> extData = new java.util.HashMap<>();
        extData.put("amazonConfig", amazonConfig);

        return AdsCampaignVO.builder()
                .externalId(this.getCampaignId())
                .name(this.getName())
                .campaignType("SP") // Amazon SP API
                .status(this.getState())
                .dailyBudget(this.getBudget() != null ? this.getBudget().getBudget() : null)
                .startDate(StringUtils.isNotEmpty(this.getStartDate()) ? LocalDate.parse(this.getStartDate()) : null)
                .endDate(StringUtils.isNotEmpty(this.getEndDate()) ? LocalDate.parse(this.getEndDate()) : null)
                .biddingStrategy(amazonConfig.getBidding() != null ? amazonConfig.getBidding().getStrategy() : null)
                .extData(JsonUtils.toJsonString(extData))
                .build();
    }
}
