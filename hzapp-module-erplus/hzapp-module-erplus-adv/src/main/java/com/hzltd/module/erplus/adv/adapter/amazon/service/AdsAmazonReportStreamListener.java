package com.hzltd.module.erplus.adv.adapter.amazon.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdsAmazonReportStreamListener {

    @SqsListener("${hzapp.aws.sqs.adv-queue}")
    public void onMessage(String message) {
        log.info("Received message: {}", message);
    }




}
