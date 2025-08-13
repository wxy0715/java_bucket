package com.cjree.core.basic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SwaggerConfig {
    /**
     * 根据@Tag 上的排序，写入x-order
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getTags()!=null){
                openApi.getTags().forEach(tag -> {
                    // 使用Map.of简化Map的创建
                    tag.setExtensions(Map.of("x-order", 1));
                });
            }
        };
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CJREE-API")
                        .version("1.0")
                        .contact(new Contact().name("cjree").url("").email(""))
                        .description( "CJREE-API")
                        .termsOfService("http://localhost"));
    }


}
