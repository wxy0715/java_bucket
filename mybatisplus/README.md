# 提前准备

官网:https://mp.baomidou.com

## pom依赖

```xml
  <!--springboot starter-->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.7.6</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.6</version>
  </dependency>
  <!--springboot test-->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.7.6</version>
    <scope>test</scope>
  </dependency>
  <!--mp-->
  <dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version>
  </dependency>
  <dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.3.1</version>
  </dependency>
  <!--模板引擎-->
  <dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity-engine-core</artifactId>
    <version>2.3</version>
  </dependency>
  <!--lombok-->
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
  </dependency>
  <!--mysql-->
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.31</version>
    <scope>runtime</scope>
  </dependency>
```

## SQL

```sql
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id BIGINT(20) NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT(11) NULL DEFAULT NULL COMMENT '年龄',
    sex CHAR(3) NULL DEFAULT '男' COMMENT '性别',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    wallets VARCHAR(3000) NULL DEFAULT NULL COMMENT '钱包',
    other_info VARCHAR(3000) NULL DEFAULT NULL COMMENT '其他信息',
    tenant_id varchar(100) default NULL COMMENT '租户ID',
    version INT(11) NULL DEFAULT 1 COMMENT '乐观锁',
    available VARCHAR(3) NOT NULL default 'YES' comment '逻辑删除字段 YES/NO',
    PRIMARY KEY (id)
);
```

## 代码一键生成

### 新版

```java
package com.wxy.mybatisplus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 代码生成
 * @author wangxingyu
 * @date 2023/01/06
 */
public class CodeGenerate {
    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://127.0.0.1:3306/shardingjdbc3?serverTimezone=Asia/Shanghai&useSSL=false", "wxy", "wxy")
            .schema("shardingjdbc3");

    public static void main(String[] args) {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("wxy")))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("com.wxy.mybatisplus")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder().enableRestStyle().enableHyphenStyle()
                        .entityBuilder().enableLombok().addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
```

### 老版

> debu运行即可,自动生成代码文件

```java
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author wxy
 * @Description 代码生成器
 */
public class CodeGenerator {

    /**读取控制台内容*/
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("wxy");
        gc.setOpen(false);
        gc.setSwagger2(true); //实体属性 Swagger2 注解
        gc.setServiceName("%sService");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mariadb://172.20.54.88:3306/ipm?useUnicode=true&characterEncoding=UTF-8&useAffectedRows=true&allowMultiQueries=true");
        dsc.setDriverName("org.mariadb.jdbc.Driver");
        dsc.setUsername("ipm");
        dsc.setPassword("sailing");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(null);
        pc.setParent("com.sailing");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/"
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setChainModel(true); //支持链式操作
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(""); //表名前缀
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
```

## 配置

### springboot的mapper配置

> 配置 MapperScan 注解

