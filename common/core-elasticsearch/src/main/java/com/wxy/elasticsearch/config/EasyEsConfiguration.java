package com.wxy.elasticsearch.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * easy-es 配置
 */
@AutoConfiguration
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
@EsMapperScan("com.wxy.**.esmapper")
public class EasyEsConfiguration {

}
