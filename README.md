# Core JDK17

## 版本说明
### 1.概述
`core-jdk17` 是一个基于 Java 17 的核心开发库，为生产级项目提供标准化、可复用的基础组件。

`core-jdk17` 由 `core-jdk8` 升级而来，在基本完整的继承了原有核心库的功能的基础上，采用多模块架构，涵盖 Web 服务、数据持久化、微服务集成、缓存、工具类、日志、文件管理等关键功能，确保系统的一致性、可维护性和扩展性。所有模块均通过 Maven 统一管理。

`core-jdk17` 将 `spring-boot` 等核心组件均升级为当前主流版本（处于官方维护中的版本），以便更好的应对信创环境下较为严格的安全审查要求。

新架构下的核心库具有以下特性：

+ **兼容信创**：可较为方便地切换至符合信创要求的组件（不影响对外接口）。
+ **标准化**：统一的依赖版本管理和接口定义。
+ **高效开发**：复用核心组件，减少重复代码。
+ **可扩展**：按需引入模块（如引入 `core-cloud` 支持微服务，`core-file` 支持文件管理）。
+ **生产环境支持**：集成日志追踪、缓存、分布式事务等特性。

### 2.架构示意图

![image-20250819085414834](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20250819085414834.png)

### 3.基础模块说明
**模块定位**

+ 💚 **核心模块**：系统基础框架与微服务核心。
+ 💠 **核心依赖模块**：核心模块依赖的必要组件。
+ 🧡 **外围功能模块**：按需引入的额外扩展能力。

##### **3.1 core-basic**
+ **功能**：基础功能
+ **定位**：核心模块
+ **主要依赖**： 
    - 集成 `core-common`、`core-cache`、`core-facade`
    - `mybatis-plus-spring-boot3-starter`：ORM 框架
+ **用途**：提供可构建单体项目的基础功能，具备 Web 服务能力和数据库操作能力。

##### **3.2 core-cloud**
+ **功能**：微服务功能
+ **定位**：核心模块
+ **主要依赖**： 
    - `spring-cloud-starter-alibaba-nacos`：服务注册与配置中心
    - `seata-spring-boot-starter`：分布式事务
+ **用途**：提供微服务功能（服务发现、配置管理）和分布式事务。

##### **3.3 core-facade**
+ **功能**：接口层
+ **定位**：核心依赖模块
+ **主要依赖**： 
    - `spring-cloud-starter-openfeign`：声明式 HTTP 客户端
    - `spring-boot-starter-web`：Web 服务支持
+ **用途**：统一管理对外接口，集成 Feign 实现服务间调用。

##### **3.4 core-model**
+ **功能**：数据模型定义
+ **定位**：核心依赖模块
+ **主要依赖**： 
    - `mybatis-plus-spring-boot3-starter`：ORM 框架（用于模型和数据库映射）
    - `knife4j-openapi3`：API 文档生成
+ **用途**：定义实体类、DTO、枚举等数据模型，同时集成 MyBatis-Plus 实现数据库映射。

##### **3.5 core-cache**
+ **功能**：缓存管理
+ **定位**：核心依赖模块
+ **主要依赖**： 
    - `redisson`：分布式锁和缓存
    - `ehcache`：本地缓存
+ **用途**：支持本地缓存（Ehcache）和分布式缓存（Redis），提供统一的缓存 API。

##### **3.6 core-common**
+ **功能**：常用工具库
+ **定位**：核心依赖模块
+ **主要依赖**： 
    - `hutool-all`：工具集
    - `fastjson`：JSON 处理
    - `commons-lang3`：Apache 公共工具
+ **用途**：提供字符串处理、集合操作、JSON 序列化等通用工具方法。

##### **3.7 core-file**
+ **功能**：文件处理与存储
+ **定位**：外围模块
+ **主要依赖**： 
    - `x-file-storage-spring`：统一文件存储接口
    - `minio`：对象存储
    - `spire`：PDF/Word/Excel 文档处理
    - `ofdrw-converter`：OFD 文件转换
+ **用途**：支持文件上传/下载、格式转换（PDF/Word/Excel/OFD）、文档内容提取。

##### **3.8 core-rabbitmq**