```java
@SpringBootApplication
@MapperScan(basePackages = {"com.wxy.mybatisplus.mapper"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 配置类

```java
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Value("${spring.name}")
    private String name;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
     * 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户配置
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(name);
            }
            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                return false;
            }
        }));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
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
```



# 分页插件

- 多个插件使用的情况，请将分页插件放到 `插件执行链` 最后面。如在租户插件前面，会出现 `COUNT` 执行 `SQL` 不准确问题。

```java
@Configuration
@MapperScan("com.wxy.mybatisplus.config.MybatisPlusConfig")
public class MybatisPlusConfig {
    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

```java
// 第一种
@Test
void lambdaPageTest() {
    LambdaQueryChainWrapper<User> wrapper2 = userService.lambdaQuery();
    wrapper2.like(User::getName, "a"); // 模糊查询
    userService.page(new Page<>(1, 10), wrapper2.getWrapper()).getRecords().forEach(System.out::print);
}

@Test
void test() {
    userService.lambdaQuery().like(User::getName, "a").list().forEach(System.out::println);
    Page page = userService.lambdaQuery().like(User::getName, "a").page(new Page<>(1, 10)); // 分页模糊查询
    page.getRecords().forEach(System.out::println);
}

// 第二种
QueryWrapper<AccessControl> queryWrapper = new QueryWrapper<>();
Page<AccessControl> page = new Page<>(accessControl.getCurrent(), accessControl.getLimit());
if (StringUtils.isNotBlank(accessControl.getSrcIp())) {
    queryWrapper.like("src_ip",accessControl.getSrcIp());
}
if (StringUtils.isNotBlank(accessControl.getTargetIp())) {
    queryWrapper.like("target_ip",accessControl.getTargetIp());
}
accessControlService.page(page,queryWrapper);
```

# 逻辑删除

## 步骤一

```yml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: available  # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 'NO' # 逻辑已删除值
      logic-not-delete-value: 'YES' # 逻辑未删除值
```

###  ~~步骤~~2: 实体类字段上加上`@TableLogic`注解

```java
@TableLogic // 该版本无需此配置
private String available;
```

# 通用枚举

## 后端存储

**数据库字段**

```java
/**
* 年龄
*/
@JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
private AgeEnum age;

/**
* 性别
*/
@JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
private SexEnum sex;
```

**注解形式@EnumValue**

```
public enum AgeEnum {
    ONE(1, "一岁"),
    TWO(2, "二岁"),
    THREE(3, "三岁");

    @EnumValue
    @JsonValue
    private final int value;

    private final String desc;

    AgeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
```

**实现类**

```java
public enum SexEnum implements IEnum<String> {
    MAN( "男"),
    WOMEN( "女");

    @JsonValue
    private final String value;

    SexEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
```



## 前端返回

**有三种方式,这里使用最简单的配置,加上注解@JsonValue**

![image-20230106232639796](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230106232639796.png)

![image-20230106232848186](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230106232848186.png)

# 字段类型处理器(对象转为字符串存到数据库)

**数据库实体**

```java
@TableName(value = "user", autoResultMap = true)
@Data
@Accessors(chain = true)
public class User implements Serializable {
  /**
     * 注意！！ 必须开启映射注解
     * @TableName(autoResultMap = true)
     * 以下两种类型处理器，二选一 也可以同时存在
     * 注意！！选择对应的 JSON 处理器也必须存在对应依赖包
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    // @TableField(typeHandler = JacksonTypeHandler.class)
    private OtherInfo otherInfo;
  
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Wallet> wallets;
}
```

**测试**

```java
    @Test
    public void testInsert() {
        User user = new User();
        user.setEmail("1");
        user.setName("1");
        user.setOtherInfo(new OtherInfo("1","1"));
        user.setWallets(Arrays.asList(new Wallet("Tom",
                Arrays.asList(new Currency("RMB", 1000d)))));
        userMapper.insert(user);
        LambdaUpdateWrapper<User> wrapper = Wrappers.<User>lambdaUpdate().set(User::getWallets, Arrays.asList(new Wallet("Tom",
                Arrays.asList(new Currency("RMB1", 11d)))), "typeHandler=com.wxy.mybatisplus.config.WalletListTypeHandler");
        wrapper.eq(User::getId, user.getId());
        //userMapper.update(new User().setName("22"), wrapper);
        System.err.println(userMapper.selectById(user.getId()));
    }
```

**存储结果**

![image-20230107000455394](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230107000455394.png)

**查询结果**

![image-20230107000547159](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230107000547159.png)

# 乐观锁

1）乐观锁

首先来看乐观锁，顾名思义，乐观锁就是持比较乐观态度的锁。就是在操作数据时非常乐观，认为别的线程不会同时修改数据，所以不会上锁，但是在更新的时候会判断在此期间别的线程有没有更新过这个数据。

2）悲观锁

反之，悲观锁就是持悲观态度的锁。就在操作数据时比较悲观，每次去拿数据的时候认为别的线程也会同时修改数据，所以每次在拿数据的时候都会上锁，这样别的线程想拿到这个数据就会阻塞直到它拿到锁。

**配置**

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    return interceptor;
}
```

**代码示例**

```java
    @Test
    public void update(){
        User user = new User();
        user.setId(1611596302775336962L);
        user.setName("2");
        user.setEmail("3");
        user.setVersion(2); // 如果此version的和数据库中的不一致,那么就不更新数据库
        int i = userMapper.updateById(user);
        System.out.println(i);
        user = userMapper.selectById(1611596302775336962L);
        System.out.println(user);
    }
