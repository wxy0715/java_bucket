# 版本5.2.0

**官网:https://shardingsphere.apache.org/document/5.2.0/cn/user-manual/shardingsphere-jdbc/yaml-config/rules/readwrite-splitting**

**gitee地址:https://gitee.com/wxy0715/project/tree/master/springboot/shardingjdbc**

# 整合springboot

## pom

```xml
<dependencies>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
            <version>5.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>1.33</version>  <!-- 必须要的 -->
        </dependency>
    </dependencies>
```

## 数据库

```sql
# 影子,加密,读写分离测试表
create table test
(
    id           int auto_increment,
    name         varchar(100)          null,
    name_plain   varchar(100)          null comment '明文用户名',
    name_encrypt varchar(100)          null comment '加密用户名',
    shadow       tinyint default false not null comment '是否是影子表',
    constraint test_pk
        primary key (id)
);
```



# 配置介绍

## 主配置文件

**application.properties**

```properties

```

## sql日志

**可以查看sql执行过程,用了哪个数据源和sql日志**

```properties
# 配置文件中加入
spring.shardingsphere.props.sql-show=true
```

![image-20230104202100795](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230104202100795.png)



## 数据源配置

```properties
# 配置真实数据源
spring.shardingsphere.datasource.names=ds1,ds2

# 配置第 1 个数据源
spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds1.jdbc-url=jdbc:mysql://localhost:3306/ds1
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=

# 配置第 2 个数据源
spring.shardingsphere.datasource.ds2.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds2.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds2.jdbc-url=jdbc:mysql://localhost:3306/ds2
spring.shardingsphere.datasource.ds2.username=root
spring.shardingsphere.datasource.ds2.password=
```



## 规则配置

### 数据分片

**分库分表有两种方式：垂直切分和水平切分**

**垂直切分：垂直分表和垂直分库**
**垂直分表：操作数据库中某张表，把这张表中一部分字段数据存到一张新表里面，再把这张表另一部分字段数据存到另外一张表里面**
**垂直分库：把单一数据库按照业务进行划分，专库专表**
**水平切分：水平分表和水平分库**
**可以将数据的水平切分理解为是按照数据行的切分，就是将表中的某些行切分到一个数据库，而另外的某些行又切分到其他的数据库中，这也就是对应的分表和分库**

**建表**

```sql
-- 不设置主键自增,交给mybatisplus自动生成(默认雪花算法)
create table user1(
	id BIGINT(20) PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	user_id BIGINT(20) NOT NULL
);
create table user2(
	id BIGINT(20) PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	user_id BIGINT(20) NOT NULL
);
```

**实体类**

```java
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
  	// mp雪花算法自动生成
    private Long id;
    private String name;
    private Long userId;
}
```

#### 单库水平分表

> 如果添加用户id是奇数把数据添加user1，如果偶数添加到user2

**配置文件**

```properties
#################################################### 单库水平分表 ######################################
# 配置真实数据源（给数据源取一个名字）
spring.shardingsphere.datasource.names=ds
# 配置第 1 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds.username=wxy
spring.shardingsphere.datasource.ds.password=wxy

# 单库水平分表策略
spring.shardingsphere.rules.sharding.tables.user.actual-data-nodes=ds.user$->{1..2}
spring.shardingsphere.rules.sharding.tables.user.table-strategy.standard.sharding-column=id
spring.shardingsphere.rules.sharding.tables.user.table-strategy.standard.sharding-algorithm-name=user-inline

# 指定主键生成策略 主键id通过雪花算法生成
spring.shardingsphere.rules.sharding.tables.user.key-generate-strategy.column=id
spring.shardingsphere.rules.sharding.tables.user.key-generate-strategy.key-generator-name=snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.type=SNOWFLAKE

# 分表算法
spring.shardingsphere.rules.sharding.sharding-algorithms.user-inline.type=INLINE
spring.shardingsphere.rules.sharding.sharding-algorithms.user-inline.props.algorithm-expression=user$->{id % 2 +1}
```

**测试类**

```java
    /**
     * 单库水平分表策略
     * 分别插入了不同的数据库
     */
    @Test
    public void test(){
        for (int i =1;i<11;i++){
            User user = new User();
            user.setName("yue");
            user.setUserId(1L);
            userMapper.insert(user);
        }
    }

	  // SELECT  id,name,user_id  FROM user1 UNION ALL SELECT  id,name,user_id  FROM user2
    @Test
    void search(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> list = userMapper.selectList(wrapper);
        for (Object user:list){
            System.out.println(user);
        }
    }
		
```

