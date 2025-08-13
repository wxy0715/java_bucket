package com.cjree.core.basic.threadpool;

import lombok.SneakyThrows;

/**
 * 线程池工具类
 */
public final class ThreadUtil {

    /**
     * 睡眠当前线程指定时间 {@param millis}
     * @param millis 睡眠时间，单位毫秒
     */
    @SneakyThrows(value = InterruptedException.class)
    public static void sleep(long millis) {
        Thread.sleep(millis);
    }
}
