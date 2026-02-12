package com.hzltd.module.erplus.amzadv.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 亚马逊广告出价策略 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_adv_bid_strategy")
@KeySequence("erplus_amz_adv_bid_strategy_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzAdvBidStrategyDO extends BaseDO {

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
     * 策略名称
     */
    private String name;

    /**
     * 策略类型
     */
    private String strategyType; // fixed, dynamic, rule_based

    /**
     * 策略配置JSON
     */
    private String config;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 关联的广告活动类型
     */
    private String campaignType; // sponsoredProducts, sponsoredBrands, sponsoredDisplay

    /**
     * 触发条件
     */
    private String triggerConditions;

    /**
     * 调整幅度
     */
    private Double adjustmentPercentage;

    /**
     * 最低出价限制
     */
    private Double minBid;

    /**
     * 最高出价限制
     */
    private Double maxBid;

    /**
     * 执行频率
     */
    private String executionFrequency; // hourly, daily, weekly

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}