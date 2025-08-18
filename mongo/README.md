# mongo 模块使用手册

## 模块概述

mongo 是一个 MongoDB 封装示例模块，展示了如何在项目中集成和使用 MongoDB 数据库功能。

## 主要功能

- MongoDB 连接管理
- MongoDB 数据操作（增删改查）
- MongoDB 配置管理

## 依赖模块

- core-basic

## 配置文件

模块使用 `application.yml` 配置文件进行配置：

```yaml
spring:
  data:
    mongodb:
        host: 192.168.30.49
        port: 27017
        database: ai
        authentication-database: admin
        username: admin
        password: admin
```

## 使用方法

1. 添加 Maven 依赖：

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
```

2. 配置 application.yml 文件中的 MongoDB 连接参数

3. 启动类加@EnableMongoRepositories

4. 创建实体类并使用相关注解：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")
public class Account implements Serializable {
    @MongoId
    @Id
    private Long id;

    private Long projectId;

    private Long limitId;

    private Long accountId;

    private String loan;
}
```

4. 创建 Repository 接口：

```java
@Repository
public interface AccountDao extends MongoRepository<Account,Long> {
    List<Account> findByProjectId(Long projectId);
}

```

5. 使用 Repository 进行数据操作：

```java
@RestController
public class AccountController {
    @Autowired
    private AccountDao accountDao;

    @GetMapping("/insert")
    public void test1() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 插入数据
        long size = 160000;
        long length = size/20000;
        for (int i = 0; i <length; i++) {
            insert();
        }
        stopWatch.stop();
        System.out.println("插入耗时秒:" + stopWatch.getTotalTimeSeconds());
    }

    @GetMapping("/find")
    public void find() {
        Long startTime = System.currentTimeMillis();
        List<Account> accountList = accountDao.findByProjectId(1840215777611362317L);
        System.out.println("查询数量:" + accountList.size());
        System.out.println("查询耗时秒:" + (System.currentTimeMillis()-startTime)/1000);
        System.out.println(accountList.size());
    }

    public void insert(){
        CompletableFuture.runAsync(() -> {
            Snowflake snowflake = new Snowflake();
            long projectId = snowflake.nextId();
            System.out.println(projectId);
            List<Account> accountList = new ArrayList<>();
            for (int i = 0; i < 20000; i++) {
                Account account = new Account();
                long id = snowflake.nextId();
                long limitId = snowflake.nextId();
                long accountId = snowflake.nextId();
                account.setId(id);
                account.setProjectId(projectId);
                account.setLimitId(limitId);
                account.setAccountId(accountId);
                account.setLoan("YES");
                accountList.add(account);
            }
            accountDao.insert(accountList);
        });
    }
}
```
