package com.hzltd.module.erplus.api.amz.event;



//@Configuration
public class AQSConfiguration {

//    @Bean
//    public SQSConnectionFactory connectionFactory() {
//// 1. 创建标准的 SqsClient
//        SqsClient sqsClient = SqsClient.builder()
//                .region(Region.US_EAST_1) // 替换为你目的地所在的区域
//                // IAM User : SPAPI_SQS_READER
//                .credentialsProvider(StaticCredentialsProvider
//                        .create(AwsBasicCredentials
//                                .create("AKIAZVFYRE4OFJILEDM6", "vvRIQ22BYcPgHY3mr3s3ABgfDnW68/+UbAC8qsls")
//                        )
//                )
//                .build();
//
//        return new SQSConnectionFactory(
//                new ProviderConfiguration(),
//                sqsClient
//        );
//
//    }
//
//    @Bean
//    public DefaultJmsListenerContainerFactory listenerContainerFactory(SQSConnectionFactory connectionFactory) {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setDestinationResolver(new DynamicDestinationResolver());
//        //开启多少线程
//        factory.setConcurrency("10-20");
//        //开启自动确认
//        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
//        return factory;
//    }
}
