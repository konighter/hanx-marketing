package com.hzltd.module.erplus.api.adptor.amz.event;

import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.hzltd.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AmzEventConsumer {

    @JmsListener(destination = "HanXErplus-OrderEventQueue-us-east", containerFactory = "listenerContainerFactory")
    public void consume(SQSTextMessage message) {
        log.info("Recieve SQS Message: {}", JsonUtils.toJsonString(message));
    }

}
