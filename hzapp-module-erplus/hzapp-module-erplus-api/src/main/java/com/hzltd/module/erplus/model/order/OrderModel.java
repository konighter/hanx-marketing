package com.hzltd.module.erplus.model.order;


import com.hzltd.module.erplus.constant.FulfillTypeEnum;
import com.hzltd.module.erplus.enums.CrossOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderModel {

    /**
     * 市场ID
     */
    private String marketId;
    /**
     * 订单ID
     */
    private String orderId;

    /**
     *  订单状态
     */
    private CrossOrderStatus orderStatus;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 交付类型
     */
    private FulfillTypeEnum fulfillmentType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;


    /**
     * 订单货币
     */
    private String currency;

    /**
     * 订单总金额(单位:分)
     */
    private Integer totalAmount;

    /**
     * 是否为Prime订单
     */
    private Boolean isPrime;

    /**
     * 是否为企业订单
     */
    private Boolean isBusiness;

     /**
      * 销售渠道
      */
    private String salesChannel;

     /**
      * 订单商品列表
      */
    private List<OrderItemModel> orderItems;

}
