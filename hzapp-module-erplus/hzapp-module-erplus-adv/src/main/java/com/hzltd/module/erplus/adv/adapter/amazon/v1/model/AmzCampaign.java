package com.hzltd.module.erplus.adv.adapter.amazon.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Amazon Ads V1 Campaign 通用模型
 * 对应 Campaign Management V1 API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmzCampaign {

    private String adProduct;
    private String campaignId;
    private String globalCampaignId;
    private String name;
    private String state;
    private String costType;
    private String portfolioId;
    private String salesChannel;
    private String marketplaceScope;
    private String purchaseOrderNumber;
    private String skanAppId;
    private String targetedPGDealId;

    private Boolean autoScaleGlobalCampaign;
    private Boolean isMultiAdGroupsEnabled;
    private Boolean targetsAmazonDeal;

    private String startDateTime;
    private String endDateTime;
    private String creationDateTime;
    private String lastUpdatedDateTime;

    private List<String> countries;
    private List<String> marketplaces;
    private List<String> siteRestrictions;

    private List<Budget> budgets;
    private List<Fee> fees;
    private List<Flight> flights;
    private List<Frequency> frequencies;
    private List<Tag> tags;
    private List<MarketplaceConfiguration> marketplaceConfigurations;

    private Optimizations optimizations;
    private AutoCreationSettings autoCreationSettings;
    private CampaignStatus status;

    // ==================== Budget ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Budget {
        private String budgetType;
        private BudgetValue budgetValue;
        private String recurrenceTimePeriod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BudgetValue {
        private MonetaryBudgetValue monetaryBudgetValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MonetaryBudgetValue {
        private MonetaryBudget monetaryBudget;
        private List<MarketplaceBudgetSetting> marketplaceSettings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MonetaryBudget {
        private String currencyCode;
        private BigDecimal value;
        private BigDecimal ruleValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MarketplaceBudgetSetting {
        private String marketplace;
        private MonetaryBudget monetaryBudget;
    }

    // ==================== Optimizations ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Optimizations {
        private BidSettings bidSettings;
        private BudgetSettings budgetSettings;
        private GoalSettings goalSettings;
        private List<String> primaryInventoryTypes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BidSettings {
        private String bidStrategy;
        private BidAdjustments bidAdjustments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BidAdjustments {
        private List<PlacementBidAdjustment> placementBidAdjustments;
        private List<AudienceBidAdjustment> audienceBidAdjustments;
        private List<CreativeBidAdjustment> creativeBidAdjustments;
        private List<ShopperSegmentBidAdjustment> shopperSegmentBidAdjustments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PlacementBidAdjustment {
        private String placement;
        private Integer percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AudienceBidAdjustment {
        private String audienceId;
        private Integer percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreativeBidAdjustment {
        private String creativeType;
        private Integer percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ShopperSegmentBidAdjustment {
        private String shopperSegment;
        private Integer percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BudgetSettings {
        private String budgetAllocation;
        private String flightBudgetRolloverStrategy;
        private String marketplaceBudgetAllocation;
        private String offAmazonBudgetControlStrategy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GoalSettings {
        private String currencyCode;
        private String goal;
        private String kpi;
        private BigDecimal kpiValue;
    }

    // ==================== Flight ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Flight {
        private String flightId;
        private String name;
        private String startDateTime;
        private String endDateTime;
        private Budget budget;
    }

    // ==================== Other ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fee {
        private String feeType;
        private BigDecimal feeValue;
        private String feeValueType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Frequency {
        private Integer eventMaxCount;
        private String frequencyTargetingSetting;
        private Integer timeCount;
        private String timeUnit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tag {
        private String key;
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AutoCreationSettings {
        private Boolean autoCreateTargets;
        private Boolean autoManageCampaign;
    }

    // ==================== Status ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CampaignStatus {
        private String deliveryStatus;
        private List<String> deliveryReasons;
        private List<MarketplaceDeliveryStatus> marketplaceSettings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MarketplaceDeliveryStatus {
        private String marketplace;
        private String deliveryStatus;
        private List<String> deliveryReasons;
    }

    // ==================== MarketplaceConfiguration ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MarketplaceConfiguration {
        private String campaignId;
        private String marketplace;
        private MarketplaceOverrides overrides;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MarketplaceOverrides {
        private String name;
        private String state;
        private String startDateTime;
        private String endDateTime;
        private Optimizations optimizations;
        private List<Tag> tags;
    }
}
