package com.hzltd.module.erplus.service.amz;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.sys.ChannelNotifySendService;
import com.hzltd.module.erplus.sys.model.NotifyMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Amazon SP-API 事件通知监听器
 * 
 * 监听 spapi-queue, 处理 ORDER_CHANGE 等 SP-API 推送的通知消息
 */
@Slf4j
@Service
public class AmzOrderNotifyEventListener extends AbstractSpApiSqsListener {

    @Resource
    private ChannelNotifySendService channelNotifySendService;

    @SqsListener("${hzapp.aws.sqs.spapi-queue}")
    public void onMessage(String message) {
        processMessage(message);
    }

    @Override
    protected void handleBusinessMessage(String message) {
        try {
            if (JSONUtil.isTypeJSON(message)) {
                JSONObject json = JSONUtil.parseObj(message);
                String notificationType = json.getStr("NotificationType");

                if ("ORDER_CHANGE".equals(notificationType)) {
                    TenantUtils.executeIgnore(
                            () ->{
                                handleOrderChange(message);
                            }
                    );

                } else {
                    log.info("[AmzNotifyEventListener] 收到未知通知类型: type={}, message={}", notificationType, message);
                }
            } else {
                log.info("[AmzNotifyEventListener] 收到非 JSON 消息: {}", message);
            }
        } catch (Exception e) {
            log.error("[AmzNotifyEventListener] 处理消息异常", e);
        }
    }

    /**
     * 处理 ORDER_CHANGE 通知
     */
    private void handleOrderChange(String message) {
        log.debug("[handleOrderChange] 收到订单变更通知, message={}", message);
        OrderChangeNotificationVO vo;
        try {
            vo = JsonUtils.parseObject(message, OrderChangeNotificationVO.class);
            if (vo.getPayload() == null || vo.getPayload().getOrderChangeNotification() == null) {
                log.warn("[handleOrderChange] Payload 或 OrderChangeNotification 为空, message={}", message);
                sendRawNotifyError(message, "Payload 或 OrderChangeNotification 为空");
                return;
            }
        } catch (Exception e) {
            log.error("[handleOrderChange] 解析失败, message={}", message, e);
            sendRawNotifyError(message, "解析异常：" + e.getMessage());
            return;
        }

        OrderChangeNotificationVO.OrderChangeNotification orderChange = vo.getPayload().getOrderChangeNotification();
        String amazonOrderId = orderChange.getAmazonOrderId();
        String sellerId = orderChange.getSellerId();
        String orderChangeType = orderChange.getOrderChangeType();

        // 变更原因
        String changeReason = "";
        if (orderChange.getOrderChangeTrigger() != null) {
            changeReason = orderChange.getOrderChangeTrigger().getChangeReason();
        }

        // SKU 列表
        String skuList = "";
        if (orderChange.getSummary() != null && orderChange.getSummary().getOrderItems() != null) {
            skuList = orderChange.getSummary().getOrderItems().stream()
                    .map(item -> item.getSellerSKU() + " x" + item.getQuantity())
                    .collect(Collectors.joining(", "));
        }

        // 详细信息
        String marketplaceId = "";
        String orderStatus = "";
        String fulfillmentType = "";
        String orderType = "";
        if (orderChange.getSummary() != null) {
            marketplaceId = orderChange.getSummary().getMarketplaceId();
            orderStatus = orderChange.getSummary().getOrderStatus();
            fulfillmentType = orderChange.getSummary().getFulfillmentType();
            orderType = orderChange.getSummary().getOrderType();
        }

        log.info("[handleOrderChange] orderId={}, sellerId={}, market={}, status={}, changeType={}, reason={}, skus={}",
                amazonOrderId, sellerId, marketplaceId, orderStatus, orderChangeType, changeReason, skuList);

        // 发送渠道通知
        sendChannelNotify(vo, orderChange, changeReason, skuList);
    }

