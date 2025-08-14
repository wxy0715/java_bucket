package com.cjree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

@SpringBootApplication
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EsApplication.class);
        application.run(args);
    }
}
