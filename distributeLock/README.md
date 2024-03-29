# 性能压测(100并发)

![image-20221214230955251](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20221214230955251.png)

## jvm锁

![image-20230512210403093](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512210403093.png)

## mysql悲观锁

![image-20230512210549752](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512210549752.png)

## mysql乐观锁

![image-20230512210646260](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512210646260.png)

## mysql唯一索引

![image-20230512210929382](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512210929382.png)

## RedisLockRegistry

![image-20230512211024853](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512211024853.png)

## redission 

![image-20230512211200496](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512211200496.png)

## zookeeper

实际成功60条

![image-20230512211236798](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512211236798.png)

## Curator

![image-20230512211337021](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20230512211337021.png)

## redission和RedisLockRegistry

```
在单节点的Redis架构下，RedisLockRegistry是可以满足一些不复杂的场景的，但是需要考虑锁的续租、Redis集群架构部署等问题的话，这个框架可能就会稍显拉垮，所以大多数情况都是使用Redisson
```

**https://www.jianshu.com/p/b60db9a717c2**

# 分布式幂等

### 定义

接口幂等性是指用户对于同一操作发起的一次请求或者多次请求的结果是一致的,不会因为多次点击而产生了不同结果。在分布式系统中,接口由于有三态(成功、失败、无响应)的问题,为了提高系统的可用性,重试是不可避免的,重试就会引发幂等性问题,这是一个因果关系。

### 常见幂等性问题

#### 重复下单

在App中下订单的时候,点击确认之后,没反应,就又点击了几次。在这种情况下,如果无法保证该接口的幂等性,那么将会出现重复下单问题。

#### 重复消费

在接收消息的时候,消息推送出现重复。如果处理消息的接口没有保证幂等性,那么重复消费消息产生的影响可能会非常大的。

#### 发送短信

如果APP重复点击调用后台接口,后台重复调用第三方接口,造成用户收到多条短信,一般情况下控制在60s内再重发短信,如果不做控制,每一次调用第三方接口都会收费。

#### form表单提交

我们在填写某些form表单时,保存按钮不小心快速点了两次,表中竟然产生了两条重复的数据,只是id不一样。

#### 接口超时重试

项目中为了解决接口超时问题,通常会引入了重试机制。第一次请求接口超时了,请求方没能及时获取返回结果,为了避免返回错误的结果,于是会对该请求重试几次,这样也会产生重复的数据。

### 天然的幂等操作

#### 查询操作

在数据不变的情况下,查询一次和多次的结果是一样的。

#### 删除操作

删除一次和删除多次的结果是一样的,都是把结果删除,有点多余感觉。

### 非幂等操作

添加和修改操作,如果不做幂等性处理,有可能每调用一次,都会对数据结果产生影响。

### 幂等性解决方案

1.数据库添加唯一索引:比如订单号是唯一索引,防止生产重复订单
2.分布式锁:防止应用程序出现并发操作
3.token机制,防止重复提交:提交后台时带token值,先判断token是否存在,存在删除 token,执行业务逻辑
4.数据库通过乐观锁(update table_name set version=version+1 where version=0)
5.JVM锁(Synchronized/Lock)只适用于单机环境
6.数据库悲观锁,通常是通过主键或唯一索引和事务一起实现

# 集群模式
https://blog.51cto.com/u_16099301/6619000

# 主从+哨兵模式
https://blog.csdn.net/xch_yang/article/details/104019552