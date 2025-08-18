# fileupload 模块使用手册

## 模块概述

fileupload 是一个文件上传代码示例模块，基于 [x-file-storage](https://gitee.com/dromara/x-file-storage) 封装实现，展示了如何在项目中集成和使用文件上传功能。

## 主要功能

- 文件上传
- 文件下载
- 文件存储管理
- 支持多种存储平台（本地、MinIO等）

## 依赖模块

- core-basic
- core-file

## 使用方法

1. 在您的项目中添加以下依赖：

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-file-jdk17</artifactId>
        </dependency>
```

2. 配置文件存储参数（在 application.yml 中）： 参考x-file-storage官方配置

```yaml
# 文件存储配置
dromara:
  x-file-storage:
    default-platform: minio-1
    thumbnail-suffix: ".min.jpg"
    minio:
      - platform: minio-1
        enable-storage: true
        access-key: admin
        secret-key: seasky123456
        end-point: http://192.168.2.100:9002
        bucket-name: budget
        domain: http://192.168.2.100:9002
        base-path: /
```

3. 启动项目,查看swagger即可
4. 接口文档: http://apipost.seaskysh.com/docs/preview/f1fe1216cf4c5961/037d532d87e52590