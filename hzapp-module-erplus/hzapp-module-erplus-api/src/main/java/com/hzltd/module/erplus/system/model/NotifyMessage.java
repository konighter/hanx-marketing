package com.hzltd.module.erplus.system.model;

import lombok.Data;

import java.util.Map;

/**
 * 通知消息模型
 * 
 * 各模块通过此模型构建通知内容，传递给 ChannelNotifySendService 发送。
 */
@Data
public class NotifyMessage {

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容 (支持 Markdown 格式)
     */
    private String content;

    /**
     * 消息级别: info / warn / error
     */
    private String level;

    /**
     * 消息分类 (用于路由到指定群组)
     * 
     * 如: ORDER_CHANGE, BUDGET_ALERT, STOCK_WARNING 等
     * 渠道配置的 config.groupMapping 中如果有该分类对应的 webhook，
     * 则发送到该群组；否则使用渠道默认的 webhookUrl 通过机器人发送。
     */
    private String category;

    /**
     * 扩展字段
     * 如 orderId, shopName, sellerId 等
     */
    private Map<String, Object> extra;

    public static NotifyMessage info(String title, String content) {
        NotifyMessage msg = new NotifyMessage();
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLevel("info");
        return msg;
    }

    public static NotifyMessage warn(String title, String content) {
        NotifyMessage msg = new NotifyMessage();
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLevel("warn");
        return msg;
    }

    public static NotifyMessage error(String title, String content) {
        NotifyMessage msg = new NotifyMessage();
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLevel("error");
        return msg;
    }
}
