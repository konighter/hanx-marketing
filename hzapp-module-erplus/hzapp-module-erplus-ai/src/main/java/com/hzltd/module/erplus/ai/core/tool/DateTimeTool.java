package com.hzltd.module.erplus.ai.core.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * 日期时间工具，用于给 AI 提供当前时间
 */
@Configuration
public class DateTimeTool {

    @Bean
    @org.springframework.context.annotation.Description("获取当前系统日期和时间")
    public Function<DateTimeRequest, String> getCurrentDateTime() {
        return request -> {
            LocalDateTime now = LocalDateTime.now();
            return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        };
    }

    public static class DateTimeRequest {
        // 可以为空
    }
}