#### 水平分库分表

> 这里还是用上面的表结构，但是在这里我们将创建两个库，springboot1和springboot2两个库，当id为奇数加入shardingjdbc1这个库当中，偶数加入到shardingjdbc2这个库当中  

```properties
# 配置真实数据源（给数据源取一个名字）
spring.shardingsphere.datasource.names=ds1,ds2
# 配置第 1 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds1.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds1.username=wxy
spring.shardingsphere.datasource.ds1.password=wxy

# 配置第 2 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds2.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds2.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds2.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc2?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds2.username=wxy
spring.shardingsphere.datasource.ds2.password=wxy

# 标准分片表配置 据源名 + 表名组成
spring.shardingsphere.rules.sharding.tables.user.actual-data-nodes=ds$->{1..2}.user$->{1..2}
# 分表配置
spring.shardingsphere.rules.sharding.tables.user.table-strategy.standard.sharding-column=id
spring.shardingsphere.rules.sharding.tables.user.table-strategy.standard.sharding-algorithm-name=user-inline
# 分表算法
spring.shardingsphere.rules.sharding.sharding-algorithms.user-inline.type=INLINE
spring.shardingsphere.rules.sharding.sharding-algorithms.user-inline.props.algorithm-expression=user$->{id % 2 +1}

# 分库配置
spring.shardingsphere.rules.sharding.tables.user.database-strategy.standard.sharding-column=id
spring.shardingsphere.rules.sharding.tables.user.database-strategy.standard.sharding-algorithm-name=database-inline
# 分库算法
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type=INLINE
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression=ds$->{id % 2 + 1}

# 指定主键生成策略 主键id通过雪花算法生成
spring.shardingsphere.rules.sharding.tables.user.key-generate-strategy.column=id
spring.shardingsphere.rules.sharding.tables.user.key-generate-strategy.key-generator-name=snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.type=SNOWFLAKE
```

#### 垂直分库

**垂直分表就不做实验了,我们每天都在做垂直分表,比如主子表的结构**

> user_detail只分配到了shardingjdbc3库中,指定专库

```sql
CREATE TABLE user_detail(
	user_id BIGINT(20) PRIMARY KEY,
	age VARCHAR(50) NOT NULL,
	sex VARCHAR(2) NOT NULL
);
```

```properties
# 配置真实数据源（给数据源取一个名字）
spring.shardingsphere.datasource.names=ds1,ds2,ds3
# 配置第 1 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds1.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds1.username=wxy
spring.shardingsphere.datasource.ds1.password=wxy

# 配置第 2 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds2.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds2.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds2.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc2?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds2.username=wxy
spring.shardingsphere.datasource.ds2.password=wxy

# 配置第 3 个数据源（对应自己的数据库）
spring.shardingsphere.datasource.ds3.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds3.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds3.url=jdbc:mysql://127.0.0.1:3306/shardingjdbc3?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds3.username=wxy
spring.shardingsphere.datasource.ds3.password=wxy


# 标准分片表配置 据源名 + 表名组成
spring.shardingsphere.rules.sharding.tables.user_detail.actual-data-nodes=ds3.user_detail
# 分表配置
spring.shardingsphere.rules.sharding.tables.user_detail.table-strategy.standard.sharding-column=user_id
spring.shardingsphere.rules.sharding.tables.user_detail.table-strategy.standard.sharding-algorithm-name=user_detail-inline
# 分表算法
spring.shardingsphere.rules.sharding.sharding-algorithms.user_detail-inline.type=INLINE
spring.shardingsphere.rules.sharding.sharding-algorithms.user_detail-inline.props.algorithm-expression=user_detail

# 指定主键生成策略 主键id通过雪花算法生成
spring.shardingsphere.rules.sharding.tables.user_detail.key-generate-strategy.column=user_id
spring.shardingsphere.rules.sharding.tables.user_detail.key-generate-strategy.key-generator-name=snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.type=SNOWFLAKE
```

#### 公共表

> 在进行分库分表之后，多个数据表的数据会存在公共使用的表，在这里shardingjdbc也提供了对公共表的操作，在多个库当中的相同表，再每对其中一个公共表进行操作之后，另外库里面的公共表也会随之进行该变

