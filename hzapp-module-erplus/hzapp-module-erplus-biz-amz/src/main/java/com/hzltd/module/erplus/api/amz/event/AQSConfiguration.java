package com.hzltd.module.erplus.api.adptor.amz.event;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import jakarta.jms.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AQSConfiguration {

    @Bean
    public SQSConnectionFactory connectionFactory() {
// 1. 创建标准的 SqsClient
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1) // 替换为你目的地所在的区域
                // IAM User : SPAPI_SQS_READER
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create("AKIAZVFYRE4OFJILEDM6", "vvRIQ22BYcPgHY3mr3s3ABgfDnW68/+UbAC8qsls")
                        )
                )
                .build();

        return new SQSConnectionFactory(
                new ProviderConfiguration(),
                sqsClient
        );

    }

    @Bean
    public DefaultJmsListenerContainerFactory listenerContainerFactory(SQSConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        //开启多少线程
        factory.setConcurrency("10-20");
        //开启自动确认
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
}
