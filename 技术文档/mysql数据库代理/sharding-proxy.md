

# ReadMe(不要根据官方给的配置,很多都改了,需要看源码修改了哪些)

很多配置字段都对不上,必须通过源代码调试找到正确的:比如

![image-20210822151416383](.\4.0.1\image\image-20210822151416383.png)

```
1.最新的版本是5.0.0-bate但是我下载按照代码给的配置,并不能够跑起来,耽误了很多的时间
2.这里使用的是4.0.1的文件:https://shardingsphere.apache.org/document/legacy/4.x/document/en/downloads/
但是这个代码是不全的,所以只能去github上下载的4.1.1版本的代码去阅读:https://github.com/apache/shardingsphere/tree/4.1.1
3.想要配置读写分离,需要提前配置好主从复制
```

![image-20210822113810470](.\4.0.1\image\image-20210822113810470.png)

![image-20210822114214656](.\4.0.1\image\image-20210822114214656.png)



```shell
这两个下载下来复制到linux上
tar后需要把mysql驱动的jar也放上去
```

# 启动停止

```shell
# 停止
stop.sh
# 启动
start.sh port # 指定端口启动
start.sh port config-?.conf # 指定配置文件启动
```

![image-20210822142437332](.\4.0.1\image\image-20210822142437332.png)



# 配置主从复制

> 前提：数据库版本一致，数据库名一致

```
前提是作为主服务器角色的数据库服务器必须开启二进制日志
主服务器上面的任何修改都会通过自己的 I/O tread(I/O 线程)保存在二进制日志 Binary log 里面。
从服务器上面也启动一个 I/O thread，通过配置好的用户名和密码, 连接到主服务器上面请求读取二进制日志，然后把读取到的二进制日志写到本地的一个Realy log（中继日志）里面。
从服务器上面同时开启一个 SQL thread 定时检查 Realy log(这个文件也是二进制的)，如果发现有更新立即把更新的内容在本机的数据库上面执行一遍。
每个从服务器都会收到主服务器二进制日志的全部内容的副本。
从服务器设备负责决定应该执行二进制日志中的哪些语句。
除非另行指定，否则主从二进制日志中的所有事件都在从站上执行。
如果需要，您可以将从服务器配置为仅处理一些特定数据库或表的事件。
```

## 编辑配置文件

### 主机

```shell
[mysqld]
server-id=1  # 必须唯一 
character_set_server=utf8mb4
collation-server=utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'
skip-character-set-client-handshake=true
#  同步的数据库名
binlog-do-db=wxy
# 开启二进制日志功能，以备Slave作为其它Slave的Master时使用
log-bin=mysql-bin
# relay_log配置中继日志
relay-log=relay-bin
#复制过滤：不需要备份的数据库，不输出（mysql库一般不同步）
binlog-ignore-db=information_schema,performance_schema,mysql
# 如果需要同步函数或者存储过程
log_bin_trust_function_creators=true
# 为每个session 分配的内存，在事务过程中用来存储二进制日志的缓存
binlog_cache_size=1M
# 主从复制的格式（mixed,statement,row，默认格式是statement）
binlog_format=mixed
# 跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断。
# 如：1062错误是指一些主键重复，1032错误是因为主从数据库数据不一致
slave_skip_errors=1062
key_buffer_size = 256M
max_allowed_packet = 1M
table_open_cache = 256
sort_buffer_size = 1M
read_buffer_size = 1M
read_rnd_buffer_size = 4M
myisam_sort_buffer_size = 64M
thread_cache_size = 8
query_cache_size= 16M
thread_concurrency = 8
max_connections = 2000     #指定mysql服务所允许的最大连接进程数
event_scheduler = ON       #开启事件
# 临时表大小，默认16MB，太小删除数据太多会报错：the total number of locks exceeds the lock table size
tmp_table_size = 512M
# DDL操作时一个临时日志文件的上限值大小，默认128M，太小释放表空间会报错
# create index 'PRIMARY' required more than 'innodb online alter log max size' bytes of modification log.
innodb_online_alter_log_max_size = 512M
# 默认256，低了会报错：the total number of locks exceeds the lock table size
innodb_buffer_pool_size = 512M

[mysql]
no-auto-rehash

[myisamchk]
key_buffer_size = 128M
sort_buffer_size = 128M
read_buffer = 2M
write_buffer = 2M

[mysqlhotcopy]
interactive-timeout
```

### 备机1