+ **功能**：rabbitmq消息队列
+ **定位**：外围模块
+ **主要依赖**： 
  - `spring-boot-starter-amqp`：ampq消息
+ **用途**：消息队列。

##### **3.9 core-elasticsearch**

+ **功能**：文档搜素
+ **定位**：外围模块
+ **主要依赖**： 
  - `easy-es`：简化es的操作

##### **3.10 core-canal**

+ **功能**：binlog读取,数据同步
+ **定位**：外围模块
+ **主要依赖**： 
  - `canal.client`：操作canal类

### 5.组件版本清单

| 依赖包 groupId | 依赖包 artifactId | 版本 | 备注 |
| --- | --- | --- | --- |
| **core-basic** | | | |
| com.cjree | core-common | - | 内部模块，详见模块说明。 |
| com.cjree | core-cache | - | 内部模块，详见模块说明。 |
| com.cjree | core-facade | - | 内部模块，详见模块说明。 |
| com.cjree | core-logstash | - | 内部模块，详见模块说明。 |
| org.springframework.boot | spring-boot-actuator | 3.3.12 | Spring Boot监控管理端点，暴露应用健康信息/性能指标。 |
| org.springframework.boot | spring-boot-actuator-autoconfigure | 3.3.12 | Actuator自动配置模块，提供监控组件的默认配置支持。 |
| jakarta.mail | jakarta.mail-api | - | Jakarta邮件服务标准API，用于发送和接收邮件。 |
| commons-codec | commons-codec | 1.16.1 | 编解码工具库，支持Base64/MD5/SHA等常用编码算法。 |
| commons-net | commons-net | 3.6 | 简化各种常见网络协议的编程实现 |
| org.apache.commons | commons-fileupload2-jakarta-servlet6 | 2.0.0-M3 | Jakarta Servlet 6.0规范的文件上传组件实现。 |
| ma.glasnost.orika | orika-core | 1.5.4 | 高性能对象映射框架，简化POJO间属性拷贝。 |
| com.github.xiaoymin | knife4j-openapi3-jakarta-spring-boot-starter | 4.5.0 | Swagger增强UI工具，生成可视化API文档界面。 |
| com.baomidou | mybatis-plus-spring-boot3-starter | 3.5.12 | MyBatis增强框架与Spring Boot3整合的启动器。 |
| com.baomidou | mybatis-plus-generator | 3.5.12 | MyBatis代码生成器，自动生成实体类/Mapper/Service。 |
| com.baomidou | mybatis-plus-extension | 3.5.12 | MyBatis扩展功能模块，提供分页/乐观锁等增强特性。 |
| com.baomidou | mybatis-plus-jsqlparser | 3.5.12 | SQL解析器，用于优化MyBatis Plus复杂查询功能。 |
| com.baomidou | dynamic-datasource-spring-boot3-starter | 4.3.1 | 动态数据源组件，支持多数据源切换和读写分离。 |
| com.alibaba | druid-spring-boot-starter | 1.2.23 | 阿里Druid数据库连接池的Spring Boot集成模块。 |
| com.alibaba | easyexcel | 4.0.3 | 高效Excel处理库，支持大数据量读写且内存占用低。 |
| mysql | mysql-connector-java | 8.0.11 | MySQL官方JDBC驱动，用于连接MySQL数据库服务器。 |
| p6spy | p6spy | 3.9.1 | SQL日志拦截工具，可打印完整可执行的SQL语句。 |
| net.logstash.logback | logstash-logback-encoder | 6.6 | Logback日志编码器，生成JSON格式日志供Logstash收集。 |
| org.dromara.x-file-storage | x-file-storage-spring | 2.2.1 | 统一文件存储接口，支持本地/MinIO/FTP等多种存储方式。 |
| io.minio | minio | 8.4.3 | 对象存储服务客户端，用于连接MinIO对象存储服务。 |
| javax.xml.bind | jaxb-api | 2.3.1 | Java XML绑定标准API，实现Java对象与XML互转。 |
| com.sun.xml.bind | jaxb-impl | 2.3.3 | JAXB标准的参考实现库，提供XML绑定具体功能。 |
| org.springframework.boot | spring-boot-starter-test | 3.3.12 | 测试支持库，包含JUnit/Mockito/SpringTest等测试工具。 |
| org.junit.jupiter | junit-jupiter-engine | - | JUnit5测试引擎，提供新一代单元测试运行支持。 |
| com.alibaba | transmittable-thread-local | 2.14.4 | 增强的threadlocal |
| com.github.oshi | oshi-core | 4.2.0 | 系统信息 |
| org.graylog2 | syslog | 0.9.60 | syslog |
| ch.ethz.ganymed | ganymed-ssh2 | build210 | SSH协议工具 |
| com.jcraft | jsch | 0.1.55 | 协议操作类 |
| io.github.burukeyou | jdframe | 0.0.2 | 集合操作类 |
| **core-cloud** | | | |
| com.cjree | core-cache | - | 内部模块，详见模块说明。 |
| com.alibaba.cloud | spring-cloud-starter-alibaba-nacos-config | 2023.0.1.0 | Nacos分布式配置中心客户端，实现配置动态更新。 |
| com.alibaba.cloud | spring-cloud-starter-alibaba-nacos-discovery | 2023.0.1.0 | Nacos服务注册发现组件，支持微服务实例动态治理。 |
| org.springframework.cloud | spring-cloud-starter-bootstrap | 2023.0.5 | Spring Cloud引导上下文工具，优先加载外部配置。 |
| org.springframework.cloud | spring-cloud-starter-loadbalancer | 2023.0.5 | 客户端负载均衡器，替代Ribbon实现服务调用负载均衡。 |
| org.apache.seata | seata-spring-boot-starter | 2.3.0 | 分布式事务框架Seata的Spring Boot快速集成包。 |
| com.baomidou | mybatis-plus-core | 3.5.12 | MyBatis增强核心库，提供通用Mapper/分页插件等基础功能。 |
| org.projectlombok | lombok | - | 代码简化工具，通过注解自动生成Getter/Setter等方法。 |
| org.aspectj | aspectjweaver | - | AOP切面编程支持库，提供运行时切面织入能力。 |
| org.apache.commons | commons-lang3 | - | Apache通用语言工具包，扩展Java基础类库功能。 |
| org.springframework.boot | spring-boot-starter-test | 3.3.12 | 测试支持库，包含JUnit/Mockito/SpringTest等测试工具。 |
| org.junit.jupiter | junit-jupiter- | - | JUnit5测试引擎，提供新一代单元测试运行支持。 |
| **core-elasticsearch** |  |  |  |
| org.dromara.easy-es | easy-es | 2.0.0-beta7 | 操作es |
| org.elasticsearch | elasticsearch | 7.14.0 | 操作es |
| org.elasticsearch.client | elasticsearch-rest-high-level-client | 7.14.0 | 操作es |
| **core-facade** | | | |
| com.cjree | core-model | - | 内部模块，详见模块说明。 |
| org.springframework.boot | spring-boot-starter-web | 3.3.12 | Web应用快速开发模块，集成Tomcat/Spring MVC等组件。 |
| org.springframework.cloud | spring-cloud-starter-openfeign | 2023.0.5 | 声明式HTTP客户端，简化微服务间RESTful接口调用。 |
| **core-model** | | | |
| org.projectlombok | lombok | - | 代码简化工具，通过注解自动生成Getter/Setter等方法。 |
| org.springframework.boot | spring-boot-starter-data-jpa | 3.3.12 | Spring Data JPA启动模块，简化数据库持久层操作。 |
| com.baomidou | dynamic-datasource-spring-boot3-starter | 4.3.1 | 动态数据源组件，支持多数据源切换和读写分离。 |
| com.alibaba | fastjson | 1.2.83 | 阿里JSON解析库1.x版本。 |
| com.github.xiaoymin | knife4j-openapi3-jakarta-spring-boot-starter | 4.5.0 | Swagger增强UI工具，生成可视化API文档界面。 |
| jakarta.persistence | jakarta.persistence-api | 3.1.0 | JPA规范标准API，定义对象关系映射的通用接口。 |
| **core-cache** | | | |
| com.cjree | core-common | - | 内部模块，详见模块说明。 |
| org.springframework.boot | spring-boot-starter-data-redis | 3.3.12 | Redis数据访问支持，提供连接池和模板化操作接口。 |
| org.ehcache | ehcache | - | 本地缓存框架，提供堆内/堆外多级缓存支持。 |
| org.redisson | redisson-spring-boot-starter | 3.49.0 | Redis分布式对象服务，提供分布式锁/集合等数据结构。 |
| **core-common** | | | |
| org.projectlombok | lombok | - | 代码简化工具，通过注解自动生成Getter/Setter等方法。 |
| org.slf4j | slf4j-api | - | 日志门面接口，提供统一日志调用方式。 |
| org.springframework.boot | spring-boot | 3.3.12 | Spring Boot核心模块，包含自动配置和容器启动逻辑。 |
| org.apache.commons | commons-lang3 | - | Apache通用语言工具包，扩展Java基础类库功能。 |
| commons-beanutils | commons-beanutils | 1.10.1 | JavaBean操作工具，支持属性复制/动态访问等功能。 |
| org.apache.commons | commons-collections4 | 4.4 | 增强版集合工具库，提供扩展数据结构与工具类。 |
| jakarta.servlet | jakarta.servlet-api | - | Jakarta Servlet规范标准API，定义HTTP请求处理接口。 |
| cn.hutool | hutool-all | 5.8.37 | 全功能工具包，集成HTTP客户端/加密等常用工具。 |
| com.alibaba.fastjson2 | fastjson2 | 2.0.57 | 阿里高性能JSON库2.x版本 |
| com.alibaba | fastjson | 1.2.83 | 阿里JSON解析库1.x版本。 |
| **core-file** | | | |
| com.cjree | core-common | - | 内部模块，详见模块说明。 |
| org.springframework.boot | spring-boot-starter | 3.3.12 | Spring Boot基础启动模块，提供核心自动配置支持。 |
| org.springframework.boot | spring-boot-starter-web | 3.3.12 | Web应用快速开发模块，集成Tomcat/Spring MVC等组件。 |
| com.github.xiaoymin | knife4j-openapi3-jakarta-spring-boot-starter | 4.5.0 | Swagger增强UI工具，生成可视化API文档界面。 |
| org.projectlombok | lombok | - | 代码简化工具，通过注解自动生成Getter/Setter等方法。 |
| com.baomidou | mybatis-plus-spring-boot3-starter | 3.5.12 | MyBatis增强框架与Spring Boot3整合的启动器。 |
| com.baomidou | mybatis-plus-generator | 3.5.12 | MyBatis代码生成器，自动生成实体类/Mapper/Service。 |
| com.baomidou | mybatis-plus-extension | 3.5.12 | MyBatis扩展功能模块，提供分页/乐观锁等增强特性。 |
| com.baomidou | mybatis-plus-jsqlparser | 3.5.12 | SQL解析器，用于优化MyBatis Plus复杂查询功能。 |
| org.dromara.x-file-storage | x-file-storage-spring | 2.2.1 | 统一文件存储接口，支持本地/MinIO/FTP等多种存储方式。 |
| io.minio | minio | 8.4.3 | 对象存储服务客户端，用于连接MinIO对象存储服务。 |
| org.apache.poi | poi | 4.1.2 | Microsoft Office文档处理库，支持读写传统格式。 |
| org.apache.poi | poi-ooxml | 4.1.2 | POI扩展模块，支持Office Open XML格式（xlsx/docx）。 |
| org.apache.poi | poi-scratchpad | 4.1.2 | POI扩展模块，处理旧版Office格式（如Visio/WMF）。 |
| org.apache.poi | poi-ooxml-schemas | 4.1.2 | Office Open XML底层模式定义文件。 |
| **core-canal** |  |  |  |
| com.alibaba.otter | canal.client | 1.1.0 | 操作canal |

