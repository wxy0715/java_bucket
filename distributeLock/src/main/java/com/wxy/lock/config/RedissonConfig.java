package com.wxy.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RedissonConfig {
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.database}")
    private Integer database;
    @Bean
    @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "single")
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "single")
    public RedissonClient redisson(@Value("classpath:/redisson-single.yml") Resource configFile) throws IOException {
        Config config = Config.fromYAML(configFile.getInputStream());
        config.useSingleServer().setAddress("redis://"+host+":"+port)
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }
}