```shell
[mysqld]
server-id=2  # 必须唯一 
character_set_server=utf8mb4
collation-server=utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4'
skip-character-set-client-handshake=true
#  同步的数据库名
binlog-do-db=wxy
# 开启二进制日志功能，以备Slave作为其它Slave的Master时使用
log-bin=mysql-bin
# relay_log配置中继日志
relay-log=relay-bin
#复制过滤：不需要备份的数据库，不输出（mysql库一般不同步）
binlog-ignore-db=information_schema,performance_schema,mysql
# 如果需要同步函数或者存储过程
log_bin_trust_function_creators=true
# 为每个session 分配的内存，在事务过程中用来存储二进制日志的缓存
binlog_cache_size=1M
# 主从复制的格式（mixed,statement,row，默认格式是statement）
binlog_format=mixed
# 跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断。
# 如：1062错误是指一些主键重复，1032错误是因为主从数据库数据不一致
slave_skip_errors=1062
key_buffer_size = 256M
max_allowed_packet = 1M
table_open_cache = 256
sort_buffer_size = 1M
read_buffer_size = 1M
read_rnd_buffer_size = 4M
myisam_sort_buffer_size = 64M
thread_cache_size = 8
query_cache_size= 16M
thread_concurrency = 8
max_connections = 2000     #指定mysql服务所允许的最大连接进程数
event_scheduler = ON       #开启事件
# 临时表大小，默认16MB，太小删除数据太多会报错：the total number of locks exceeds the lock table size
tmp_table_size = 512M
# DDL操作时一个临时日志文件的上限值大小，默认128M，太小释放表空间会报错
# create index 'PRIMARY' required more than 'innodb online alter log max size' bytes of modification log.
innodb_online_alter_log_max_size = 512M
# 默认256，低了会报错：the total number of locks exceeds the lock table size
innodb_buffer_pool_size = 512M

[mysql]
no-auto-rehash

[myisamchk]
key_buffer_size = 128M
sort_buffer_size = 128M
read_buffer = 2M
write_buffer = 2M

[mysqlhotcopy]
interactive-timeout
```

### 备机2

```
同备机一,但server-id=2
```

## 重启mysql

```shell
systemctl restart mariadb
```

## 配置主从

```mysql
主机进入mysql执行:
stop slave;
reset slave;
# 给两台从服务器授权可以读取主的binlog文件
grant replication slave, replication client on *.* to 'wxy'@"ip" identified by 'wxy';
grant replication slave, replication client on *.* to 'wxy'@"ip" identified by 'wxy';
# 查看状态
MariaDB [(none)]> select user,host from mysql.user;
+-------------+----------------+
| User        | Host           |
+-------------+----------------+
| wxy         | ip |
| wxy         | ip  |

# 日志文件名：mysql-bin.000001
# 复制的位置：328
MariaDB [(none)]> show master status;
+------------------+----------+--------------+---------------------------------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB                            |
+------------------+----------+--------------+---------------------------------------------+
| mysql-bin.000001 |      328 | wxy          | information_schema,performance_schema,mysql |
```

```mysql
备机执行:
change master to master_host='ip', master_user='wxy', master_password='wxy', master_port=3306, master_log_file='mysql-bin.000001',master_log_pos=328;
start slave;
# 查看状态
show master status\G;
Slave_IO_Running: Yes
Slave_SQL_Running: Yes
以上两个yes代表换成功

# 备机1同上
change master to master_host='ip', master_user='wxy', master_password='wxy', master_port=3306, master_log_file='mysql-bin.000001',master_log_pos=328;
start slave;
# 查看状态
show master status\G;
Slave_IO_Running: Yes
Slave_SQL_Running: Yes
以上两个yes代表换成功
```

## 常见错误

```shell
# 使用`start slave`开启主从复制过程后，如果SlaveIORunning一直是Connecting，则说明主从复制一直处于连接状态，这种情况一般是下面几种原因造成的，我们可以根据 Last_IO_Error提示予以排除。
1. 网络不通
   检查ip,端口
2. 密码不对
   检查是否创建用于同步的用户和用户密码是否正确
3. pos不对
   检查Master的 Position
   
# Slave_SQL_Running: No
先stop slave;
set global sql_slave_skip_counter=1; 
start slave;
show slave status\G ;
```

# 配置代理用户

```
配置的用户和密码,都是虚拟的,实际并不存在
```

> server.yaml

```yaml
#orchestration:
#  name: orchestration_ds
#  overwrite: true
#  registry:
#    type: zookeeper
#    serverLists: localhost:2181
#    namespace: orchestration

authentication:
 users:
   wxy: # 代理数据库用户名
     password: wxy  # 代理数据库密码
     authorizedSchemas: wxy # 该用户授权可访问的数据库，多个用逗号分隔。缺省将拥有root权限，可访问全部数据库。
   wxy1: # 代理数据库用户名
     password: wxy1  # 代理数据库密码
     authorizedSchemas: wxy1 # 该用户授权可访问的数据库，多个用逗号分隔。缺省将拥有root权限，可访问全部数据库。
props:
 max.connections.size.per.query: 1
 acceptor.size: 16  # The default value is available processors count * 2.
 executor.size: 16  # Infinite by default.
 proxy-frontend-flush-threshold: 128  # The default value is 128.
 proxy-transaction-type: LOCAL
 xa-transaction-manager-type: Atomikos
 proxy-opentracing-enabled: false
 proxy-hint-enabled: false
 query.with.cipher.column: true
 sql.show: true
 check-table-metadata-enabled: true #在程序启动和更新时，是否检查分片元数据的结构一致性。
 lock-wait-timeout-milliseconds: 50000 # The maximum time to wait for a lock
 proxy-backend-query-fetch-size: -1
```





# 配置读写分离

> config-master_slave.yaml

