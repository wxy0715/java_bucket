# core-cache 模块使用手册

## 模块概述

core-cache 是一个缓存管理模块，提供统一的缓存操作接口，支持本地缓存（Ehcache）和分布式缓存（Redis）。该模块基于模板方法模式设计，通过 CacheCommand 接口统一了不同缓存实现的操作方式。

## 主要功能

- 本地缓存管理（基于 Ehcache）
- 分布式缓存管理（基于 Redisson）
- 缓存锁机制（基于 Redisson）
- 统一缓存操作接口

## 核心组件

### 1. 缓存服务类
- EhCacheService：本地缓存实现类
- RedissonService：Redis 分布式缓存实现类
- LocalCacheUtil：本地内存缓存工具类

### 2. 缓存接口
- CacheCommand：缓存操作接口，定义了缓存的基本操作方法
- Lock：锁接口，定义了分布式锁的基本操作方法

### 3. 配置类
- EhCacheConfig：Ehcache 配置类
- RedissonConfig：Redisson 配置类

### 4. 工厂类
- CacheUtil：缓存工厂类，用于获取缓存操作实例

### 5. 锁实现
- RedissonLock：基于 Redisson 的分布式锁实现

## 配置说明

### Ehcache 配置(排除redis)

在项目的 application.yml 中配置缓存类型：

```
# 缓存类型配置，redis表示使用Redis，其他值表示使用Ehcache
cacheName: local
```

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-basic-jdk17</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-redis</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.redisson</groupId>
                    <artifactId>redisson-spring-boot-starter</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

### Redisson 配置
在项目的 application.yml 中配置缓存类型：

```yaml
# 缓存类型配置，redis表示使用Redis，其他值表示使用Ehcache
cacheName: redis

# Redis 配置
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: 
      database: 0
```

## 使用方法

### 1. 获取缓存操作实例

```java
// 通过工厂类获取缓存操作实例
CacheCommand cacheCommand = CacheUtil.getCacheCommand();
```

### 2. 基本缓存操作

```java
// 设置缓存
cacheCommand.set("key", "value");
cacheCommand.set("key", "value", 3600); // 设置过期时间（秒）

// 获取缓存
Object value = cacheCommand.get("key");

// 删除缓存
cacheCommand.del("key");

// 判断缓存是否存在
Boolean exists = cacheCommand.isExists("key");

// 获取缓存类型
String type = cacheCommand.type("key");
```

### 3. 批量缓存操作

```java
// 批量设置缓存
Map<String, Serializable> map = new HashMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
cacheCommand.setBatch(map);

// 批量获取缓存
List<String> keys = Arrays.asList("key1", "key2");
List<Object> values = cacheCommand.getBatch(keys);

// 批量删除缓存
List<String> delKeys = Arrays.asList("key1", "key2");
cacheCommand.delBatch(delKeys);
```

### 4. 哈希数据结构操作

```java
// 设置哈希字段
cacheCommand.hset("hashKey", "field1", "value1");

// 获取哈希字段
Object fieldValue = cacheCommand.hget("hashKey", "field1");

// 删除哈希字段
cacheCommand.hdel("hashKey", "field1");
```

### 5. 计数器操作

```java
// 自增操作
Long newValue = cacheCommand.incr("counterKey");
```

### 6. 分布式锁使用

```java
@Autowired
private RedissonClient redissonClient;

RLock lock = redissonClient.getLock(key);
try {
  lock.lock();
  // 业务
} catch (Exception e){
  log.error("业务失败", e);
  ExceptionUtil.error("业务失败,请重试");
} finally {
  lock.unlock();
}
```

### 7. 本地内存缓存使用

```java
// 设置本地缓存
LocalCacheUtil.set("key", "value", 60000); // 60秒过期

// 获取本地缓存
Object value = LocalCacheUtil.get("key");

// 删除本地缓存
LocalCacheUtil.remove("key");

// 获取缓存大小
int size = LocalCacheUtil.getCacheSize();
```
