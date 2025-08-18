# elasticsearch 模块使用手册

## 依赖开源项目

https://www.easy-es.cn/

## 模块概述

elasticsearch 是一个基于 Easy-Es 的 Elasticsearch 操作示例模块，展示了如何在项目中集成和使用 Elasticsearch 进行全文检索功能。

## 主要功能

- Elasticsearch 数据索引
- 全文检索功能
- 数据增删改查
- 高级搜索功能

## 依赖模块

- core-basic
- core-elasticsearch

## 配置文件

模块使用 `application.yml` 配置文件进行配置：

```yaml
# elk日志配置
easy-es:
  # 默认为true 打印banner 若您不期望打印banner,可配置为false
  banner: false
  # 心跳策略时间 单位:ms
  keep-alive-millis: 18000
  # 连接超时时间 单位:ms
  connectTimeout: 5000
  # 通信超时时间 单位:ms
  socketTimeout: 600000
  # 连接请求超时时间 单位:ms
  connectionRequestTimeout: 5000
  # 最大连接数 单位:个
  maxConnTotal: 100
  # 最大连接路由数 单位:个
  maxConnPerRoute: 100
  global-config:
    #索引处理模式,smoothly:平滑模式,默认开启此模式, not_smoothly:非平滑模式, manual:手动模式
    process-index-mode: SMOOTHLY
    # 开启控制台打印通过本框架生成的DSL语句,默认为开启,测试稳定后的生产环境建议关闭,以提升少量性能
    print-dsl: true
    # 当前项目是否分布式项目,默认为true,在非手动托管索引模式下,若为分布式项目则会获取分布式锁,非分布式项目只需synchronized锁.
    distributed: false
    # 重建索引超时时间 单位小时,默认72H 可根据ES中存储的数据量调整
    reindexTimeOutHours: 72
    # 异步处理索引是否阻塞主线程 默认阻塞 数据量过大时调整为非阻塞异步进行 项目启动更快
    async-process-index-blocking: true
    # 分布式环境下,平滑模式,当前客户端激活最新索引最大重试次数,若数据量过大,重建索引数据迁移时间超过4320/60=72H,可调大此参数值,此参数值决定最大重试次数,超出此次数后仍未成功,则终止重试并记录异常日志
    active-release-index-max-retry: 4320
    # 分布式环境下,平滑模式,当前客户端激活最新索引最大重试次数 分布式环境下,平滑模式,当前客户端激活最新索引重试时间间隔 若您期望最终一致性的时效性更高,可调小此值,但会牺牲一些性能
    active-release-index-fixed-delay: 60
    db-config:
      index-prefix: jdk17_
      # 是否开启下划线转驼峰 默认为false
      map-underscore-to-camel-case: true
      # id生成策略 customize为自定义,id值由用户生成,比如取MySQL中的数据id,如缺省此项配置,则id默认策略为es自动生成
      id-type: customize
      # 字段更新策略 默认为not_null
      field-strategy: NOT_NULL
      # 默认开启,查询若指定了size超过1w条时也会自动开启,开启后查询所有匹配数据,若不开启,会导致无法获取数据总条数,其它功能不受影响.
      enable-track-total-hits: true
      # 数据刷新策略,默认为不刷新
      refresh-policy: IMMEDIATE
      # 批量更新接口的阈值 默认值为1万,突破此值需要同步调整enable-track-total-hits=true,@IndexName.maxResultWindow > 1w,并重建索引.
      batch-update-threshold: 10000
      # 是否智能为字段添加.keyword后缀 默认开启,开启后会根据当前字段的索引类型及当前查询类型自动推断本次查询是否需要拼接.keyword后缀
      smartAddKeywordSuffix: true
  compatible: true # 兼容模式开关,默认为false,若您的es客户端版本小于8.x,务必设置为true才可正常使用,8.x及以上则可忽略此项配置
  enable: true #默认为true,若为false则认为不启用本框架
  address: 192.168.10.101:9200 # es的连接地址,必须含端口 若为集群,则可以用逗号隔开 例如:127.0.0.1:9200,127.0.0.2:9200
```

## 使用方法

1. 添加 Maven 依赖：

```xml
        <dependency>
            <groupId>com.cjree</groupId>
            <artifactId>core-elasticsearch-jdk17</artifactId>
        </dependency>
```

2. 启动类加@EsMapperScan("com.cjree.easyes.esmapper")
3. 创建实体类并使用相关注解：

