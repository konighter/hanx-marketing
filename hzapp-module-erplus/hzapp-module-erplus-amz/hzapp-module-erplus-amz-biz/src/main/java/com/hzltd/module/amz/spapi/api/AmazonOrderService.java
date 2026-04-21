package com.hzltd.module.amz.spapi.api;

import cn.hutool.core.date.DateUtil;
import com.amazon.SellingPartnerAPIAA.LWAException;
import com.hzltd.module.amz.spapi.AbsAmzPlatformApiService;
import com.hzltd.module.amz.spapi.model.AmzOrderModel;
import com.hzltd.module.erplus.spapi.api.ServiceRegister;
import com.hzltd.module.erplus.spapi.enums.CrossOrderStatus;
import com.hzltd.module.erplus.spapi.enums.FulfillTypeEnum;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.order.GetOrdersRequest;
import com.hzltd.module.erplus.spapi.model.order.OrderItemModel;
import com.hzltd.module.erplus.spapi.model.order.OrderModel;
import com.hzltd.module.erplus.spapi.service.order.OrderApi;
import com.hzltd.module.erplus.system.annotation.CrossplatformApiLog;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.service.SystemPlatformService;
import com.hzltd.module.erplus.system.service.SystemShopService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.spapi.ApiException;
import software.amazon.spapi.api.orders.v0.OrdersV0Api;
import software.amazon.spapi.models.orders.v0.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = OrderApi.class)
public class AmazonOrderService extends AbsAmzPlatformApiService implements OrderApi {
    @Resource
    private SystemShopService systemShopService;

    @Resource
    private SystemPlatformService systemPlatformService;


    @Override
    @CrossplatformApiLog
    public ApiResponse<List<OrderModel>> searchOrders(ApiRequest<GetOrdersRequest> request) {

        try {
            OrdersV0Api ordersApi = getOrderApi(request);
            GetOrdersRequest ordersRequest = request.getRequest();
            List<String> fulfillTypes = null;
            if (ordersRequest.getFulfillType() != null) {
                fulfillTypes = List.of(convert(ordersRequest.getFulfillType()));
            }
            List<String> orderStatuses = null;
            if (ordersRequest.getStatuses() != null) {
                orderStatuses = ordersRequest.getStatuses().stream().map(CrossOrderStatus::getCode).collect(Collectors.toList());
            }

            // 使用店铺时区解析时间，前端传入的 LocalDateTime 应视为店铺当地时间
            String timezone = StringUtils.isNotEmpty(request.getTimeZone()) ? request.getTimeZone() : "UTC";
            ZoneId shopZone = ZoneId.of(timezone);
            OffsetDateTime start = ordersRequest.getCreateTimeStart() == null ? null :ordersRequest.getCreateTimeStart().atZone(shopZone).toOffsetDateTime().withNano(0);
            OffsetDateTime maxCreateBefore = OffsetDateTime.now(shopZone).minusMinutes(5).withNano(0);
            OffsetDateTime end =  ordersRequest.getCreateTimeEnd() == null ? maxCreateBefore : ordersRequest.getCreateTimeEnd().atZone(shopZone).toOffsetDateTime().withNano(0);

            // 结束时间限制为当前时间减5分钟（使用店铺时区计算"当前时间"）
            if (end.isAfter(maxCreateBefore)) {
                end = maxCreateBefore;
            }

            // 调用亚马逊API获取订单详情
            GetOrdersResponse response = ordersApi.getOrders(systemShopService.getShopMarketplace(request.getShopId()),
                    start == null ? null : start.toString(), end.toString(), null, null, orderStatuses, fulfillTypes,
                    null, null, null, null, null, null, ordersRequest.getNextToken(),
                    ordersRequest.getOrderIds(), null, null, null, null, null, null, null, null);

            if (response.getPayload() == null) {
                return ApiResponse.error("Order not found");
            }

            List<OrderModel> orderModels = response.getPayload().getOrders().stream().map(order -> {

                OrderModel orderModel = convertOrder(order);
                ApiResponse<List<OrderItemModel>> orderItemResponse = this.getOrderItems(new ApiRequest<String>().setShopId(request.getShopId()).setRequest(orderModel.getOrderId()));

                if (orderItemResponse.success()) {
                    orderModel.setOrderItems(orderItemResponse.getData());
                }
                return orderModel;

            }).collect(Collectors.toList());
            return ApiResponse.success(orderModels).setCursor(response.getPayload().getNextToken());
        } catch (ApiException | LWAException e) {
            log.error("Get order error", e);
            return ApiResponse.error("Search orders failed: " + e.getMessage());
        }
    }


