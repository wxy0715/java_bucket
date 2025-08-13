# Core JDK17

## 更新纪要
| 更新时间 | 更新内容 | 备注              |
| --- | --- |-----------------|
| 2025-08-12 | 增加jdframe框架,文件上传文档 | 操作集合框架,代替stream |
| 2025-08-12 | RID与TraceId合并，统一为traceId |                 |
| 2025-07-15 | jdk1.8升级为jdk17 | 详见[使用说明](#使用说明) |

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
![](http://git.seaskysh.com.cn:8800/java/core-jdk17/-/raw/main/image.png)


### 3.模块说明
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



### 4.组件清单
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
| commons-codec | commons-codec | - | 编解码工具库，支持Base64/MD5/SHA等常用编码算法。 |
| org.apache.httpcomponents | httpcore | - | HTTP协议核心实现，提供底层HTTP报文处理能力。 |
| org.apache.httpcomponents.client5 | httpclient5 | - | HTTP客户端库5.x版本，支持HTTP/1.1和HTTP/2协议。 |
| org.apache.commons | commons-fileupload2-jakarta-servlet6 | 2.0.0-M3 | Jakarta Servlet 6.0规范的文件上传组件实现。 |
| ma.glasnost.orika | orika-core | 1.5.4 | 高性能对象映射框架，简化POJO间属性拷贝。 |
| org.apache.shiro | shiro-spring | 2.0.3 | 集成Spring的Apache Shiro安全框架，提供认证授权功能。 |
| org.apache.shiro | shiro-core | 2.0.3 | Shiro安全框架核心库，包含认证/授权/会话管理。 |
| org.apache.shiro | shiro-web | 2.0.3 | Shiro的Web支持模块，提供Servlet过滤器等Web集成。 |
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
| org.junit.jupiter | junit-jupiter-engine | - | JUnit5测试引擎，提供新一代单元测试运行支持。 |
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
| org.springframework.boot | spring-boot-starter-integration | 3.3.12 | 企业集成模式支持，提供消息通道/路由等EIP实现。 |
| org.springframework.integration | spring-integration-redis | - | Spring Integration对Redis的扩展，实现消息队列等功能。 |
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
| **core-logstash** | | | |
| org.springframework | spring-context | - | Spring核心容器模块，提供依赖注入与事件驱动支持。 |
| org.springframework.boot | spring-boot | 3.3.12 | Spring Boot核心模块，包含自动配置和容器启动逻辑。 |
| jakarta.servlet | jakarta.servlet-api | - | Jakarta Servlet规范标准API，定义HTTP请求处理接口。 |
| net.logstash.logback | logstash-logback-encoder | 6.6 | Logback日志编码器，生成JSON格式日志供Logstash收集。 |
| ch.qos.logback.db | logback-classic-db | 1.2.11.1 | Logback扩展，支持将日志记录到数据库表中。 |
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
| e-iceblue | spire.pdf | 10.10.7 | 商业PDF处理库（需授权），支持生成/转换/打印PDF。 |
| e-iceblue | spire.doc | 12.6.2 | 商业Word处理库，支持doc/docx格式操作。 |
| e-iceblue | spire.xls | 14.6.2 | 商业Excel处理库，支持xls/xlsx格式读写。 |
| e-iceblue | spire.barcode | 5.1.11 | 商业条形码生成与识别库。 |
| e-iceblue | spire.presentation | 9.6.0 | 商业PPT处理库，支持PPT/PPTX操作。 |
| org.ofdrw | ofdrw-converter | 2.3.3 | 国产OFD版式文档转换工具。 |

## 使用说明
### 1.工具类
原core工具类已精简，基本所有工具都在hutool中集成，可用工具类可在以下文档中寻找  
文档地址：https://doc.hutool.cn/pages/index/  

### 2.线程/线程池/Async使用方式

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

### 3.Http请求示例(hutool)

```java
// get请求
HttpRequest request = HttpRequest.get("https://www.baidu.com")
  .addInterceptor(new TLogHutoolhttpInterceptor()) // 使用http请求公司内部接口时需要添加，用于传递链路追踪ID
  .timeout(20000);
log.info("hutool请求日志:{}",request.toString());
String body = request.execute().body();
log.info("hutool请求结果:{}",body);

// post请求 todo

```

具体参考:https://doc.hutool.cn/pages/HttpUtil

### 4.RabbitMq使用示例

#### 引入依赖

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-mq-jdk17</artifactId>
        </dependency>
```

#### 直连交换机

配置交换机和队列

```java

@Configuration
public class DirectRabbitMqConfig {

    //队列 起名：TestDirectQueue
    @Bean
    public Queue TestDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);
        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("TestDirectQueue",true);
    }


    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange TestDirectExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange("TestDirectExchange",true,false);
    }

    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
    }
}
```

生产者

```java
    @Resource
    private RabbitMqUtils rabbitMqUtils;

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String,Object> map = new HashMap<>();
        map.put("messageId",messageId);
        map.put("messageData",messageData);
        map.put("createTime",createTime);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitMqUtils.send("TestDirectExchange", "TestDirectRouting", map);
        return "ok";
    }
