package com.wxy.lock.controller;

import com.wxy.lock.config.RedissonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/redisson")
public class RedissonController {
    @Resource
    RedissonUtils redissonUtils;
    /**
     Redisson的分布式RBucket Java对象是一种通用对象桶，可以用来存放任意类型的对象。
     除了同步接口外，还提供异步(Async)、反射式(Reactive)和RxJava2标准的接口。
     还可以通过RBuckets接口实现批量操作多个RBucket对象。
     */
    @PostMapping("/bucket")
    public String bucket() {
        RBuckets buckets = redissonUtils.getBuckets();
        Map<String, Object> map = new HashMap<>();
        map.put("myBucket1", "value1");
        map.put("myBucket2", 30L);
        buckets.set(map);
        Map<String, String> loadedBuckets = buckets.get("myBucket1", "myBucket2", "myBucket3");
        log.info("跨桶String 测试数据：{}", loadedBuckets);

        RBuckets buckets1 = redissonUtils.getBuckets();
        map.put("myBucket3", 320L);
        buckets1.set(map);
        Map<String, String> loadedBuckets1 = buckets1.get("myBucket1", "myBucket2", "myBucket3");
        log.info("跨桶String 测试数据1：{}", loadedBuckets1);
        return "ok";
    }

    /**
     * 基于Redisson的分布式映射结构的RMap Java对象实现了java.util.concurrent.ConcurrentMap和java.util.Map接口，
     * 与HashMap不同的是，RMap 保持了元素的插入顺序。
     * 该对象的最大容量受Redis限制，最大元素数量是4294967295个。
     */
    @PostMapping("/hash")
    public String hash() {
        RMap<Object, Object> map = redissonUtils.getMap("mapDemo");
        map.put("demoId1", "123");
        map.put("demoId1", "1234"); // 会覆盖
        map.put("demoId100", "13000");
        Object demoId1Obj = map.get("demoId1");
        log.info("Hash 测试数据：{}", demoId1Obj);
        return "ok";
    }

    /**
     * 基于Redisson的分布式Set结构的RSet Java对象实现了java.util.Set接口，
     * 通过元素的互相状态比较保证了每个元素的唯一性，该对象的最大容量受Redis限制，
     * 最大元素数量是4294967295个。
     */
    @PostMapping("/set")
    public String set() {
        RSet<String> set = redissonUtils.getSet("1");
        set.add("value777");
        log.info("Set 测试数据");
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            log.info(next);
        }
        return "ok";
    }

    /**
     基于Redisson的分布式列表 List 结构的RList Java对象在实现了java.util.List接口的同时，
     确保了元素插入时的顺序，该对象的最大容量受Redis限制，
     最大元素数量是4294967295个。
     */
    @PostMapping("/list")
    public String list() {
        RList<String> list = redissonUtils.getList("listDemo");
        list.add("listValue1");
        list.add("listValue2");
        log.info("List 测试数据：{}", list.get(1));
        redissonUtils.delete("listDemo");
        return "ok";
    }

    @PostMapping("/getKeys")
    public String getKeys() {
        RKeys keys = redissonUtils.getKeys();
        Iterable<String> allKeys = keys.getKeys();
        StringBuilder sb = new StringBuilder();
        for (String key : allKeys) {
            sb = sb.append(key).append(",");
        }
        log.info("所有的key：{}", sb.substring(0, sb.length() - 1));
        // 模糊查询以 map 打头的所有 key
        allKeys = keys.getKeysByPattern("map*");
        sb = new StringBuilder();
        for (String key : allKeys) {
            sb = sb.append(key).append(",");
        }
        log.info("模糊匹配到的key：{}", sb.substring(0, sb.length() - 1));
        return "ok";
    }
}
