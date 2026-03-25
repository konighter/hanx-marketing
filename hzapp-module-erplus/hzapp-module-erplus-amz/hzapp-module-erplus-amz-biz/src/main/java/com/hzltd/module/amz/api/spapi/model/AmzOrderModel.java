package com.hzltd.module.amz.api.spapi.model;

import com.hzltd.module.spapi.model.order.OrderModel;
import lombok.Data;
import software.amazon.spapi.models.orders.v0.Order;

@Data
public class AmzOrderModel extends OrderModel {

    private Order amazonOrder;
}
