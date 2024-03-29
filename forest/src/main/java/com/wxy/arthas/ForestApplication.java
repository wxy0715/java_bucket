package com.wxy.arthas;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// forest扫描远程接口所在的包名
@ForestScan(basePackages = "com.wxy.arthas.rpc.client")
@SpringBootApplication
public class ForestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForestApplication.class, args);
    }
}
