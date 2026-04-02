package com.hzltd.module.erplus.spapi.model.common;

import lombok.Data;

@Data
public class InventoryModel {

    private String platformProductCode;

    private String sellerSku;

    private String fnSku;

    private Integer fulfillableQuantity;

    private Integer inboundWorkingQuantity;

    private Integer inboundShippedQuantity;

    private Integer inboundReceivingQuantity;

    // === Reserved Quantity ===
    private Integer reservedQuantity;

    private Integer reservedPendingCustomerOrderQuantity;

    private Integer reservedPendingTransshipmentQuantity;

    private Integer reservedFcProcessingQuantity;

    private Integer researchingQuantity;

    // === Unfulfillable Quantity ===
    private Integer unfulfillableQuantity;

    /**
     * The time when the inventory data was last updated, in epoch seconds.
     */
    private Long lastUpdateTime;

}