```yaml
schemaName: wxy

dataSources:
 master_ds:
   url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
   username: wxy
   password: wxy
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
 slave_ds_0:
   url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
   username: wxy
   password: wxy
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
 slave_ds_1:
   url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
   username: wxy
   password: wxy
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50

masterSlaveRule:
 name: ms_ds
 masterDataSourceName: master_ds
 slaveDataSourceNames:
   - slave_ds_0
   - slave_ds_1
 loadBalanceAlgorithmType: RANDOM # 随机的机制,每次读操作都随机指定读表
 #loadBalanceAlgorithmType: ROUND_ROBIN # 轮训机制,每次mysql连接的时候进行轮训读表
```

## 以下是loadBalanceAlgorithmType: RANDOM的结果,每次访问都是读表,但是是随机的

![image-20210822142017934](.\4.0.1\image\image-20210822142017934.png)

![image-20210822142036221](.\4.0.1\image\image-20210822142036221.png)

## 以下是loadBalanceAlgorithmType: ROUND_ROBIN的结果,每次连接都是轮训,连接后进行读操作就不会切换读表了

![image-20210822142231933](.\4.0.1\image\image-20210822142231933.png)



# 配置数据分库分表

```yaml
schemaName: wxy

dataSources:
 slave_ds_0:
   url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
   username: wxy
   password: wxy
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
   minPoolSize: 1
 slave_ds_1:
   url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
   username: wxy
   password: wxy
   connectionTimeoutMilliseconds: 30000
   idleTimeoutMilliseconds: 60000
   maxLifetimeMilliseconds: 1800000
   maxPoolSize: 50
   minPoolSize: 1

shardingRule:
 tables:
   db_proxy:
     actualDataNodes: slave_ds_${0..1}.db_proxy_${0..1} # 由数据源名 + 表名组成，以小数点分隔
     tableStrategy:
       inline:
         shardingColumn: user_id
         algorithmExpression: db_proxy_${user_id % 3}  # 分表
     keyGenerator: # 自增列值生成器配置，缺省表示使用默认自增主键生成器
       type: SNOWFLAKE # 雪花算法
       column: user_id
 bindingTables:
   - db_proxy
 defaultDatabaseStrategy: # 分库策略，缺省表示使用默认分库策略
   inline:
     shardingColumn: user_id # 分片列名称
     algorithmExpression: slave_ds_${user_id % 2} # 分片算法行表达式,分库
 defaultTableStrategy:
   none:

```



# 配置数据脱敏

## 限制条件

1. 用户需要自行处理数据库中原始的存量数据、洗数。
2. 使用脱敏功能+分库分表功能，部分特殊SQL不支持，请参考[SQL使用规范]( https://shardingsphere.apache.org/document/current/cn/features/sharding/use-norms/sql/)。
3. 脱敏字段无法支持比较操作，如：大于小于、ORDER BY、BETWEEN、LIKE等。
4. 脱敏字段无法支持计算操作，如：AVG、SUM以及计算表达式     

## MD5

```yaml
schemaName: wxy

dataSource:
 url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
 username: wxy
 password: wxy
 connectionTimeoutMilliseconds: 30000
 idleTimeoutMilliseconds: 60000
 maxLifetimeMilliseconds: 1800000
 maxPoolSize: 50

encryptRule:
 encryptors:
   md5_ecryptor:
     type: MD5
 tables:
   db_proxy:
     columns:
       id:
         cipherColumn: user_plain # 存储密文的字段
         encryptor: md5_ecryptor # 存储密文的字段
```

### id是实际数据库不存在的,但是客户端存在

![image-20210822164046078](.\4.0.1\image\image-20210822164046078.png)



![image-20210822164104026](.\4.0.1\image\image-20210822164104026.png)





## AES

```xml
官方解释:

```



### 真实数据库

![image-20210822172435520](.\4.0.1\image\image-20210822172435520.png)

### 代理的

![image-20210822172515808](.\4.0.1\image\image-20210822172515808.png)

```yaml
schemaName: wxy

dataSource:
 url: jdbc:mysql://ip:3306/wxy?serverTimezone=UTC&useSSL=false
 username: wxy
 password: wxy
 connectionTimeoutMilliseconds: 30000
 idleTimeoutMilliseconds: 60000
 maxLifetimeMilliseconds: 1800000
 maxPoolSize: 50
 minPoolSize: 1

encryptRule:
 encryptors:
   aes_encryptor:
     type: AES #加解密器类型，可自定义或选择内置类型：MD5/AES
     props: #属性配置, 注意：使用AES加密器，需要配置AES加密器的KEY属性：aes.key.value
       aes.key.value: 123456abc
       query.with.cipher.column: true # 是否使用密文查询
 tables:
   db_proxy:
     columns:
       id:
         #plainColumn: user_plain        # 存储明文的字段,系统迁移前存在.钱以后不需要了
         cipherColumn: user_cipher      # 存储密文的字段
         #assistedQueryColumn: #辅助查询字段，针对ShardingQueryAssistedEncryptor类型的加解密器进行辅助查询
         encryptor: aes_encryptor #加密器名字
```