```java
package com.cjree.easyes.document;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.*;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.util.Date;

/**
 * 文档实体
 */
@Data
@Accessors(chain = true)
@Settings(shardsNum = 3, replicasNum = 2)
@IndexName(value = "easyes_document", keepGlobalPrefix = true)
public class Document {
    /**
     * es中的唯一id,如果你想自定义es中的id为你提供的id,比如MySQL中的id,请将注解中的type指定为customize或直接在全局配置文件中指定,如此id便支持任意数据类型)
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    /**
     * 文档标题,不指定类型默认被创建为keyword类型,可进行精确查询
     */
    private String title;
    /**
     * 文档内容,指定了类型及存储/查询分词器
     */
    @HighLight(mappingField = "highlightContent")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    private String content;
    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    /**
     * es中实际不存在的字段,但模型中加了,为了不和es映射,可以在此类型字段上加上 注解@TableField,并指明exist=false
     */
    @IndexField(exist = false)
    private String notExistsField;
    /**
     * 地理位置经纬度坐标 例如: "40.13933715136454,116.63441990026217"
     */
    @IndexField(fieldType = FieldType.GEO_POINT)
    private String location;
    /**
     * 图形(例如圆心,矩形)
     */
    @IndexField(fieldType = FieldType.GEO_SHAPE)
    private String geoLocation;
    /**
     * 自定义字段名称
     */
    @IndexField(value = "wu-la", fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_SMART, fieldData = true)
    private String customField;

    /**
     * 演示copy_to复制字段
     */
    private String copyToField;

    /**
     * 高亮返回值被映射的字段
     */
    private String highlightContent;
    /**
     * 文档点赞数
     */
    private Integer starNum;
}

```

4. 创建 Mapper 接口：

```java
package com.cjree.easyes.esmapper;

import com.cjree.easyes.document.Document;
import org.dromara.easyes.core.kernel.BaseEsMapper;
import org.springframework.stereotype.Component;

@Component
public interface DocumentMapper extends BaseEsMapper<Document> {
}

```

5. 使用 Mapper 进行数据操作：

```java
package com.cjree.easyes.controller;

import com.cjree.easyes.document.Document;
import com.cjree.easyes.esmapper.DocumentMapper;
import jakarta.annotation.Resource;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * es操作
 */
@RestController
public class EsController {
    @Resource
    private DocumentMapper documentMapper;

    /**
     * 初始化插入数据,默认开启自动挡,自动挡模式下,索引会自动创建及更新. 若未开启自动挡,则在此步骤前需先调用创建索引API完成索引创建
     */
    @GetMapping("insert")
    public Integer insert() {
        Document document = new Document();
        document.setId("11");
        document.setTitle("测试1");
        document.setContent("测试内容1");
        document.setGmtCreate(new Date());
        return documentMapper.insert(document);
    }

    /**
     * 演示根据标题精确查询文章
     * 例如title传值为 我真帅,那么在当前配置的索引下,所有标题为'我真帅'的文章都会被查询出来
     * 其它各种场景的查询使用,请移步至test模块
     *
     * @param title
     * @return
     */
    @GetMapping("/listDocumentByTitle")
    public List<Document> listDocumentByTitle(@RequestParam(name = "title") String title) {
        // 实际开发中会把这些逻辑写进service层 这里为了演示方便就不创建service层了
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, title);
        return documentMapper.selectList(wrapper);
    }


    /**
     * 演示根据title删除文章，同时会被 DeleteInterceptor 拦截，执行逻辑删除
     *
     * @param title
     * @return
     */
    @GetMapping("/deleteDocumentByTitle")
    public Integer deleteDocumentByTitle(@RequestParam(name = "title") String title) {
        // 实际开发中会把这些逻辑写进service层 这里为了演示方便就不创建service层了
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, title);
        return documentMapper.delete(wrapper);
    }

    /**
     * 自定义注解指定高亮返回字段,高亮查询测试
     *
     * @param content
     * @return
     */
    @GetMapping("/highlightSearch")
    public List<Document> highlightSearch(@RequestParam(name = "content") String content) {
        // 实际开发中会把这些逻辑写进service层 这里为了演示方便就不创建service层了
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, content);
        return documentMapper.selectList(wrapper);
    }

}

```

## 注意事项

1. 确保 Elasticsearch 服务正在运行
2. 根据实际环境修改 Elasticsearch 连接地址
3. 注意版本兼容性，Easy-Es 与 Elasticsearch 版本需要匹配
4. 合理设计索引结构和分词器以提高搜索性能