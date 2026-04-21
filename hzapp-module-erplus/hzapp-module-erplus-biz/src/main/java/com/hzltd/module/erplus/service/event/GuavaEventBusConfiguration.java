package com.hzltd.module.erplus.service.event;

import com.hzltd.module.erplus.event.ErpEventBus;
import com.hzltd.module.erplus.event.ErpEventListener;

import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * Guava EventBus 自动注册配置
 * 扫描带有 @ErpEventListener 注解的 Bean 并注册到对应的 ErpEventBus 主题中
 *
 * @author 翰展科技
 */
@Configuration
public class GuavaEventBusConfiguration implements BeanPostProcessor {

    @Resource
    private ErpEventBus erpEventBus;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取类上的注解
        ErpEventListener annotation = bean.getClass().getAnnotation(ErpEventListener.class);
        if (annotation != null) {
            // 自动注册到指定的主题
            erpEventBus.register(annotation.theme(), bean);
        }
        return bean;
    }

}
