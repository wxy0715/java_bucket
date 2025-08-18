# seata 模块使用手册

## 模块概述

seata 是一个分布式事务处理示例模块，基于 Seata 实现，展示了如何在微服务架构中处理分布式事务。

## 主要功能

- 分布式事务管理
- Seata 配置集成
- AT模式事务处理
- TCC模式事务处理

## 依赖模块

- core-basic
- core-cloud

## 模块结构

```
seata/
├── example1/     # Seata 示例1
├── example2/     # Seata 示例2
│   ├── seata-api/  # API接口定义
│   └── src/        # 实现代码
└── README.md     # 说明文档
```

## 配置文件

模块使用 `bootstrap.yml` 配置文件进行配置：

```yaml
seata:
  enabled: true
  enable-auto-data-source-proxy: true
  data-source-proxy-mode: AT
  tx-service-group: seata_test_group # 定义事务组的名称
  registry:
    type: nacos
    nacos:
      server-addr: ${spring.cloud.nacos.server}
      namespace: 80a5b418-586c-485f-9180-0f41be4d9eb8
      group: SEATA_GROUP
      username: nacos
      password: nacos
  config:
    type: nacos
    nacos:
      server-addr: ${spring.cloud.nacos.server}
      namespace: 80a5b418-586c-485f-9180-0f41be4d9eb8
      group: SEATA_GROUP
      username: nacos
      password: nacos

```

## 使用方法

### 1. 服务端搭建

参考文档中的链接搭建 Seata 服务端：

https://github.com/wxy0715/docker-compose/tree/main/seata

### 2. 客户端依赖导入

在您的项目中添加以下依赖：

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>seata-api</artifactId>
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
```

### 3. 在需要分布式事务的方法上添加 @GlobalTransactional 注解：

### 4. 确保业务数据库支持 Seata AT 模式（需要 undo_log 表）：

```sql
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
    ) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);
```

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

```
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