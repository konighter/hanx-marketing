package com.hzltd.module.erplus.dal.dataobject.cross;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 订单 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_order")
@KeySequence("erplus_cross_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpCrossOrderDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 店铺ID
     */
    private Integer shopId;
    /**
     * 市场ID
     */
    private String marketId;
    /**
     * 配送类型
     */
    private Integer fulfillType;
    /**
     * 平台订单ID
     */
    private String platformOrderId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 订单金额(分)
     */
    private Integer amount;
    /**
     * Item数量
     */
    private Integer itemCount;
    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 销售渠道
     */
    private String saleChannel;
    /**
     * 是否会员订单
     */
    private Boolean isPrime;
    /**
     * 是否企业订单
     */
    private Boolean isBusiness;


}