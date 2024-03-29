package com.wxy.retry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry // 启用重试
@EnableAspectJAutoProxy(exposeProxy = true)
public class RetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }
}
