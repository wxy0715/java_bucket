package com.cjree.core.cache.config;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class EhCacheConfig {

    @Bean(name = "myEhCacheManager")
    public CacheManager cacheManager() {
        URL myUrl = getClass().getResource("/ehcache.xml");
        // 读取XML文件
        org.ehcache.config.Configuration xmlConfig = new XmlConfiguration(myUrl);
        CacheManager ehCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        ehCacheManager.init();
        return ehCacheManager;
    }

}