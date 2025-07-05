package com.hzltd.module.erpls.api.model.product;

import com.hzltd.module.erpls.api.model.common.SkuModel;

import java.util.List;

public class CreateProductResponse {

    private String productId;

    private List<SkuModel> skus;

    private List<String> warnings;

}
