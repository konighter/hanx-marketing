package com.hzltd.module.amz.spapi.model;

import com.hzltd.module.erplus.spapi.model.product.ProductModel;
import lombok.Data;
import software.amazon.spapi.models.listings.items.v2021_08_01.Item;

@Data
public class AmzProductModel extends ProductModel {

    private Item item;
}
