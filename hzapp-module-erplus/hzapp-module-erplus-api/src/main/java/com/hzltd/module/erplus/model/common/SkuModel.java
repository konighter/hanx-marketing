package com.hzltd.module.erplus.model.common;

import com.hzltd.module.erplus.model.common.IdentifierCodeModel;
import com.hzltd.module.erplus.model.common.InventoryModel;
import com.hzltd.module.erplus.model.common.PriceModel;
import com.hzltd.module.erplus.model.common.SkuAttribute;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class SkuModel {

    /**
     * 平台的SKUID, AMZ是ASIN
     */
    private String skuId;

    private String sellerSku;

    private String externalSkuId;

    private List<SkuAttribute> salesAttributes;

    private List<InventoryModel> inventory;

    private PriceModel price;

    private IdentifierCodeModel identifierCode;

    private Image image;
}
