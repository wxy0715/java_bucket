package com.cjree;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EsMapperScan("com.cjree.easyes.esmapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EsApplication.class);
        application.run(args);
    }
}