```



![image-20230107133906339](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230107133906339.png)

# 自动填充功能

```java
public class User {
    // 注意！这里需要标记为填充字段
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
```

```java
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); 
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); 
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); 
    }
}
```

```java
public enum FieldFill {
    /**
     * 默认不处理
     */
    DEFAULT,
    /**
     * 插入填充字段
     */
    INSERT,
    /**
     * 更新填充字段
     */
    UPDATE,
    /**
     * 插入和更新填充字段
     */
    INSERT_UPDATE
}
```



# 多租户功能

**配置租户**

```properties
spring.name=user
```

**配置类**

```java
@Configuration
@MapperScan("com.wxy.mybatisplus.mapper")
public class MybatisPlusConfig {

    @Value("${spring.name}")
    private String name;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
     * 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户配置,读取配置文件来决定是哪个环境
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(name);
            }
            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                return false;
            }
        }));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

```

**测试类**

```java
@SpringBootTest
public class TenantTest {
    @Autowired
    private UserMapper mapper;

    @Test
    public void aInsert() {
        User user = new User();
        user.setName("一一");
        mapper.insert(user);
        user = mapper.selectById(user.getId());
        System.out.println(user);
    }

    @Test
    public void manualSqlTenantFilterTest() {
        System.out.println(mapper.selectCount(null));
    }
}
```

**执行sql**

```sql
SELECT COUNT(*) AS total FROM user WHERE available = 'YES' AND tenant_id = 'user'
INSERT INTO user (id, name, tenant_id) VALUES (?, ?, 'user')
```

# 防全表更新与删除插件

**配置**

```java
@Configuration
public class MybatisPlusConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
  }
}
```

**测试**

```java
@SpringBootTest
public class QueryWrapperTest {

  @Autowired
  private UserService userService;

  /**
  + SQL：UPDATE user  SET name=?,email=?;
  */
  @Test
  public void test() {
    User user = new User();
    user.setId(999L);
    user.setName("custom_name");
    user.setEmail("xxx@mail.com");
    // com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: Prohibition of table update operation
    userService.saveOrUpdate(user, null);
  }
}
```

# 动态表名插件

**在访问数据库表的时候,会把表名替换为另一个**

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            // 获取参数方法
            Map<String, Object> paramMap = RequestDataHelper.getRequestData();
            paramMap.forEach((k, v) -> System.err.println(k + "----" + v));

            String year = "_2018";
            int random = new Random().nextInt(10);
            if (random % 2 == 1) {
                year = "_2019";
            }
            return tableName + year;
        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }
}
```



# **CRUD**接口

