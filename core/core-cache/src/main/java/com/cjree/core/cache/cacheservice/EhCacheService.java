package com.cjree.core.cache.cacheservice;

import com.cjree.core.cache.base.CacheCommand;
import com.cjree.core.cache.config.EhCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ehcache缓存服务
 */
@Slf4j
public class EhCacheService implements CacheCommand {
    private final CacheManager cacheManager;
    private final Cache<String, Serializable> ehCache;
    private final Map<String, Cache<String, Map<String, String>>> hashCaches = new ConcurrentHashMap<>();

    public EhCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.ehCache = cacheManager.getCache("service", String.class, Serializable.class);
    }

    @Override
    public Object get(final String key) {
        return ehCache.get(key);
    }

    @Override
    public Iterable<String> getKeys(String pattern) {
        // Ehcache 没有原生模式匹配，需要遍历所有键
        Iterator<Cache.Entry<String, Serializable>> iterator = ehCache.iterator();
        List<String> keys = new ArrayList<>();
        while (iterator.hasNext()) {
            Cache.Entry<String, Serializable> entry = iterator.next();
            if (entry.getKey().matches(pattern.replace("*", ".*"))) {
                keys.add(entry.getKey());
            }
            keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public Boolean isExists(String key) {
        return ehCache.containsKey(key);
    }

    @Override
    public void set(String key, Serializable value) {
        ehCache.put(key, value);
    }

    @Override
    public void set(String key, Serializable value, long expired) {
        // 创建带过期时间的缓存配置
        Cache<String, Object> cache = cacheManager.createCache(
                "expirableCache_" + key,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, Object.class,
                                ResourcePoolsBuilder.heap(1)
                        )
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(expired)))
        );
        cache.put(key, value);
    }

    @Override
    public void del(final String key) {
        ehCache.remove(key);
    }

    @Override
    public List<Object> getBatch(List<String> keys) {
        return keys.stream().map(this::get).collect(Collectors.toList());
    }

    @Override
    public void setBatch(Map<String, Serializable> map) {
        map.forEach(ehCache::put);
    }

    @Override
    public void delBatch(List<String> keys) {
        keys.forEach(ehCache::remove);
    }

    @Override
    public final String type(final String key) {
        Object value = ehCache.get(key);
        if (value == null) return "NONE";
        if (value instanceof String) return "STRING";
        if (value instanceof Map) return "HASH";
        return value.getClass().getSimpleName().toUpperCase();
    }

    @Override
    public void hset(String key, String field, String value) {
        // 获取或创建哈希缓存
        Cache<String, Map<String, String>> hashCache = hashCaches.computeIfAbsent(key, k ->
                cacheManager.createCache(
                        "hashCache_" + k,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, (Class<Map<String, String>>) (Class<?>) Map.class,
                                ResourcePoolsBuilder.heap(100)
                        )
                ));
        Map<String, String> hashMap = Optional.ofNullable(hashCache.get(key))
                .orElseGet(ConcurrentHashMap::new);

        // 更新字段值
        hashMap.put(field, value);
        hashCache.put(key, hashMap);
    }

    @Override
    public Object hget(String key, String field) {
        Cache<String, Map<String, String>> hashCache = hashCaches.get(key);
        if (hashCache == null) return null;
        Map<String, String> hashMap = hashCache.get(key);
        if (hashMap == null) return null;
        return hashMap.get(field);
    }
    @Override
    public void hdel(String key, String field) {
        Cache<String, Map<String, String>> hashCache = hashCaches.get(key);
        if (hashCache == null) return;
        Map<String, String> hashMap = hashCache.get(key);
        if (hashMap == null) return;
        hashMap.remove(field);
        // 如果哈希表为空，则删除整个缓存
        if (hashMap.isEmpty()) {
            hashCache.remove(key);
            hashCaches.remove(key);
        } else {
            hashCache.put(key, hashMap);
        }
    }

    @Override
    public Long incr(String key) {
        Long value = (Long) ehCache.get(key);
        value++;
        ehCache.put(key, value);
        return value;
    }
}
