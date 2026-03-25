package com.hzltd.module.spapi.model.product;

import java.util.HashMap;

public class MultiMarketProductModel extends HashMap<String, ProductModel> {

    public ProductModel getOrCreate(String market) {
        if (this.containsKey(market)) {
            return this.get(market);
        } else {
            ProductModel product = new ProductModel();
            this.put(market, product);
            return product;
        }
    }
}
