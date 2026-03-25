package com.hzltd.module.amz.api.adv.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Amazon SQS 消息监听抽象基类
 * 
 * 提供 SNS SubscriptionConfirmation 确认逻辑的公共处理，
 * 子类只需实现 handleBusinessMessage 处理具体业务消息。
 */
@Slf4j
public abstract class AbstractAmazonSqsListener {

    /**
     * 处理 SQS 消息的公共入口
     * 1. 空消息过滤
     * 2. SNS 订阅确认自动处理
     * 3. 委托给子类 handleBusinessMessage
     */
    protected void processMessage(String message) {
        if (StrUtil.isBlank(message)) {
            return;
        }
        log.info("[processMessage][{}] 收到 SQS 消息, 长度={}", getListenerName(), message.length());
        try {
            // 处理 SNS 订阅确认消息
            if (JSONUtil.isTypeJSON(message)) {
                JSONObject jsonObject = JSONUtil.parseObj(message);
                String type = jsonObject.getStr("Type");
                if ("SubscriptionConfirmation".equals(type)) {
                    String subscribeUrl = jsonObject.getStr("SubscribeURL");
                    log.info("[processMessage][{}] 收到 SNS 订阅确认消息, TopicArn={}, 准备请求 SubscribeURL: {}", 
                            getListenerName(), jsonObject.getStr("TopicArn"), subscribeUrl);
                    if (StrUtil.isNotBlank(subscribeUrl)) {
                        String result = HttpUtil.get(subscribeUrl);
                        log.info("[processMessage][{}] SNS 订阅确认完成, 返回结果={}", getListenerName(), result);
                    }
                    return;
                }
            }

            TenantUtils.executeIgnore(() -> {
                handleBusinessMessage(message);
            });

        } catch (Exception e) {
            log.error("[processMessage][{}] 处理消息失败, message={}", getListenerName(), message, e);
            // SQS 消息消费失败后会自动重试
        }
    }

    /**
     * 子类实现具体业务消息处理逻辑
     */
    protected abstract void handleBusinessMessage(String message);

    /**
     * 返回 Listener 名称，用于日志区分
     */
    protected abstract String getListenerName();
}
