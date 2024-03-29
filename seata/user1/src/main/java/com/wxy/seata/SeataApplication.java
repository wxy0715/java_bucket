package com.wxy.seata;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAutoDataSourceProxy
@EnableFeignClients(basePackages = {
        "com.wxy.seata.api",
})
public class SeataApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataApplication.class, args);
    }
}
