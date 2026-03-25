package com.hzltd.module.erplus.service.notify.sender;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzltd.module.erplus.dal.dataobject.notify.NotifyChannelDO;
import com.hzltd.module.system.enums.NotifyChannelTypeEnum;
import com.hzltd.module.spapi.model.system.NotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 飞书 Webhook 通知发送器
 *
 * 支持向指定群组发送消息:
 * - channel.config JSON 中的 groupMapping 字段定义消息分类与群组 webhook 的映射
 * - 消息的 category 字段用于匹配 groupMapping 中的 key
 * - 如果没有匹配的映射，则使用 channel.webhookUrl (默认机器人) 发送
 *
 * config 结构示例:
 * {
 *   "groupMapping": {
 *     "ORDER_CHANGE": "https://open.feishu.cn/open-apis/bot/v2/hook/xxx-order-group",
 *     "BUDGET_ALERT": "https://open.feishu.cn/open-apis/bot/v2/hook/xxx-budget-group"
 *   }
 * }
 */
@Slf4j
@Component
public class FeishuChannelSender implements NotifyChannelSender {

    @Override
    public NotifyChannelTypeEnum getChannelType() {
        return NotifyChannelTypeEnum.FEISHU;
    }

    @Override
    public void send(NotifyChannelDO channel, NotifyMessage message) {
        // 解析目标 webhook: 优先按 category 查找群组映射，没有则使用默认 webhook
        String targetWebhook = resolveWebhookUrl(channel, message);

        try {
            String body = buildFeishuMessage(message);
            HttpResponse response = HttpRequest.post(targetWebhook)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(body)
                    .timeout(5000)
                    .execute();
            
            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                Integer code = result.getInt("code");
                if (code != null && code != 0) {
                    log.warn("[FeishuChannelSender] 发送失败, code={}, msg={}", code, result.getStr("msg"));
                } else {
                    log.info("[FeishuChannelSender] 发送成功, channel={}, category={}", 
                            channel.getName(), message.getCategory());
                }
            } else {
                log.error("[FeishuChannelSender] HTTP 请求失败, status={}, channel={}", 
                        response.getStatus(), channel.getName());
            }
        } catch (Exception e) {
            log.error("[FeishuChannelSender] 发送异常, channel={}", channel.getName(), e);
        }
    }

    /**
     * 解析目标 Webhook URL
     * 
     * 1. 如果消息有 category 且 channel config 的 groupMapping 中有对应的 webhook → 使用群组 webhook
     * 2. 否则 → 使用 channel 默认的 webhookUrl (机器人直发)
     */
    private String resolveWebhookUrl(NotifyChannelDO channel, NotifyMessage message) {
        if (StrUtil.isNotBlank(message.getCategory()) && StrUtil.isNotBlank(channel.getConfig())) {
            try {
                JSONObject config = JSONUtil.parseObj(channel.getConfig());
                JSONObject groupMapping = config.getJSONObject("groupMapping");
                if (groupMapping != null) {
                    String groupWebhook = groupMapping.getStr(message.getCategory());
                    if (StrUtil.isNotBlank(groupWebhook)) {
                        log.debug("[FeishuChannelSender] 匹配到群组映射: category={}, webhook={}...", 
                                message.getCategory(), groupWebhook.substring(0, Math.min(40, groupWebhook.length())));
                        return groupWebhook;
                    }
                }
            } catch (Exception e) {
                log.warn("[FeishuChannelSender] 解析 config 失败, 使用默认 webhook", e);
            }
        }
        return channel.getWebhookUrl();
    }

    /**
     * 构建飞书 Interactive Card 消息格式
     */
    private String buildFeishuMessage(NotifyMessage message) {
        // 根据 level 设置颜色标识
        String levelTag = switch (message.getLevel() != null ? message.getLevel() : "info") {
            case "error" -> "🔴 ";
            case "warn" -> "🟡 ";
            default -> "🟢 ";
        };

        JSONObject content = new JSONObject();
        content.set("msg_type", "interactive");

        // 使用 Card 消息格式
        JSONObject card = new JSONObject();

        // Header
        JSONObject header = new JSONObject();
        JSONObject title = new JSONObject();
        title.set("tag", "plain_text");
        title.set("content", levelTag + message.getTitle());
        header.set("title", title);

        // 根据 level 设置 header 颜色
        String template = switch (message.getLevel() != null ? message.getLevel() : "info") {
            case "error" -> "red";
            case "warn" -> "orange";
            default -> "blue";
        };
        header.set("template", template);
        card.set("header", header);

        // Elements - 使用 Markdown 内容
        JSONObject element = new JSONObject();
        element.set("tag", "div");
        JSONObject text = new JSONObject();
        text.set("tag", "lark_md");
        text.set("content", message.getContent());
        element.set("text", text);
        card.set("elements", new Object[]{element});

        content.set("card", card);
        return content.toString();
    }
}

