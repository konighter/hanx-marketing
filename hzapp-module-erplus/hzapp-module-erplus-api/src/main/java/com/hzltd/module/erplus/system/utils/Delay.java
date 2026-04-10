package com.hzltd.module.erplus.system.utils;


import java.util.concurrent.Callable;

public class Delay {

    public static <T> T delay(Callable<T> callable, long delayMillis) {
        try {
            Thread.sleep(delayMillis);
            return callable.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T retry(Callable<T> callable, int maxRetries, long delayMillis) {
        Exception lastException = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                return callable.call();
            } catch (Exception e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                }
            }
        }
        throw new RuntimeException("Execution failed after " + maxRetries + " retries", lastException);
    }

}
