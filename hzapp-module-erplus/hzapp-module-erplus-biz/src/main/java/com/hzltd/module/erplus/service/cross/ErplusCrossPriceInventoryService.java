package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.erplus.controller.admin.cross.vo.CrossProductPriceUpdateRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductPriceDO;
import com.hzltd.module.erplus.spapi.model.pricing.GetOfferResponse;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;

import java.util.List;

public interface ErplusCrossPriceInventoryService {

    /**
     * 更新跨境商品价格
     * @param request
     * @return
     */
    Boolean updateCrossPlatformProductPrice(CrossProductPriceUpdateRequest request);


    /**
     * 获取跨境商品报价
     * @param productId 本地商品ID, 使用SKU查询, 只能查询自己刊登过的商品报价
     * @return
     */
    GetOfferResponse getCrossProductOffer(Long productId);

    /**
     * 获取跨境商品所有报价
     * @param productCode 跨境商品代码
     * @return
     */
    GetOfferResponse getCrossProductAllOffer(String productCode, CrossPlatformEnum platform, Integer shopId, String marketId);

    /**
     * 获取跨境商品库存
     * @param productId 本地商品ID, 使用SKU查询, 只能查询自己刊登过的商品库存
     * @return
     */
    void getCrossInventory(Long productId);

    void getCrossInventories(List<Long> productIds);

    CrossProductPriceDO getEffectivePrice(Long productId);

}
