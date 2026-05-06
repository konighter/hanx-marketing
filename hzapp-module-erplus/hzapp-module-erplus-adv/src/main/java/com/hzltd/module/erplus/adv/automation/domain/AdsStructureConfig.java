package com.hzltd.module.erplus.adv.automation.domain;

import lombok.Data;
import java.util.List;

/**
 * 广告结构与命名配置
 */
@Data
public class AdsStructureConfig {

    /**
     * 广告活动命名模版
     * 变量: {optimizer}, {productName}, {sku}, {campaignType}, {matchType}
     * 示例: {optimizer}_{productName}_{sku}_{campaignType}_{matchType}
     */
    private String campaignNamingTemplate = "{optimizer}_{productName}_{sku}_{campaignType}_{matchType}";

    /**
     * 广告组命名模版
     * 变量: {asin}, {productName}, {date}, {campaignType}, {matchType}
     * 示例: {asin}_{productName}_{date}_{campaignType}_{matchType}
     */
    private String adGroupNamingTemplate = "{asin}_{productName}_{date}_{campaignType}_{matchType}";

    /**
     * 优化师姓名 (用于命名变量 {optimizer})
     */
    private String optimizerName;

    /**
     * 产品通称 (用于命名变量 {productName})
     */
    private String productName;

    /**
     * 转移词匹配类型 (EXACT, PHRASE, BROAD)
     */
    private List<String> promotionMatchTypes;

    /**
     * 转移词初始出价系数 (相对于目标 CPC)
     * 例如: EXACT -> 1.0, PHRASE -> 0.8
     */
    private java.util.Map<String, Double> bidFactors;

    /**
     * 是否在原广告活动中否定转移词
     */
    private Boolean negateInSource = true;

    /**
     * 平台特有配置 (如 Amazon 的分位置出价策略)
     */
    private java.util.Map<String, Object> platformConfig;

    /**
     * 默认竞价策略 (Down Only, Up and Down, Fixed)
     */
    private String biddingStrategy = "LEGACY_FOR_SALES"; // Amazon Down Only

    /**
     * 分组策略
     * BY_SKU_ONLY: 所有词放在一个 SKU 维度的活动中
     * BY_THEME: 按词根/主题自动分组
     * ONE_CAMPAIGN_PER_KEYWORD: 每个词独立一个活动 (极端隔离)
     */
    private String groupingStrategy = "BY_THEME";

    /**
     * 是否开启高绩效隔离
     * 开启后，ROAS 超过阈值的词会触发独立 Campaign 创建
     */
    private Boolean isolationEnabled = true;

    /**
     * 结构角色配置 (定义需要创建的广告活动角色及其参数)
     */
    private List<AdsCampaignRoleConfig> roles;

    @Data
    public static class AdsCampaignRoleConfig {
        /**
         * 角色: SOURCE, SINK_SHARED, SINK_ISOLATED
         */
        private String role;
        /**
         * 广告类型: sponsoredProducts, sponsoredBrands, etc.
         */
        private String campaignType = "sponsoredProducts";
        /**
         * 投放类型: auto, manual
         */
        private String targetingType;
        /**
         * 匹配类型 (针对 Manual): EXACT, PHRASE, BROAD
         */
        private String matchType;
        /**
         * 命名模板 (覆盖全局设置)
         */
        private String nameTemplate;
        /**
         * 初始预算
         */
        private java.math.BigDecimal initialBudget;
        /**
         * 平台特有参数 (方案 A: Map 存储)
         */
        private java.util.Map<String, Object> platformConfig;
    }

}