```sql
create table common(
	common_id BIGINT(20) PRIMARY KEY,
	common_name VARCHAR(50) NOT NULL,
	common_detail VARCHAR(20) NOT NULL
)
```

```properties
# 配置公共表
spring.shardingsphere.rules.sharding.broadcast-tables=common
spring.shardingsphere.rules.sharding.tables.common.key-generate-strategy.column=common_id
spring.shardingsphere.rules.sharding.tables.common.key-generate-strategy.key-generator-name=snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.type=SNOWFLAKE
```



### 读写分离

**读取算法:random(随机),round_robin(轮训)**

**创建两读一写数据源,目前只支持一个写数据源,还不支持写数据源数据自动同步到读数据源,需要额外的技术实现该功能**

```properties
# data source
spring.shardingsphere.datasource.names=write1,read1,read2

# write
spring.shardingsphere.datasource.write1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.write1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.write1.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1
spring.shardingsphere.datasource.write1.username=wxy
spring.shardingsphere.datasource.write1.password=wxy

# read1
spring.shardingsphere.datasource.read1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.read1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.read1.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc2
spring.shardingsphere.datasource.read1.username=wxy
spring.shardingsphere.datasource.read1.password=wxy

# read2
spring.shardingsphere.datasource.read2.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.read2.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.read2.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc3
spring.shardingsphere.datasource.read2.username=wxy
spring.shardingsphere.datasource.read2.password=wxy

# 读写分离静态配置
spring.shardingsphere.rules.readwrite-splitting.data-sources.readwrite.static-strategy.write-data-source-name= write1
spring.shardingsphere.rules.readwrite-splitting.data-sources.readwrite.static-strategy.read-data-source-names= read1,read2
# 轮训
spring.shardingsphere.rules.readwrite-splitting.data-sources.readwrite.load-balancer-name= round_robin
spring.shardingsphere.rules.readwrite-splitting.load-balancers.round_robin.type= ROUND_ROBIN
# 随机
#spring.shardingsphere.rules.readwrite-splitting.data-sources.readwrite.load-balancer-name= random
#spring.shardingsphere.rules.readwrite-splitting.load-balancers.random.type= random
```

```java
@Test
public void testReadWrite_read(){
  for (int i = 0; i < 10; i++) {
    com.wxy.shardingjdbc.model.Test test = testMapper.selectByPrimaryKey(1);
    System.out.println(JSON.toJSONString(test));
  }
}

@Test
public void testReadWrite_write(){
  com.wxy.shardingjdbc.model.Test test = new com.wxy.shardingjdbc.model.Test();
  test.setName("1");
  test.setShadow(0);
  testMapper.insert(test);
}
```

![image-20230105105005853](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230105105005853.png)

### 数据加密

**设置了原文列之后,会存储明文数据到数据库,spring.shardingsphere.props.query-with-cipher-column即可设置为true**

**一般是不用该功能的,都是代码实现较多**

```properties
# data source
spring.shardingsphere.datasource.names=write1
# write
spring.shardingsphere.datasource.write1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.write1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.write1.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1
spring.shardingsphere.datasource.write1.username=wxy
spring.shardingsphere.datasource.write1.password=wxy

# 加密算法类型
spring.shardingsphere.rules.encrypt.encryptors.name-encryptor.type=AES
# 加密算法属性配置
spring.shardingsphere.rules.encrypt.encryptors.name-encryptor.props.aes-key-value=123456abc
# 加密列名称
spring.shardingsphere.rules.encrypt.tables.test.columns.name_encrypt.cipher-column=name_encrypt
# 原文列名称
spring.shardingsphere.rules.encrypt.tables.test.columns.name_encrypt.plain-column=name_plain
# 加密算法名称
spring.shardingsphere.rules.encrypt.tables.test.columns.name_encrypt.encryptor-name=name-encryptor
# 是否使用加密列进行查询。在有原文列的情况下，可以使用原文列进行查询
spring.shardingsphere.props.query-with-cipher-column=false
```

**不使用原文列,数据库可以不配置name_plain字段**

```properties
# 加密列名称
spring.shardingsphere.rules.encrypt.tables.test.columns.name_encrypt.cipher-column=name_encrypt
# 加密算法名称
spring.shardingsphere.rules.encrypt.tables.test.columns.name_encrypt.encryptor-name=name-encryptor
# 是否使用加密列进行查询。在有原文列的情况下，可以使用原文列进行查询
spring.shardingsphere.props.query-with-cipher-column=true
```

