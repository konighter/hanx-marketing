package com.hzltd.module.erplus.spapi.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 亚马逊订单变更事件
 *
 * @author antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderChangeEvent implements Serializable {

    /**
     * 亚马逊订单号
     */
    private String platformOrderId;

    /**
     * 卖家 ID
     */
    private String sellerId;

    /**
     * 站点 ID
     */
    private String marketplaceId;

    /**
     * 店铺 ID
     */
    private Integer shopId;

    /**
     * 平台 ID
     */
    private Integer platformId;

    /**
     * 租户 ID
     */
    private Integer tenantId;

}
