package com.wxy.lock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.lock.entity.MysqlLock;
import com.wxy.lock.entity.Stock;
import com.wxy.lock.mapper.LockMapper;
import com.wxy.lock.mapper.StockMapper;
import com.wxy.lock.service.StockService;
import com.wxy.lock.util.ZkLock;
import com.wxy.lock.config.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

    private static final Logger log = LoggerFactory.getLogger(Class.class);

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private LockMapper lockMapper;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void jvm() {
        log.info("进入方法");
        lock.lock();
        //1.查询库存
        try {
            log.info("获取到锁");
            Stock stock = baseMapper.selectOne(new QueryWrapper<Stock>().eq("product_code", 1001));
            //2.判断库存是否充足
            if (stock != null && stock.getCount() > 0) {
                stock.setCount(stock.getCount() - 1);
                //3.更新库存
                baseMapper.updateById(stock);
            }
        } finally {
            lock.unlock();
            log.info("释放锁");
        }
    }

    //使用mysql悲观锁  select  for update
    //    for update仅适用于InnoDB，且必须在事务块(BEGIN/COMMIT)中才能生效。
    //    在进行事务操作时，通过“for update”语句，MySQL会对查询结果集中每行数据都添加排他锁，其他线程对该记录的更新与删除操作都会阻塞。
    //    排他锁包含行锁、表锁。
    @Override
    @Transactional//开启事务
    public void mysql() {
        log.info("查询库存");
        List<Stock> stocks = this.stockMapper.queryStock(1001);
        Stock stock = stocks.get(0);
        if (stock != null && stock.getCount() > 0) {
            stock.setCount(stock.getCount() - 1);
            log.info("更新"+stock.getCount());
            this.stockMapper.updateById(stock);
        }
    }

    //使用mysql乐观锁
    //@Transactional  使用乐观锁不能加事务，不然会导致等待时间过久报连接超时
    @Override
    public void mysql1() {
        //1.查询库存
        List<Stock> stocks = this.stockMapper.selectList(new QueryWrapper<Stock>().eq("product_code", 1001));
        //取第一个（省略仓库判断）
        Stock stock = stocks.get(0);
        //2.判断库存是否充足
        if (stock != null && stock.getCount() > 0) {
            stock.setCount(stock.getCount() - 1);
            Integer version = stock.getVersion();
            stock.setVersion(version + 1);
            //3.更新库存
            if (this.stockMapper.update(stock, new QueryWrapper<Stock>().eq("id", stock.getId()).eq("version", version)) == 0) {
                try {
                    //递归方法设置一定的休眠时间防止栈溢出
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //更新失败重试
                this.mysql1();
            }
        }
    }

    //使用mysql实现分布式锁（性能低下！）:利用mysql唯一索引原理
    @Override
    public void mysql2() {
        try {
            //加锁
            MysqlLock lock = new MysqlLock();
            lock.setMylock("lock");
            lockMapper.insert(lock);

            //1.查询库存
            String stock = (String)redisCache.get("stock");
            //2.判断库存是否充足
            if (!StringUtils.isEmpty(stock)) {
                Integer st = Integer.valueOf(stock);
                if (st > 0) {
                    //3.更新库存
                    redisCache.set("stock", String.valueOf(st - 1),1,TimeUnit.HOURS);
                }
            }

            //解锁
            lockMapper.deleteById(lock.getId());
        } catch (Exception e) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.mysql2();
        }
    }

    @Override
    public void redisLockRegistry() {
        log.info("抢锁开始！！！");
        Lock lock = redisLockRegistry.obtain("key");
        try {
            boolean lockResult = lock.tryLock(50, TimeUnit.SECONDS);
            if (!lockResult) {
                throw new RuntimeException("获取锁失败");
            }
            log.info("我获得了锁！！！");
            Stock stock = baseMapper.selectOne(new QueryWrapper<Stock>().eq("product_code", 1001));
            //2.判断库存是否充足
            if (stock != null && stock.getCount() > 0) {
                stock.setCount(stock.getCount() - 1);
                //3.更新库存
                baseMapper.updateById(stock);
            }
        } catch (Exception e){
            throw new RuntimeException("方法异常");
        }finally {
            lock.unlock();
            log.info("释放锁！！！");
        }
    }

    // 自带可重入和自动续期
    @Override
    public void redisson() {
        log.info("抢锁开始！！！");
        RLock lock = redissonClient.getLock("lock");
        try {
            lock.lock();
            log.info("我获得了锁！！！");
            Stock stock = baseMapper.selectOne(new QueryWrapper<Stock>().eq("product_code", 1001));
            //2.判断库存是否充足
            if (stock != null && stock.getCount() > 0) {
                stock.setCount(stock.getCount() - 1);
                //3.更新库存
                baseMapper.updateById(stock);
            }
            //为了测试自动续期，增加了业务处理时间
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    //（性能低下！）-还会出错
    @Override
    public void zookeeper() {
        log.info("我进入了方法！");
        try (ZkLock zkLock = new ZkLock()) {
            if (zkLock.getLock("order")){
                log.info("我获得了锁！！！");
                Stock stock = baseMapper.selectOne(new QueryWrapper<Stock>().eq("product_code", 1001));
                //2.判断库存是否充足
                if (stock != null && stock.getCount() > 0) {
                    stock.setCount(stock.getCount() - 1);
                    //3.更新库存
                    baseMapper.updateById(stock);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成！");
    }


    @Override
    public void curator() {
        log.info("我进入了方法！");
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/order");
        try{
            if (lock.acquire(30, TimeUnit.SECONDS)){
                log.info("我获得了锁！！");
                Stock stock = baseMapper.selectOne(new QueryWrapper<Stock>().eq("product_code", 1001));
                //2.判断库存是否充足
                if (stock != null && stock.getCount() > 0) {
                    stock.setCount(stock.getCount() - 1);
                    //3.更新库存
                    baseMapper.updateById(stock);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                log.info("我释放了锁！！");
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("方法执行完成！");
    }
}
