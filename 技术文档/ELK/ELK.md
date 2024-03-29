# 介绍

**主要为了解决分布式系统中的日志查找**

**版本:7.13.3**

**一键安装程序:https://gitee.com/wxy0715/project/blob/master/script/sh/system_upgrade.sh**

# 构建ELK+filebeat

## 搭建ElasticSearch

### 下载安装

```sh
cd /opt/software
# 该包我已上传到oss,直接要即可,里面包含了ik分词器插件
wget https://wxy-oss.oss-cn-beijing.aliyuncs.com/software/ELK/elasticsearch-7.13.3.tar.gz
tar -zxvf elasticsearch-7.13.3.tar.gz -C /opt/install
# 创建es用户
adduser es
# 设置密码
echo -e "es\nes\n" | passwd es
# 增加权限
chown -R es:es /opt/install/elasticsearch-7.13.3/
chmod 775 /opt/install/elasticsearch-7.13.3/
# 修改系统配置
sysctl -w vm.max_map_count=655360
sysctl -p
# 启动命令,需要切换用户
/opt/install/elasticsearch-7.13.3/bin/elasticsearch -d
```

### 访问ip:9200出现以下内容即成功

![image-20230102225602754](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102225602754.png)

## 搭建logstash

### 下载地址 

**https://www.elastic.co/cn/downloads/past-releases/logstash-7-13-3**

```
# 创建目录
$ mkdir -p /opt/install/logstash
$ cd /opt/install/logstash
# 下载
$ wget https://artifacts.elastic.co/downloads/logstash/logstash-7.13.3-linux-x86_64.tar.gz
# 解压
$ tar -zxvf filebeat-7.13.3-linux-x86_64.tar.gz
```

### 查看配置

```sh
$ cat config/logstash-sample.conf
input {  # 日志输入
        tcp {
                mode => "server"
                        host => "0.0.0.0"
                        port => 5000 # 监听的端口,java程序可以向该地址发送日志数据
                        codec => json #json格式
                        type=> "A"  # 用来区分不同的系统作用,es根据改type创建不同的索引
        }
        beats {  # filebeat整合
                type => "B"
                port => 5044
        }
}

output {  # 日志写入es
        if [type] == "A" {
                elasticsearch {
                        hosts => ["http://localhost:9200"]
                        #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
                         index => "a-%{+YYYY.MM.dd}"  # es创建索引
                }
        }
        if [type] == "B" {
                elasticsearch {
                        hosts => ["http://localhost:9200"]
                        #index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
                        index => "b-%{+YYYY.MM.dd}"
                }
        }
}
```

### 启动

**nohup bin/logstash -f config/logstash-sample.conf &**

## 搭建filebeat

### 下载地址 

**https://www.elastic.co/cn/downloads/past-releases/filebeat-7-13-3**

```sh
# 创建目录
$ mkdir -p /opt/install/Filebeat
$ cd /opt/install/Filebeat
# 下载
$ wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.13.3-linux-x86_64.tar.gz
# 解压
$ tar -zxvf filebeat-7.13.3-linux-x86_64.tar.gz
```

### 修改配置 filebeat.yml

```
filebeat.inputs:
- type: log
	enabled: true
  paths:
    - /var/log/*.log
#-------------------------- Elasticsearch output ------------------------------
#output.elasticsearch:
  # Array of hosts to connect to.
  # hosts: ["localhost:9200"]

#----------------------------- Logstash output --------------------------------
output.logstash:
  hosts: ["localhost:5044"]
```

### 启动

```
nohup ./filebeat &
可以通过 data/registry/filebeat/log.json 查看采集进度
```



##  Elasticsearch Head 查看日志采集进度

### 拓展程序加入插件

![image-20230102224543279](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102224543279.png)

### 添加es地址

![image-20230102224644147](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102224644147.png)



## 搭建kibana

### 下载地址 

**https://www.elastic.co/cn/downloads/past-releases/kibana-7-13-3**

```sh
# 创建目录
$ mkdir -p /opt/install/Kibana
$ cd /opt/install/Kibana
# 下载
$ wget https://artifacts.elastic.co/downloads/kibana/kibana-7.13.3-linux-x86_64.tar.gz
# 解压
$ kibana-7.13.3-linux-x86_64.tar.gz
$ cd kibana-7.13.3-linux-x86_64
```

### 修改配置config/kibana.yml

```yml
# 打开以下注释的内容
server.port: 5601
server.host: "0.0.0.0"
elasticsearch.hosts: ["http://localhost:9200"]
kibana.index: ".kibana"
```

### 启动

```
nohup bin/kibana &
```

### 访问IP:5601

![image-20230102223200683](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223200683.png)

### 根据logstash配置文件( logstash-sample.conf)中es创建的索引来创建搜索索引

![image-20230102223344679](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223344679.png)

![image-20230102223304905](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223304905.png)

![image-20230102223538614](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223538614.png)

![image-20230102223551187](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223551187.png)

![image-20230102223618747](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223618747.png)

### 查看日志

![image-20230102223650688](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223650688.png)

![image-20230102223701668](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102223701668.png)



# 加入消息中间件



# 整合springboot

### 代码示例

**https://gitee.com/wxy0715/project/tree/master/springboot/elk**

### pom增加依赖

```xml
<!--logstash-->
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
  <version>6.6</version>
</dependency>
```

### 增加logback.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <!-- 日志文件保存位置 注：前面加/ 表示以盘符为绝对路径  不加则是以项目为相对路径 -->
    <property name="LOG_HOME" value="logs"/>
    <!-- 彩色日志 -->
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr"
                    converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
    <conversionRule conversionWord="wex"
                    converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>


    <!-- ① 设置日志控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>DEBUG</level>
        </filter>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <!--            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%logger{32}] %msg%n</pattern>-->
        </encoder>
    </appender>

    <!-- ② 设置日志输出为文件： 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志文件输出的文件名 -->
            <fileNamePattern>${LOG_HOME}/demo.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志文件保留天数 -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!-- ③ 日志写入logstash -->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>101.43.60.8:5000</destination>
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
        <resetJUL>true</resetJUL>
    </contextListener>

    <!-- 日志输出级别 -->
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="LOGSTASH"/>
    </root>

</configuration>
```

### 发送日志

![image-20230102225933258](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102225933258.png)

![image-20230102225945310](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230102225945310.png)

