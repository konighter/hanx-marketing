package com.hzltd.module.erplus.service.event;

import java.lang.annotation.*;

/**
 * ERP 事件监听器注解
 * 用于标记基于 Guava EventBus 的监听器类
 *
 * @author 翰展科技
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ErpEventListener {

    /**
     * 主题名称，默认为 "DEFAULT"
     * 不同的主题对应不同的 EventBus 实例，实现物理隔离
     */
    String theme() default "DEFAULT";

}
