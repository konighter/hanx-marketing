package com.hzltd.module.erplus.service.amz;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmzNotifyEventListener {

    @SqsListener("${hzapp.aws.sqs.spapi-queue}")
    public void onMessage(String message) {
        log.info("SPAPI Received message: {}", message);
    }


}
