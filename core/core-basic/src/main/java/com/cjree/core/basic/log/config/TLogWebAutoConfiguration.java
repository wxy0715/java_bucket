package com.cjree.core.basic.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置TLogWebConfig
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.web.servlet.config.annotation.WebMvcConfigurer, org.springframework.boot.web.servlet.FilterRegistrationBean"})
public class TLogWebAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(TLogWebConfig.class)
    public TLogWebConfig tLogWebConfig(){
        return new TLogWebConfig();
    }
}
