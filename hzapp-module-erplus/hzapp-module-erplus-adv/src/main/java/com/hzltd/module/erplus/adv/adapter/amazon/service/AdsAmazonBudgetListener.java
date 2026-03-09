package com.hzltd.module.erplus.adv.adapter.amazon.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Amazon 广告预算 Stream 消息监听器
 * 
 * 监听 budget-queue, 处理预算相关的实时消息
 */
@Slf4j
@Service
public class AdsAmazonBudgetListener extends AbstractAmazonSqsListener {

    @SqsListener("${hzapp.aws.sqs.budget-queue}")
    public void onMessage(String message) {
        processMessage(message);
    }

    @Override
    protected void handleBusinessMessage(String message) {
        log.info("[AdsAmazonBudgetListener] 收到预算消息: {}", message);
        // TODO: 实现预算消息处理逻辑
    }

    @Override
    protected String getListenerName() {
        return "Budget";
    }
}
