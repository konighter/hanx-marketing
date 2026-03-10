package com.hzapp.module.erplus;

import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import lombok.extern.slf4j.Slf4j;
import software.amazon.spapi.api.notifications.v1.NotificationsApi;
import software.amazon.spapi.api.orders.v0.OrdersV0Api;
import software.amazon.spapi.api.producttypedefinitions.v2020_09_01.DefinitionsApi;
import software.amazon.spapi.models.notifications.v1.GetDestinationsResponse;
import software.amazon.spapi.models.orders.v0.GetOrderResponse;
import software.amazon.spapi.models.producttypedefinitions.v2020_09_01.ProductTypeList;

import java.util.Collections;
import java.util.List;

@Slf4j
public class AmzNotificationApiTest {

    private static String clientId = "amzn1.application-oa2-client.a8c3086adc4b48aa967b38d34dcbe2d1";
    private static String clientSecret = "amzn1.oa2-cs.v1.7c60a89b8422fc4815eba8917c3a03dc1f39f3452906bdad03d1f445ac710b15";
    private static String refreshToken = "Atzr|IwEBIEsBjM1ehCAODOL_HXkzREzXKJ1EIsFIuHJDMMtWnXxIfORfzHZTjodukP78Vkp567hvpf6lDbDalVRJNTDQ0JUunxTdK2qQ8ukmWT49ukhoXPJET0H838BwvL-SgGUOLgFyjOMEHMdB5fr0gIc_qJrgXYVE_YFy6JWrJwIfOi1itl8wmiLenBRRMVEgOdxk3VzxlTqS7ce9X2tfzM-kHWWoUi1rm27P_uquxq-MQmoPK_pF_BWL8JCHAYtH7OZjB22M1-SZIQUu76-57L0q8NRoy2ZmezGaNZJOJ-yk8iZvKqaj48_i8481fLQz-Ihtep89HbIFqWeo4vft_Kt-LVmJ";
    private static String endpoint = "https://sellingpartnerapi-na.amazon.com";

    @org.junit.jupiter.api.Test
    public void testGetDestinationsWithSdk() {
        log.info("Starting testGetDestinationsWithSdk using pure POJO NotificationsApi...");

        try {
            // 纯对象构建 Auth 凭证
            LWAAuthorizationCredentials credentials = LWAAuthorizationCredentials.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .refreshToken(refreshToken)
                    .endpoint("https://api.amazon.com/auth/o2/token")
                    .build();

            // 纯对象初始化官方 SDK 的 NotificationsApi 对象 (无 Spring 环境)
            NotificationsApi notificationsApi = new NotificationsApi.Builder()
                    .lwaAuthorizationCredentials(credentials)
                    .endpoint(endpoint)
                    .build();
            
            // 使用 SDK 调用 getDestinations
            GetDestinationsResponse response = notificationsApi.getDestinations();
            
            if (response != null && response.getPayload() != null) {
                log.info("Successfully fetched destinations natively via pure bean NotificationsApi: {}", response.getPayload());
            } else {
                log.error("Failed or empty response from NotificationsApi: {}", response);
            }
        } catch (Exception e) {
            log.error("Exception during NotificationsApi.getDestinations: ", e);
        }
    }

    @org.junit.jupiter.api.Test
    public void testSearchDefinitionsProductTypes() {
        log.info("Starting testSearchDefinitionsProductTypes using pure POJO DefinitionsApi...");

        try {
            LWAAuthorizationCredentials credentials = LWAAuthorizationCredentials.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .refreshToken(refreshToken)
                    .endpoint("https://api.amazon.com/auth/o2/token")
                    .build();

            DefinitionsApi definitionsApi = new DefinitionsApi.Builder()
                    .lwaAuthorizationCredentials(credentials)
                    .endpoint(endpoint)
                    .build();

            // ATVPDKIKX0DER = US marketplace
            List<String> marketplaceIds = Collections.singletonList("ATVPDKIKX0DER");
            ProductTypeList response = definitionsApi.searchDefinitionsProductTypes(marketplaceIds, null, null, "en_US", null);

            log.info("Successfully fetched product definitions natively via pure bean DefinitionsApi: {}", response);
        } catch (Exception e) {
            log.error("Exception during DefinitionsApi.searchDefinitionsProductTypes: ", e);
        }
    }

    @org.junit.jupiter.api.Test
    public void testSearchOrder() {
        log.info("Starting testSearchOrder using pure POJO OrdersV0Api...");

        try {
            LWAAuthorizationCredentials credentials = LWAAuthorizationCredentials.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .refreshToken(refreshToken)
                    .endpoint("https://api.amazon.com/auth/o2/token")
                    .build();

            OrdersV0Api ordersApi = new OrdersV0Api.Builder()
                    .lwaAuthorizationCredentials(credentials)
                    .endpoint(endpoint)
                    .build();

            String orderId = "114-6372446-8941821";
            GetOrderResponse response = ordersApi.getOrder(orderId);

            log.info("Successfully fetched order natively via pure bean OrdersV0Api: {}", response);
        } catch (Exception e) {
            log.error("Exception during OrdersV0Api.getOrder: ", e);
        }
    }
}
