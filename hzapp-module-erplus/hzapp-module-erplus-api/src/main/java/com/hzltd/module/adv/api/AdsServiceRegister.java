package com.hzltd.module.adv.api;


import com.hzltd.module.system.enums.AdsPlatformEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdsServiceRegister {
    AdsPlatformEnum platform();
    Class<?> serviceClass() default Void.class;
}