```

消费者

```java
@Component
@RabbitListener(queues = "TestDirectQueue")
@Slf4j
public class DirectReceiver {

    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("DirectReceiver消费者收到消息  : " + o.toString());
        });
    }
}
```

#### 主题交换机

配置交换机和队列

```java
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    //绑定键
    public final static String man = "topic.man";
    public final static String woman = "topic.woman";

    @Bean
    public Queue firstQueue() {
        return new Queue(TopicRabbitConfig.man);
    }

    @Bean
    public Queue secondQueue() {
        return new Queue(TopicRabbitConfig.woman);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("topicExchange");
    }


    //将firstQueue和topicExchange绑定,而且绑定的键值为topic.man
    //这样只要是消息携带的路由键是topic.man,才会分发到该队列
    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(firstQueue()).to(exchange()).with(man);
    }

    //将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
    // 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
    @Bean
    Binding bindingExchangeMessage2() {
        return BindingBuilder.bind(secondQueue()).to(exchange()).with("topic.#");
    }
}
```

生产者

```java
    @Resource
    private RabbitMqUtils rabbitMqUtils;

    @GetMapping("/sendTopicMessage1")
    public String sendTopicMessage1() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: M A N ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> manMap = new HashMap<>();
        manMap.put("messageId", messageId);
        manMap.put("messageData", messageData);
        manMap.put("createTime", createTime);
        rabbitMqUtils.send("topicExchange", "topic.man", manMap);
        return "ok";
    }

    @GetMapping("/sendTopicMessage2")
    public String sendTopicMessage2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: woman is all ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> womanMap = new HashMap<>();
        womanMap.put("messageId", messageId);
        womanMap.put("messageData", messageData);
        womanMap.put("createTime", createTime);
        rabbitMqUtils.send("topicExchange", "topic.woman", womanMap);
        return "ok";
    }

```

消费者

```java
@Component
@RabbitListener(queues = "topic.man")
@Slf4j
public class TopicManReceiver {
    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("TopicManReceiver消费者收到消息  : " + o.toString());
        });
    }
}

@Component
@RabbitListener(queues = "topic.woman")
@Slf4j
public class TopicTotalReceiver {
    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("TopicTotalReceiver消费者收到消息  : " + o.toString());
        });
    }
}
```

#### 扇形交换机

配置交换机和队列

```java
@Configuration
public class FanoutRabbitConfig {
    /**
     *  创建三个队列 ：fanout.A   fanout.B  fanout.C
     *  将三个队列都绑定在交换机 fanoutExchange 上
     *  因为是扇型交换机, 路由键无需配置,配置也不起作用
     */
    @Bean
    public Queue queueA() {
        return new Queue("fanout.A");
    }

    @Bean
    public Queue queueB() {
        return new Queue("fanout.B");
    }

