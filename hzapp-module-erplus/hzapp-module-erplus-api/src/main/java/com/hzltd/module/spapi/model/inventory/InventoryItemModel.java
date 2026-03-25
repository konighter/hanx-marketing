package com.hzltd.module.spapi.model.inventory;


import lombok.Data;

@Data
public class InventoryItemModel {

    private String sellerSku;

    private String platformProductId;

    private Integer quantity;

    private Integer preOwner;
}
