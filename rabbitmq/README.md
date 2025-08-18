# rabbitmq 模块使用手册

## 模块概述

rabbitmq 是一个 RabbitMQ 消息队列示例模块，展示了如何在项目中集成和使用 RabbitMQ 消息队列功能。该模块分为生产者（producer）和消费者（consumer）两个子模块。

## 主要功能

- RabbitMQ 消息生产
- RabbitMQ 消息消费
- RabbitMQ 配置管理
- 消息确认机制

## 依赖模块

- core-basic
- core-rabbitmq

## 模块结构

```
rabbitmq/
├── producer/     # 消息生产者模块
└── consumer/     # 消息消费者模块
```

## 配置文件

模块使用 `application.yml` 配置文件进行配置：

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
    #虚拟host 可以不设置,使用server默认host
    virtual-host: rabbit
    #确认消息已发送到交换机(Exchange)
    #publisher-confirms: true
    publisher-confirm-type: correlated
    #确认消息已发送到队列(Queue)
    publisher-returns: true
```

## 使用方法

1. 添加 Maven 依赖：

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-basic-jdk17</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-rabbitmq-jdk17</artifactId>
        </dependency>
```

2. 更多参考代码