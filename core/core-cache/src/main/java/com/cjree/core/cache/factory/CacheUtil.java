package com.cjree.core.cache.factory;

import com.cjree.core.cache.base.CacheCommand;
import com.cjree.core.cache.cacheservice.EhCacheService;
import com.cjree.core.cache.cacheservice.RedissonService;
import com.cjree.core.common.config.SpringContainer;
import org.ehcache.CacheManager;

import java.util.Objects;

import static com.cjree.core.common.constants.CacheName.EHCACHE;
import static com.cjree.core.common.constants.CacheName.REDIS;

/**
 * 缓存工厂
 */
public class CacheUtil {
    private static CacheCommand cacheCommand;

    public static CacheCommand getCacheCommand() {
        if (Objects.isNull(cacheCommand)) {
            String cacheName = SpringContainer.getProperty("cacheName");
            cacheName = cacheName == null ? "" : cacheName;
            switch (cacheName) {
                case REDIS:
                    cacheCommand = SpringContainer.getBeanOfType(RedissonService.class);
                    break;
                case EHCACHE:
                    cacheCommand = new EhCacheService(SpringContainer.applicationContext.getBean("myEhCacheManager", CacheManager.class));
                    break;
                default:
                    cacheCommand = new EhCacheService(SpringContainer.applicationContext.getBean("myEhCacheManager", CacheManager.class));
            }
        }

        return cacheCommand;
    }
}
