package com.wxy.rabbitmq.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class RabbitmqProducer {
    public static void main(String[] args) {
        SpringApplication.run(RabbitmqProducer.class, args);
    }
}