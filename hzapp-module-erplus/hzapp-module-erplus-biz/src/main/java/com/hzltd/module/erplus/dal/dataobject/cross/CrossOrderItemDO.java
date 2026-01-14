package com.hzltd.module.erplus.dal.dataobject.cross;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 订单项 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_order_item")
@KeySequence("erplus_cross_order_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossOrderItemDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 平台订单ID
     */
    private String platformOrderId;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 平台产品code
     */
    private String platformProductCode;
    /**
     * 本地SKU
     */
    private String sellerSkuCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 数量
     */
    private Integer itemCount;
    /**
     * 币种
     */
    private String currency;
    /**
     * 单价
     */
    private Integer amount;
    /**
     * Item价格(单价*数量)
     */
    private Integer itemPrice;
    /**
     * 税费
     */
    private Integer itemTax;
    /**
     * 总价
     */
    private Integer totalAmount;
    /**
     * 运费
     */
    private Integer shipFee;
    /**
     * 运费税费
     */
    private Integer shipFeeTax;
    /**
     * 运费折扣
     */
    private Integer shipFeeDiscount;
    /**
     * 运费折扣税费
     */
    private Integer shipFeeDiscountTax;
    /**
     * 优惠折扣
     */
    private Integer promoDiscount;
    /**
     * 优惠税费
     */
    private Integer promoDiscountTax;
    /**
     * cod费用
     */
    private Integer codFee;
    /**
     * cod折扣
     */
    private Integer codFeeDiscount;
    /**
     * 积分
     */
    private Integer pointsNum;
    /**
     * 积分抵扣金额
     */
    private Integer pointsAsMoney;
    /**
     * 预估佣金(商家)
     */
    private Integer estimatedReferralFee;
    /**
     * 佣金(商家)
     */
    private Integer actualReferralFee;
    /**
     * 预估平台配送费(商家)
     */
    private Integer estimatedFulfillFee;
    /**
     * 平台配送费(商家)
     */
    private Integer actualFulfillFee;

     /**
     * 预估总费用(包含佣金、配送费、税费等)
     */
    private Integer estimatedTotalFee;
    /**
     * 总费用(包含佣金、配送费、税费等)
     */
    private Integer actualTotalFee;


}