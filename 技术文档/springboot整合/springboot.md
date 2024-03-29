# 注解

## 忽略某个配置类

```java
@ComponentScan(excludeFilters  = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {DataSourceConfig.class})})
```

## 忽略某个自动装配类

```java
@SpringBootApplication(exclude = {DataSourceConfig.class})
@AliasFor(
annotation = EnableAutoConfiguration.class
)
```