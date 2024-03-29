package com.wxy.study.wechat.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.kafka")
public class KafkaProperties {

	private String bootstrapServers;
	private String acksConfig;
	private String partitionerClass;

}