    /**
     * 发送渠道通知 (飞书等)
     */
    private void sendChannelNotify(OrderChangeNotificationVO vo,
                                   OrderChangeNotificationVO.OrderChangeNotification orderChange,
                                   String changeReason, String skuList) {
        try {
            String amazonOrderId = orderChange.getAmazonOrderId();

            StringBuilder content = new StringBuilder();
            content.append("**订单号**: ").append(amazonOrderId).append("\n");
            content.append("**变更类型**: ").append(orderChange.getOrderChangeType()).append("\n");
            content.append("**变更原因**: ").append(changeReason).append("\n");
            content.append("**SKU**: ").append(skuList).append("\n");
            content.append("**卖家ID**: ").append(orderChange.getSellerId()).append("\n");

            if (orderChange.getSummary() != null) {
                content.append("**站点**: ").append(orderChange.getSummary().getMarketplaceId()).append("\n");
                content.append("**订单状态**: ").append(orderChange.getSummary().getOrderStatus()).append("\n");
                content.append("**配送方式**: ").append(orderChange.getSummary().getFulfillmentType()).append("\n");
                content.append("**订单类型**: ").append(orderChange.getSummary().getOrderType()).append("\n");
            }

            content.append("**事件时间**: ").append(vo.getEventTime());

            String level = "BuyerRequestedChange".equals(orderChange.getOrderChangeType()) ? "warn" : "info";

            NotifyMessage message = new NotifyMessage();
            message.setTitle("Amazon 订单变更通知");
            message.setContent(content.toString());
            message.setLevel(level);
            message.setCategory("ORDER_CHANGE");

            channelNotifySendService.send(message);
        } catch (Exception e) {
            log.error("[sendChannelNotify] 发送渠道通知失败, orderId={}", orderChange.getAmazonOrderId(), e);
        }
    }

    private void sendRawNotifyError(String message, String reason) {
        try {
            NotifyMessage notifyMsg = new NotifyMessage();
            notifyMsg.setTitle("Amazon 订单变更通知解析失败");
            notifyMsg.setContent("**原因**: " + reason + "\n**原始消息**:\n" + message);
            notifyMsg.setLevel("error");
            notifyMsg.setCategory("ORDER_CHANGE");
            channelNotifySendService.send(notifyMsg);
        } catch (Exception e) {
            log.error("[sendRawNotifyError] 发送失败", e);
        }
    }

    @Override
    protected String getListenerName() {
        return "SpOrderNotify";
    }

    // ==================== 内部 VO ====================

    @Data
    static class OrderChangeNotificationVO {
        @JsonProperty("NotificationVersion")
        private String notificationVersion;
        @JsonProperty("NotificationType")
        private String notificationType;
        @JsonProperty("PayloadVersion")
        private String payloadVersion;
        @JsonProperty("EventTime")
        private String eventTime;
        @JsonProperty("Payload")
        private Payload payload;
        @JsonProperty("NotificationMetadata")
        private NotificationMetadata notificationMetadata;

        @Data
        static class Payload {
            @JsonProperty("OrderChangeNotification")
            private OrderChangeNotification orderChangeNotification;
        }

        @Data
        static class OrderChangeNotification {
            @JsonProperty("NotificationLevel")
            private String notificationLevel;
            @JsonProperty("SellerId")
            private String sellerId;
            @JsonProperty("AmazonOrderId")
            private String amazonOrderId;
            @JsonProperty("OrderChangeType")
            private String orderChangeType;
            @JsonProperty("OrderChangeTrigger")
            private OrderChangeTrigger orderChangeTrigger;
            @JsonProperty("Summary")
            private Summary summary;
        }

        @Data
        static class OrderChangeTrigger {
            @JsonProperty("TimeOfOrderChange")
            private String timeOfOrderChange;
            @JsonProperty("ChangeReason")
            private String changeReason;
        }

        @Data
        static class Summary {
            @JsonProperty("MarketplaceId")
            private String marketplaceId;
            @JsonProperty("OrderStatus")
            private String orderStatus;
            @JsonProperty("PurchaseDate")
            private String purchaseDate;
            @JsonProperty("DestinationPostalCode")
            private String destinationPostalCode;
            @JsonProperty("FulfillmentType")
            private String fulfillmentType;
            @JsonProperty("OrderType")
            private String orderType;
            @JsonProperty("OrderPrograms")
            private List<String> orderPrograms;
            @JsonProperty("ShippingPrograms")
            private List<String> shippingPrograms;
            @JsonProperty("OrderItems")
            private List<OrderItem> orderItems;
        }

        @Data
        static class OrderItem {
            @JsonProperty("OrderItemId")
            private String orderItemId;
            @JsonProperty("SellerSKU")
            private String sellerSKU;
            @JsonProperty("SupplySourceId")
            private String supplySourceId;
            @JsonProperty("Quantity")
            private Integer quantity;
        }

        @Data
        static class NotificationMetadata {
            @JsonProperty("ApplicationId")
            private String applicationId;
            @JsonProperty("SubscriptionId")
            private String subscriptionId;
            @JsonProperty("PublishTime")
            private String publishTime;
            @JsonProperty("NotificationId")
            private String notificationId;
        }
    }
}


