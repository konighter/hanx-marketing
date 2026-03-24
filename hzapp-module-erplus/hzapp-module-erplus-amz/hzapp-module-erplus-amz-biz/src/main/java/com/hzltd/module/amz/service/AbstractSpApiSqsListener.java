package com.hzltd.module.amz.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Amazon SP-API SQS 消息监听抽象基类
 *
 * 提供 SNS Notification 消息解包逻辑:
 * - 如果收到的是 SNS 包装的消息，解析外层 "Message" 字段
 * - 如果是直接的消息体，直接透传
 *
 * 子类实现 handleBusinessMessage 处理具体业务逻辑
 */
@Slf4j
public abstract class AbstractSpApiSqsListener {

    /**
     * 处理 SQS 消息的公共入口
     */
    protected void processMessage(String message) {
        if (StrUtil.isBlank(message)) {
            return;
        }
        log.info("[processMessage][{}] 收到 SQS 消息, 长度={}", getListenerName(), message.length());
        try {
            String businessMessage = message;

            // 处理 SNS 包装的消息: 外层有 Type / Message 字段
            if (JSONUtil.isTypeJSON(message)) {
                JSONObject jsonObject = JSONUtil.parseObj(message);
                String type = jsonObject.getStr("Type");

                if ("Notification".equals(type)) {
                    // SNS 推送的通知消息，实际内容在 "Message" 字段中
                    businessMessage = jsonObject.getStr("Message");
                    log.info("[processMessage][{}] 解析 SNS Notification, TopicArn={}", 
                            getListenerName(), jsonObject.getStr("TopicArn"));
                }
            }

            handleBusinessMessage(businessMessage);
        } catch (Exception e) {
            log.error("[processMessage][{}] 处理消息失败, message={}", getListenerName(), message, e);
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