    private ApiResponse<List<OrderItemModel>> getOrderItems(ApiRequest<String> request) {
        OrdersV0Api ordersApi = getOrderApi(request);

        List<OrderItemModel> allItems = Lists.newArrayList();
        try {
            String nextToken = null;
            GetOrderItemsResponse orderItemsResponse;
            do {
                orderItemsResponse = ordersApi.getOrderItems(request.getRequest(), null);
                if (orderItemsResponse.getPayload() == null) {
                    return ApiResponse.error("Order items not found");
                }
                List<OrderItemModel> orderItemModel = orderItemsResponse.getPayload().getOrderItems().stream().map(item -> convertOrderItem(item, request.getRequest())).collect(Collectors.toList());
                allItems.addAll(orderItemModel);
            } while ((nextToken = orderItemsResponse.getPayload().getNextToken()) != null);
        } catch (ApiException | LWAException e) {
            log.error("Get order items error", e);
            return ApiResponse.error("Get order items failed: " + e.getMessage());
        }
        return ApiResponse.success(allItems);
    }



    @Override
    @CrossplatformApiLog
    public ApiResponse<OrderModel> getOrder(ApiRequest<String> request) {
        try {
            OrdersV0Api ordersApi = getOrderApi(request);

            // 调用亚马逊API获取订单详情
            GetOrderResponse response = ordersApi.getOrder(request.getRequest());

            if (response.getPayload() == null) {
                return ApiResponse.error("Order not found");
            }

            OrderModel orderModel = convertOrder(response.getPayload());
            return ApiResponse.success(orderModel);

        } catch (ApiException | LWAException e) {
            log.error("Get order error", e);
            return ApiResponse.error("Get order failed: " + e.getMessage());
        }
    }


    private OrderModel convertOrder(Order amazonOrder) {

        OrderModel amzOrderModel = new AmzOrderModel()
                .setAmazonOrder(amazonOrder)
                .setOrderId(amazonOrder.getAmazonOrderId())
                .setMarketId(amazonOrder.getMarketplaceId())
                .setIsPrime(amazonOrder.isIsPrime())
                .setIsBusiness(amazonOrder.isIsBusinessOrder())
                .setSalesChannel(amazonOrder.getSalesChannel())
                .setOrderStatus(CrossOrderStatus.of(amazonOrder.getOrderStatus().getValue()))
                .setOrderType(amazonOrder.getOrderType().getValue())
                .setFulfillmentType(Order.FulfillmentChannelEnum.AFN.equals(amazonOrder.getFulfillmentChannel()) ? FulfillTypeEnum.FBA : FulfillTypeEnum.FBM)
                .setCreateTime(DateUtil.parse(amazonOrder.getPurchaseDate()).toLocalDateTime())
                .setUpdateTime(DateUtil.parse(amazonOrder.getLastUpdateDate()).toLocalDateTime());


        if (amazonOrder.getOrderTotal() != null) {
            amzOrderModel.setCurrency(amazonOrder.getOrderTotal().getCurrencyCode())
                    .setTotalAmount(new BigDecimal(amazonOrder.getOrderTotal().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        return amzOrderModel;
    }

    private OrderItemModel convertOrderItem(OrderItem amazonOrderItem, String orderId) {
        OrderItemModel item = new OrderItemModel()
                .setOrderId(orderId)
                .setPlatformProductCode(amazonOrderItem.getASIN())
                .setSellerSku(amazonOrderItem.getSellerSKU())
                .setTitle(amazonOrderItem.getTitle())
                .setQuantity(amazonOrderItem.getQuantityOrdered());

        if (amazonOrderItem.getItemPrice() != null) {
            item.setCurrency(amazonOrderItem.getItemPrice().getCurrencyCode())
                    .setItemPrice(new BigDecimal(amazonOrderItem.getItemPrice().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getItemTax() != null) {
            item.setItemTax(new BigDecimal(amazonOrderItem.getItemTax().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getShippingPrice() != null) {
            item.setShipFee(new BigDecimal(amazonOrderItem.getShippingPrice().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getShippingTax() != null) {
            item.setShipFeeTax(new BigDecimal(amazonOrderItem.getShippingTax().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getShippingDiscount() != null) {
            item.setShipFeeDiscount(new BigDecimal(amazonOrderItem.getShippingDiscount().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getShippingDiscountTax() != null) {
            item.setShipFeeDiscountTax(new BigDecimal(amazonOrderItem.getShippingDiscountTax().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getPromotionDiscount() != null) {
            item.setPromoDiscount(new BigDecimal(amazonOrderItem.getPromotionDiscount().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getPromotionDiscountTax() != null) {
            item.setPromoDiscountTax(new BigDecimal(amazonOrderItem.getPromotionDiscountTax().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getCoDFee() != null) {
            item.setCodFee(new BigDecimal(amazonOrderItem.getCoDFee().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getCoDFeeDiscount() != null) {
            item.setCodFeeDiscount(new BigDecimal(amazonOrderItem.getCoDFeeDiscount().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }

        if (amazonOrderItem.getPointsGranted() != null) {
            item.setPointsNum(amazonOrderItem.getPointsGranted().getPointsNumber().intValue())
                    .setPointsAsMoney(new BigDecimal(amazonOrderItem.getPointsGranted().getPointsMonetaryValue().getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        }


        return item;
    }

}
