package com.cjree.core.common.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Spring容器
 */
public class SpringContainer implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    private static Environment environment = null;

    public static ApplicationContext getContainer() {
        return applicationContext;
    }

    public static <T> T getBeanOfType(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        return applicationContext.getBeansOfType(requiredType);
    }

    public static Object getBeanOfName(String requiredName) {
        return applicationContext.getBean(requiredName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContainer.applicationContext = applicationContext;
    }

    public static String getProperty(String key){
        if (environment == null){
            environment = getBeanOfType(Environment.class);
        }
        return environment.getProperty(key);
    }

}
