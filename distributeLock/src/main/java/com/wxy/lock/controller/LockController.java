package com.wxy.lock.controller;

import com.wxy.lock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/jvm")
    public String jvm() {
        //使用JVM锁：ReentrantLock、synchronized
        stockService.jvm();
        return "ok";
    }

    @GetMapping("/mysql")
    public String mysql() {
        //使用mysql悲观锁  select for update
        stockService.mysql();
        return "ok";
    }

    @GetMapping("/mysql1")
    public String mysql1() {
        //使用mysql乐观锁
        stockService.mysql2();
        return "ok";
    }

    @GetMapping("/mysql2")
    public String mysql2() {
        //使用mysql实现分布式锁
        stockService.mysql2();
        return "ok";
    }

    @GetMapping("/redisLockRegistry")
    public String redisLockRegistry() {
        //使用redisLockRegistry框架实现分布式锁
        stockService.redisLockRegistry();
        return "ok";
    }

    @GetMapping("/redisson")
    public String redisson() {
        //使用Redisson框架实现分布式锁（自带可重入和自动续期）
        stockService.redisson();
        return "ok";
    }

    @GetMapping("/zookeeper")
    public String zookeeper() {
        //使用zookeeper实现分布式锁
        stockService.zookeeper();
        return "ok";
    }

    @GetMapping("/curator")
    public String curator() {
        //使用curator框架实现分布式锁
        stockService.curator();
        return "ok";
    }
}
