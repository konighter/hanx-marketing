package com.hzltd.module.erplus.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跨平台 API 日志注解
 * 用于拦截并记录接口调用的 shopId、平台、方法名及成功状态
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossplatformApiLog {
    /**
     * 平台名称，如 "SP-API" 或 "AmazonAds"
     * 如果为空，切面将尝试根据类名自动识别
     */
    String platform() default "";
}
