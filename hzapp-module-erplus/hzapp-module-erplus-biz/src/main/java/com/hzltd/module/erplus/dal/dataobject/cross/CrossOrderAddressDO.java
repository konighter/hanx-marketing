package com.hzltd.module.erplus.dal.dataobject.cross;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 跨境订单收货地址 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_order_address")
@KeySequence("erplus_cross_order_address_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossOrderAddressDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 平台订单ID
     */
    private String platformOrderId;

    /**
     * 买家姓名
     */
    private String name;
    /**
     * 买家email
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 国家
     */
    private String country;
    /**
     * 城市
     */
    private String city;
    /**
     * 行政区
     */
    private String stateOrRegion;
    /**
     * 邮编
     */
    private String postalCode;
    /**
     * 详细地址
     */
    private String address;

}
