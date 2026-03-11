package com.hzltd.module.erplus.ai.masv0.framework.event;

import reactor.core.publisher.Flux;

/**
 * MAS 响应式事件总线接口
 */
public interface MasEventBus {

    /**
     * 发布事件
     */
    void publish(MasEvent event);

    /**
     * 订阅指定类型的事件
     */
    <T extends MasEvent> Flux<T> subscribe(Class<T> eventType);

    /**
     * 订阅所有事件
     */
    Flux<MasEvent> subscribeAll();
}
