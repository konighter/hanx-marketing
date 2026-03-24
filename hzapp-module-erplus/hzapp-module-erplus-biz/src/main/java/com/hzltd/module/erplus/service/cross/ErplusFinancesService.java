package com.hzltd.module.erplus.service.cross;

import com.hzltd.module.spapi.model.order.FeeModel;

import java.util.List;

public interface ErplusFinancesService {

    void syncProductFee(List<Long> orderId);

    List<FeeModel> getProductEstimatedFee(Integer platformId, Integer shopId, String marketId, List<String> sellerSkuList);

    FeeModel getProductEstimatedFee(Long productId);
}
