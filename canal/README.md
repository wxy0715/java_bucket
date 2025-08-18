# canal 模块使用手册

## 模块概述

canal 是一个基于 MySQL binlog 的增量订阅&消费组件，主要用于数据库数据同步和订阅消费。

## 主要功能

- 增量数据订阅
- 数据实时同步
- 数据变更捕获
- 支持多种消息协议

## 依赖模块

- core-basic
- core-canal

## 配置文件

模块使用 `application.yml` 配置文件进行配置：

```yaml
canal:
  server:
    enabled: YES
    hostname: 192.168.11.177
    port: 11111
```

## 使用方法

1. 添加 Maven 依赖：

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-canal-jdk17</artifactId>
        </dependency>
```

2. 创建数据变更监听器：

```java
package com.cjree.canal.process;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cjree.canal.entity.User;
import com.cjree.core.canal.BaseAbstractStrategy;
import com.cjree.core.canal.CanalDataHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserProcess extends BaseAbstractStrategy<User> {

    @PostConstruct
    private void init(){
        tableContext.attach("user", this); // 监听的表名
    }

    @Override
    public void syncInsert(User user) {
        syncUpdate(user);
    }

    @Override
    public void syncUpdate(User user) {
        log.info("syncUpdate user value:{}", JSONObject.toJSONString(user));
    }

    @Override
    public void syncDelete(User user) {
        log.info("delete user value:{}", JSONObject.toJSONString(user));
    }

    @Override
    public User coverData(List<CanalEntry.Column> data) {
        return CanalDataHandler.convertToBean(data, User.class);
    }
}
```

## 注意事项

1. 确保 MySQL 已开启 binlog，并设置格式为 ROW 模式
2. 确保 Canal Server 正在运行
3. 根据实际环境修改 Canal 连接参数
4. 注意处理数据一致性问题