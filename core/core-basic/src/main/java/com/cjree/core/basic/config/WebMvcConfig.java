package com.cjree.core.basic.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.cjree.core.basic.filter.CsrfFilter;
import com.cjree.core.basic.interceptor.DefalutInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.servlet.MultipartConfigElement;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Spring MVC 配置类
 */

//@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<CsrfFilter> csrfFilter() {
        FilterRegistrationBean<CsrfFilter> registration = new FilterRegistrationBean<CsrfFilter>(new CsrfFilter());
        registration.setName("csrfFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("default");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // super.configureMessageConverters(converters);
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNonStringValueAsString
        );
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.valueOf("application/json;charset=UTF-8"));
        fastMediaTypes.add(MediaType.valueOf("text/html"));
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter);
    }

    /**
     * jackson配置
     */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        /**
         * 序列换成json时,将所有的long变成string
         * 因为js中得数字类型不能包含所有的java long值
         */
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        //设置日期格式
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        objectMapper.setDateFormat(smt);
        //反序列化时忽略不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //设置中文编码格式
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        return mappingJackson2HttpMessageConverter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefalutInterceptor())
                .addPathPatterns("/*");
    }

    /**
     * 文件上传配置
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 文件最大
        factory.setMaxFileSize(DataSize.ofMegabytes(1024));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(1024));
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源过滤
        // knife4j 增强 swagger 配置 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // knife4j 增强 swagger 配置 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");//静态资源路径 css,js,img等

    }

    /**
     * Spring validator 方法级别的校验
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    //代码设置不扫描Manifest文件。
    //添加下面的代码到自己的主启动类中，能够完美解决问题
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
            }
        };
    }

}
