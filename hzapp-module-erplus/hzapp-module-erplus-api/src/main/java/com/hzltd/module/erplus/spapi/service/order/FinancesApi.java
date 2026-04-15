package com.hzltd.module.erplus.spapi.service.order;

import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.common.FeeModel;
import com.hzltd.module.erplus.spapi.model.order.OrderFeeRequest;
import com.hzltd.module.erplus.spapi.model.order.ProductFeeEstimateRequest;

import java.util.List;

public interface FinancesApi {


    ApiResponse<List<FeeModel>> getProductEstimatedFee(ApiRequest<List<ProductFeeEstimateRequest>> request);

    ApiResponse<?> getOrderFee(ApiRequest<List<OrderFeeRequest>> request);
}
