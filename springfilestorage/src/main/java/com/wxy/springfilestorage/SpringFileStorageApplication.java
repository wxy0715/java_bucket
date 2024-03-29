package com.wxy.springfilestorage;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFileStorage
public class SpringFileStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringFileStorageApplication.class, args);
    }
}
