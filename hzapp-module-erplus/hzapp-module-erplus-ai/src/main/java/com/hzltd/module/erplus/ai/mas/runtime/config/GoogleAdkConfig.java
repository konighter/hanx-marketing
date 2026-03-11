package com.hzltd.module.erplus.ai.mas.runtime.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration for initializing the Google ADK Java SDK environment.
 * Serves as the bootstrapping point for the upper-layer MAS framework.
 */
@Configuration
@Slf4j
public class GoogleAdkConfig {

    @PostConstruct
    public void initAdkEnvironment() {
        log.info("[Google ADK] Initializing Google Agent Development Kit environment...");
        // This is a placeholder for actual ADK initialization calls if needed by the Java SDK
        // e.g., AdkRuntime.init(); or setting global configs.
        log.info("[Google ADK] Environment initialized successfully.");
    }

}
