package com.wxy.shardingjdbc.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.wxy.shardingjdbc.dao")
public class MybatisPlusConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
     * 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 全局地开启或关闭配置⽂件中的所有映射器已经配置的任何缓存
            configuration.setCacheEnabled(true);
            // 默认枚举处理
            configuration.setDefaultEnumTypeHandler(org.apache.ibatis.type.EnumOrdinalTypeHandler.class);
            configuration.setMapUnderscoreToCamelCase(true);
            // sql查询字段为空也返回
            configuration.setCallSettersOnNulls(true);
            configuration.setJdbcTypeForNull(JdbcType.NULL);
        };
    }

}
