package com.cjree.core.cache.cacheservice;

import com.cjree.core.cache.base.CacheCommand;
import com.cjree.core.common.log.TLogContext;
import jakarta.annotation.Resource;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class RedissonService implements CacheCommand {
    private final Logger log = LoggerFactory.getLogger(RedissonService.class);
    @Resource
    private RedissonClient redissonClient;

    /**
     * 默认缓存时间
     */
    private static final Long DEFAULT_EXPIRED = 32000L;


    @Override
    public Object get(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public Iterable<String> getKeys(String pattern) {
        RKeys keys = redissonClient.getKeys();
        // 模糊查询以 map 打头的所有 key
        return keys.getKeysByPattern(pattern);
    }

    @Override
    public void set(String key, Serializable value) {
        String traceId = TLogContext.getTraceId();
        RBucket<Serializable> bucketTrace = redissonClient.getBucket(traceId+"-"+key);
        bucketTrace.set(value);
        bucketTrace.expire(Duration.ofSeconds(60));

        RBucket<Serializable> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 判断缓存是否存在
     */
    @Override
    public Boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }


    @Override
    public void set(String key, Serializable value, long expired) {
        String traceId = TLogContext.getTraceId();
        RBucket<Serializable> bucketTrace = redissonClient.getBucket(traceId+"-"+key);
        bucketTrace.set(value);
        bucketTrace.expire(Duration.ofSeconds(60));

        RBucket<Serializable> bucket = redissonClient.getBucket(key);
        bucket.set(value);
        bucket.expire(Duration.ofSeconds(expired <= 0L ? DEFAULT_EXPIRED : expired));
    }

    @Override
    public void del(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public List<Object> getBatch(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        // 使用管道批量获取
        RBatch batch = redissonClient.createBatch();
        keys.forEach(key -> batch.getBucket(key).getAsync());
        try {
            BatchResult<?> result = batch.execute();
            // 处理结果并转换为泛型列表
            return result.getResponses().stream()
                    .map(res -> {
                        // 处理 Optional 包装
                        if (res instanceof Optional) {
                            return ((Optional<?>) res).orElse(null);
                        }
                        // 直接返回结果（Redisson 默认不会包装为 Optional）
                        return res;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 异常处理：记录日志或返回空列表
            log.error("批量获取缓存失败，keys: {}", keys, e);
            return keys.stream().map(k -> null).collect(Collectors.toList());
        }
    }

    @Override
    public void setBatch(Map<String, Serializable> map) {
        // 使用管道批量写入
        RBatch batch = redissonClient.createBatch();
        map.forEach((key, value) -> {
            String traceId = TLogContext.getTraceId();
            RBucket<Serializable> bucketTrace = redissonClient.getBucket(traceId+"-"+key);
            bucketTrace.set(value);
            bucketTrace.expire(Duration.ofSeconds(60));

            batch.getBucket(key).setAsync(value);
        });
        batch.execute();
    }

    @Override
    public void delBatch(List<String> keys) {
        // 批量删除（Redisson 3.16+）
        if (keys != null && !keys.isEmpty()) {
            redissonClient.getKeys().delete(keys.toArray(new String[0]));
        }
    }


    /**
     * 返回key所储存的值的类型的名称。
     */
    @Override
    public String type(String key) {
        RType type = redissonClient.getKeys().getType(key);
        return type != null ? type.name() : "NONE";
    }


    @Override
    public void hset(String key, String field, String value) {
        RMap<Object, Object> map = redissonClient.getMap(key);
        map.put(field, value);

    }

    @Override
    public Object hget(String key, String field) {
        RMap<Object, Object> map = redissonClient.getMap(key);
        return map.get(field);
    }

    @Override
    public void hdel(String key, String field) {
        RMap<Object, Object> map = redissonClient.getMap(key);
        map.remove(field);
    }

    @Override
    public Long incr(String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.incrementAndGet();
    }
}