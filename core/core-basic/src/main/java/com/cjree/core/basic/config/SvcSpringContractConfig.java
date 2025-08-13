package com.cjree.core.basic.config;


import feign.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.ArrayList;
import java.util.List;

/**
 * 契约协议配置
 */
@Configuration
@ConditionalOnClass(SpringMvcContract.class)
@Order(Integer.MIN_VALUE)
public class SvcSpringContractConfig {

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Bean
    public Contract feignContract() {
        return new SvcSpringContract(parameterProcessors, new DefaultConversionService(), true);
    }
}

