package com.cjree.core.basic.threadpool;

import cn.hutool.core.lang.Assert;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * 线程池 {@link ThreadPoolExecutor} 构建器, 构建者模式
 */
public final class ThreadPoolBuilder implements Builder<ThreadPoolExecutor> {

    /**
     * 核心线程数 默认cpu核数*2
     * cpu密集的赋值为cpu核数*2
     * io密集的赋值为cpu核数+1
     */
    private int corePoolSize = calculateCoreNum();

    /**
     * 最大线程数
     */
    private int maximumPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * 非核心线程空闲超过keepAliveTime会被回收
     */
    private long keepAliveTime = 30000L;

    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 默认队列
     */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(4096);

    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 是否守护线程,true的时候线程会随JVM的关闭而关闭
     */
    private boolean isDaemon = false;

    /**
     * 线程前缀
     */
    private String threadNamePrefix;

    /**
     * 线程工厂
     */
    private ThreadFactory threadFactory;

    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).multiply(new BigDecimal("2")).intValue();
    }

    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        if (maximumPoolSize < this.corePoolSize) {
            this.corePoolSize = maximumPoolSize;
        }
        return this;
    }

    public ThreadPoolBuilder threadFactory(String threadNamePrefix, Boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
        return this;
    }

    public ThreadPoolBuilder threadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    @Override
    public ThreadPoolExecutor build() {
        if (threadFactory == null) {
            Assert.notEmpty(threadNamePrefix, "The thread name prefix cannot be empty or an empty string.");
            threadFactory = ThreadFactoryBuilder.builder().prefix(threadNamePrefix).daemon(isDaemon).build();
        }
        ThreadPoolExecutor executorService;
        try {
            executorService = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    timeUnit,
                    workQueue,
                    threadFactory,
                    rejectedExecutionHandler);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Error creating thread pool parameter.", ex);
        }
        return executorService;
    }
}
