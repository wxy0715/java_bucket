package com.cjree.core.elasticsearch;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * easy-es 配置
 */
@AutoConfiguration
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
@EsMapperScan("com.cjree.**.esmapper")
public class EasyEsConfiguration {

}
