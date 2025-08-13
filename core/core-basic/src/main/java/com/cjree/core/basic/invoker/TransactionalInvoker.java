package com.cjree.core.basic.invoker;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.cjree.core.cache.base.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class TransactionalInvoker {

    public static TransmittableThreadLocal<ArrayList<String>> localTCCacheKeys = new TransmittableThreadLocal<>();

    /**
     * 记录本次事物写入缓存的记录，用于回滚后清理缓存数据
     */
    public void setGlobalTCAndLocalTCCacheKeys(Object entity) {
        if (entity instanceof Cacheable) {
            Cacheable cacheable = (Cacheable) entity;
            String key = cacheable.getCacheKey().toString();
            ArrayList<String> keys = localTCCacheKeys.get();
            if (keys == null) {
                keys = new ArrayList<>();
                localTCCacheKeys.set(keys);
            }
            log.info("setTransactionalCacheKeys:ID:" + key);
            keys.add(key);
        }
    }
}
