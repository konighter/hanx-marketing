package com.hzltd.module.erplus.adv.adapter.amazon.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdsAmazonReportStreamListener {

    @Resource
    private AdsAmazonReportService adsAmazonReportService;

    @SqsListener("${hzapp.aws.sqs.adv-queue}")
    public void onMessage(String message) {
        if (StrUtil.isBlank(message)) {
            return;
        }
        log.info("[onMessage] 收到 SQS 消息, 长度={}", message.length());
        try {
            // 处理 SNS 订阅确认消息
            if (JSONUtil.isTypeJSON(message)) {
                JSONObject jsonObject = JSONUtil.parseObj(message);
                String type = jsonObject.getStr("Type");
                if ("SubscriptionConfirmation".equals(type)) {
                    String subscribeUrl = jsonObject.getStr("SubscribeURL");
                    log.info("[onMessage] 收到 SNS 订阅确认消息, TopicArn={}, 准备请求 SubscribeURL: {}", 
                            jsonObject.getStr("TopicArn"), subscribeUrl);
                    if (StrUtil.isNotBlank(subscribeUrl)) {
                        String result = HttpUtil.get(subscribeUrl);
                        log.info("[onMessage] SNS 订阅确认完成, 返回结果={}", result);
                    }
                    return;
                }
            }

            adsAmazonReportService.processStreamMessage(message);
        } catch (Exception e) {
            log.error("[onMessage] 处理 Stream 消息失败, message={}", message, e);
            // SQS 消息消费失败后会自动重试
        }
    }

}
