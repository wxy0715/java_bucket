package com.wxy.lock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.lock.entity.Stock;

public interface StockService extends IService<Stock> {

    void jvm();

    void mysql();

    void mysql1();

    void mysql2();

    void redisLockRegistry();

    void redisson();

    void zookeeper();

    void curator();
}
