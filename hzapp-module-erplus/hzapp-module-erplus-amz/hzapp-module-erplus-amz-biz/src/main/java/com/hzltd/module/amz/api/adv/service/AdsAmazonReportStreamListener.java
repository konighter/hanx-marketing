package com.hzltd.module.amz.api.adv.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Amazon 广告流量/转化 Stream 消息监听器
 * 
 * 监听 adv-queue, 处理 sp-traffic / sp-conversion 等数据集消息
 */
@Slf4j
@Service
public class AdsAmazonReportStreamListener extends AbstractAmazonSqsListener {

    @Resource
    private AdsAmazonReportStreamHandler adsAmazonReportStreamHandler;

    @SqsListener("${hzapp.aws.sqs.adv-queue}")
    public void onMessage(String message) {
        processMessage(message);
    }

    @Override
    protected void handleBusinessMessage(String message) {
        adsAmazonReportStreamHandler.processStreamMessage(message);
    }

    @Override
    protected String getListenerName() {
        return "ReportStream";
    }
}
