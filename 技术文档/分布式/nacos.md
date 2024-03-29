## nacos优先使用本地配置

```properties
# 如果本地配置优先级高，那么 override-none 设置为 true，包括系统环境变量、本地配置文件等配置
spring.cloud.config.override-none=true
# 如果想要远程配置优先级高，那么 allow-override 设置为 false，如果想要本地配置优先级高那么 allow-override 设置为 true
spring.cloud.config.allow-override=true
# 只有系统环境变量或者系统属性才能覆盖远程配置文件的配置，本地配置文件中配置优先级低于远程配置；注意本地配置文件不是系统属性
spring.cloud.config.override-system-properties=false
```

