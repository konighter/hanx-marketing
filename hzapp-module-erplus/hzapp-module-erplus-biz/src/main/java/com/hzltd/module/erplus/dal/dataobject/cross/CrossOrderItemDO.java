package com.hzltd.module.erplus.dal.dataobject.crossorderitem;

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
     * 总价
     */
    private Integer totalAmount;
    /**
     * 运费
     */
    private Integer shipFee;
    /**
     * cod费用
     */
    private Integer codFee;
    /**
     * 积分
     */
    private Integer pointsNum;
    /**
     * 积分抵扣金额
     */
    private Integer pointsMoneyValue;


}