package com.wxy.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DistributeLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributeLockApplication.class, args);
    }

}
