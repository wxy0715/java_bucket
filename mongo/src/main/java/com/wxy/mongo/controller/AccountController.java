package com.wxy.mongo.controller;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import com.mongodb.client.result.UpdateResult;
import com.wxy.mongo.dao.AccountDao;
import com.wxy.mongo.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

//    @GetMapping("/update")
//    public void update() {
//        Account account = new Account();
//        account.setAccountId(1749609744215195650L);
//        account.setLoan("NO");
//        accountDao.updateAccountsByAccountId(account);
//    }

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
