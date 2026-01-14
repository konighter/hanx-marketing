package com.hzltd.module.erplus.service.order;

import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.ApiResponse;
import com.hzltd.module.erplus.model.order.GetOrdersRequest;
import com.hzltd.module.erplus.model.order.OrderModel;

import java.util.List;

public interface OrderApi {

    /**
     * Search orders by request.
     * @param request the request
     * @return the response
     */
    ApiResponse<List<OrderModel>> searchOrders(ApiRequest<GetOrdersRequest> request);

    /**
     * Get order by order ID.
     * @param request the request
     * @return the response
     */
    ApiResponse<OrderModel> getOrder(ApiRequest<String> request);
}