## 集成中间件示例

| 介绍                | 模块            | 文档地址                                                     |
| ------------------- | --------------- | ------------------------------------------------------------ |
| 缓存                | `core-cache`    | https://github.com/wxy0715/java_bucket/tree/main/core/core-cache |
| binlog监听处理      | `canal`         | https://github.com/wxy0715/java_bucket/tree/main/canal       |
| ElasticSearch       | `elasticsearch` | https://github.com/wxy0715/java_bucket/tree/main/elasticsearch |
| ftp服务及客户端代理 | `ftpProxy`      | https://github.com/wxy0715/java_bucket/tree/main/ftpProxy    |
| kafka               | `kafka`         | https://github.com/wxy0715/java_bucket/tree/main/kafka       |
| mongo封装           | `mongo`         | https://github.com/wxy0715/java_bucket/tree/main/mongo       |
| 通信框架            | `netty`         | https://github.com/wxy0715/java_bucket/tree/main/netty       |
| 消息中间件          | `rabbitmq`      | https://github.com/wxy0715/java_bucket/tree/main/rabbitmq    |
| 分布式事务          | `seata`         | https://github.com/wxy0715/java_bucket/tree/main/seata       |
| 分库分表(todo)      | `shardingjdbc`  | https://github.com/wxy0715/java_bucket/tree/main/shardingjdbc |
| 定时服务(todo)      | `xxl-job`       |                                                              |

