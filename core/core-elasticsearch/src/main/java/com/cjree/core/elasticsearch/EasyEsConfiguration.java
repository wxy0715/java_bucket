package com.cjree.core.elasticsearch;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * easy-es 配置
 */
@Configuration
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
@EsMapperScan("com.cjree.**.esmapper")
public class EasyEsConfiguration {

}
