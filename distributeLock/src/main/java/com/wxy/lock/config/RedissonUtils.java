package com.wxy.lock.config;

import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonUtils extends RedisCache {

    /**
     * 默认缓存时间
     */
    private static final Long DEFAULT_EXPIRED = 32000L;


    /**
     * 自动装配redisson client对象
     */
    @Resource
    private RedissonClient redissonClient;


    /**
     * 用于操作key
     * @return RKeys 对象
     */
    public RKeys getKeys() {
        return redissonClient.getKeys();
    }


    public Iterable<String> getKeys(String pattern) {
        RKeys keys = getKeys();
        // 模糊查询以 map 打头的所有 key
        return keys.getKeysByPattern(pattern);
    }


    /**
     * 移除缓存
     *
     * @param key
     */
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }


    /**
     * 获取getBuckets 对象
     *
     * @return RBuckets 对象
     */
    public RBuckets getBuckets() {
        return redissonClient.getBuckets();
    }


    /**
     * 读取缓存中的字符串，永久有效
     *
     * @param key 缓存key
     * @return 字符串
     */
    public String getStr(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }


    /**
     * 缓存字符串
     *
     * @param key
     * @param value
     */
    public void setStr(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }


    /**
     * 缓存带过期时间的字符串
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param expired 缓存过期时间，long类型，必须传值
     */
    public void setStr(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value, expired <= 0L ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }


    /**
     * string 操作，如果不存在则写入缓存（string方式，不带有redisson的格式信息）
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param expired 缓存过期时间
     */
    public Boolean setIfAbsent(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        return bucket.trySet(value, expired <= 0L ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }


    /**
     * 如果不存在则写入缓存（string方式，不带有redisson的格式信息），永久保存
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    public Boolean setIfAbsent(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        return bucket.trySet(value);
    }


    /**
     * 判断缓存是否存在
     *
     * @param key
     * @return true 存在
     */
    public Boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }


    /**
     * 获取RList对象
     *
     * @param key RList的key
     * @return RList对象
     */
    public <T> RList<T> getList(String key) {
        return redissonClient.getList(key);
    }


    /**
     * 获取RMapCache对象
     *
     * @param key
     * @return RMapCache对象
     */
    public <K, V> RMapCache<K, V> getMap(String key) {
        return redissonClient.getMapCache(key);
    }


    /**
     * 获取RSET对象
     *
     * @param key
     * @return RSET对象
     */
    public <T> RSet<T> getSet(String key) {
        return redissonClient.getSet(key);
    }


    /**
     * 获取RScoredSortedSet对象
     *
     * @param key
     * @param <T>
     * @return RScoredSortedSet对象
     */
    public <T> RScoredSortedSet<T> getScoredSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }
}