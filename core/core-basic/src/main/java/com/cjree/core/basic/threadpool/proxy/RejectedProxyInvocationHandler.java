package com.cjree.core.basic.threadpool.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池拒绝策略代理执行器
 */
@Slf4j
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    /**
     * Target object
     */
    private final Object target;

    /**
     * Reject count
     */
    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();
        try {
            log.error("线程池执行拒绝策略");
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}