package com.hzltd.module.erplus.service.stock.v2.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ERP 库存变动事件
 * 用于主业务模块（如销售、采购）触发库存更新
 *
 * @author 翰展科技
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpStockUpdateEvent {

    /**
     * 单据大类 (10:入, 20:出, 30:调, 40:平)
     */
    private Integer type;

    /**
     * 来源类型 (WH, VENDOR, CUST, VIRTUAL)
     */
    private String fromType;
    /**
     * 来源ID
     */
    private Long fromId;

    /**
     * 去向类型 (WH, VENDOR, CUST, VIRTUAL)
     */
    private String toType;
    /**
     * 去向ID
     */
    private Long toId;

    /**
     * 业务关联单据类型 (SO, PO, CHECK, etc.)
     */
    private String refType;
    /**
     * 关联单据号
     */
    private String refCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 变动明细
     */
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        /**
         * SKU
         */
        private String sellerSku;
        /**
         * 变动数量 (正数表示增加，负数表示减少)
         */
        private Integer qty;
    }

}
