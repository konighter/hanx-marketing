package com.hzltd.module.erplus.ai.masv0.framework.event;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 基于 Project Reactor 的默认事件总线实现
 */
@Component
public class DefaultMasEventBus implements MasEventBus {

    private final Sinks.Many<MasEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void publish(MasEvent event) {
        sink.tryEmitNext(event);
    }

    @Override
    public <T extends MasEvent> Flux<T> subscribe(Class<T> eventType) {
        return sink.asFlux()
                .filter(eventType::isInstance)
                .map(eventType::cast);
    }

    @Override
    public Flux<MasEvent> subscribeAll() {
        return sink.asFlux();
    }
}
