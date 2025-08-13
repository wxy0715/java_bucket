package com.cjree.core.basic.threadpool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTest extends LogInheritableTask{

    @Override
    public void runTask() {
        log.info("线程内日志:{}", Thread.currentThread().getId());
    }
}
