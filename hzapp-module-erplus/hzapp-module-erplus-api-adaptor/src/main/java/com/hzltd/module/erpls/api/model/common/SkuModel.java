package com.hzltd.module.erpls.api.model.common;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class SkuModel {

    private String sellerSku;

    private String externalSkuId;

    private List<SkuAttribute> salesAttributes;

    private List<InventoryModel> inventory;

    private PriceModel price;

    private IdentifierCodeModel identifierCode;

    private Image image;
}
