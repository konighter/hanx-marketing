package com.hzltd.module.erplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.hzltd.module.erplus.ozon"})
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class);
    }
}
