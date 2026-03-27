package com.hzltd.module.erplus.api.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hzltd.module.spapi.api.ServiceRegister;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class CrossApiRegistrationPostProcessor implements BeanPostProcessor {

    private final Map<Class<?>, AbsCrossApiServiceFactory> serviceFactoryMap = new HashMap<>();
    @Autowired
    public CrossApiRegistrationPostProcessor(List<AbsCrossApiServiceFactory> registries) {
        registries.forEach(factory -> serviceFactoryMap.put(factory.getCrossApiServiceClass(), factory));
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        ServiceRegister serviceRegister = bean.getClass().getAnnotation(ServiceRegister.class);
        if (serviceRegister == null) {
            return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        }
        CrossPlatformEnum platform = serviceRegister.platform();
        AbsCrossApiServiceFactory factory = serviceFactoryMap.get(serviceRegister.serviceClass());
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for service class: " + serviceRegister.serviceClass());
        }
        factory.registerCrossApiService(platform, bean);


        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
