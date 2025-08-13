package com.cjree.core.basic.config;

import com.cjree.core.common.config.SystemProperties;
import com.google.common.collect.Lists;
import com.cjree.core.common.config.SpringContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 基本配置类
 */

@Configuration
@Slf4j
public class Config {

    @Bean
    SystemProperties systemProperties(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();
        String[] profiles = environment.getActiveProfiles();
        Properties props = getConfig(profiles);
        propertySources.addLast(new PropertiesPropertySource("thirdEnv", props));
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource.getSource() instanceof Map) {
                Map map = (Map) propertySource.getSource();
                for (Object key : map.keySet()) {
                    String keyStr = key.toString();
                    Object value = map.get(key);
                    log.info("properties key=> {}，value=> {}", keyStr, value.toString());
                    SystemProperties.put(keyStr, value.toString());
                }
            }
        }
        log.info("* cjree Read configuration file finished.");
        return new SystemProperties();
    }

    /**
     * 加载配置文件
     */
    private Properties getConfig(String[] profiles) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resouceList = Lists.newArrayList();
        addResources(resolver, resouceList, "classpath:application.properties");
        if (profiles != null) {
            for (String p : profiles) {
                addResources(resolver, resouceList, "classpath:application-" + p + "*.properties");
            }
        }
        try {
            PropertiesFactoryBean config = new PropertiesFactoryBean();
            config.setLocations(resouceList.toArray(new Resource[]{}));
            config.afterPropertiesSet();
            return config.getObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 加载配置文件
    private void addResources(PathMatchingResourcePatternResolver resolver, List<Resource> resouceList, String path) {
        try {
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                resouceList.add(resource);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Bean
    public SpringContainer springContainer() {
        return new SpringContainer();
    }
}
