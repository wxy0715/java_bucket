package com.cjree.core.cache.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 缓存命令接口
 */
public interface CacheCommand {
    /**
     * 通过key获取对应的值
     */
    Object get(final String key);

    /**
     * 返回所有匹配的key
     */
    Iterable<String> getKeys(String pattern);

    /**
     * 判断缓存是否存在
     */
    Boolean isExists(String key);

    /**
     * 将字符串值value关联到key。
     */
    void set(final String key, final Serializable value);

    /**
     * 将字符串值value关联到key。 单位秒
     */
    void set(final String key, final Serializable value, final long expired);

    /**
     * 删除给定的一个key 。
     */
    void del(final String key);

    /**
     * 批量获取cache
     */
    List<Object> getBatch(final List<String> keys);

    /**
     * 批量插入cache
     */
    void setBatch(final Map<String, Serializable> map);

    /**
     * 批量删除cache
     */
    void delBatch(final List<String> keys);

    /**
     * 返回key所储存的值的类型的名称。
     */
    String type(final String key);

    /**
     * 将哈希表key中的域field的值设为value 。
     * 如果key不存在，一个新的哈希表被创建并进行HSET操作。
     * 如果域field已经存在于哈希表中，旧值将被覆盖。
     */
    void hset(String key, String field, String value);

    /**
     * 返回哈希表key中给定域field的值。
     */
    Object hget(String key, String field);

    /**
     * 删除哈希表key中的一个或多个指定域，不存在的域将被忽略。
     */
    void hdel(String key, String field);

    /**
     * 将key中储存的数字值增一。
     * 如果key不存在，那么key的值会先被初始化为0 ，然后再执行INCR操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在64位(bit)有符号数字表示之内。
     * @param key
     * @return 自增后的值
     */
    Long incr(String key);
}
