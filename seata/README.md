# Docker-Compose服务端搭建

## NACOS+MYSQL+seata1.5.2

[docker-compos搭建mysql8](https://gitee.com/wxy0715/docker-compose/blob/main/mysql/%E6%90%AD%E5%BB%BAmysql8.md)

[Nacos安装](https://gitee.com/wxy0715/docker-compose/blob/main/nacos/%E6%90%AD%E5%BB%BAnacos.md)

[seata安装](https://gitee.com/wxy0715/docker-compose/blob/main/seata/seata%E6%9C%8D%E5%8A%A1%E6%90%AD%E5%BB%BA.md)

# 客户端调用

> 示例:https://gitee.com/wxy0715/project/tree/master/seata

## 依赖导入

```xml
        <!-- Seata -->
				<dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>*</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>io.seata</groupId>
                    <artifactId>seata-spring-boot-starter</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-spring-boot-starter</artifactId>
        </dependency>

        <!-- SpringCloud Alibaba Nacos -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>log4j-api</artifactId>
                    <groupId>org.apache.logging.log4j</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>log4j-to-slf4j</artifactId>
                    <groupId>org.apache.logging.log4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```

## user1模块

### 增加事务表和测试user表

```sql
CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    sex CHAR(3) NULL DEFAULT '男' COMMENT '性别',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    wallets VARCHAR(3000) NULL DEFAULT NULL COMMENT '钱包',
    other_info VARCHAR(3000) NULL DEFAULT NULL COMMENT '其他信息',
    tenant_id varchar(100) default NULL COMMENT '租户ID',
    version INT(11) NULL DEFAULT 1 COMMENT '乐观锁',
    available VARCHAR(3) NOT NULL default 'YES' comment '逻辑删除字段 YES/NO',
    PRIMARY KEY (id)
);

-- for AT mode you must to init this sql for you business database. the seata server not need it.
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
```

### bootstrap.yml

```yml
server:
 port: 7001

spring:
  cloud:
    nacos:
      discovery:
        server-addr: ip:8848
        group: DEFAULT_GROUP
        weight: 0
      config:
        server-addr: ip:8848
        group: DEFAULT_GROUP

seata:
  enabled: true
  # seata-spring-boot-starter的数据源自动代理
  enable-auto-data-source-proxy: true
  # Seata 事务组编号，用于 TC 集群名
  tx-service-group: seata-tx-group
  config:
    type: nacos
    nacos:
      server-addr: ip:8848
      group: SEATA_GROUP
      namespace: 9e70a40c-559f-40ee-a1af-1865e41adb6b
      username: nacos
      password: nacos
      data-id: seata-server.properties
  registry:
    type: nacos
    nacos:
      server-addr: ip:8848
      group: SEATA_GROUP
      namespace: 9e70a40c-559f-40ee-a1af-1865e41adb6b
      username: nacos
      password: nacos
      cluster: default
```

### application.yml

```yml
mybatis-plus:
  # 启动时是否检查 MyBatis XML 文件的存在，默认不检查
  checkConfigLocation: false
  global-config:
    db-config:
      # 主键类型
      # AUTO 自增 NONE 空 INPUT 用户输入 ASSIGN_ID 雪花 ASSIGN_UUID 唯一 UUID
      idType: ASSIGN_ID
      logic-delete-field: available # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 'NO'
      logic-not-delete-value: 'YES'
      insertStrategy: NOT_NULL
      updateStrategy: NOT_NULL
      where-strategy: NOT_NULL
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler

spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          username: root
          password: root
          url: jdbc:mysql://ip:3306/test1?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
          driver-class-name: com.mysql.jdbc.Driver
    hikari:
      connection-timeout: 60000
      validation-timeout: 3000
      idle-timeout: 60000 # 此属性控制允许连接在池中保持空闲状态的最长时间
      login-timeout: 5
      max-lifetime: 60000
      maximum-pool-size: 10
      minimum-idle: 10
      read-only: false
      connection-test-query: select 1
  application:
    name: user1

ribbon:
  ReadTimeout: 10000
  ConnectTimeout: 10000
```

### 启动类

```java
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAutoDataSourceProxy  // seata生效注解 默认AT模式
@EnableFeignClients(basePackages = {
        "com.wxy.seata.api",
})  // feign获取user2模块
public class SeataApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeataApplication.class, args);
    }
}
```

### 测试

```java
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IUserTestController users; // user2 模块的service

    @GlobalTransactional(rollbackFor = Exception.class)  // 分布式事务
    @Override
    public void save(){
        User user = new User();
        user.setAge(AgeEnum.ONE);
        user.setName("1");
        baseMapper.insert(user);
        users.save();
    }

}
```

## user2模块

### 增加事务表和测试user表

```sql
CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    sex CHAR(3) NULL DEFAULT '男' COMMENT '性别',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    wallets VARCHAR(3000) NULL DEFAULT NULL COMMENT '钱包',
    other_info VARCHAR(3000) NULL DEFAULT NULL COMMENT '其他信息',
    tenant_id varchar(100) default NULL COMMENT '租户ID',
    version INT(11) NULL DEFAULT 1 COMMENT '乐观锁',
    available VARCHAR(3) NOT NULL default 'YES' comment '逻辑删除字段 YES/NO',
    PRIMARY KEY (id)
);

-- for AT mode you must to init this sql for you business database. the seata server not need it.
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
```

### bootstrap.yml

```yml
server:
 port: 7002

spring:
  cloud:
    nacos:
      server-addr: ip:8848
      discovery:
        group: DEFAULT_GROUP
      config:
        group: DEFAULT_GROUP

seata:
  enabled: true
  # seata-spring-boot-starter的数据源自动代理
  enable-auto-data-source-proxy: true
  # Seata 事务组编号，用于 TC 集群名
  tx-service-group: seata-tx-group
  config:
    type: nacos
    nacos:
      server-addr: ip:8848
      group: SEATA_GROUP
      namespace: 9e70a40c-559f-40ee-a1af-1865e41adb6b
      username: nacos
      password: nacos
      data-id: seata-server.properties
  registry:
    type: nacos
    nacos:
      server-addr: ip:8848
      group: SEATA_GROUP
      namespace: 9e70a40c-559f-40ee-a1af-1865e41adb6b
      username: nacos
      password: nacos
      cluster: default
```

### application.yml

```yml
mybatis-plus:
  # 启动时是否检查 MyBatis XML 文件的存在，默认不检查
  checkConfigLocation: false
  global-config:
    db-config:
      # 主键类型
      # AUTO 自增 NONE 空 INPUT 用户输入 ASSIGN_ID 雪花 ASSIGN_UUID 唯一 UUID
      idType: ASSIGN_ID
      logic-delete-field: available # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 'NO'
      logic-not-delete-value: 'YES'
      insertStrategy: NOT_NULL
      updateStrategy: NOT_NULL
      where-strategy: NOT_NULL
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler

spring:
  application:
    name: user2
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          username: root
          password: root
          url: jdbc:mysql://ip:3306/test2?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
          driver-class-name: com.mysql.jdbc.Driver
    hikari:
      connection-timeout: 60000
      validation-timeout: 3000
      idle-timeout: 60000 # 此属性控制允许连接在池中保持空闲状态的最长时间
      login-timeout: 5
      max-lifetime: 60000
      maximum-pool-size: 10
      minimum-idle: 10
      read-only: false
      connection-test-query: select 1
ribbon:
  ReadTimeout: 10000
  ConnectTimeout: 10000
```

### 启动类

```java
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAutoDataSourceProxy
public class Seata1Application {
    public static void main(String[] args) {
        SpringApplication.run(Seata1Application.class, args);
    }
}
```

### 测试

```java
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
  
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void save(){
        User user = new User();
        user.setAge(AgeEnum.THREE);
        user.setName("2");
        baseMapper.insert(user);
        int i = 10/0;   // 抛出异常 事务生效
    }

}
```



## 测试

启动user1模块和user2模块 调用user1的save接口 发现user2抛出异常,此时user1的数据进行回滚



# 达梦数据库支持

地址:https://gitee.com/wxy0715/project/tree/master/springcloud/seata

## 达梦安装

[docker-compose安装](https://gitee.com/wxy0715/project/blob/master/docker-compose/%E8%BE%BE%E6%A2%A6/dm8%E5%AE%89%E8%A3%85.md)

[本地安装](https://blog.csdn.net/weixin_47343544/article/details/128029326)

## 数据迁移

试用达梦数据迁移工具,新建工程和迁移配置

![image-20230503142833066](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503142833066.png)

![image-20230503142933485](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503142933485.png)

![image-20230503143033178](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143033178.png)

![image-20230503143049972](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143049972.png)

![image-20230503143106667](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143106667.png)

![image-20230503143136519](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143136519.png)

![image-20230503143151183](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143151183.png)

![image-20230503143228774](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503143228774.png)

迁移完成

## 增加文件

### pom

```xml
            <!--  数据库 dm  -->
            <dependency>
                <groupId>dm</groupId>
                <artifactId>dm.jdbc.driver</artifactId>
                <version>18</version>
            </dependency>
```

## 修改配置

### 数据库关键字修改

seata的undo_log表中的context是达梦关键字,所以要进行修改

```sql
# 执行
SELECT * FROM "V$DM_INI" WHERE PARA_NAME LIKE 'EXCLUDE_RESERVED_WORDS';
sp_set_para_string_value(2,'EXCLUDE_RESERVED_WORDS','context');
# 重启数据库
```

### 数据库连接配置修改

```
username: SYSDBA
password: SYSDBA
url: jdbc:dm://127.0.0.1:5236?schema=SEATA1
driver-class-name: dm.jdbc.driver.DmDriver
```

### po

```java
@TableName(value = "\"USER\"", autoResultMap = true) // user用大写
```

### 添加io.seata

地址:https://gitee.com/wxy0715/project/tree/master/springcloud/seata/user1/src/main/java

![image-20230503142648927](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230503142648927.png)

## 启动自己的服务,完工



## 问题整理

### java.sql.SQLException: not support oracle driver 8.1
1.达梦数据库版本在DM8 1.2.38以下需要更换为2021年8月以后的版本。

2.数据库在linux，应用系统在window的IDEA中，会出现window项目启动的时候 驱动包识别的是本台机器上的，识别不到数据库服务器上的dm.svc.conf的配置内容，所以window需要放到需要在指定目录下放dmsvc.conf文件。

### 无法解析的成员访问表达式[UNDO_LOG_SEQ.NEXTVAL]
CREATE SEQUENCE UNDO_LOG_SEQ START WITH 1 INCREMENT BY 1;
在数据库中在执行这句SQL语句就好了。