    @Bean
    public Queue queueC() {
        return new Queue("fanout.C");
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    Binding bindingExchangeA() {
        return BindingBuilder.bind(queueA()).to(fanoutExchange());
    }

    @Bean
    Binding bindingExchangeB() {
        return BindingBuilder.bind(queueB()).to(fanoutExchange());
    }

    @Bean
    Binding bindingExchangeC() {
        return BindingBuilder.bind(queueC()).to(fanoutExchange());
    }
}
```

生产者

```java
    @Resource
    private RabbitMqUtils rabbitMqUtils;

    @GetMapping("/sendFanoutMessage")
    public String sendFanoutMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: testFanoutMessage ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitMqUtils.send("fanoutExchange", null, map);
        return "ok";
    }
```

消费者

```java
@Component
@RabbitListener(queues = "fanout.A")
@Slf4j
public class FanoutReceiverA {
    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("FanoutReceiverA消费者收到消息  : " + o.toString());
        });
    }
}

@Component
@RabbitListener(queues = "fanout.B")
@Slf4j
public class FanoutReceiverB {
    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("FanoutReceiverB消费者收到消息  : " + o.toString());
        });
    }
}

@Component
@RabbitListener(queues = "fanout.C")
@Slf4j
public class FanoutReceiverC {
    @RabbitHandler
    public void process(TLogMqWrapBean tLogMqWrapBean) {
        TLogMqConsumerProcessor.process(tLogMqWrapBean, (TLogMqRunner<Map>) o -> {
            //业务操作
            log.info("FanoutReceiverC消费者收到消息  : " + o.toString());
        });
    }
}
```

### 5.缓存使用方式

具体更多方法参考:base.com.cjree.core.cache.CacheCommand

```java
// 普通key-value
CacheUtil.getCacheCommand().set("key1", "value1");
Object o = CacheUtil.getCacheCommand().get("key1");
System.out.println(o);

// 批量key-value
Map<String, Serializable> keys = new HashMap<>();
keys.put("keys1", "value1_斯1额");
keys.put("keys2", "value2_斯1额");
CacheUtil.getCacheCommand().setBatch(keys);
List<Object> batch = CacheUtil.getCacheCommand().getBatch(Arrays.asList("keys1", "keys2"));
for (Object key : batch) {
  System.out.println(key);
}
```

### 6.编译加密
原maven集成方式已不支持，使用一下命令对jar报进行编译，使用流水线进行编译的会自行加密可以不管  
```java
java -jar classfinal-fatjar-1.3.2.jar -file {文件名称}.jar  -packages com.cjree -pwd # -Y
```

### 7.老版本feign facade二方包引入
例如引入basedata2 需要排除老版本的swagger依赖
如果原facade的依赖较少，例如basedata2，直接引入没有问题，facade依赖项过多产生冲突可能仍需处理
```java
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>base-data-api</artifactId>
            <version>2.0.2-SNAPSHOT</version>
            <exclusions>
                <exclusion>
                    <artifactId>knife4j-spring-boot-starter</artifactId>
                    <groupId>com.github.xiaoymin</groupId>
                </exclusion>
            </exclusions>
        </dependency>
```
### 8.分布式锁

```java
RLock lock = redissonClient.getLock(key);
try {
  lock.lock();
  // 业务
} catch (DataVerifyException dataVerifyException) {
  log.error("项目冻结失败",dataVerifyException);
  ExceptionUtil.error("项目冻结失败"+dataVerifyException.getMessage());
} catch (Exception e){
  log.error("项目冻结失败", e);
  ExceptionUtil.error("项目冻结失败,请重试");
}finally {
  lock.unlock();
}
```

### 9.操作集合框架

参考链接: https://mp.weixin.qq.com/s/DtwrYyFGNnFZow7fLpawWA

### 10.文件上传

介绍: 单纯的文件上传下载,不需要后端参与,只需要前端处理即可

https://www.yuque.com/fuwoquxuexi/gyqger/hokfvqdccyz7kbwf?singleDoc# 《前端附件改造》

接口文档: http://apipost.seaskysh.com/docs/preview/f1fe1216cf4c5961/037d532d87e52590

使用方式:引入依赖即可

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-file-jdk17</artifactId>
        </dependency>
```

数据库表:file_detail
