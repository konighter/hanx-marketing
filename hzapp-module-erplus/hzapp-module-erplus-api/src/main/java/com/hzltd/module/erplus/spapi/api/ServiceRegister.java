package com.hzltd.module.erplus.spapi.api;

import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceRegister {

    CrossPlatformEnum platform();
    Class<?> serviceClass() default Void.class;

}
