package com.hzltd.module.erplus.api.annotations;

import com.hzltd.module.system.enums.CrossPlatformEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceRegister {

    CrossPlatformEnum platform();
    Class<?> serviceClass() default Void.class;

}
