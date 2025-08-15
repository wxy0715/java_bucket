package com.cjree.core.cache.factory;

import com.cjree.core.cache.base.CacheCommand;
import com.cjree.core.cache.cacheservice.EhCacheService;
import com.cjree.core.cache.cacheservice.RedissonService;
import com.cjree.core.common.config.SpringContainer;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Objects;

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
                default:
                    try {
                        CacheManager cacheManager = SpringContainer.applicationContext
                                .getBean("myEhCacheManager", CacheManager.class);
                        cacheCommand = new EhCacheService(cacheManager);
                    } catch (NoSuchBeanDefinitionException e) {
                        // 如果找不到bean，回退到Redis或抛出更明确的异常
                        throw new RuntimeException("EhCache配置错误：找不到myEhCacheManager bean", e);
                    }
            }
        }

        return cacheCommand;
    }
}
