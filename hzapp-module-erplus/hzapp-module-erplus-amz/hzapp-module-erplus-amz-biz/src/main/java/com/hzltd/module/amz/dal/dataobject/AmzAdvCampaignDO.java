package com.hzltd.module.amz.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 亚马逊广告活动 DO
 *
 * @author 翰展科技
 */
@TableName(value = "erplus_amz_adv_campaign", autoResultMap = true)
@KeySequence("erplus_amz_adv_campaign_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzAdvCampaignDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 广告活动ID (Amazon侧)
     */
    private String campaignId;

    /**
     * 同步状态
     */
    private Integer syncStatus; // 0-未同步，1-同步中，2-已同步，3-同步失败

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;

    /**
     * 同步错误信息
     */
    private String syncErrorMsg;

    /**
     * 广告活动名称
     */
    private String name;

    /**
     * 广告活动状态
     */
    private String state; // enabled, paused, archived

    /**
     * 广告类型 (sponsoredProducts, sponsoredBrands, sponsoredDisplay)
     */
    private String campaignType;

    /**
     * 每日预算
     */
    private Double dailyBudget;

    /**
     * 出价策略
     */
    private String biddingStrategy; // legacyForSales, autoForSales, manual

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;

    /**
     * 目标市场
     */
    private String targetingType; // auto, manual

    /**
     * 广告系列类型
     */
    private String campaignSubType; // stores, headline, video

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private String tags;

    /**
     * 竞价和位置出价策略
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private DynamicBidding dynamicBidding;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DynamicBidding {
        /**
         * 竞价策略: LEGACY_FOR_SALES, AUTO_FOR_SALES, MANUAL, RULE_BASED_BIDDING
         */
        private String strategy;

        /**
         * 展示位置出价调整
         */
        private List<PlacementBidding> placementBidding;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacementBidding {
        /**
         * 调整百分比 (如 50 表示 +50%)
         */
        private Integer percentage;

        /**
         * 展示位置: PLACEMENT_PRODUCT_PAGE, PLACEMENT_TOP_OF_SEARCH 等
         */
        private String placement;
    }
}