```java
    @Test
    public void aInsert() {
        User user = new User();
        user.setName("小羊");
        user.setAge(3);
        user.setEmail("abc@mp.com");
        assertThat(mapper.insert(user)).isGreaterThan(0);
        // 成功直接拿回写的 ID
        assertThat(user.getId()).isNotNull();
    }


    @Test
    public void bDelete() {
        assertThat(mapper.deleteById(3L)).isGreaterThan(0);
        assertThat(mapper.delete(new QueryWrapper<User>()
                .lambda().eq(User::getName, "Sandy"))).isGreaterThan(0);
    }


    @Test
    public void cUpdate() {
        assertThat(mapper.updateById(new User().setId(1L).setEmail("ab@c.c"))).isGreaterThan(0);
        assertThat(
                mapper.update(
                        new User().setName("mp"),
                        Wrappers.<User>lambdaUpdate()
                                .set(User::getAge, 3)
                                .eq(User::getId, 2)
                )
        ).isGreaterThan(0);
        User user = mapper.selectById(2);
        assertThat(user.getAge()).isEqualTo(3);
        assertThat(user.getName()).isEqualTo("mp");

        mapper.update(
                null,
                Wrappers.<User>lambdaUpdate().set(User::getEmail, null).eq(User::getId, 2)
        );
        assertThat(mapper.selectById(1).getEmail()).isEqualTo("ab@c.c");
        user = mapper.selectById(2);
        assertThat(user.getEmail()).isNull();
        assertThat(user.getName()).isEqualTo("mp");

        mapper.update(
                new User().setEmail("miemie@baomidou.com"),
                new QueryWrapper<User>()
                        .lambda().eq(User::getId, 2)
        );
        user = mapper.selectById(2);
        assertThat(user.getEmail()).isEqualTo("miemie@baomidou.com");

        mapper.update(
                new User().setEmail("miemie2@baomidou.com"),
                Wrappers.<User>lambdaUpdate()
                        .set(User::getAge, null)
                        .eq(User::getId, 2)
        );
        user = mapper.selectById(2);
        assertThat(user.getEmail()).isEqualTo("miemie2@baomidou.com");
        assertThat(user.getAge()).isNull();
    }


    @Test
    public void dSelect() {
        mapper.insert(
                new User().setId(10086L)
                        .setName("miemie")
                        .setEmail("miemie@baomidou.com")
                        .setAge(3));
        assertThat(mapper.selectById(10086L).getEmail()).isEqualTo("miemie@baomidou.com");
        User user = mapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getId, 10086));
        assertThat(user.getName()).isEqualTo("miemie");
        assertThat(user.getAge()).isEqualTo(3);

        mapper.selectList(Wrappers.<User>lambdaQuery().select(User::getId))
                .forEach(x -> {
                    assertThat(x.getId()).isNotNull();
                    assertThat(x.getEmail()).isNull();
                    assertThat(x.getName()).isNull();
                    assertThat(x.getAge()).isNull();
                });
        mapper.selectList(new QueryWrapper<User>().select("id", "name"))
                .forEach(x -> {
                    assertThat(x.getId()).isNotNull();
                    assertThat(x.getEmail()).isNull();
                    assertThat(x.getName()).isNotNull();
                    assertThat(x.getAge()).isNull();
                });
    }

    @Test
    public void orderBy() {
        List<User> users = mapper.selectList(Wrappers.<User>query().orderByAsc("age"));
        assertThat(users).isNotEmpty();
        //多字段排序
        List<User> users2 = mapper.selectList(Wrappers.<User>query().orderByAsc("age", "name"));
        assertThat(users2).isNotEmpty();
        //先按age升序排列，age相同再按name降序排列
        List<User> users3 = mapper.selectList(Wrappers.<User>query().orderByAsc("age").orderByDesc("name"));
        assertThat(users3).isNotEmpty();
    }

    @Test
    public void selectMaps() {
        List<Map<String, Object>> mapList = mapper.selectMaps(Wrappers.<User>query().orderByAsc("age"));
        assertThat(mapList).isNotEmpty();
        assertThat(mapList.get(0)).isNotEmpty();
        System.out.println(mapList.get(0));
    }

    @Test
    public void selectMapsPage() {
        IPage<Map<String, Object>> page = mapper.selectMapsPage(new Page<>(1, 5), Wrappers.<User>query().orderByAsc("age"));
        assertThat(page).isNotNull();
        assertThat(page.getRecords()).isNotEmpty();
        assertThat(page.getRecords().get(0)).isNotEmpty();
        System.out.println(page.getRecords().get(0));
    }

    @Test
    public void orderByLambda() {
        List<User> users = mapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getAge));
        assertThat(users).isNotEmpty();
        //多字段排序
        List<User> users2 = mapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getAge, User::getName));
        assertThat(users2).isNotEmpty();
        //先按age升序排列，age相同再按name降序排列
        List<User> users3 = mapper.selectList(Wrappers.<User>lambdaQuery().orderByAsc(User::getAge).orderByDesc(User::getName));
        assertThat(users3).isNotEmpty();
    }

    @Test
    public void testSelectMaxId() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("max(id) as id");
        User user = mapper.selectOne(wrapper);
        System.out.println("maxId=" + user.getId());
        List<User> users = mapper.selectList(Wrappers.<User>lambdaQuery().orderByDesc(User::getId));
        Assertions.assertEquals(user.getId().longValue(), users.get(0).getId().longValue());
    }

    @Test
    public void testGroup() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("age, count(*)")
                .groupBy("age");
        List<Map<String, Object>> maplist = mapper.selectMaps(wrapper);
        for (Map<String, Object> mp : maplist) {
            System.out.println(mp);
        }
        /**
         * lambdaQueryWrapper groupBy orderBy
         */
        LambdaQueryWrapper<User> lambdaQueryWrapper = new QueryWrapper<User>().lambda()
                .select(User::getAge)
                .groupBy(User::getAge)
                .orderByAsc(User::getAge);
        for (User user : mapper.selectList(lambdaQueryWrapper)) {
            System.out.println(user);
        }
    }

    @Test
    public void testTableFieldExistFalse() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("age, count(age) as count")
                .groupBy("age");
        List<User> list = mapper.selectList(wrapper);
        list.forEach(System.out::println);
        list.forEach(x -> {
            Assertions.assertNull(x.getId());
            Assertions.assertNotNull(x.getAge());
            Assertions.assertNotNull(x.getCount());
        });
        mapper.insert(
                new User().setId(10088L)
                        .setName("miemie")
                        .setEmail("miemie@baomidou.com")
                        .setAge(3));
        User miemie = mapper.selectById(10088L);
        Assertions.assertNotNull(miemie);

    }

    @Test
    public void testSqlCondition() {
        Assertions.assertEquals(user2Mapper.selectList(Wrappers.<User2>query()
                .setEntity(new User2().setName("n"))).size(), 2);
        Assertions.assertEquals(user2Mapper.selectList(Wrappers.<User2>query().like("name", "J")).size(), 2);
        Assertions.assertEquals(user2Mapper.selectList(Wrappers.<User2>query().gt("age", 18)
                .setEntity(new User2().setName("J"))).size(), 1);
    }
```