## 使用说明

### 1.工具类
core工具类已精简，基本所有工具都在hutool中集成，可用工具类可在以下文档中寻找  
文档地址：https://doc.hutool.cn/pages/index/  

### 2.线程/线程池/Async-集成链路追踪的示例

```java
    // 1.注解形式,需要带上customAsyncExecutor
 	@Async("customAsyncExecutor")

	//2.runnable形式
	@PostMapping("runnable")
    @Operation(description = "该方法适用runnable/thread")
    public Result<Object> runnable() {
        log.info("普通线程外日志:{}", Thread.currentThread().getId());
        LogInheritableTask logInheritableTask = new LogInheritableTask() {
            @Override
            public void runTask() {
                log.info("普通线程内日志:{}", Thread.currentThread().getId());
            }
        };
        logInheritableTask.start();
        return ok(ResponseCode.SUCCESS, 1L);
    }

	//3.thread形式
    @PostMapping("thread")
    @Operation(description = "自定义线程thread")
    public Result<Object> thread() {
        log.info("thread线程外日志:{}", Thread.currentThread().getId());
        ThreadTest logInheritableTask = new ThreadTest();
        logInheritableTask.start();
        return ok(ResponseCode.SUCCESS, 1L);
    }

	//4.completableFuture形式
    @PostMapping("completableFuture")
    @Operation(description = "completableFuture")
    public Result<Object> completableFuture() {
        log.info("异步线程外日志:{}", Thread.currentThread().getId());
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.builder().threadFactory("test").build();
        CompletableFuture.runAsync(new LogInheritableTask() {
            @Override
            public void runTask() {
                log.info("异步线程内日志:{}", Thread.currentThread().getId());
            }
        },threadPoolExecutor);
        return ok(ResponseCode.SUCCESS, 1L);
    }

	//5.线程池形式
    @PostMapping("threadPool")
    @Operation(description = "线程池")
    public Result<Object> threadPool() {
        log.info("线程池外日志:{}", Thread.currentThread().getId());
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.builder().threadFactory("test").build();
        threadPoolExecutor.execute(new LogInheritableTask() {
            @Override
            public void runTask() {
                log.info("线程池内日志:{}", Thread.currentThread().getId());
            }
        });
        return ok(ResponseCode.SUCCESS, 1L);
    }
```

### 3.hutool-Http请求集成链接追踪示例

```java
// get请求
HttpRequest request = HttpRequest.get("https://www.baidu.com")
  .addInterceptor(new TLogHutoolhttpInterceptor()) // 使用http请求公司内部接口时需要添加，用于传递链路追踪ID
  .timeout(20000);
log.info("hutool请求日志:{}",request.toString());
String body = request.execute().body();
log.info("hutool请求结果:{}",body);
```

具体参考:https://doc.hutool.cn/pages/HttpUtil

### 4.编译加密
原maven集成方式已不支持，使用一下命令对jar报进行编译
```java
java -jar classfinal-fatjar-1.3.2.jar -file {文件名称}.jar  -packages com.cjree -pwd # -Y
```

### 5.操作集合框架

参考链接: https://mp.weixin.qq.com/s/DtwrYyFGNnFZow7fLpawWA
