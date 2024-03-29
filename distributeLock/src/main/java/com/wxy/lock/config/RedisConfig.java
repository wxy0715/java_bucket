package com.wxy.lock.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * redis配置
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    /**
        * this.redisTemplate.opsForValue(); //提供了操作string类型的所有方法
        * this.redisTemplate.opsForList(); // 提供了操作list类型的所有方法
        * this.redisTemplate.opsForSet(); //提供了操作set的所有方法
        * this.redisTemplate.opsForHash(); //提供了操作hash表的所有方法
        * this.redisTemplate.opsForZSet(); //提供了操作zset的所有方法
     */
    @Bean(name = "myRedisTemplate")
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        // JSON解析任意对象
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域,field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 配置具体的序列化方式
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        // String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key采用String的序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //hash的key也采用String的序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //value的序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        //设置所有配置
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