```java
// 加密插入
@Test
public void encryptInsert(){
  com.wxy.shardingjdbc.model.Test test = new com.wxy.shardingjdbc.model.Test();
  test.setName("1");
  test.setShadow(0);
  test.setNameEncrypt("1");
  testMapper.insertSelective(test);
}
```

![image-20230105110521416](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230105110521416.png)

```java
// 解密读取
@Test
public void encryptSelect(){
  com.wxy.shardingjdbc.model.Test test = testMapper.selectByPrimaryKey(1);
  System.out.println(JSON.toJSONString(test));
}
```

![image-20230105110546836](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230105110546836.png)

### SQL 解析

```properties
# 是否解析 SQL 注释
spring.shardingsphere.rules.sql-parser.sql-comment-parse-enabled=true
 # SQL 语句本地缓存初始容量
spring.shardingsphere.rules.sql-parser.sql-statement-cache.initial-capacity=2000
# SQL 语句本地缓存最大容量
spring.shardingsphere.rules.sql-parser.sql-statement-cache.maximum-size=65535
# 解析树本地缓存初始容量
spring.shardingsphere.rules.sql-parser.parse-tree-cache.initial-capacity=128
#  解析树本地缓存最大容量
spring.shardingsphere.rules.sql-parser.parse-tree-cache.maximum-size=1024
```

### 影子库

**主要作用是想要测试压测线上数据库的时候不真正的操作线上的环境,从而造一个和线上一模一样的数据库,然后数据的插入和更新都会在该影子数据库内完成**

```sql
# data source
spring.shardingsphere.datasource.names=remote,local

# 生产数据源
spring.shardingsphere.datasource.remote.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.remote.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.remote.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc1
spring.shardingsphere.datasource.remote.username=wxy
spring.shardingsphere.datasource.remote.password=wxy

# 影子数据源
spring.shardingsphere.datasource.local.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.local.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.local.jdbc-url=jdbc:mysql://127.0.0.1:3306/shardingjdbc2
spring.shardingsphere.datasource.local.username=wxy
spring.shardingsphere.datasource.local.password=wxy

# 配置生产和影子关联的数据源名称(shadow-data-source)
spring.shardingsphere.rules.shadow.data-sources.shadow-data-source.production-data-source-name=remote
spring.shardingsphere.rules.shadow.data-sources.shadow-data-source.shadow-data-source-name=local

# 配置影子数据源
spring.shardingsphere.rules.shadow.tables.test.data-source-names=shadow-data-source
# 配置影子算法 ,数据库值匹配,字段shadow=1的都在影子库
spring.shardingsphere.rules.shadow.tables.test.shadow-algorithm-names=user-id-insert-match-algorithm,user-id-select-match-algorithm,simple-hint-algorithm
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-insert-match-algorithm.type=VALUE_MATCH
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-insert-match-algorithm.props.operation=insert
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-insert-match-algorithm.props.column=shadow
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-insert-match-algorithm.props.value=1
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-select-match-algorithm.type=VALUE_MATCH
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-select-match-algorithm.props.operation=select
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-select-match-algorithm.props.column=shadow
spring.shardingsphere.rules.shadow.shadow-algorithms.user-id-select-match-algorithm.props.value=1
# 数据库表的注释需要有 foo:bar的注释,就会匹配
#spring.shardingsphere.rules.shadow.shadow-algorithms.simple-hint-algorithm.type=SIMPLE_HINT
#spring.shardingsphere.rules.shadow.shadow-algorithms.simple-hint-algorithm.props.foo=bar
```

```java
@Test
public void select(){
  TestExample testExample = new TestExample();
  testExample.createCriteria().andShadowEqualTo(1);
  List<com.wxy.shardingjdbc.model.Test> tests = testMapper.selectByExample(testExample);
  System.out.println(JSON.toJSONString(tests));
}

@Test
public void insert(){
  com.wxy.shardingjdbc.model.Test test = new com.wxy.shardingjdbc.model.Test();
  test.setName("1");
  test.setShadow(0);
  test.setNameEncrypt("1");
  testMapper.insertSelective(test);
}
以上测试shadow为1的都会在影子库里为0的都在生产库里
```

![image-20230105115124565](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230105115124565.png)

![image-20230105115144451](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230105115144451.png)