# 条件构造器



## 查询

```java
    @Test
    public void tests() {
        System.out.println("----- 普通查询 ------");
        List<User> plainUsers = userMapper.selectList(new QueryWrapper<User>().eq("role_id", 2L));
        List<User> lambdaUsers = userMapper.selectList(new QueryWrapper<User>().lambda().eq(User::getRoleId, 2L));
        Assertions.assertEquals(plainUsers.size(), lambdaUsers.size());
        print(plainUsers);

        System.out.println("----- 带子查询(sql注入) ------");
        List<User> plainUsers2 = userMapper.selectList(new QueryWrapper<User>()
                .inSql("role_id", "select id from role where id = 2"));
        List<User> lambdaUsers2 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .inSql(User::getRoleId, "select id from role where id = 2"));
        Assertions.assertEquals(plainUsers2.size(), lambdaUsers2.size());
        print(plainUsers2);

        System.out.println("----- 带嵌套查询 ------");
        List<User> plainUsers3 = userMapper.selectList(new QueryWrapper<User>()
                .nested(i -> i.eq("role_id", 2L).or().eq("role_id", 3L))
                .and(i -> i.ge("age", 20)));
        List<User> lambdaUsers3 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .nested(i -> i.eq(User::getRoleId, 2L).or().eq(User::getRoleId, 3L))
                .and(i -> i.ge(User::getAge, 20)));
        Assertions.assertEquals(plainUsers3.size(), lambdaUsers3.size());
        print(plainUsers3);

        System.out.println("----- 自定义(sql注入) ------");
        // 方式一
        List<User> plainUsers4 = userMapper.selectList(new QueryWrapper<User>()
                .apply("role_id = 2"));
/*        List<User> lambdaUsers4 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .apply("role_id = 2"));*/
        // 方式二
        List<User> plainUsers5 = userMapper.selectList(new QueryWrapper<User>()
                .apply("role_id = {0}",2));
/*        List<User> lambdaUsers5 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .apply("role_id = {0}",2));*/
        print(plainUsers4);
        Assertions.assertEquals(plainUsers4.size(), plainUsers5.size());

        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.set("email", null);
        uw.eq("id", 4);
        userMapper.update(new User(), uw);
        User u4 = userMapper.selectById(4);
        Assertions.assertNull(u4.getEmail());


    }

    @Test
    public void lambdaQueryWrapper() {
        System.out.println("----- 普通查询 ------");
        List<User> plainUsers = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getRoleId, 2L));
        List<User> lambdaUsers = userMapper.selectList(new QueryWrapper<User>().lambda().eq(User::getRoleId, 2L));
        Assertions.assertEquals(plainUsers.size(), lambdaUsers.size());
        print(plainUsers);

        System.out.println("----- 带子查询(sql注入) ------");
        List<User> plainUsers2 = userMapper.selectList(new LambdaQueryWrapper<User>()
                .inSql(User::getRoleId, "select id from role where id = 2"));
        List<User> lambdaUsers2 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .inSql(User::getRoleId, "select id from role where id = 2"));
        Assertions.assertEquals(plainUsers2.size(), lambdaUsers2.size());
        print(plainUsers2);

        System.out.println("----- 带嵌套查询 ------");
        List<User> plainUsers3 = userMapper.selectList(new LambdaQueryWrapper<User>()
                .nested(i -> i.eq(User::getRoleId, 2L).or().eq(User::getRoleId, 3L))
                .and(i -> i.ge(User::getAge, 20)));
        List<User> lambdaUsers3 = userMapper.selectList(new QueryWrapper<User>().lambda()
                .nested(i -> i.eq(User::getRoleId, 2L).or().eq(User::getRoleId, 3L))
                .and(i -> i.ge(User::getAge, 20)));
        Assertions.assertEquals(plainUsers3.size(), lambdaUsers3.size());
        print(plainUsers3);

        System.out.println("----- 自定义(sql注入) ------");
        List<User> plainUsers4 = userMapper.selectList(new QueryWrapper<User>()
                .apply("role_id = 2"));
        print(plainUsers4);

        UpdateWrapper<User> uw = new UpdateWrapper<>();
        uw.set("email", null);
        uw.eq("id", 4);
        userMapper.update(new User(), uw);
        User u4 = userMapper.selectById(4);
        Assertions.assertNull(u4.getEmail());
    }

    private <T> void print(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(System.out::println);
        }
    }

    /**
     * SELECT id,name,age,email,role_id FROM user
     * WHERE ( 1 = 1 ) AND ( ( name = ? AND age = ? ) OR ( name = ? AND age = ? ) )
     */
    @Test
    public void testSql() {
        QueryWrapper<User> w = new QueryWrapper<>();
        w.and(i -> i.eq("1", 1))
                .nested(i ->
                        i.and(j -> j.eq("name", "a").eq("age", 2))
                                .or(j -> j.eq("name", "b").eq("age", 2)));
        userMapper.selectList(w);
    }

    /**
     * SELECT id,name FROM user
     * WHERE (age BETWEEN ? AND ?) ORDER BY role_id ASC,id ASC
     */
    @Test
    public void testSelect() {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.select("id","name").between("age",20,25)
                .orderByAsc("role_id","id");
        List<User> plainUsers = userMapper.selectList(qw);

        LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
        lwq.select(User::getId,User::getName).between(User::getAge,20,25)
                .orderByAsc(User::getRoleId,User::getId);
        List<User> lambdaUsers = userMapper.selectList(lwq);

        print(plainUsers);
        Assertions.assertEquals(plainUsers.size(), lambdaUsers.size());
    }
```





