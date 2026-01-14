package com.hzltd.module.erplus.service.order;

import cn.hutool.db.sql.Order;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.order.FeeModel;
import com.hzltd.module.erplus.model.order.OrderFeeRequest;
import com.hzltd.module.erplus.model.order.ProductFeeEstimateRequest;

import java.util.List;

public interface FinancesApi {


    ApiResponse<List<FeeModel>> getProductEstimatedFee(ApiRequest<List<ProductFeeEstimateRequest>> request);

    ApiResponse<?> getOrderFee(ApiRequest<List<OrderFeeRequest>> request);
}
