package com.hzltd.module.erplus.service.executor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorService {

    /**
     * 发布任务提交线程池
     */
    private static final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(5, 10, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(100)));


    @Getter
    private static final ThreadPoolExecutor callbackExecutorService = new ThreadPoolExecutor(1, 5, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(100));


    public static <T> ListenableFuture<T> submitProductPublishTask(Callable<T> task) {
        return listeningExecutorService.submit(task);
    }

}
