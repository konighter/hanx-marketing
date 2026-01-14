package com.hzltd.module.erplus.api.amz.model;

import com.hzltd.module.erplus.model.order.OrderModel;
import lombok.Data;
import software.amazon.spapi.models.orders.v0.Order;

@Data
public class AmzOrderModel extends OrderModel {

    private Order amazonOrder;
}
