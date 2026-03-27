package com.hzltd.module.system.service;

import com.hzltd.module.spapi.model.system.ShopModel;

import java.util.List;

public interface SystemShopService {

    /**
     * @param shopId 本地ShopId
     * @return
     */
    ShopModel getShopById(Long shopId);

    /**
     * @param shopId 跨境平台的shopId
     * @return
     */
    ShopModel getShopByExtraId(String shopId);

    /**
     * @param shopId 跨境平台的shopId
     * @return
     */
    List<String> getShopRegion(String shopId);


    ShopModel createOrLoadShop(ShopModel shop);

}
