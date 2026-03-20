package com.hzltd.module.erplus.ai.mas.execution;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for retrying failed node execution operations.
 */
@Data
@Builder
public class RetryPolicy {
    /**
     * Maximum number of retry attempts.
     */
    private int maxAttempts;

    /**
     * Initial backoff delay in milliseconds.
     */
    private long initialBackoffMs;

    /**
     * Multiplier for exponential backoff.
     */
    private double backoffMultiplier;

    /**
     * Maximum backoff delay in milliseconds.
     */
    private long maxBackoffMs;

    /**
     * Default policy: 3 attempts, starting at 1s, doubling up to 10s.
     */
    public static RetryPolicy defaultPolicy() {
        return RetryPolicy.builder()
                .maxAttempts(3)
                .initialBackoffMs(1000)
                .backoffMultiplier(2.0)
                .maxBackoffMs(10000)
                .build();
    }
    
    /**
     * No-retry policy.
     */
    public static RetryPolicy none() {
        return RetryPolicy.builder()
                .maxAttempts(0)
                .build();
    }
}
