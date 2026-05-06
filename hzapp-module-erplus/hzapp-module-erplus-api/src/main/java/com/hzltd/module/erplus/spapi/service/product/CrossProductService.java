package com.hzltd.module.erplus.spapi.service.product;

import com.hzltd.module.erplus.spapi.model.product.ProductModel;

import java.util.List;

public interface CrossProductService {

    /**
     * 根据本地ID获取产品信息
     * @param productId
     * @return
     */
    ProductModel getProductModel(Long productId);

    /**
     * 根据店铺ID和sellerSku获取产品信息
     * @param shopId
     * @param sellerSku
     * @return
     */
    List<ProductModel> getProductModel(Long shopId, List<String> sellerSku);

    /**
     * 根据店铺ID和平台产品code获取产品信息
     * @param platformProductCode
     * @param shopId
     * @return
     */
    List<ProductModel> getProductModel(List<String> platformProductCode, Long shopId);

}
