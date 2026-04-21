package com.hzltd.module.erplus.system.enums;

/**
 * ERP 库存账单状态枚举
 */
public interface ErpInventoryBillStatusEnum {

    /**
     * 已完成
     */
    Integer COMPLETED = 10;

    /**
     * 待收货 (仅用于调拨单)
     */
    Integer WAIT_RECEIVE = 20;

    /**
     * 已作废
     */
    Integer CANCELLED = 90;

}
