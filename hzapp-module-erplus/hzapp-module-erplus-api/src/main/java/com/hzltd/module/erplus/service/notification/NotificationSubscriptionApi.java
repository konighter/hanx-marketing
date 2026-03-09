package com.hzltd.module.erplus.service.notification;

/**
 * 消息通知订阅 API 接口
 * 
 * 各平台实现各自的通知订阅逻辑，通过 @ServiceRegister 注册到工厂。
 * 在店铺授权完成后由 ShopServiceImpl 统一调用。
 */
public interface NotificationSubscriptionApi {

    /**
     * 店铺授权后，初始化该店铺所需的所有通知订阅
     *
     * @param shopId 店铺 ID
     */
    void setupNotificationSubscriptions(Long shopId);
}
