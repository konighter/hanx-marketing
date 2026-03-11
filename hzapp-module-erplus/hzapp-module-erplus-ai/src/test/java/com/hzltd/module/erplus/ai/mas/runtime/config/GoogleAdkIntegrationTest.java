package com.hzltd.module.erplus.ai.mas.runtime.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = GoogleAdkConfig.class)
public class GoogleAdkIntegrationTest {

    @Test
    public void testAdkContextLoads() {
        // Simple test to ensure the ADK classes are available on the classpath
        // and the spring context can initialize our config wrapper.
        GoogleAdkConfig config = new GoogleAdkConfig();
        assertNotNull(config, "Config should instantiate successfully with ADK on classpath");
        config.initAdkEnvironment();
    }
}
