参考:https://blog.csdn.net/fhf2424045058/article/details/113185446

# druid和hikaricp

- 功能角度考虑，Druid 功能更全面，除具备连接池基本功能外，还支持sql级监控、扩展、SQL防注入等。最新版甚至有集群监控
- 单从性能角度考虑，从数据上确实HikariCP要强，但Druid有更多、更久的生产实践，它可靠。
- 单从监控角度考虑，如果我们有像skywalking、prometheus等组件是可以将监控能力交给这些的 HikariCP也可以将metrics暴露出去。

# 本文是结合了mybatisplus的多数据源实战

## pom

```xml
<!-- sql性能分析插件 -->
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
    <version>3.9.1</version>
</dependency>
```

## 配置文件

```yml
# 数据源配置
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.dynamic.p6spy=true
spring.datasource.dynamic.strict=true
spring.datasource.dynamic.p6spy=true
# 最大连接池数量
spring.datasource.dynamic.hikari.maxPoolSize=20  
# 最小空闲线程数量
spring.datasource.dynamic.hikari.minIdle=10
# 配置获取连接等待超时的时间
spring.datasource.dynamic.hikari.connectionTimeout=30000
# 校验超时时间
spring.datasource.dynamic.hikari.validationTimeout=5000
# 空闲连接存活最大时间，默认10分钟
spring.datasource.dynamic.hikari.idleTimeout=600000
# 此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认30分钟
spring.datasource.dynamic.hikari.maxLifetime=1800000
# 连接测试query（配置检测连接是否有效）
spring.datasource.dynamic.hikari.connectionTestQuery=SELECT 1
# 多久检查一次连接的活性
spring.datasource.dynamic.hikari.keepaliveTime=30000


# 多数据源配置
spring.datasource.dynamic.primary=master
spring.datasource.dynamic.strict=true
# 编制广中医数据源
spring.datasource.dynamic.datasource.master.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.dynamic.datasource.master.username=1
spring.datasource.dynamic.datasource.master.password=1
spring.datasource.dynamic.datasource.master.url=jdbc:sqlserver://ip:port;Databasename=test
spring.datasource.dynamic.datasource.projectplan.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## 新建p6spy配置文件

![image-20230424214335197](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230424214335197.png)

```properties
# p6spy 性能分析插件配置文件
modulelist=com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory,com.p6spy.engine.outage.P6OutageFactory
# 自定义日志打印
logMessageFormat=com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger
#日志输出到控制台
appender=com.baomidou.mybatisplus.extension.p6spy.StdoutLogger
# 使用日志系统记录 sql
#appender=com.p6spy.engine.spy.appender.Slf4JLogger
# 设置 p6spy driver 代理
#deregisterdrivers=true
# 取消JDBC URL前缀
useprefix=true
# 配置记录 Log 例外,可去掉的结果集有error,info,batch,debug,statement,commit,rollback,result,resultset.
excludecategories=info,debug,result,commit,resultset
# 日期格式
dateformat=yyyy-MM-dd HH:mm:ss
# SQL语句打印时间格式
databaseDialectTimestampFormat=yyyy-MM-dd HH:mm:ss
# 实际驱动可多个
#driverlist=org.h2.Driver
# 是否开启慢SQL记录
outagedetection=true
# 慢SQL记录标准 2 秒
outagedetectioninterval=2
# 是否过滤 Log
filter=true
# 过滤 Log 时所排除的 sql 关键字，以逗号分隔
exclude=SELECT 1
```

![image-20230424214512493](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230424214512493.png)
