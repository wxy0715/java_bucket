package com.wxy.seata1;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAutoDataSourceProxy
public class Seata2Application {
    public static void main(String[] args) {
        SpringApplication.run(Seata2Application.class, args);
    }
}
