package com.wxy.rabbitmq.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class RabbitmqConsumer {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqConsumer.class, args);
    }
}