package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.system.model.ShopModel;

import java.util.List;

public interface SystemShopService {

    /**
     * @param shopId 本地ShopId
     * @return
     */
    ShopModel getShopById(Long shopId);

    default ShopModel getShopById(Integer shopId) {
        return this.getShopById(shopId.longValue());
    }

    /**
     * @param shopId 跨境平台的shopId
     * @return
     */
    ShopModel getShopByExtraId(String shopId);

    /**
     * @param shopId 跨境平台的shopId
     * @return
     */
    List<String> getShopMarketplace(String shopId);


    ShopModel createOrLoadShop(ShopModel shop);

    /**
     * 根据卖家ID和国家代码查找店铺
     * @param sellerId 卖家ID
     * @param marketplaceId 站点ID
     * @return 店铺信息
     */
    ShopModel getShopBySellerIdAndMarketplaceId(String sellerId, String marketplaceId);

    /**
     * 根据卖家ID和国家代码查找店铺, 非租户环境下
     * @param sellerId 卖家ID
     * @param marketplaceId 站点ID
     * @return 店铺信息
     */
    ShopModel getShopBySellerIdAndMarketplaceIdWithoutTenant(String sellerId, String marketplaceId);


    ShopModel getShopByIdWithoutTenant(Integer shopId);

    List<ShopModel> getAvailableShops();
}
