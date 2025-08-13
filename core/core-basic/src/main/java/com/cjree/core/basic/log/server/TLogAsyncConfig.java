package com.cjree.core.basic.log.server;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync // 开启异步支持
@Slf4j
public class TLogAsyncConfig {
    @Bean("customAsyncExecutor")
    public Executor customAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("customAsyncExecutor-");
        executor.setTaskDecorator(runnable -> {
            // 复制父线程MDC上下文
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> originalChildMdc = MDC.getCopyOfContextMap();
                try {
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    }
                    runnable.run();
                } finally {
                    // 恢复子线程MDC
                    if (originalChildMdc != null) {
                        MDC.setContextMap(originalChildMdc);
                    } else {
                        MDC.clear();
                    }
                }
            };
        });
        executor.initialize();
        return executor;
    }
}
