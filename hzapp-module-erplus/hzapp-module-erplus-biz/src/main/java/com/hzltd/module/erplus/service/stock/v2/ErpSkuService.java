package com.hzltd.module.erplus.service.stock.v2;

/**
 * SKU 解析 Service 接口
 * 用于在不同模块间转换产品标识符（如 Product ID -> Seller SKU）
 *
 * @author 翰展科技
 */
public interface ErpSkuService {

    /**
     * 根据产品 ID 获得对应的 Seller SKU
     *
     * @param productId 产品 ID
     * @return Seller SKU
     */
    String getSkuByProductId(Long productId);

}
