package com.hzltd.module.erplus.service.event;

import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ERP 事件总线包装类
 * 提供多主题支持，避免不同业务事件相互影响
 *
 * @author 翰展科技
 */
@Component
public class ErpEventBus {

    private final Map<String, EventBus> busMap = new ConcurrentHashMap<>();

    /**
     * 获取指定主题的事件总线
     *
     * @param theme 主题名称
     * @return EventBus 实例
     */
    public EventBus getBus(String theme) {
        return busMap.computeIfAbsent(theme, EventBus::new);
    }

    /**
     * 获取默认主题的事件总线
     *
     * @return EventBus 实例
     */
    public EventBus getBus() {
        return getBus("DEFAULT");
    }

    /**
     * 发布事件到默认主题
     *
     * @param event 事件对象
     */
    public void post(Object event) {
        getBus().post(event);
    }

    /**
     * 发布事件到指定主题
     *
     * @param theme 主题名称
     * @param event 事件对象
     */
    public void post(String theme, Object event) {
        getBus(theme).post(event);
    }

    /**
     * 注册监听器到指定主题
     *
     * @param theme    主题名称
     * @param listener 监听器实例
     */
    public void register(String theme, Object listener) {
        getBus(theme).register(listener);
    }

}
