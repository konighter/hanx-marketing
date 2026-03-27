package com.hzltd.module.erplus.adv.adapter;

import com.hzltd.module.adv.api.AdsServiceRegister;
import com.hzltd.module.system.enums.AdsPlatformEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdsApiRegistrationPostProcessor implements BeanPostProcessor {

    private final Map<Class<?>, AbsAdsApiServiceFactory> serviceFactoryMap = new HashMap<>();
    @Autowired
    public AdsApiRegistrationPostProcessor(List<AbsAdsApiServiceFactory> registries) {
        registries.forEach(factory -> serviceFactoryMap.put(factory.getAdsApiServiceClass(), factory));
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        AdsServiceRegister serviceRegister = bean.getClass().getAnnotation(AdsServiceRegister.class);
        if (serviceRegister == null) {
            return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        }
        AdsPlatformEnum platform = serviceRegister.platform();
        AbsAdsApiServiceFactory factory = serviceFactoryMap.get(serviceRegister.serviceClass());
        if (factory == null) {
            throw new IllegalArgumentException("No factory found for service class: " + serviceRegister.serviceClass());
        }
        factory.registerAdsApiService(platform, bean);


        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