## 更新

```java
    @Autowired
    private UserMapper userMapper;

    /**
     * UPDATE user SET age=?, email=? WHERE (name = ?)
     */
    @Test
    public void tests() {

        //方式一：
        User user = new User();
        user.setAge(29);
        user.setEmail("test3update@baomidou.com");

        userMapper.update(user,new UpdateWrapper<User>().eq("name","Tom"));

        //方式二：
        //不创建User对象
        userMapper.update(null,new UpdateWrapper<User>()
                .set("age",29).set("email","test3update@baomidou.com").eq("name","Tom"));

    }

    /**
     * 使用lambda条件构造器
     * UPDATE user SET age=?, email=? WHERE (name = ?)
     */
    @Test
    public void testLambda() {

        //方式一：
        User user = new User();
        user.setAge(29);
        user.setEmail("test3update@baomidou.com");

        userMapper.update(user,new LambdaUpdateWrapper<User>().eq(User::getName,"Tom"));

        //方式二：
        //不创建User对象
        userMapper.update(null,new LambdaUpdateWrapper<User>()
                .set(User::getAge,29).set(User::getEmail,"test3update@baomidou.com").eq(User::getName,"Tom"));

    }

```







# 多数据源

## 实现原理

```
1.先通过自动配置注入配置文件数据源信息,然后如果数据源名称有下划线,那么数据源会通过下划线前面的字符串进行分组,会加快数据源切换选择
2.当类中使用数据源注解的时候,会被mp拦截,先查询数据源信息,然后本地ThreadLocal通过先进后出的队列存储数据源信息,在查询结束后释放
3.注意 如果一个类中多个方法需要切换数据源,需用都过AopContext/bean获取的方式去查询数据,不然不会走动态代理
```

![image-20230111203615431](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230111203615431.png)

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

