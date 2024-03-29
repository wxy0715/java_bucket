package com.wxy.transactional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class TransactionalApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionalApplication.class, args);
    }
}
