# template 模块使用手册

## 模块概述

template 是一个项目模板示例模块，展示了如何基于 core 模块快速构建新的应用程序。

## 主要功能

- 项目结构模板
- 基础配置示例
- 常用功能集成示例

## 模块结构

```
template/
├── template-api/     # API接口定义模块
└── src/              # 实现代码模块
```

## 使用方法

1. 添加 Maven 依赖：**按需引入**

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>template-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-basic-jdk17</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-cloud-jdk17</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-rabbitmq-jdk17</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-file-jdk17</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-canal-jdk17</artifactId>
        </dependency>
```

3. 创建启动类：

```java
package com.cjree;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients(basePackages = {
        "com.cjree.some-api" // 根据feign实际修改
        ,"com.cjree.other-api" // 根据feign实际修改

})
@Slf4j
public class Application {
    public static void main(String[] args) {
        StopWatch adjust = new StopWatch("core-template");
        adjust.start();
        SpringApplication.run(Application.class, args);
        adjust.stop();
        log.info("服务启动完成,耗时：{}秒", adjust.getTime(TimeUnit.SECONDS));
    }
}
```
