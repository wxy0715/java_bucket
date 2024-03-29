package com.wxy.study.wechat.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "template")
public class WechatTemplateProperties {

	private List<WechatTemplate> templates;
	private int templateResultType; // 0-文件获取 1-数据库获取 2-ES
	private String templateResultFilePath;

	@Data
	public static class WechatTemplate{
		private String templateId;
		private String templateFilePath;
		private boolean active;
	}

}
