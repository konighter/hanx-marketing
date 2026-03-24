package com.hzltd.module.spapi.service.order;

import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.order.FeeModel;
import com.hzltd.module.spapi.model.order.OrderFeeRequest;
import com.hzltd.module.spapi.model.order.ProductFeeEstimateRequest;

import java.util.List;

public interface FinancesApi {


    ApiResponse<List<FeeModel>> getProductEstimatedFee(ApiRequest<List<ProductFeeEstimateRequest>> request);

    ApiResponse<?> getOrderFee(ApiRequest<List<OrderFeeRequest>> request);
}
