## 模块基础功能

| 介绍                 | 模块                   |
|--------------------|----------------------|
| 常用组件封装             | `common`             |
| 对外服务               | `api`                |
| windows+linux      | `changePwd`          |
| 缓存                 | `distributeLock`     |
| elasticsearch      | `elasticsearch`      |
| 监听日志文件             | `filelsten`          |
| httpclient封装       | `forest`             |
| ftp服务及客户端代理        | `ftpProxy`           |
| java核心技术           | `javacore`           |
| kafka              | `kafka`              |
| 日志标签               | `logstash-tlog`      |
| 数据库操作封装            | `mybatisplus`        |
| 数据库操作封装            | `mybatisplus`        |
| 事务处理               | `transactional`      |
| 通信框架               | `netty`              |
| 消息中间件              | `rabbitmq`           |
| 分布式事务              | `seata`              |
| 分库分表               | `shardingjdbc`       |
| 服务重试               | `spring-retry`       |
| 文件操作               | `springfilestorage`  |
| 定时服务               | `xxl-job`            |

## Undertow使用方式

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-tomcat</artifactId>
    </exclusion>
  </exclusions>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

```yml
server:
  # undertow 配置
  undertow:
    # HTTP post内容的最大大小。当值为-1时，默认值为大小是无限的
    max-http-post-size: -1
    # 以下的配置会影响buffer,这些buffer会用于服务器连接的IO操作,有点类似netty的池化内存管理
    # 每块buffer的空间大小,越小的空间被利用越充分
    buffer-size: 512
    # 是否分配的直接内存
    direct-buffers: true
    threads:
      # 设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接, 默认设置每个CPU核心一个线程
      io: 8
      # 阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程,它的值设置取决于系统的负载
      worker: 256
```
