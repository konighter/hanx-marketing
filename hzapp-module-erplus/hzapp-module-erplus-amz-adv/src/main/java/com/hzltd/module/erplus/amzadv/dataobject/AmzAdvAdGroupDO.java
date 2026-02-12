package com.hzltd.module.erplus.amzadv.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 亚马逊广告组 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_amz_adv_ad_group")
@KeySequence("erplus_amz_adv_ad_group_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmzAdvAdGroupDO extends BaseDO {

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
     * 广告组ID (Amazon侧)
     */
    private String adGroupId;

    /**
     * 广告活动ID
     */
    private String campaignId;

    /**
     * 广告组名称
     */
    private String name;

    /**
     * 广告组状态
     */
    private String state; // enabled, paused, archived

    /**
     * 默认出价
     */
    private Double defaultBid;

    /**
     * 最高出价
     */
    private Double maxBid;

    /**
     * 目标设备
     */
    private String placement; // desktop, mobile, other

    /**
     * 商品投放类型
     */
    private String placementType; // detail, homepage, other

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}