```yml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx) # 内置加密,使用请查看详细文档
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
       #......省略
       #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2
       
# 多主多从                      纯粹多库（记得设置primary）                   混合配置
spring:                               spring:                               spring:
  datasource:                           datasource:                           datasource:
    dynamic:                              dynamic:                              dynamic:
      datasource:                           datasource:                           datasource:
        master_1:                             mysql:                                master:
        master_2:                             oracle:                               slave_1:
        slave_1:                              sqlserver:                            slave_2:
        slave_2:                              postgresql:                           oracle_1:
        slave_3:                              h2:                                   oracle_2:
```

使用 **@DS** 切换数据源。

**@DS** 可以注解在方法上或类上，**同时存在就近原则 方法上注解 优先于 类上注解**。

|     注解      |                   结果                   |
| :-----------: | :--------------------------------------: |
|    没有@DS    |                默认数据源                |
| @DS("dsName") | dsName可以为组名也可以为具体某个库的名称 |

## hikari

```yml
spring:
  name: shanghai
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          username: wxy
          password: wxy
          url: jdbc:mysql://ip:3306/shardingjdbc1?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
          driver-class-name: com.mysql.jdbc.Driver
        slave1:
          username: wxy
          password: wxy
          url: jdbc:mysql://ip:3306/shardingjdbc2?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
          driver-class-name: com.mysql.jdbc.Driver
        slave2:
          username: wxy
          password: wxy
          url: jdbc:mysql://ip:3306/shardingjdbc3?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
          driver-class-name: com.mysql.jdbc.Driver
    hikari:
      connection-timeout: 60000
      validation-timeout: 3000
      idle-timeout: 60000 # 此属性控制允许连接在池中保持空闲状态的最长时间
      login-timeout: 5
      max-lifetime: 60000
      maximum-pool-size: 10
      minimum-idle: 10
      read-only: false
      connection-test-query: select 1
```

## druid

```properties
# 多数据源配置
mybatis-plus.mapper-locations=classpath*:/com/seasky/**/*.xml
spring.datasource.dynamic.primary=sfp
# sfp_main数据源
spring.datasource.dynamic.strict=false
spring.datasource.dynamic.datasource.sfp.username=SFP_SA
spring.datasource.dynamic.datasource.sfp.password=Seasky$2012$
spring.datasource.dynamic.datasource.sfp.url=jdbc:sqlserver://ip:1433;databaseName=SFP_Main
spring.datasource.dynamic.datasource.sfp.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
# 指标数据源
spring.datasource.dynamic.datasource.quota.username=sa
spring.datasource.dynamic.datasource.quota.password=Seaskysh@db2020
spring.datasource.dynamic.datasource.quota.url=jdbc:sqlserver://ip:8433;databaseName=Budget_Quota_Test
spring.datasource.dynamic.datasource.quota.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
# druid配置
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.allow=127.0.0.1
spring.datasource.druid.stat-view-servlet.reset-enable=false
spring.datasource.druid.stat-view-servlet.login-username=druid
spring.datasource.druid.stat-view-servlet.login-password=druid
spring.datasource.dynamic.druid.initial-size=1
spring.datasource.dynamic.druid.min-idle=1
spring.datasource.dynamic.druid.max-active=5
spring.datasource.dynamic.druid.validation-query=SELECT 1
spring.datasource.dynamic.druid.validation-query-timeout=1
spring.datasource.dynamic.druid.test-while-idle=true
spring.datasource.dynamic.druid.test-on-borrow=true
spring.datasource.dynamic.druid.keep-alive=true
```

```java
@Service
@DS("slave")
public class UserServiceImpl implements UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List selectAll() {
    return  jdbcTemplate.queryForList("select * from user");
  }
  
  @Override
  @DS("slave_1")
  public List selectByCondition() {
    return  jdbcTemplate.queryForList("select * from user where age >10");
  }
}
```

# 脚本自动维护

**把本地修改的sql都加入到sql脚本里 同步到线上**

```java
@Component
public class MysqlDdl extends SimpleDdl {
    /**
     * 执行 SQL 脚本方式
     */
    @Override
    public List<String> getSqlFiles() {
        return Arrays.asList(
                // 内置包方式,谁在前面先执行谁
                "db/table.sql",
                "db/struct.sql"
        );
    }
}
```

![image-20230107144638331](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230107144638331.png)

**在程序运行后会执行这两个sql文件,会创建ddl_history表记录执行日志,想要下次还会执行sql文件,必须要删除表ddl_history**

![image-20230107144710437](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230107144710437.png)
