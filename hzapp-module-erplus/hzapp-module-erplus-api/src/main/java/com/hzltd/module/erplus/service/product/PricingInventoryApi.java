package com.hzltd.module.erplus.service.product;

import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.pricing.ChangeProductInventoryRequest;
import com.hzltd.module.erplus.model.pricing.ChangeProductInventoryResponse;
import com.hzltd.module.erplus.model.pricing.ChangeProductPricingRequest;
import com.hzltd.module.erplus.model.pricing.ChangeProductPricingResponse;

public interface PricingApi {

    /**
     * 获取ASIN的竞争力价格
     * @param request
     * @return
     */
    ApiResponse<?> getCompetitivePricing(ApiRequest<?> request);

    /**
     * 获取ASIN的商品报价
     * @param request
     * @return
     */
    ApiResponse<?> getItemOffers(ApiRequest<String> request);

    /**
     * 获取ASIN的商品 listing 报价
     * @param request
     * @return
     */
    ApiResponse<?> getListingsOffers(ApiRequest<String> request);


    /**
     * 修改商品价格
     * @param request
     * @return
     */
    ApiResponse<ChangeProductPricingResponse> changeProductPricing(ApiRequest<ChangeProductPricingRequest> request);

    /**
     * 修改商品库存
     * @param request
     * @return
     */
    ApiResponse<ChangeProductInventoryResponse> changeProductInventory(ApiRequest<ChangeProductInventoryRequest> request);



}
