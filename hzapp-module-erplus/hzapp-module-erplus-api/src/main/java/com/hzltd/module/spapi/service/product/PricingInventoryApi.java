package com.hzltd.module.spapi.service.product;

import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.common.InventoryModel;
import com.hzltd.module.spapi.model.pricing.*;

import java.util.List;

/**
 * 价格库存操作API
 */
public interface PricingInventoryApi {

    // -- 价格相关 --
    /**
     * 获取ASIN的竞争力价格
     * @param request
     * @return
     */
    ApiResponse<GetOfferResponse> getCompetitivePricing(ApiRequest<?> request);

    /**
     * 获取ASIN的商品报价
     * @param request
     * @return
     */
    ApiResponse<GetOfferResponse> getItemOffers(ApiRequest<String> request);

    /**
     * 获取SKU的商品 listing 报价
     * @param request
     * @return
     */
    ApiResponse<GetOfferResponse> getListingsOffers(ApiRequest<String> request);


    /**
     * 修改商品价格
     * @param request
     * @return
     */
    ApiResponse<ChangePriceResponse> changeProductPricing(ApiRequest<ChangePriceRequest> request);




    // -- 库存相关 --

    /**
     * 获取ASIN的商品库存
     * @param request
     * @return
     */
    ApiResponse<List<InventoryModel>> getInventory(ApiRequest<GetInventoryRequest> request);

    /**
     * 修改商品库存
     * @param request
     * @return
     */
    ApiResponse<ChangeInventoryResponse> changeProductInventory(ApiRequest<ChangeInventoryRequest> request);

}
