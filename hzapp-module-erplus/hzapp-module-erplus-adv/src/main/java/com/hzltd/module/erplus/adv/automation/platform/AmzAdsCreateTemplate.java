package com.hzltd.module.erplus.adv.automation.platform;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Amazon 广告创建特有配置模版
 * 对应方案 A 中的平台特有参数
 */
@Data
public class AmzAdsCreateTemplate {

    /**
     * 动态出价策略
     * DYNAMIC_BIDDING_DOWN_ONLY: 只降低
     * DYNAMIC_BIDDING_UP_AND_DOWN: 提高或降低
     * FIXED_BIDDING: 固定出价
     */
    private String biddingStrategy = "DYNAMIC_BIDDING_DOWN_ONLY";

    /**
     * 分位置加价配置
     * PLACEMENT_TOP_OF_SEARCH: 搜索结果顶部 (首页)
     * PLACEMENT_PRODUCT_PAGES: 商品页面
     */
    private List<PlacementAdjustment> placementAdjustments;

    /**
     * 分时调价策略 ID (可选，关联系统内的分时模板)
     */
    private Long daypartingTemplateId;

    /**
     * 扩展参数
     */
    private Map<String, Object> extra;

    @Data
    public static class PlacementAdjustment {
        private String placement; // PLACEMENT_TOP_OF_SEARCH, PLACEMENT_PRODUCT_PAGES
        private Integer percentage; // 0-900
    }
}
