package com.hzltd.module.amz.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AmzJacksonConfiguration {

    @Bean
    public Module time() {
        log.info("ThreeTenModule registed");
        return new ThreeTenModule();
    }


}
